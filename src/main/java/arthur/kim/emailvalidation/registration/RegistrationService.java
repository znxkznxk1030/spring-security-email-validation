package arthur.kim.emailvalidation.registration;

import arthur.kim.emailvalidation.user.AppUser;
import arthur.kim.emailvalidation.user.AppUserRole;
import arthur.kim.emailvalidation.user.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());

        if (!isValidEmail) {
            throw new IllegalStateException("email not valid");
        }

        appUserService.signupUser(new AppUser(
            request.getFirstname(),
                request.getLastname(),
                request.getEmail(),
                request.getPassword(),
                AppUserRole.USER,
                true,
                false
        ));

        return request.toString();
    }
}
