import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**xml-база данных. Отвечает за запись, проверку и чтение информации о пользователях */
public class BaseData {

    private final List<InfoAccount> AccountsList;
    private final List<Group> groups;

    private File fXmlFile;
    private DocumentBuilderFactory dbFactory;
    private DocumentBuilder dBuilder;
    private Document doc;
    private String path;

    //save
    private Transformer tr;
    private DOMSource source;
    private FileOutputStream fos;
    private StreamResult result;

    public BaseData() {
        //initial AccountsList and Groups
        AccountsList = new ArrayList<>();
        groups = new ArrayList<>();
        path = "BaseData.xml"; //src/main/resources/BaseData.xml

        try {
            fXmlFile = new File(path); //src/main/resources/BaseData.xml
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
            tr = TransformerFactory.newInstance().newTransformer();
            source = new DOMSource(doc);
            //fos = new FileOutputStream("src/main/resources/BaseData.xml");
            //result = new StreamResult(fos);

            //initial accounts
            NodeList nList = doc.getElementsByTagName("Account");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    InfoAccount account = new InfoAccount(eElement.getAttribute("name"),
                            Integer.valueOf(eElement.getAttribute("score"))); //possibility of error
                    AccountsList.add(account);
                }
            }

            //initial groups
            NodeList nGroups = doc.getElementsByTagName("Group");
            for (int temp = 0; temp < nGroups.getLength(); temp++) {
                Node nNode = nGroups.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String creater = eElement.getAttribute("admin");
                    String score = eElement.getAttribute("score");
                    String name = eElement.getAttribute("name");
                    InfoAccount infoAccount = findAccount(creater);

                    if (infoAccount != null) {
                        Group group = new Group(infoAccount, name);
                        group.addScore(Integer.valueOf(score));
                        groups.add(group);
                        infoAccount.group = group;

                        NodeList nUsers = eElement.getElementsByTagName("User");

                        for (int temp1 = 0; temp1 < nUsers.getLength(); temp1++) {
                            Node node1 = nUsers.item(temp1);

                            if (node1.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElement1 = (Element) node1;
                                String user = eElement1.getAttribute("name");
                                if (user.equals(creater)) continue;

                                for (int j = 0; j < AccountsList.size(); j++)
                                    if (AccountsList.get(j).nickname.equals(user)) {
                                        group.addUser(AccountsList.get(j));
                                        AccountsList.get(j).group = group;
                                    }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**Проверить на наличие пользователя в базе данных */
    boolean CheckData(String name, String password)
    {
        try {
            XMLStreamReader xmlr = XMLInputFactory.newInstance()
                    .createXMLStreamReader(path, new FileInputStream(path));

            while (xmlr.hasNext()) {
                xmlr.next();

                if (xmlr.isStartElement())
                    if (xmlr.getLocalName().equals("Account")) {
                        if (xmlr.getAttributeValue(0).equals(name) && xmlr.getAttributeValue(1).equals(password))
                            return true;
                }
            }
        } catch (FileNotFoundException | XMLStreamException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**Добавление в базу данных xml */
    boolean addAccount(String name, String password) { //check boolean
        if (CheckData(name, password)) return false; //check existing account

        try {
            Node root = doc.getDocumentElement();

            //needs
            Element nd = doc.createElement("Account");
            nd.setAttribute("name", name);
            nd.setAttribute("password", password);
            nd.setAttribute("score", "0");
            root.appendChild(nd);

            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        InfoAccount account = new InfoAccount();
        account.nickname = name;
        AccountsList.add(account);

        return true;
    }

    public boolean addGroup(String name, String nameAdmin) {

        //Создаем группу
        InfoAccount adm = findAccount(nameAdmin);
        if (adm == null) return false;

        Group group = new Group(adm, nameAdmin);
        groups.add(group);
        adm.group = group;

        try {
            //Node Groups = doc.getElementById("Groups");
            NodeList list = doc.getElementsByTagName("Groups");
            Element element = (Element) list.item(0);

            //needs
            Element nd = doc.createElement("Group");
            nd.setAttribute("name", name);
            nd.setAttribute("admin", nameAdmin);
            nd.setAttribute("score", "0");

            Element user = doc.createElement("User");
            user.setAttribute("name", nameAdmin);
            nd.appendChild(user);

            element.appendChild(nd);

            save();
        } catch (Exception e) {
            e.printStackTrace();
            groups.remove(group);
            return false;
        }

        return true;
    }

    /**Добавление пользователя в группу */
    public void addUserToGroup(String nickname, String namegroup) {

        Group group = null;
        InfoAccount account = findAccount(nickname);

        for (int j = 0; j < groups.size(); j++)
            if (groups.get(j).getName().equals(namegroup)) {
                group = groups.get(j);
                break;
            }
        if (account == null || group == null)
            return;

        group.addUser(account);
    }

    /**Удаление пользователя из группы */
    public void removeUserFromGroup(String nickname) {
        InfoAccount account = findAccount(nickname);
        if (account == null) return;

        Group group = account.group;
        group.removeUser(account);

        //if nickname is admin group then delete group //think about it
        if (group.getNameAdmin().equals(nickname)) {
            group.DeleteGroup();
            groups.remove(group);
        }

        group = null;
    }

    /**Отправка очков выбранному пользователю*/
    public void SendScore(String from_name, String who_name, int points) {
        InfoAccount from = null, whom = null;

        for (int j = 0; j < AccountsList.size(); j++) {
            if (AccountsList.get(j).nickname.equals(from_name))
                from = AccountsList.get(j);
            if (AccountsList.get(j).nickname.equals(who_name))
                whom = AccountsList.get(j);
        }

        if (from != null && whom != null && from.score >= points) {
            from.score -= points;
            whom.score += points;
        }
        UpdateUserData(from);
        UpdateUserData(whom);
    }
    public boolean SendScoreToGroup(String score, Group group, String nameSender) {

        InfoAccount account = findAccount(nameSender);
        int scorei = 0;

        if (score.matches("[0-9]+") && account != null) {
            scorei = Integer.valueOf(score);
            group.addScore(scorei);
        } else
            return false;

        account.score -= scorei;
        UpdateUserData(account);

        return true;
    }

    private void UpdateBaseData() { //full update all users of xml-basedata and groups
        for (InfoAccount account : AccountsList)
            UpdateUserData(account);
    }
    /**Обновить данные 1 аккаунта */
    private void UpdateUserData(InfoAccount account) {

        try {
            NodeList nList = doc.getElementsByTagName("Account");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    if (eElement.getAttribute("name").equals(account.nickname)) {
                        eElement.setAttribute("score", String.valueOf(account.score));
                        break;
                    }
                }
            }
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**Операция обновления группы. Обычно требуется при отключения сервера или периодического обновления */
    public void UpdateAllGroups() {

        //update data
        NodeList nGroups = doc.getElementsByTagName("Group");

        while (nGroups.getLength() != 0) {
            Node nNode = nGroups.item(0);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                NodeList listGr = doc.getElementsByTagName("Groups");
                Element eGroups = (Element) listGr.item(0);
                eGroups.removeChild(eElement); //delete group
            }
        }

        for (int temp = 0; temp < groups.size(); temp++) {
                Group group = groups.get(temp);
                NodeList listGr = doc.getElementsByTagName("Groups");
                Element eGroups = (Element) listGr.item(0);
                Element newGroup = doc.createElement("Group");
                newGroup.setAttribute("admin", group.getNameAdmin());
                newGroup.setAttribute("name", group.getName());
                newGroup.setAttribute("score", String.valueOf(group.getGroupScore()));
                eGroups.appendChild(newGroup);

                //Обновление пользователей
                List<InfoAccount> users = groups.get(temp).getUsersCopy();

                /*while (list.getLength() != 0)
                    eElement.removeChild(list.item(0));*/
                for (int j = 0; j < users.size(); j++) {
                    Element user = doc.createElement("User");
                    user.setAttribute("name", users.get(j).nickname);
                    newGroup.appendChild(user);
                }
        }
        save();
    }


    /**Важная строка инициализации. Инициализирует InfoAccount в Client.
     * Этап полной инициализации */
    void initializeClient(Client client, String nickname) {
        //баг, infoAccount нет. Нужно его добавлять в addAccount
        for (int j = 0; j < AccountsList.size(); j++)
            if (AccountsList.get(j).nickname.equals(nickname)) {
                client.setInfoAccount(AccountsList.get(j));
                client.ininitiaze = true;
            }
    }

    /**Возвращает json массив всех пользователей */
    String getListJSONAccounts() {
        JSONArray array = new JSONArray();

        for (int j = 0; j < AccountsList.size(); j++) {
            JSONObject obj = new JSONObject();
            obj.put("name", AccountsList.get(j).nickname);
            obj.put("score", AccountsList.get(j).score);
            array.add(obj);
        }
        return array.toString();
    }

    List<Group> getGroups() { return groups; }

    private void save()
    {
        try {
            fos = new FileOutputStream(path);
            result = new StreamResult(fos);
            tr.transform(source, result);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
    private InfoAccount findAccount(String nickname) {

        for (int j = 0; j < AccountsList.size(); j++)
            if (AccountsList.get(j).nickname.equals(nickname))
                return AccountsList.get(j);
        return null;
    }
}
