package corpoints.server.Server.controllers;

import corpoints.server.Server.models.Account;
import corpoints.server.Server.security.AccountDetails;
import corpoints.server.Server.sevices.AccountService;
import corpoints.server.Server.util.AccountsValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountsValidator validator;

    @GetMapping("/getAll")
    public List<Account> getAll() {
        return accountService.findAll();
    }

    @GetMapping("/getAccount")
    public Account getAccount(String name) {
        return accountService.findByUsername(name);
    }

    @PatchMapping("/SendScore")
    public String SendScore(@RequestParam int score, @RequestParam String name,
                            Authentication authentication) {
        Account sender = ((AccountDetails) authentication.getPrincipal()).getAccount();
        accountService.SendScore(score, name, sender);

        return "Ok";
    }


    @PostMapping("/register")
    public String register(@RequestBody @Valid Account account,
                         BindingResult bindingResult) {
        validator.validate(account, bindingResult);

        if (bindingResult.hasErrors())
            return bindingResult.getAllErrors().toString();

        accountService.register(account);

        return "Ok";
    }

    @GetMapping("/AuthTrue")
    public String ResultAuthTrue() {
        return "Ok";
    }

    @GetMapping("/AuthFalse")
    public String ResultAuthFalse() {
        return "No";
    }

}
