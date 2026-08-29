package founder_spring.common.util;

import io.github.thibaultmeyer.cuid.CUID;
import org.springframework.stereotype.Component;

@Component
public class CuidGenerator {

    public String generate() {
        return CUID.randomCUID2().toString();
    }
}