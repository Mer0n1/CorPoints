package corpoints.server.Server.sevices;

import corpoints.server.Server.models.Account;
import corpoints.server.Server.models.Group;
import corpoints.server.Server.models.RequestInGroup;
import corpoints.server.Server.repositories.RequestInGroupRepository;
import corpoints.server.Server.util.exception.GroupNotFoundException;
import corpoints.server.Server.util.exception.UncorrectException;
import corpoints.server.Server.util.exception.YouHaveGroupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RequestService {

    @Autowired
    private RequestInGroupRepository repository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private GroupService groupService;

    public List<RequestInGroup> getRequests() {
        return repository.findAll();
    }

    public RequestInGroup getRequest(int id) {
        return repository.getById(id);
    }

    public List<RequestInGroup> getRequestsFromGroup(String NameGroup) {
        return repository.findAllByGroup(groupService.getGroup(NameGroup));
    }

    @Transactional
    public void DeleteRequest(int id) {
        repository.deleteById(id);
    }

    @Transactional
    public void createRequest(RequestInGroup request) {
        repository.save(request);
    }

    @Transactional
    public void tryRequest(Account sender, RequestInGroup request) {
        Group group = groupService.getGroup(request.getGroup().getName());

        if (sender.getGroup() != null)
            throw new YouHaveGroupException();
        if (group == null)
            throw new GroupNotFoundException();

        request.setGroup(group);
        request.setOwner(sender);
    }

    @Transactional
    public void ProcessRequest(RequestInGroup request, Account sender) {
        Group group = request.getGroup();
        Account person  = request.getOwner();

        if (person.getGroup() != null)
            throw new YouHaveGroupException();
        if (group == null)
            throw new GroupNotFoundException();
        if (request.getAnswer() == null)
            throw new UncorrectException();
        if (request.getId() == 0)
            throw new UncorrectException();
        if (!group.getAdmin().getUsername().equals(sender.getUsername()))
            throw new UncorrectException();

        group = groupService.getGroup(group.getName());
        person = accountService.findByUsername(person.getUsername());

        if (request.getAnswer()) {
            group.getUsers().add(person);
            person.setGroup(group);
            accountService.update(person);

            //Delete all request this user
            List<RequestInGroup> list = repository.findAllByOwner(person);
            for (RequestInGroup request1 : list)
                DeleteRequest(request1.getId());
        }

        DeleteRequest(request.getId());
    }
}


