package corpoints.server.Server.sevices;

import corpoints.server.Server.models.Account;
import corpoints.server.Server.models.Group;
import corpoints.server.Server.models.RequestInGroup;
import corpoints.server.Server.repositories.GroupRepository;
import corpoints.server.Server.repositories.RequestInGroupRepository;
import corpoints.server.Server.util.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class GroupService {

    @Autowired
    private GroupRepository repository;
    @Autowired
    private AccountService accountService;


    @Transactional
    public void create(Group group) {
        repository.save(group);
    }

    public List<Group> getGroups() {
        return repository.findAll();
    }

    public Group getGroup(String name) {
        return repository.getGroupByName(name).stream().findAny().orElse(null);
    }

    @Transactional
    public void update(Group group) {
        repository.save(group);
    }

    @Transactional
    public void tryGroup(Group group, Account sender) {
        //get from Base Data of Account-sender
        if (sender.getGroup() != null)
            throw new YouHaveGroupException();

        group.setAdmin(sender);
        group.setUsers(new ArrayList<>());
        group.getUsers().add(sender);
        create(group);
        sender.setGroup(group);
        accountService.update(sender);
    }

    @Transactional
    public void SendScoreToGroup(int score, Account sender) {
        Group group = sender.getGroup();

        if (group == null)
            throw new GroupNotFoundException();
        if (sender.getScore() < score)
            throw new NotEnoughPointsException();

        sender.addScore(-score);
        group.addScore(score);

        update(group);
        accountService.update(sender);
    }


    public void test() {
        if (Math.random() > 0.5)
            throw new testException();
    }

}
