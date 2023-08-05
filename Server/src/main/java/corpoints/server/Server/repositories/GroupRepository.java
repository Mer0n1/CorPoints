package corpoints.server.Server.repositories;

import corpoints.server.Server.models.Account;
import corpoints.server.Server.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    List<Group> getGroupByName(String name);
}