package Service;

import Model.Account;

public interface AccountService {
    Account register (String username, String password);
    boolean login(String username, String password);

}
