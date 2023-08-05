package corpoints.server.Server.repositories;

import corpoints.server.Server.models.Account;
import corpoints.server.Server.models.Group;
import corpoints.server.Server.models.RequestInGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestInGroupRepository extends JpaRepository<RequestInGroup, Integer> {
    public RequestInGroup getById(int id);
    public void deleteById(int id);
    public List<RequestInGroup> findAllByGroup(Group group);
    public List<RequestInGroup> findAllByOwner(Account account);
}
