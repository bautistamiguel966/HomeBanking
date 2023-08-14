package com.ap.homebanking.dtos;

import com.ap.homebanking.models.Account;
import com.ap.homebanking.models.Client;

import java.util.Set;
import java.util.stream.Collectors;


public class ClientDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    Set<AccountDTO> accounts;
    Set<ClientLoanDTO> loans;
    Set<CardDTO> cards;

    public ClientDTO(Client client) {
        this.id = client.getId();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.email = client.getEmail();
        this.accounts = client.getAccounts().stream().map(account -> new AccountDTO(account)).collect(Collectors.toSet());
        this.loans = client.getClientLoans().stream().map(clientLoan -> new ClientLoanDTO(clientLoan)).collect(Collectors.toSet());
        this.cards = client.getCards().stream().map(card -> new CardDTO(card)).collect(Collectors.toSet());
    }

    public long getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public Set<AccountDTO> getAccounts() {
        return accounts;
    }
    public Set<ClientLoanDTO> getLoans() {
        return loans;
    }

    public Set<CardDTO> getCards() {
        return cards;
    }
}
