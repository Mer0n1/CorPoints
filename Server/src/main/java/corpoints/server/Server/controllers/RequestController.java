package corpoints.server.Server.controllers;

import corpoints.server.Server.models.Account;
import corpoints.server.Server.models.Group;
import corpoints.server.Server.models.RequestInGroup;
import corpoints.server.Server.security.AccountDetails;
import corpoints.server.Server.sevices.RequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {
    @Autowired
    private RequestService requestService;

    @GetMapping("/getRequests")
    public List<RequestInGroup> getRequests() {
        return requestService.getRequests();
    }

    @GetMapping("/getRequestsOfGroup")
    public List<RequestInGroup> getRequestsFromGroup(String NameGroup) {
        return requestService.getRequestsFromGroup(NameGroup);
    }

    @GetMapping("/getRequest")
    public RequestInGroup getRequest(int id) { return requestService.getRequest(id);}

    @PostMapping("/join")
    public String join(@RequestBody @Valid RequestInGroup request,
                       BindingResult bindingResult, Authentication authentication) {

        if (bindingResult.hasErrors())
            return bindingResult.getAllErrors().toString();

        Account sender = ((AccountDetails) authentication.getPrincipal()).getAccount();
        requestService.tryRequest(sender, request);
        requestService.createRequest(request);

        return "Ok";
    }

    @PostMapping("/ResultRequest")
    public String ResultRequest(@RequestBody @Valid RequestInGroup request,
                                BindingResult bindingResult,
                                Authentication authentication) {

        if (bindingResult.hasErrors())
            return bindingResult.getAllErrors().toString();

        Account sender = ((AccountDetails) authentication.getPrincipal()).getAccount();
        requestService.ProcessRequest(request, sender);

        return "Ok";
    }
}
