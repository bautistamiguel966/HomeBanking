package com.ap.homebanking.controllers;

import com.ap.homebanking.dtos.AccountDTO;
import com.ap.homebanking.models.Account;
import com.ap.homebanking.models.AccountType;
import com.ap.homebanking.models.Client;
import com.ap.homebanking.models.Transaction;
import com.ap.homebanking.services.AccountService;
import com.ap.homebanking.services.ClientService;
import com.ap.homebanking.services.TransactionService;
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


@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private TransactionService transactionService;

//    @RequestMapping(path = "/accounts", method = RequestMethod.GET)
    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts(){
        return accountService.getAccountsDTO();
    }

//    @RequestMapping(path = "/accounts/admin/{id}", method = RequestMethod.GET)
    @GetMapping("/accounts/admin/{id}")
    public AccountDTO getAccount(@PathVariable long id){
        return accountService.getAccountDTO(id);
    }
//    @RequestMapping("/accounts/{id}")
    @GetMapping("/accounts/{id}")
    public ResponseEntity<Object> getAccount(Authentication authentication, @PathVariable long id){
        Client client = clientService.findByEmail(authentication.getName());
        //Valido que el id de la cuenta pertenezca al cliente autenticado.
        boolean flag = false;
        for(Account account : client.getAccounts()){
            if(account.getId() == id){
                flag = true;
            }
        }
        if(flag == true){
            return new ResponseEntity<>(accountService.getAccountDTO(id), HttpStatus.ACCEPTED);
        }else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

//    @RequestMapping(path = "/clients/current/accounts", method = RequestMethod.GET)
    @GetMapping("/clients/current/accounts")
    public ResponseEntity<Set<AccountDTO>> getAccounts(Authentication authentication){
        Client client = clientService.findByEmail(authentication.getName());
        return ResponseEntity.ok(client.getAccounts().stream().map(account -> new AccountDTO(account)).collect(Collectors.toSet()));
    }
//    @RequestMapping(path = "/clients/current/accounts", method = RequestMethod.POST)
    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication, @RequestParam AccountType accountType) {

        Client client = clientService.findByEmail(authentication.getName());
        boolean flag = false;
        for(AccountType type : AccountType.values()){
            if(type.name().equals(accountType.name())){
                flag = true;
            }
        }
        if(!flag){
            return new ResponseEntity<>("The account type doesn´t exist", HttpStatus.FORBIDDEN);
        }

        if(client.getAccounts().size() >= 3){
            return new ResponseEntity<>("You already have 3 accounts", HttpStatus.FORBIDDEN);
        }
        String accountNumber;
        do {
            int minDigits = 5;
            int maxDigits = 8;

            Random random = new Random();

            // Generar un número aleatorio entre 10000 y 99999999
            int randomNumber = random.nextInt((int)Math.pow(10, maxDigits) - (int)Math.pow(10, minDigits)) + (int)Math.pow(10, minDigits);
            accountNumber = "VIN" + randomNumber;
        }while (accountService.findByNumber(accountNumber) != null);

        Account account = new Account(accountNumber, LocalDate.now(), 0, accountType);
        client.addAccount(account);
        accountService.save(account);
        clientService.save(client);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/clients/current/accounts/{id}")
    public ResponseEntity<Object> deleteAccount(Authentication authentication, @PathVariable long id){
        Client client = clientService.findByEmail(authentication.getName());
        Account account = accountService.findById(id);

        if(client.getAccounts().contains(account)){
            if(account.getTransactions() != null){
                for(Transaction transaction : account.getTransactions()){
                    transactionService.delete(transaction.getId());
                }
                accountService.delete(id);
            }
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>("The account doesn´t belong to this client ",HttpStatus.FORBIDDEN);
    }
}
