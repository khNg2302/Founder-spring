package founder_spring.account.repository;

import founder_spring.account.entity.Account;
import founder_spring.account.entity.AccountProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository
        extends JpaRepository<Account, String> {

    Optional<Account> findByProviderAndProviderAccountId(
            AccountProvider provider,
            String providerAccountId
    );

    Optional<Account> findByProviderAndEmail(
            AccountProvider provider,
            String email
    );

    Optional<Account> findByEmail(String email);
}