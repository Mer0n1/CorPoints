package corpoints.server.Server.util;

import corpoints.server.Server.models.Group;
import corpoints.server.Server.sevices.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class GroupValidator implements Validator {
    @Autowired
    private GroupService service;

    @Override
    public boolean supports(Class<?> clazz) {
        return Group.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Group group = (Group) target;

        if (service.getGroup(group.getName()) != null)
            errors.rejectValue("users", "", "Group already exists");
    }


}
