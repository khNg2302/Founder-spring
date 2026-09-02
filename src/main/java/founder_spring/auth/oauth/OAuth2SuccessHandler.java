package founder_spring.auth.oauth;

import founder_spring.auth.service.OAuthLoginResult;
import founder_spring.auth.service.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler
        implements AuthenticationSuccessHandler {

    private final OAuthService oauthService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public OAuth2SuccessHandler(OAuthService oauthService,OAuth2AuthorizedClientService authorizedClientService) {
        this.oauthService = oauthService;
        this.authorizedClientService =
                authorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2AuthenticationToken oauthToken =
                (OAuth2AuthenticationToken) authentication;

        String provider =
                oauthToken.getAuthorizedClientRegistrationId();

        OAuth2User oauth2User =
                (OAuth2User) authentication.getPrincipal();

        OAuthLoginResult result;

        String registrationId =
                oauthToken.getAuthorizedClientRegistrationId();

        String principalName =
                authentication.getName();

        OAuth2AuthorizedClient client =
                authorizedClientService.loadAuthorizedClient(
                        registrationId,
                        principalName
                );

        if (client == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "OAuth client not found"
            );
            return;
        }

        String githubAccessToken =
                client.getAccessToken().getTokenValue();

        if ("google".equals(provider)) {
            result = oauthService.loginWithGoogle(oauth2User);

        } else if ("github".equals(provider)) {

            result = oauthService.loginWithGitHub(
                    oauth2User,
                    githubAccessToken
            );
        } else {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Unsupported OAuth provider"
            );
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
            {
                "accessToken": "%s",
                "refreshToken": "%s"
            }
            """.formatted(
                result.accessToken(),
                result.refreshToken()
        ));
    }
}