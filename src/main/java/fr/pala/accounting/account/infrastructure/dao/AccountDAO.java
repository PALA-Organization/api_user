package fr.pala.accounting.account.infrastructure.dao;

import fr.pala.accounting.account.domain.model.Account;
import fr.pala.accounting.account.domain.model.InvalidFieldException;
import fr.pala.accounting.user.infrastructure.dao.UserDAO;
import fr.pala.accounting.user.domain.model.UserModel;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountDAO {

    private final MongoTemplate mongoTemplate;

    private final UserDAO userDAO;

    public AccountDAO(MongoTemplate mongoTemplate, UserDAO userDAO) {
        this.mongoTemplate = mongoTemplate;
        this.userDAO = userDAO;
    }

    public Account addAccount(String email, Account account) throws InvalidFieldException {
        UserModel user = userDAO.getUserByEmail(email);
        // TODO : Use User to create UserDetails, and get ID directly instead of findUserByEmail

        account.setId(new ObjectId().toString());
        //to set an autogenerated Id to the account
        List<Account> accounts = AccountAdapter.modelListToAccountList(user.getAccounts());
        accounts.add(account);
        List<AccountModel> accountModels = AccountAdapter.transactionListToModelList(accounts);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(user.getUser_id()));
        Update update = new Update();
        update.set("accounts", accountModels);
        mongoTemplate.findAndModify(query, update, UserModel.class);

        return account;
    }

    public List<Account> getAllAccountsOfUsersByUser_id(String user_id) throws InvalidFieldException {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(user_id));

        UserModel user = mongoTemplate.findOne(query, UserModel.class);

        if(user != null){
            return AccountAdapter.modelListToAccountList(user.getAccounts());
        }
        return null;
    }

    public List<Account> getAllAccountsOfUserByEmail(String email) throws InvalidFieldException {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));

        UserModel user = mongoTemplate.findOne(query, UserModel.class);

        if(user != null){
            return AccountAdapter.modelListToAccountList(user.getAccounts());
        }
        return null;
    }

    public Account getAccountOfUser(String email, String account_id) throws InvalidFieldException {
        List<Account> accounts = getAllAccountsOfUserByEmail(email);

        Account accountResult = null;
        for (Account account : accounts) {
            if (account.getId().equals(account_id)) {
                accountResult = account;
                break;
            }
        }
        return accountResult;
    }



    public void updateAccount(String email, String account_id, AccountModel account) {
        UserModel user = userDAO.getUserByEmail(email);
        List<AccountModel> accounts = user.getAccounts();

        for (AccountModel accountModel : accounts) {
            if (accountModel.getId().equals(account_id)) {
                accountModel.setAmount(account.getAmount());
                accountModel.setTransactions_ids(account.getTransactions_ids());
                break;
            }
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(user.getUser_id()));
        Update update = new Update();
        update.set("accounts", accounts);
        mongoTemplate.findAndModify(query, update, UserModel.class);
    }

    public void deleteAccount(String email, String account_id) {

        UserModel user = userDAO.getUserByEmail(email);
        List<AccountModel> accounts = user.getAccounts();

        for (int i = 0; i < accounts.size(); i++) {
            if(accounts.get(i).getId().equals(account_id)){
                accounts.remove(i);
                break;
            }
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(user.getUser_id()));
        Update update = new Update();
        update.set("accounts", accounts);
        mongoTemplate.findAndModify(query, update, UserModel.class);
    }
}
