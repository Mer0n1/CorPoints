import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
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
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            source = new DOMSource(doc);
            result = new StreamResult(new StringWriter());


            //initial accounts
            NodeList nList = doc.getElementsByTagName("Account");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    InfoAccount account = new InfoAccount
                            (eElement.getAttribute("name"),
                             eElement.getAttribute("password"),
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
                    String score   = eElement.getAttribute("score");
                    String name    = eElement.getAttribute("name");
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
    boolean CheckAccount(String name)
    {
        for (int j = 0; j < AccountsList.size(); j++)
            if (AccountsList.get(j).nickname.equals(name))
                return true;

        return false;
    }
    boolean CheckAutAccount(String name, String password) {
        for (int j = 0; j < AccountsList.size(); j++)
            if (AccountsList.get(j).nickname.equals(name) &&
                AccountsList.get(j).password.equals(password))
                return true;

        return false;
    }

    /**Добавление в базу данных xml */
    boolean addAccount(String name, String password) { //check boolean

        if (CheckAccount(name)) return false; //check existing account

        InfoAccount account = new InfoAccount(name, password, 0);
        AccountsList.add(account);

        return true;
    }

    public boolean addGroup(String name, String nameAdmin) {

        //Создаем группу
        InfoAccount adm = findAccount(nameAdmin);
        if (adm == null) return false;

        Group group = new Group(adm, name);
        groups.add(group);
        adm.group = group;

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
        account.group = group;
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

        account.group = null;
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
        UpdateAllAccounts();
        UpdateAllGroups();
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
                Element eGroup = (Element) listGr.item(0);
                eGroup.removeChild(eElement); //delete group
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

    /**Обновить всю информациб об аккаунтах */
    public void UpdateAllAccounts() {

        NodeList nList = doc.getElementsByTagName("Account");

        while (nList.getLength() != 0) {
            Node nNode = nList.item(0);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                NodeList listGr = doc.getElementsByTagName("Accounts");
                Element eAccount = (Element) listGr.item(0);
                eAccount.removeChild(eElement); //delete account
            }
        }

        for (InfoAccount account : AccountsList) {
            Element nd = doc.createElement("Account");
            nd.setAttribute("name", account.nickname);
            nd.setAttribute("password", account.password);
            nd.setAttribute("score", String.valueOf(account.score));

            NodeList nAccounts = doc.getElementsByTagName("Accounts");
            Element eAccounts = (Element) nAccounts.item(0);
            eAccounts.appendChild(nd);
        }
        save();
    }


    /**Важная строка инициализации. Инициализирует InfoAccount в Client.
     * Этап полной инициализации */
    public void initializeClient(Client client, String nickname) {
        //баг, infoAccount нет. Нужно его добавлять в addAccount
        for (int j = 0; j < AccountsList.size(); j++)
            if (AccountsList.get(j).nickname.equals(nickname)) {
                client.setInfoAccount(AccountsList.get(j));
                client.ininitiaze = true;
            }
    }

    /**Возвращает json массив всех пользователей */
    public String getListJSONAccounts() {
        JSONArray array = new JSONArray();

        for (int j = 0; j < AccountsList.size(); j++) {
            JSONObject obj = new JSONObject();
            obj.put("name", AccountsList.get(j).nickname);
            obj.put("score", AccountsList.get(j).score);
            array.add(obj);
        }
        return array.toString();
    }

    public List<Group> getGroups() { return groups; }

    private void save()
    {
        try {
            fos = new FileOutputStream(path);
            result = new StreamResult(fos);
            tr.transform(source, result);

            //delete empty lines xml
            XPath xp = XPathFactory.newInstance().newXPath();
            NodeList nl = (NodeList) xp.evaluate("//text()[normalize-space(.)='']", doc, XPathConstants.NODESET);

            for (int i=0; i < nl.getLength(); ++i) {
                Node node = nl.item(i);
                node.getParentNode().removeChild(node);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
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
