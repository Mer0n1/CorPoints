package corpoints.server.Server.util;

import corpoints.server.Server.models.Account;
import corpoints.server.Server.sevices.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AccountsValidator implements Validator {
    @Autowired
    private AccountService service;

    @Override
    public boolean supports(Class<?> clazz) {
        return Account.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {


        if (service.findByUsername(((Account)target).getUsername()) != null)
            errors.rejectValue("username", "",
                    "Пользователь с таким именем уже есть");
    }
}
