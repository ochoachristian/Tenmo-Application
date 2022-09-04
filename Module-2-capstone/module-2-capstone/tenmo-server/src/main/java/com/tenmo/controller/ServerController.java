package com.tenmo.controller;

import com.tenmo.dao.AccountDao;
import com.tenmo.dao.TransferDao;
import com.tenmo.dao.UserDao;
import com.tenmo.model.Account;
import com.tenmo.model.Transfer;
import com.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class ServerController {
    private AccountDao accountDao;
    private TransferDao transferDao;
    private UserDao userDao;

    public ServerController(AccountDao accountDao, TransferDao transferDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

     //Account
    @GetMapping(path= "/account/balance/{id}")
    public BigDecimal getBalance(@PathVariable int id) {
        return accountDao.getBalance(id);
    }

    @GetMapping(path= "/username/{id}")
    public String getUsername(@PathVariable int id) { return accountDao.getUsername(id);}

    @GetMapping(path= "/user/{id}")
    public Account getByUserId(@PathVariable int id) {
        return accountDao.findByUserId(id);
    }

    @GetMapping(path= "/account/{id}")
    public Account getByAccountId(@PathVariable int id) {
        return accountDao.getByAccountId(id);
    }

    //User
    @GetMapping(path="/user")
    public List<User> getAllUsers(){
        return userDao.findAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path="/transfer/{userFrom}/{userTo}")
    public boolean createTransfer(@RequestBody Transfer transfer, @PathVariable int userFrom, @PathVariable int userTo) throws Exception {
        return transferDao.createTransfer(transfer);
    }

    @GetMapping(path="/transfer/history/{id}")
    public List<Transfer> getAllTransfers(@PathVariable int id){
        return transferDao.getTransferList(id);
    }

    @GetMapping(path="/transfer/history/user/{id}")
    public Transfer getTransferById(@PathVariable int id) {
        return transferDao.getTransferById(id);
    }

    @PutMapping(path="/account/{id}")
    public Account updateAccount(@PathVariable int id, Account account) {
        return accountDao.updateAccountBalance(id, account);
    }


}
