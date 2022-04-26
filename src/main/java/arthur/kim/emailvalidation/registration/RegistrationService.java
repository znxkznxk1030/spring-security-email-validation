package arthur.kim.emailvalidation.registration;

import arthur.kim.emailvalidation.user.AppUser;
import arthur.kim.emailvalidation.user.AppUserRole;
import arthur.kim.emailvalidation.user.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    public static final String EMAIL_NOT_VALID = "email not valid";

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());

        if (!isValidEmail) {
            throw new IllegalStateException(EMAIL_NOT_VALID);
        }

        appUserService.signupUser(new AppUser(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPassword(),
            AppUserRole.USER,
            true,
            false
        ));

        return request.toString();
    }
}
