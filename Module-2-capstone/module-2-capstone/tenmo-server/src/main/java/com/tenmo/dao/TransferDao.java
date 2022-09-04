package com.tenmo.dao;

import com.tenmo.model.Transfer;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

public interface TransferDao {
    public List<Transfer> getTransferList(int id);
    public Transfer getTransferById(int transactionId);
    public boolean createTransfer(@Valid @RequestBody Transfer transfer); //might need this


}
