package com.ap.homebanking.controllers;

import com.ap.homebanking.dtos.LoanApplicationDTO;
import com.ap.homebanking.dtos.LoanDTO;
import com.ap.homebanking.models.*;
import com.ap.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    ClientLoanRepository clientLoanRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @RequestMapping(path = "/loans", method = RequestMethod.GET)
    public ResponseEntity<List<LoanDTO>> getLoans(){
        return ResponseEntity.ok(loanRepository.findAll().stream().map(loan -> new LoanDTO(loan)).collect(Collectors.toList()));
    }
    @Transactional
    @RequestMapping(path = "/loans", method = RequestMethod.POST)
    public ResponseEntity<Object> applyForLoan(Authentication authentication, @RequestBody LoanApplicationDTO loanApplicationDTO){
        Client client = clientRepository.findByEmail(authentication.getName());
        Loan loan = loanRepository.findById(loanApplicationDTO.getLoanId());
        Account account = accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber());

        if(loanApplicationDTO.getLoanId() == 0 || loanApplicationDTO.getAmount() == 0 || loanApplicationDTO.getPayments() == 0 || loanApplicationDTO.getToAccountNumber().isEmpty()){
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
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
//        if(client.getAccounts().contains(account.getNumber()) == false){
//            return new ResponseEntity<>("The account does not belong to the authenticated client", HttpStatus.FORBIDDEN);
//        }
        if(loanApplicationDTO.getAmount() > loan.getMaxAmount()){
            return new ResponseEntity<>("The amount exceeded what was allowed", HttpStatus.FORBIDDEN);
        }
        if(!loan.getPayments().contains(loanApplicationDTO.getPayments())){
            return new ResponseEntity<>("Not available", HttpStatus.FORBIDDEN);
        }

        ClientLoan clientLoan = new ClientLoan((loanApplicationDTO.getAmount() + loanApplicationDTO.getAmount() * 0.2), loanApplicationDTO.getPayments());
        client.addClientLoans(clientLoan);
        loan.addClientLoans(clientLoan);
        Transaction transaction = new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(), loan.getName() + " Loan approved", LocalDate.now());
        account.addTransactions(transaction);
        account.setBalance(account.getBalance() + loanApplicationDTO.getAmount());

        transactionRepository.save(transaction);
        clientLoanRepository.save(clientLoan);
        loanRepository.save(loan);
        accountRepository.save(account);
        clientRepository.save(client);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
