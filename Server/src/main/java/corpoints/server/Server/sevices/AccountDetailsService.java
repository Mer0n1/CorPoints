package corpoints.server.Server.sevices;

import corpoints.server.Server.models.Account;
import corpoints.server.Server.repositories.AccountRepository;
import corpoints.server.Server.security.AccountDetails;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> person = repository.findByUsername(username);

        if (person.isEmpty())
            throw new UsernameNotFoundException("User not found!");

        return new AccountDetails(person.get());
    }

}
