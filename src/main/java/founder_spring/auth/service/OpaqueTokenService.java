package founder_spring.auth.service;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HexFormat;

@Service
public class OpaqueTokenService {

    private final SecureRandom secureRandom = new SecureRandom();

    private final Argon2PasswordEncoder encoder =
            Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    public String generateSecret() {

        byte[] bytes = new byte[64];

        secureRandom.nextBytes(bytes);

        return HexFormat.of().formatHex(bytes);
    }

    public String hash(String secret) {

        return encoder.encode(secret);
    }

    public boolean verify(
            String hash,
            String secret
    ) {

        return encoder.matches(secret, hash);
    }

    public ParsedToken parse(String token) {

        String[] parts = token.split("\\.", -1);

        if (parts.length != 2) {
            return null;
        }

        if (parts[0].isBlank() || parts[1].isBlank()) {
            return null;
        }

        return new ParsedToken(
                parts[0],
                parts[1]
        );
    }

    public String build(
            String tokenId,
            String secret
    ) {

        return tokenId + "." + secret;
    }

    public record ParsedToken(
            String tokenId,
            String secret
    ) {
    }
}