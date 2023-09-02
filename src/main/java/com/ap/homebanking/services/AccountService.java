package com.ap.homebanking.services;

import com.ap.homebanking.dtos.AccountDTO;
import com.ap.homebanking.models.Account;

import java.util.List;

public interface AccountService {
    List<AccountDTO> getAccountsDTO();

    void save(Account account);

    Account findById(long id);

    AccountDTO getAccountDTO(long id);

    Account findByNumber(String accountNumber);
}
