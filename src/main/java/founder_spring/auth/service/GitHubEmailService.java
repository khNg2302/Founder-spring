package founder_spring.auth.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class GitHubEmailService {

    private final RestClient restClient;

    public GitHubEmailService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.github.com")
                .build();
    }

    public String getVerifiedPrimaryEmail(String accessToken) {

        List<GitHubEmailResponse> emails =
                restClient.get()
                        .uri("/user/emails")
                        .headers(headers ->
                                headers.setBearerAuth(accessToken)
                        )
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

        if (emails == null) {
            throw new IllegalStateException(
                    "Unable to retrieve GitHub emails"
            );
        }

        return emails.stream()
                .filter(GitHubEmailResponse::verified)
                .filter(GitHubEmailResponse::primary)
                .map(GitHubEmailResponse::email)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "GitHub account has no verified primary email"
                        )
                );
    }

    public record GitHubEmailResponse(
            String email,
            boolean primary,
            boolean verified,
            String visibility
    ) {}
}