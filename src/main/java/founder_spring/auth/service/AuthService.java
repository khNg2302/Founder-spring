package founder_spring.auth.service;

import founder_spring.account.entity.Account;
import founder_spring.account.entity.AccountProvider;
import founder_spring.account.entity.AccountStatus;
import founder_spring.account.repository.AccountRepository;
import founder_spring.common.exception.ConflictException;
import founder_spring.common.util.CuidGenerator;
import founder_spring.email_verification_token.service.EmailVerificationTokenService;
import founder_spring.user.entity.User;
import founder_spring.user.entity.UserStatus;
import founder_spring.user.repository.UserRepository;
import founder_spring.auth.dto.RegisterRequest;
import founder_spring.auth.dto.RegisterResponse;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final CuidGenerator cuidGenerator;

    private final Argon2PasswordEncoder passwordEncoder =
            Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    public AuthService(
            UserRepository userRepository,
            AccountRepository accountRepository,
            EmailVerificationTokenService emailVerificationTokenService,
            CuidGenerator cuidGenerator
    ) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.emailVerificationTokenService =
                emailVerificationTokenService;
        this.cuidGenerator = cuidGenerator;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {


        boolean exists = accountRepository
                .findByProviderAndEmail(
                        AccountProvider.LOCAL,
                        request.email()
                )
                .isPresent();

        if (exists) {
            throw new ConflictException(

                    "Email already exists"
            );
        }


        String passwordHash =
                passwordEncoder.encode(request.password());


        User user = new User();

        user.setId(
                cuidGenerator.generate()
        );

        user.setName(request.name());
        user.setStatus(UserStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        user = userRepository.save(user);


        Account account = new Account();

        account.setId(
                cuidGenerator.generate()
        );

        account.setUserId(user.getId());
        account.setProvider(AccountProvider.LOCAL);
        account.setEmail(request.email());
        account.setPasswordHash(passwordHash);
        account.setStatus(AccountStatus.ACTIVE);

        account = accountRepository.save(account);


        EmailVerificationTokenService.CreatedVerificationToken verificationToken =
                emailVerificationTokenService.create(
                        account.getId()
                );


        return new RegisterResponse(
                user.getId(),
                account.getId(),
                verificationToken.token(),
                verificationToken.expiresAt()
        );
    }
}