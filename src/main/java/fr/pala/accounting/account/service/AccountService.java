package fr.pala.accounting.account.service;

import fr.pala.accounting.account.domain.model.Account;
import fr.pala.accounting.account.domain.model.InvalidFieldException;
import fr.pala.accounting.account.infrastructure.dao.AccountAdapter;
import fr.pala.accounting.account.infrastructure.dao.AccountDAO;
import fr.pala.accounting.account.service.exception.AccountNotCreatedException;
import fr.pala.accounting.account.service.exception.AccountNotFetchedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {
    private final AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Account createAccount(String email) {
        Account newAccount;
        try {
            newAccount = new Account(null, (double) 0, new ArrayList<>());
            return accountDAO.addAccount(email, newAccount);
        } catch (InvalidFieldException e) {
            throw new AccountNotCreatedException();
        }
    }

    public Account getAccount(String email, String account_id) {
        try {
            return accountDAO.getAccountOfUser(email, account_id);
        } catch (InvalidFieldException e) {
            throw new AccountNotFetchedException();
        }
    }

    public List<Account> getAccounts(String email) {
        return accountDAO.getAllAccountsOfUserByEmail(email);
        try {
            return AccountAdapter.modelListToAccountList(accountModels);
        } catch (InvalidFieldException e) {
            throw new AccountNotCreatedException();
        }
    }

    public void deleteAccount(String email, String accountId) {
        accountDAO.deleteAccount(email, accountId);
    }

    public Account getAccount(String email, String accountId) {
        try {
            return AccountAdapter.modelToAccount(accountDAO.getAccountOfUser(email, accountId));
        } catch (InvalidFieldException e) {
            throw new AccountNotFetchedException();
        }
    }

    public Double getAccountAmount(String email, String accountId) {
        try {
            Account account = AccountAdapter.modelToAccount(accountDAO.getAccountOfUser(email, accountId));
            return account.getAmount();
        } catch (InvalidFieldException e) {
            throw new AccountNotFetchedException();
        }
    }
}
