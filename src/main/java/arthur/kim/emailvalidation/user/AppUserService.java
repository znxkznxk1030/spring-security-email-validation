package arthur.kim.emailvalidation.user;

import arthur.kim.emailvalidation.registration.token.ConfirmationToken;
import arthur.kim.emailvalidation.registration.token.ConfirmationTokenService;
import ch.qos.logback.core.util.TimeUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND = "user with email %s not found";
    public static final String EMAIL_ALREADY_TAKEN = "email already taken";
    public static final String USER_NOT_EXIST = "user not exist";

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND, email)));
    }

    public String signupUser(AppUser appUser) {
        boolean userExists = appUserRepository.findByEmail(appUser.getEmail()).isPresent();

        if (userExists) {
            throw new IllegalStateException(EMAIL_ALREADY_TAKEN);
        }

        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());

        appUser.setPassword(encodedPassword);

        appUserRepository.save(appUser);

        String token = UUID.randomUUID().toString();
        System.out.println(token);

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        // TODO: Send Email
        return token;
    }

    public void enableAppUser(String email) {
        AppUser appUser = appUserRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(USER_NOT_EXIST));

        appUser.setEnabled(true);
        appUser.setLocked(false);

        appUserRepository.save(appUser);
    }
}
