package jittr.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@Controller
class HomeController {

    @ModelAttribute("module")
    String module() {
        return "home";
    }

    @GetMapping("/")
    String index(Principal principal) {
        return principal != null ? "home/homeSignedIn" : "home/homeNotSignedIn";
    }
    
    @GetMapping("/login")
    String login(Principal principal) {
        return "home/homeNotSignedIn";
    }
    
    @GetMapping("/logout")
    String logout(Principal principal) {
        return "home/homeNotSignedIn";
    }
}
