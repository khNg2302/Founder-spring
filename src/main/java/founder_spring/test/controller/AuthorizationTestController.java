package founder_spring.test.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class AuthorizationTestController {

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('user:read')")
    public String userRead() {
        return "You have user:read permission";
    }

    @GetMapping("/project")
    @PreAuthorize("hasAuthority('project:read')")
    public String projectRead() {
        return "You have project:read permission";
    }
}