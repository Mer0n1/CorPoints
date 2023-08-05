package corpoints.server.Server.sevices;

import corpoints.server.Server.models.Account;
import corpoints.server.Server.repositories.AccountRepository;
import corpoints.server.Server.util.exception.UncorrectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AccountService {
    @Autowired
    private AccountRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void register(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        repository.save(account);
    }

    public Account findByUsername(String username) {
        return repository.findByUsername(username).stream().findAny().orElse(null);
    }

    public List<Account> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void update(Account account) {
        repository.save(account);
    }

    @Transactional
    public void SendScore(int score, String who_name, Account sender) {
        Account target = findByUsername(who_name);

        if (sender.getScore() >= score & target != null) {
            sender.addScore(-score);
            target.addScore(score);
            update(sender);
        } else
            throw new UncorrectException();
    }
}
