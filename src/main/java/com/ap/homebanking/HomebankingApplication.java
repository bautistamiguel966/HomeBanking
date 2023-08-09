package com.ap.homebanking;

import com.ap.homebanking.models.Account;
import com.ap.homebanking.models.Client;
import com.ap.homebanking.models.Transaction;
import com.ap.homebanking.models.TransactionType;
import com.ap.homebanking.repositories.AccountRepository;
import com.ap.homebanking.repositories.ClientRepository;
import com.ap.homebanking.repositories.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.LocalDate;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
		return (args) -> {

			//clientRepository.save(new Client("Melba", "Morel", "melba@mindhub.com"));
			//clientRepository.save(new Client("Rocio", "Diaz", "rocio@mindhub.com"));

			Client client1 = new Client("Melba", "Morel", "melba@mindhub.com");
			Client client2 = new Client("Rocio", "Diaz", "rocio@mindhub.com");

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



		};
	}

}
