package com.tenmo.dao;

import com.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {
    Account findByUserId(int userId);
    Account getByAccountId(int accountId);
    BigDecimal getBalance(String username);
    BigDecimal getBalance(int userId); //added this for JdbcTransferDao sendTransfer method
    Account updateAccountBalance(int id, Account account);
    String getUsername(int id);

    int getAccountId(int userId);
}
