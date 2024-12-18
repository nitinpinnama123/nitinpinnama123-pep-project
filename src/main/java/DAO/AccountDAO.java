package DAO;

import static org.mockito.ArgumentMatchers.nullable;

import Model.Account;

public class AccountDAO {
    private static AccountDAO accountDAO = null;
    public AccountDAO()
    {
        
    }

    public static AccountDAO instance() {
        if (accountDAO == null)
        {
            accountDAO = new AccountDAO();
        }
        return accountDAO;
    }

    public Account registerUser(String username, String password)
    {
        Account a = new Account(username, password);
        return a;
    }

    public void loginUser(AccountDAO accountDAO)
    {
        this.accountDAO = accountDAO;
    }
    
}
