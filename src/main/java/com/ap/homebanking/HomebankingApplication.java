package com.ap.homebanking;

import com.ap.homebanking.models.*;
import com.ap.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {
	@Autowired
	private PasswordEncoder passwordEncoder;
	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
		return (args) -> {

			Client client1 = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("1234"));
			Client client2 = new Client("Rocio", "Diaz", "rocio@mindhub.com", passwordEncoder.encode("4321"));
			Client client3 = new Client("admin", "admin", "admin@mindhub.com", passwordEncoder.encode("admin"));

			Account account1 = new Account("VIN001", LocalDate.now(), 5000);
			Account account2 = new Account("VIN002", LocalDate.now().plusDays(1), 7500);
			Account account3 = new Account("VIN003", LocalDate.now().plusDays(3), 4000);
			Account account4 = new Account("VIN004", LocalDate.now().plusDays(7), 9000);

			Transaction transaction1 = new Transaction(TransactionType.CREDIT, 10000, "Description 1", LocalDate.now());
			Transaction transaction2 = new Transaction(TransactionType.DEBIT, -12000, "Description 2", LocalDate.now().plusDays(13));
			Transaction transaction3 = new Transaction(TransactionType.CREDIT, 10500, "Description 3", LocalDate.now());
			Transaction transaction4 = new Transaction(TransactionType.DEBIT, -5200, "Description 4", LocalDate.now().plusDays(2));
			Transaction transaction5 = new Transaction(TransactionType.CREDIT, 10000, "Description 5", LocalDate.now());
			Transaction transaction6 = new Transaction(TransactionType.DEBIT, 2000, "Description 6", LocalDate.now().plusDays(18));
			Transaction transaction7 = new Transaction(TransactionType.CREDIT, 31000, "Description 7", LocalDate.now());
			Transaction transaction8 = new Transaction(TransactionType.DEBIT, 700, "Description 8", LocalDate.now().plusDays(4));

			List<Integer> payments1 = List.of(12, 24, 36, 48, 60);
			List<Integer> payments2 = List.of(6, 12, 24);
			List<Integer> payments3 = List.of(6, 12, 24, 36);

			Loan loan1 = new Loan("Hipotecario", 500000, payments1);
			Loan loan2 = new Loan("Personal", 100000, payments2);
			Loan loan3 = new Loan("Automotriz", 300000, payments3);

			ClientLoan clientLoan1 = new ClientLoan(400000, 60);
			ClientLoan clientLoan2 = new ClientLoan(50000, 12);
			ClientLoan clientLoan3 = new ClientLoan(100000, 24);
			ClientLoan clientLoan4 = new ClientLoan(200000, 36);

			//Tarjetas para Melba
			Card card1 = new Card(CardType.DEBIT, CardColor.GOLD, "1231-2312-1233-6542", 123, LocalDate.now(), LocalDate.now().plusYears(5));
			Card card2 = new Card(CardType.CREDIT, CardColor.TITANIUM, "1231-2315-1235-7654", 456, LocalDate.now(), LocalDate.now().plusYears(5));
			//Tarjeta para Rocio
			Card card3 = new Card(CardType.CREDIT, CardColor.SILVER, "4431-2315-1235-5386", 789, LocalDate.now(), LocalDate.now().plusYears(5));

			//Se le asignan las tarjetas a Melba
			client1.addCard(card1);
			client1.addCard(card2);

			//Se le asigna su tarjeta a Rocio
			client2.addCard(card3);

			//Se le asigna a Melba, sus dos prestamos
			client1.addClientLoans(clientLoan1);
			loan1.addClientLoans(clientLoan1);

			client1.addClientLoans(clientLoan2);
			loan2.addClientLoans(clientLoan2);

			//Se le asigna a Rocio sus dos prestamos
			client2.addClientLoans(clientLoan3);
			loan2.addClientLoans(clientLoan3);

			client2.addClientLoans(clientLoan4);
			loan3.addClientLoans(clientLoan4);

			account1.addTransactions(transaction1);
			account1.addTransactions(transaction2);

			account2.addTransactions(transaction3);
			account2.addTransactions(transaction4);

			account3.addTransactions(transaction5);
			account3.addTransactions(transaction6);

			account4.addTransactions(transaction7);
			account4.addTransactions(transaction8);

			client1.addAccount(account1);
			client1.addAccount(account2);

			client2.addAccount(account3);
			client2.addAccount(account4);

			clientRepository.save(client1);
			clientRepository.save(client2);
			clientRepository.save(client3);

			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);
			accountRepository.save(account4);

			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);
			transactionRepository.save(transaction5);
			transactionRepository.save(transaction6);
			transactionRepository.save(transaction7);
			transactionRepository.save(transaction8);

			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);

			clientLoanRepository.save(clientLoan1);
			clientLoanRepository.save(clientLoan2);
			clientLoanRepository.save(clientLoan3);
			clientLoanRepository.save(clientLoan4);

			cardRepository.save(card1);
			cardRepository.save(card2);
			cardRepository.save(card3);
		};
	}

}
