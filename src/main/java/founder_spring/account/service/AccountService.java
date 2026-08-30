package founder_spring.account.service;

import founder_spring.account.entity.Account;
import founder_spring.account.entity.AccountProvider;
import founder_spring.account.exception.AccountNotFoundException;
import founder_spring.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public Account findById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Account findByProviderAndProviderAccountId(
            AccountProvider provider,
            String providerAccountId
    ) {
        return accountRepository
                .findByProviderAndProviderAccountId(
                        provider,
                        providerAccountId
                )
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Account findByProviderAndEmail(
            AccountProvider provider,
            String email
    ) {
        return accountRepository
                .findByProviderAndEmail(provider, email)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Account findByEmail(String email) {
        return accountRepository
                .findByEmail(email)
                .orElse(null);
    }
}