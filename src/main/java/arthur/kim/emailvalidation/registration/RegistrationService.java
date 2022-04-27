package arthur.kim.emailvalidation.registration;

import arthur.kim.emailvalidation.email.EmailSender;
import arthur.kim.emailvalidation.registration.token.ConfirmationToken;
import arthur.kim.emailvalidation.registration.token.ConfirmationTokenService;
import arthur.kim.emailvalidation.user.AppUser;
import arthur.kim.emailvalidation.user.AppUserRole;
import arthur.kim.emailvalidation.user.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    public static final String EMAIL_NOT_VALID = "email not valid";
    public static final String TOKEN_NOT_FOUND = "token not found";
    public static final String ALREADY_CONFIRMED = "email already confirmed";
    public static final String TOKEN_EXPIRED = "token was expired";

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());

        if (!isValidEmail) {
            throw new IllegalStateException(EMAIL_NOT_VALID);
        }

        String token = appUserService.signupUser(new AppUser(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPassword(),
            AppUserRole.USER,
            true,
            false
        ));

        emailSender.send(request.getEmail(), buildEmail(token));

        return token;
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException(TOKEN_NOT_FOUND));
        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException(ALREADY_CONFIRMED);
        }

        LocalDateTime expiredAt = confirmationToken.getExpiredAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(TOKEN_EXPIRED);
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenService.setConfirmedAt(confirmationToken);

        appUserService.enableAppUser(confirmationToken.getAppUser().getEmail());
        return "confirmed";
    }

    private String buildEmail(String token) {
        return "<html><body><div><a href=\"http://localhost:8080/api/v1/registration/confirm?token=" + token + "\">click to confirm</a></div></body></html>";
    }
}
