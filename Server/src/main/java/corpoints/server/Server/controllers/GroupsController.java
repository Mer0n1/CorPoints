package corpoints.server.Server.controllers;

import corpoints.server.Server.models.Account;
import corpoints.server.Server.models.Group;
import corpoints.server.Server.models.RequestInGroup;
import corpoints.server.Server.security.AccountDetails;
import corpoints.server.Server.sevices.AccountService;
import corpoints.server.Server.sevices.GroupService;
import corpoints.server.Server.util.GroupValidator;
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
@RequestMapping("/groups")
public class GroupsController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private GroupValidator validator;

    @GetMapping("/getGroups")
    public List<Group> getGroups() {
        return groupService.getGroups();
    }

    @GetMapping("/getGroup")
    public Group getGroup(String name) { return groupService.getGroup(name);}

    @PostMapping("/create")
    public String create(@RequestBody @Valid Group group, BindingResult bindingResult,
                         Authentication authentication) {

        validator.validate(group, bindingResult);

        if (bindingResult.hasErrors())
            return bindingResult.getAllErrors().toString();

        Account sender = ((AccountDetails) authentication.getPrincipal()).getAccount();

        groupService.tryGroup(group, sender);
        //groupService.create(group);

        return "Ok";
    }

    @PatchMapping("/leave")
    public String leave(Authentication authentication) {
        Account sender = ((AccountDetails) authentication.getPrincipal()).getAccount();

        if (sender.getGroup() == null)
            return "Uncorrect";

        sender.setGroup(null);
        accountService.update(sender);

        return "Ok";
    }

    @PostMapping("/test")
    public String test(String name) {
        System.out.println("test");
        System.err.println(name);
        /*System.err.println("Test started");
        groupService.test();
        System.err.println("Test ended");*/
        return "Ok";
    }

    @PatchMapping("/SendScoreToGroup")
    public String SendScore(@RequestParam int score, Authentication authentication) {
        Account sender = ((AccountDetails) authentication.getPrincipal()).getAccount();

        groupService.SendScoreToGroup(score, sender);

        return "Ok";
    }

}
