package arthur.kim.emailvalidation.registration;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/")
    public String register(@RequestBody RegistrationRequest request) {
        registrationService.register(request);
        return "work";
    }

    @GetMapping(path = "confirm")
    public String validate(@RequestParam String token) {
        return registrationService.confirmToken(token);
    }
}
