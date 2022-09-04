package com.tenmo.dao;

import com.tenmo.model.Account;
import com.tenmo.model.AccountNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;
    public JdbcAccountDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Account findByUserId(int id) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            account = mapRowToAccount(results);
        } else {
            throw new AccountNotFoundException();
        }
        return account;
    }

    @Override
    public Account getByAccountId(int accountId) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        if (results.next()) {
            account = mapRowToAccount(results);
        } else {
            throw new AccountNotFoundException();
        }
        return account;
    }

    @Override
    public String getUsername(int id) {
        String sql = "SELECT username FROM tenmo_user JOIN account USING(user_id)" +
                "WHERE account.account_id = ?;";
       return jdbcTemplate.queryForObject(sql,String.class,id);
    }

    @Override
    public BigDecimal getBalance(String username) {
        Account account = new Account();
        String sql = "SELECT balance FROM account JOIN tenmo_user USING(user_id) WHERE username = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account.getBalance();
    }

    @Override
    public BigDecimal getBalance(int userId) { //added this method for if condition in sendTransfer
       // Account account = new Account(); <--why do we need this?
        String sql = "SELECT balance FROM account WHERE user_id = ?;";

        return jdbcTemplate.queryForObject(sql,BigDecimal.class, userId);
    }

    public int getAccountId(int id) {
        String sql = "SELECT account_id FROM account WHERE user_id = ?;";
        return jdbcTemplate.queryForObject(sql,int.class,id);
    }

    @Override
    public Account updateAccountBalance(int id, Account account) {
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";
        jdbcTemplate.update(sql, account.getBalance(), id);
        return account;
    }

    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getInt("account_id"));
        account.setUserId(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));
        return account;
    }
}
