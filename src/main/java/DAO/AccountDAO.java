package DAO;

import static org.mockito.ArgumentMatchers.nullable;

import java.sql.Connection;

// import static org.mockito.ArgumentMatchers.nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import Model.Account;
import Util.ConnectionUtil;

public class AccountDAO {

    private static AccountDAO accountDAO = null;
    private List<Account> accounts = new ArrayList<Account>();


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

    public Account registerUser(String username, String password) throws SQLException
    {
        String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        int acct_id = -1;
        Account a = null;
        try {
            conn = ConnectionUtil.getConnection();
            statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.execute();

            rs = statement.getGeneratedKeys();
            while ( rs.next() ) {
                acct_id = rs.getInt(1);
                System.out.println("Account created with id  = " + acct_id);
            }

            conn.commit();
            
            a = getAccountById(acct_id);

       } catch (SQLException e) {
            throw e;
       }
       finally {
        if (rs != null) {rs.close();}
        if (statement != null) { statement.close(); }
       }
       return a;
    }

    public Account getAccountByUsername(String username) throws SQLException
    {
        Account accountToGet = null;
        String sql = "SELECT account_id, username, password FROM account WHERE username = ?";
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;

        try {
            System.out.println("Getting account with username = " + username);
            conn = ConnectionUtil.getConnection();

            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, username);

            rs = pStmt.executeQuery();
            
            while (rs.next())
            {
                accountToGet = new Account();
                accountToGet.setAccount_id(rs.getInt("account_id"));
                accountToGet.setUsername(rs.getString("username"));
                accountToGet.setPassword(rs.getString("password"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            if (rs != null) rs.close();
            if (pStmt != null) pStmt.close();
        }
        if (accountToGet != null) {
            System.out.println("Got account = " + accountToGet);
        } else {
            System.out.println("No account with username = " + username);
        }
        return accountToGet;
    }


    public boolean loginUser(String username, String password) throws SQLException
    {
        //this.accountDAO = accountDAO;
        ResultSet resultSet = null;
        PreparedStatement pStmt = null;
        ResultSet rsKeys = null;
        int acct_id = -1;
        boolean retVal = false;
        try {
            String sql = "SELECT * from account WHERE username = ? AND password = ?";
            Connection conn = ConnectionUtil.getConnection();
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, username);
            pStmt.setString(2, password);
            resultSet = pStmt.executeQuery();

            if(resultSet.next())
            {
                System.out.println("Welcome. ");
                retVal = true;
            }
            else {
                System.out.println("Invalid username and password");
                retVal = false;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (resultSet != null)
            {
                resultSet.close();
            }
            if (pStmt != null)
            {
                pStmt.close();
            }
        }
        return retVal;
    }

    public Account getAccountById(int id) throws SQLException
    {
        Account accountToGet = null;
        String sql = "SELECT account_id, username, password FROM account WHERE account_id = ?";
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;

        try {
            System.out.println("Getting account with ID = " + id);
            conn = ConnectionUtil.getConnection();

            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1, id);

            rs = pStmt.executeQuery();
            
            while (rs.next())
            {
                accountToGet = new Account();
                accountToGet.setAccount_id(rs.getInt("account_id"));
                accountToGet.setUsername(rs.getString("username"));
                accountToGet.setPassword(rs.getString("password"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            if (rs != null) rs.close();
            if (pStmt != null) pStmt.close();
        }
        System.out.println("Got account = " + accountToGet);
        return accountToGet;   
    }
    
}
