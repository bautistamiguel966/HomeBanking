package com.ap.homebanking.controllers;

import com.ap.homebanking.dtos.AccountDTO;
import com.ap.homebanking.dtos.ClientDTO;
import com.ap.homebanking.models.Account;
import com.ap.homebanking.models.Client;
import com.ap.homebanking.repositories.AccountRepository;
import com.ap.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping("/accounts")
    public List<AccountDTO> getAccounts(){
        //return clientRepository.findAll().stream().map(client -> new ClientDTO(client)).collect(toList());
        return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(toList());
    }

    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable long id){
        return accountRepository.findById(id).map(account -> new AccountDTO(account)).orElse(null);
    }

    @RequestMapping(path = "/clients/current/accounts", method = RequestMethod.GET)
    public ResponseEntity<Set<AccountDTO>> getAccounts(Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        return ResponseEntity.ok(client.getAccounts().stream().map(account -> new AccountDTO(account)).collect(Collectors.toSet()));
    }
    @RequestMapping(path = "/clients/current/accounts", method = RequestMethod.POST)
    public ResponseEntity<Object> createAccount(Authentication authentication) {

        Client client = clientRepository.findByEmail(authentication.getName());

        //System.out.println("Nombre del cliente: " + client.getFirstName());
        if(client.getAccounts().size() >= 3){
            return new ResponseEntity<>("You already have 3 accounts", HttpStatus.FORBIDDEN);
        }
        int minDigits = 5;
        int maxDigits = 8;

        Random random = new Random();

        // Generar un número aleatorio entre 10000 y 99999999
        int randomNumber = random.nextInt((int)Math.pow(10, maxDigits) - (int)Math.pow(10, minDigits)) + (int)Math.pow(10, minDigits);
        String accountNumber = "VIN" + randomNumber;
        Account account = new Account(accountNumber, LocalDate.now(), 0);
        //System.out.println("ID: " + account.getId() + " NUMERO: " + account.getNumber());
        client.addAccount(account);
        accountRepository.save(account);
        clientRepository.save(client);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
