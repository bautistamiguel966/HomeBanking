package com.ap.homebanking.controllers;

import com.ap.homebanking.dtos.AccountDTO;
import com.ap.homebanking.models.Account;
import com.ap.homebanking.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    AccountRepository accountRepository;

    @RequestMapping("/accounts")
    public List<AccountDTO> getAccounts(){
        //return clientRepository.findAll().stream().map(client -> new ClientDTO(client)).collect(toList());
        return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(toList());
    }

    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable long id){
        return accountRepository.findById(id).map(account -> new AccountDTO(account)).orElse(null);
    }
}
