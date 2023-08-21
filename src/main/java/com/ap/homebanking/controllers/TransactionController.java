package com.ap.homebanking.controllers;

import com.ap.homebanking.models.Account;
import com.ap.homebanking.models.Client;
import com.ap.homebanking.models.Transaction;
import com.ap.homebanking.models.TransactionType;
import com.ap.homebanking.repositories.AccountRepository;
import com.ap.homebanking.repositories.ClientRepository;
import com.ap.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;


    @Transactional
    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    public ResponseEntity<Object> createTransaction(Authentication authentication,
                                                    @RequestParam String fromAccountNumber,
                                                    @RequestParam String toAccountNumber,
                                                    @RequestParam double amount,
                                                    @RequestParam String description){

        Client client = clientRepository.findByEmail(authentication.getName());
        Account accountFrom = accountRepository.findByNumber(fromAccountNumber);
        Account accountTo = accountRepository.findByNumber(toAccountNumber);

        if (amount == 0 || description.isEmpty() || fromAccountNumber.isEmpty() || toAccountNumber.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if(fromAccountNumber.equals(toAccountNumber)){
            return new ResponseEntity<>("Both accounts cannot be the same", HttpStatus.FORBIDDEN);
        }
        if(accountFrom == null){
            return new ResponseEntity<>("Origin account doesn´t exists", HttpStatus.FORBIDDEN);
        }
        //Creo una bandera para ver si la cuenta de origen pertenece al cliente autenticado
        boolean flag = false;
        for(Account account : client.getAccounts()){
            if(account.getNumber().equals(fromAccountNumber)){
                flag = true;
            }
        }
        if(flag == false){
            return new ResponseEntity<>("The account does not belong to the authenticated client", HttpStatus.FORBIDDEN);
        }
        if(accountTo == null){
            return new ResponseEntity<>("Account doesn´t exists", HttpStatus.FORBIDDEN);
        }
        if(accountFrom.getBalance() < amount){
            return new ResponseEntity<>("You don't have enough balance", HttpStatus.FORBIDDEN);
        }

        Transaction transactionFrom = new Transaction(TransactionType.DEBIT, -amount, description + fromAccountNumber, LocalDate.now());
        Transaction transactionTo = new Transaction(TransactionType.CREDIT, amount, description + toAccountNumber, LocalDate.now());

        accountFrom.addTransactions(transactionFrom);
        accountTo.addTransactions(transactionTo);

        accountFrom.setBalance(accountFrom.getBalance() - amount);
        accountTo.setBalance(accountTo.getBalance() + amount);

        transactionRepository.save(transactionFrom);
        transactionRepository.save(transactionTo);

        accountRepository.save(accountFrom);
        accountRepository.save(accountTo);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
