package founder_spring.security;

import founder_spring.authorization.service.AuthorizationService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Component
public class JwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private final AuthorizationService authorizationService;

    public JwtAuthenticationConverter(
            AuthorizationService authorizationService
    ) {
        this.authorizationService = authorizationService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        String userId = jwt.getSubject();

        Set<String> permissions =
                authorizationService.getPermissions(userId);

        Collection<GrantedAuthority> authorities =
                permissions.stream()
                        .map(permission ->
                                (GrantedAuthority) new SimpleGrantedAuthority(permission))
                        .toList();

        return new JwtAuthenticationToken(
                jwt,
                authorities,
                userId
        );
    }
}