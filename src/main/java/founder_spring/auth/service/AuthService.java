package founder_spring.auth.service;

import founder_spring.account.entity.Account;
import founder_spring.account.entity.AccountProvider;
import founder_spring.account.entity.AccountStatus;
import founder_spring.account.repository.AccountRepository;
import founder_spring.auth.dto.*;
import founder_spring.common.exception.BadRequestException;
import founder_spring.common.exception.ConflictException;
import founder_spring.common.exception.InvalidCredentialsException;
import founder_spring.common.exception.ResourceNotFoundException;
import founder_spring.common.util.CuidGenerator;
import founder_spring.email_verification_token.service.EmailVerificationTokenService;
import founder_spring.refresh_token.entity.RefreshToken;
import founder_spring.refresh_token.service.CreatedRefreshToken;
import founder_spring.refresh_token.service.RefreshTokenService;
import founder_spring.user.entity.User;
import founder_spring.user.entity.UserStatus;
import founder_spring.user.repository.UserRepository;
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

    private final RefreshTokenService refreshTokenService;
    private final TokenService tokenService;

    public AuthService(
            UserRepository userRepository,
            AccountRepository accountRepository,
            EmailVerificationTokenService emailVerificationTokenService,
            CuidGenerator cuidGenerator,
            RefreshTokenService refreshTokenService,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.emailVerificationTokenService =
                emailVerificationTokenService;
        this.cuidGenerator = cuidGenerator;
        this.refreshTokenService = refreshTokenService;
        this.tokenService = tokenService;
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
    @Transactional
    public LoginResponse login(LoginRequest request) {

        Account account = accountRepository
                .findByProviderAndEmail(
                        AccountProvider.LOCAL,
                        request.email()
                )
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        User user = userRepository.findById(account.getUserId())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        if (account.getPasswordHash() == null) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        boolean validPassword =
                passwordEncoder.matches(
                        request.password(),
                        account.getPasswordHash()
                );

        if (!validPassword) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        CreatedRefreshToken createdRefreshToken =
                refreshTokenService.create(
                        account.getUserId(),
                        account.getId()
                );

        String accessToken =
                tokenService.createAccessToken(
                        account.getUserId(),
                        account.getId(),
                        createdRefreshToken.sessionId()
                );

        return new LoginResponse(
                accessToken,
                createdRefreshToken.token()
        );
    }

    @Transactional
    public RefreshTokenResponse refresh(String rawRefreshToken) {

        RefreshToken oldToken =
                refreshTokenService.findValidToken(rawRefreshToken);

        if (oldToken == null) {
            throw new InvalidCredentialsException(
                    "Invalid refresh token"
            );
        }

        User user = userRepository.findById(oldToken.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException(
                    "User account is not active"
            );
        }

        refreshTokenService.revoke(oldToken);

        CreatedRefreshToken newRefreshToken =
                refreshTokenService.create(
                        oldToken.getUserId(),
                        oldToken.getAccountId()
                );

        String accessToken =
                tokenService.createAccessToken(
                        oldToken.getUserId(),
                        oldToken.getAccountId(),
                        newRefreshToken.sessionId()
                );

        return new RefreshTokenResponse(
                accessToken,
                newRefreshToken.token()
        );
    }

    @Transactional
    public void logout(String rawRefreshToken) {

        RefreshToken refreshToken =
                refreshTokenService.findValidToken(rawRefreshToken);

        if (refreshToken == null) {
            return;
        }

        refreshTokenService.revoke(refreshToken);
    }
}