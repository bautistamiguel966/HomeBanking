package com.ap.homebanking.controllers;

import com.ap.homebanking.dtos.AccountDTO;
import com.ap.homebanking.models.Account;
import com.ap.homebanking.models.Client;
import com.ap.homebanking.services.AccountService;
import com.ap.homebanking.services.ClientService;
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

    @RequestMapping(path = "/accounts", method = RequestMethod.GET)
    public List<AccountDTO> getAccounts(){
        return accountService.getAccountsDTO();
    }

    @RequestMapping(path = "/accounts/admin/{id}", method = RequestMethod.GET)
    public AccountDTO getAccount(@PathVariable long id){
        return accountService.getAccountDTO(id);
    }
    @RequestMapping("/accounts/{id}")
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

    @RequestMapping(path = "/clients/current/accounts", method = RequestMethod.GET)
    public ResponseEntity<Set<AccountDTO>> getAccounts(Authentication authentication){
        Client client = clientService.findByEmail(authentication.getName());
        return ResponseEntity.ok(client.getAccounts().stream().map(account -> new AccountDTO(account)).collect(Collectors.toSet()));
    }
    @RequestMapping(path = "/clients/current/accounts", method = RequestMethod.POST)
    public ResponseEntity<Object> createAccount(Authentication authentication) {

        Client client = clientService.findByEmail(authentication.getName());

        //System.out.println("Nombre del cliente: " + client.getFirstName());
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

        Account account = new Account(accountNumber, LocalDate.now(), 0);
        //System.out.println("ID: " + account.getId() + " NUMERO: " + account.getNumber());
        client.addAccount(account);
        accountService.save(account);
        clientService.save(client);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
