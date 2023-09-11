package com.ap.homebanking.controllers;

import com.ap.homebanking.dtos.LoanApplicationDTO;
import com.ap.homebanking.dtos.LoanDTO;
import com.ap.homebanking.models.*;
import com.ap.homebanking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LoanController {
    @Autowired
    private LoanService loanService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientLoanService clientLoanService;
    @Autowired
    private TransactionService transactionService;


    @GetMapping("/loans")
    public ResponseEntity<List<LoanDTO>> getLoans(){
        return ResponseEntity.ok(loanService.getLoansDTO());
    }
    @PostMapping("/loans/create")
    public ResponseEntity<Object> createLoan(@RequestParam String name, @RequestParam double maxAmount, @RequestParam double percentage, @RequestParam List<Integer> payments){
        if (name.isBlank()){
            return new ResponseEntity<>("Name cannot be empty", HttpStatus.FORBIDDEN);
        }
        if (maxAmount == 0){
            return new ResponseEntity<>("Max amount cannot be 0", HttpStatus.FORBIDDEN);
        }
        if (maxAmount < 0){
            return new ResponseEntity<>("Max amount cannot be negative", HttpStatus.FORBIDDEN);
        }
        if (percentage == 0){
            return new ResponseEntity<>("Percentage cannot be 0", HttpStatus.FORBIDDEN);
        }
        if (percentage < 0){
            return new ResponseEntity<>("Percentage cannot be negative", HttpStatus.FORBIDDEN);
        }
        if(payments.isEmpty()){
            return new ResponseEntity<>("Payments cannot be empty", HttpStatus.FORBIDDEN);
        }
        Loan loan = new Loan(name, maxAmount, payments, percentage);
        loanService.save(loan);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> applyForLoan(Authentication authentication, @RequestBody LoanApplicationDTO loanApplicationDTO){
        Client client = clientService.findByEmail(authentication.getName());
        Loan loan = loanService.findById(loanApplicationDTO.getLoanId());
        Account account = accountService.findByNumber(loanApplicationDTO.getToAccountNumber());

        //Quito todos los espacios en blanco del nombre de la cuenta
        String cleanedToAccountNumber = loanApplicationDTO.getToAccountNumber().replace(" ", "");
        if(loanApplicationDTO.getLoanId() == 0){
            return new ResponseEntity<>("Missing Loan ID", HttpStatus.FORBIDDEN);
        }
        if(loanApplicationDTO.getAmount() == 0){
            return new ResponseEntity<>("Amount cannot be 0", HttpStatus.FORBIDDEN);
        }
        if(loanApplicationDTO.getAmount() < 0){
            return new ResponseEntity<>("Amount cannot be nevative", HttpStatus.FORBIDDEN);
        }
        if(loanApplicationDTO.getPayments() == 0){
            return new ResponseEntity<>("Missing payments", HttpStatus.FORBIDDEN);
        }
        if(cleanedToAccountNumber.isEmpty()){
            return new ResponseEntity<>("Account number cannot be empty", HttpStatus.FORBIDDEN);
        }

        if(account == null){
            return new ResponseEntity<>("The Account doesn´t exist", HttpStatus.FORBIDDEN);
        }
        if(loan == null){
            return new ResponseEntity<>("The Loan doesn´t exist", HttpStatus.FORBIDDEN);
        }
        //Creo una bandera para ver si la cuenta de origen pertenece al cliente autenticado
        boolean flag = false;
        for(Account accountClient : client.getAccounts()){
            if(accountClient.getNumber().equals(account.getNumber())){
                flag = true;
            }
        }
        if(flag == false){
            return new ResponseEntity<>("The account does not belong to the authenticated client", HttpStatus.FORBIDDEN);
        }
        if(loanApplicationDTO.getAmount() > loan.getMaxAmount()){
            return new ResponseEntity<>("The amount exceeded what was allowed", HttpStatus.FORBIDDEN);
        }
        if(!loan.getPayments().contains(loanApplicationDTO.getPayments())){
            return new ResponseEntity<>("Not available", HttpStatus.FORBIDDEN);
        }

//        ClientLoan clientLoan = new ClientLoan((loanApplicationDTO.getAmount() + loanApplicationDTO.getAmount() * 0.2),
//                loanApplicationDTO.getPayments());
        ClientLoan clientLoan = new ClientLoan((loanApplicationDTO.getAmount() +
                (loanApplicationDTO.getAmount() * (loan.getPercentage() / 100))),
                loanApplicationDTO.getPayments());

        client.addClientLoans(clientLoan);
        loan.addClientLoans(clientLoan);
        Transaction transaction = new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(), loan.getName() + " Loan approved", LocalDate.now());
        account.addTransactions(transaction);
        account.setBalance(account.getBalance() + loanApplicationDTO.getAmount());

        transactionService.save(transaction);
        clientLoanService.save(clientLoan);
        loanService.save(loan);
        accountService.save(account);
        clientService.save(client);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
