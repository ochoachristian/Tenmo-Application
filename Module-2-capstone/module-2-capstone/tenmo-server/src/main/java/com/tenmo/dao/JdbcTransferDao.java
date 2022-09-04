package com.tenmo.dao;

import com.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    @Autowired
    private final JdbcTemplate jdbcTemplate;
    private final AccountDao accountDao;
    public JdbcTransferDao(AccountDao accountDao, DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.accountDao = accountDao;
    }

    @Override
    public List<Transfer> getTransferList(int id) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount "
                + "FROM transfer "
                + "WHERE account_from = ? OR account_to = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,id,id);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results); //missing line
            transfers.add(transfer);
        }
        return transfers;
    }

    @Override
    public Transfer getTransferById(int transferId) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount "
                + "FROM transfer WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,transferId);
        if (results.next()) {
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    @Override
    public boolean createTransfer(Transfer transfer) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) "
        + "VALUES (?, ?, ?, ?, ?);"
        + "UPDATE account SET balance = balance + ?"
        + "WHERE account_id = ?;"
        + "UPDATE account SET balance = balance - ?"
        + "WHERE account_id = ?;";

        return jdbcTemplate.update(sql,transfer.getTransferTypeId(),transfer.getTransferStatusId(),
                transfer.getAccountFrom(),transfer.getAccountTo(),transfer.getAmount(),transfer.getAmount(),
                transfer.getAccountTo(),transfer.getAmount(),transfer.getAccountFrom()) == 3;

    }

    private Transfer mapRowToTransfer(SqlRowSet rowSet) {
        Transfer transfer = new Transfer();
        transfer.setTransferID(rowSet.getInt("transfer_id"));
        transfer.setTransferTypeId(rowSet.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rowSet.getInt("transfer_status_id"));
        transfer.setAccountFrom(rowSet.getInt("account_from"));
        transfer.setAccountTo(rowSet.getInt("account_to"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));
        return transfer;
    }
}
