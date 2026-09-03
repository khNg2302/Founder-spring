package founder_spring.auth.service;

import founder_spring.account.entity.Account;
import founder_spring.account.entity.AccountProvider;
import founder_spring.account.entity.AccountStatus;
import founder_spring.account.repository.AccountRepository;
import founder_spring.common.exception.ConflictException;
import founder_spring.common.exception.ResourceNotFoundException;
import founder_spring.common.util.CuidGenerator;
import founder_spring.refresh_token.service.CreatedRefreshToken;
import founder_spring.refresh_token.service.RefreshTokenService;
import founder_spring.user.entity.User;
import founder_spring.user.entity.UserStatus;
import founder_spring.user.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OAuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RefreshTokenService refreshTokenService;
    private final TokenService tokenService;
    private final CuidGenerator cuidGenerator;
    private final GitHubEmailService gitHubEmailService;

    public OAuthService(
            UserRepository userRepository,
            AccountRepository accountRepository,
            RefreshTokenService refreshTokenService,
            TokenService tokenService,
            CuidGenerator cuidGenerator,
            GitHubEmailService gitHubEmailService
    ) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.refreshTokenService = refreshTokenService;
        this.tokenService = tokenService;
        this.cuidGenerator = cuidGenerator;
        this.gitHubEmailService = gitHubEmailService;
    }

    @Transactional
    public OAuthLoginResult loginWithGoogle(
            OAuth2User oauth2User
    ) {
        String email =
                oauth2User.getAttribute("email");

        String name =
                oauth2User.getAttribute("name");

        String avatarUrl =
                oauth2User.getAttribute("picture");

        String providerAccountId =
                oauth2User.getAttribute("sub");

        Boolean emailVerified =
                oauth2User.getAttribute("email_verified");

        if (email == null
                || providerAccountId == null
                || !Boolean.TRUE.equals(emailVerified)) {

            throw new IllegalStateException(
                    "Google account email is not verified"
            );
        }

        Account account =
                accountRepository
                        .findByProviderAndProviderAccountId(
                                AccountProvider.GOOGLE,
                                providerAccountId
                        )
                        .orElse(null);

        User user;

        if (account != null) {

            user = account.getUser();

        } else {

            // 1. Tìm User hiện có theo email
            user = findUserByEmail(email);

            // 2. Không có User → tạo User mới
            if (user == null) {

                user = new User();

                user.setId(cuidGenerator.generate());
                user.setName(name);
                user.setAvatarUrl(avatarUrl);
                user.setStatus(UserStatus.ACTIVE);

                userRepository.save(user);
            }

            // 3. Tạo Google Account cho User
            account = new Account();

            account.setId(cuidGenerator.generate());
            account.setUserId(user.getId());
            account.setProvider(AccountProvider.GOOGLE);
            account.setProviderAccountId(providerAccountId);
            account.setEmail(email);
            account.setStatus(AccountStatus.ACTIVE);

            accountRepository.save(account);
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                    "OAuth account is not active"
            );
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException(
                    "User account is not active"
            );
        }

        CreatedRefreshToken refreshToken =
                refreshTokenService.create(
                        user.getId(),
                        account.getId()
                );

        String accessToken =
                tokenService.createAccessToken(
                        user.getId(),
                        account.getId(),
                        refreshToken.sessionId()
                );

        return new OAuthLoginResult(
                accessToken,
                refreshToken.token()
        );
    }


    @Transactional
    public OAuthLoginResult loginWithGitHub(
            OAuth2User oauth2User,
            String accessToken
    ) {
        Object githubId = oauth2User.getAttribute("id");

        if (githubId == null) {
            throw new IllegalStateException("GitHub user id is missing");
        }

        String providerAccountId = githubId.toString();

        Object nameAttribute = oauth2User.getAttribute("name");
        String name = nameAttribute != null
                ? nameAttribute.toString()
                : null;

        Object avatarAttribute = oauth2User.getAttribute("avatar_url");
        String avatarUrl = avatarAttribute != null
                ? avatarAttribute.toString()
                : null;

        String email =
                gitHubEmailService.getVerifiedPrimaryEmail(accessToken);

        Account account =
                accountRepository
                        .findByProviderAndProviderAccountId(
                                AccountProvider.GITHUB,
                                providerAccountId
                        )
                        .orElse(null);

        User user;

        if (account != null) {

            user = account.getUser();

        } else {


            user = findUserByEmail(email);


            if (user == null) {

                user = new User();

                user.setId(cuidGenerator.generate());
                user.setName(name);
                user.setAvatarUrl(avatarUrl);
                user.setStatus(UserStatus.ACTIVE);

                userRepository.save(user);
            }

            account = new Account();

            account.setId(cuidGenerator.generate());
            account.setUserId(user.getId());
            account.setProvider(AccountProvider.GITHUB);
            account.setProviderAccountId(providerAccountId);
            account.setEmail(email);
            account.setStatus(AccountStatus.ACTIVE);

            accountRepository.save(account);
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                    "OAuth account is not active"
            );
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException(
                    "User account is not active"
            );
        }

        CreatedRefreshToken refreshToken =
                refreshTokenService.create(
                        user.getId(),
                        account.getId()
                );

        String accessTokenFounder =
                tokenService.createAccessToken(
                        user.getId(),
                        account.getId(),
                        refreshToken.sessionId()
                );

        return new OAuthLoginResult(
                accessTokenFounder,
                refreshToken.token()
        );
    }

    private User findUserByEmail(String email) {

        List<Account> accounts =
                accountRepository.findAllByEmailIgnoreCase(email);

        if (accounts.isEmpty()) {
            return null;
        }

        Set<String> userIds = accounts.stream()
                .map(Account::getUserId)
                .collect(Collectors.toSet());

        if (userIds.size() > 1) {
            throw new ConflictException(
                    "Email is linked to multiple users"
            );
        }

        String userId = userIds.iterator().next();

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }
}