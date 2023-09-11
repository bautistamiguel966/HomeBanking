package com.ap.homebanking.controllers;

import com.ap.homebanking.models.*;
import com.ap.homebanking.repositories.AccountRepository;
import com.ap.homebanking.repositories.ClientRepository;
import com.ap.homebanking.repositories.TransactionRepository;
import com.ap.homebanking.services.AccountService;
import com.ap.homebanking.services.CardService;
import com.ap.homebanking.services.ClientService;
import com.ap.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private CardService cardService;


    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(Authentication authentication,
                                                    @RequestParam String fromAccountNumber,
                                                    @RequestParam String toAccountNumber,
                                                    @RequestParam double amount,
                                                    @RequestParam String description){

        Client client = clientService.findByEmail(authentication.getName());
        Account accountFrom = accountService.findByNumber(fromAccountNumber);
        Account accountTo = accountService.findByNumber(toAccountNumber);
        //Quito todos los espacios en blanco en los numeros de cuenta
        String cleanedFromAccountNumber = fromAccountNumber.replace(" ", "");
        String cleanedToAccountNumber = toAccountNumber.replace(" ", "");

        if(description.trim().isEmpty()){
            return new ResponseEntity<>("Description cannot be empty", HttpStatus.FORBIDDEN);
        }
        if(cleanedFromAccountNumber.isEmpty()){
            return new ResponseEntity<>("Origin account cannot be empty", HttpStatus.FORBIDDEN);
        }
        if(cleanedToAccountNumber.isEmpty()){
            return new ResponseEntity<>("Destination account cannot be empty", HttpStatus.FORBIDDEN);
        }
        if(amount == 0){
            return new ResponseEntity<>("Amount cannot be 0", HttpStatus.FORBIDDEN);
        }
        if(amount < 0){
            return new ResponseEntity<>("Amount cannot be negative", HttpStatus.FORBIDDEN);
        }
        if(fromAccountNumber.equals(toAccountNumber)){
            return new ResponseEntity<>("Both accounts cannot be the same", HttpStatus.FORBIDDEN);
        }
        if(accountFrom == null){
            return new ResponseEntity<>("Origin account doesn´t exists", HttpStatus.FORBIDDEN);
        }
        if(accountTo == null){
            return new ResponseEntity<>("Destination account doesn´t exists", HttpStatus.FORBIDDEN);
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
        if(accountFrom.getBalance() < amount){
            return new ResponseEntity<>("You don't have enough balance", HttpStatus.FORBIDDEN);
        }

        Transaction transactionFrom = new Transaction(TransactionType.DEBIT, -amount, description + fromAccountNumber, LocalDate.now());
        Transaction transactionTo = new Transaction(TransactionType.CREDIT, amount, description + toAccountNumber, LocalDate.now());

        //Le seteo al campo amountAccount de cada transaction, el balance de su cuenta - y +  el monto de la transaction respectivamente
        transactionFrom.setAmountAccount(accountFrom.getBalance() - amount);
        transactionTo.setAmountAccount(accountTo.getBalance() + amount);

        accountFrom.addTransactions(transactionFrom);
        accountTo.addTransactions(transactionTo);

        accountFrom.setBalance(accountFrom.getBalance() - amount);
        accountTo.setBalance(accountTo.getBalance() + amount);

        transactionService.save(transactionFrom);
        transactionService.save(transactionTo);

        accountService.save(accountFrom);
        accountService.save(accountTo);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    //Pagar con una tarjeta
    @Transactional
    @PostMapping("/payments")
    public ResponseEntity<Object> createPayment(Authentication authentication,
                                                @RequestParam String number,
                                                @RequestParam int cvv,
                                                @RequestParam double amount,
                                                @RequestParam String description){

        Client client = clientService.findByEmail(authentication.getName());
        Card card = cardService.findByNumber(number);
        if(!cardService.existByNumberAndClient(number, client)){
            return new ResponseEntity<>("The card does not belong to the authenticated client", HttpStatus.FORBIDDEN);
        }
        if(card.getThruDate().isBefore(LocalDate.now())){
            return new ResponseEntity<>("The card expired", HttpStatus.FORBIDDEN);
        }
        if(card.getCvv() != cvv){
            return new ResponseEntity<>("The cvv is incorrect", HttpStatus.FORBIDDEN);
        }
        boolean paymentSuccessful = false;
        for(Account account : client.getAccounts()){
            if(account.getBalance() >= amount){
                Transaction transaction = new Transaction(TransactionType.DEBIT, -amount, description + account.getNumber(), LocalDate.now());
                transaction.setAmountAccount(account.getBalance() - amount);
                account.addTransactions(transaction);
                account.setBalance(account.getBalance() - amount);
                transactionService.save(transaction);
                accountService.save(account);
                paymentSuccessful = true;
                break;
            }
        }
        if(!paymentSuccessful){
            return new ResponseEntity<>("No account has sufficient balance for the payment", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
