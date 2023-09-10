package com.ap.homebanking;

import com.ap.homebanking.models.*;
import com.ap.homebanking.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RepositoriesTest {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private TransactionRepository transactionRepository;


    @Test
    public void existAccounts(){
        List<Account> accounts = accountRepository.findAll();
        assertThat(accounts, is(not(empty())));
//        assertThat(accounts, is(empty()));
    }
    //Veo  si el listado de cuentas tiene longitud 10, por probar algo.
    @Test
    public void lengthAccount(){
        List<Account> accounts = accountRepository.findAll();
        assertThat(accounts, hasSize(5));
    }

    //Busco una card por id, y verifico si existe
    @Test
    public void existCardById(){
        Card card = cardRepository.findById(23L).orElse(null);
        assertThat(card, is(notNullValue()));
//        assertThat(card, is(null));
    }
    //Verifico si todos los cvv tengan longitud 3
    @Test
    public void cardCVV() {
        List<Card> cards = cardRepository.findAll();

        for (Card card : cards) {
            String cvvString = String.valueOf(card.getCvv()); // Convierto en string
            assertThat(cvvString, hasLength(3)); // Verifico que la longitud sea igual a 3
        }
    }



    //Veo si tengo menos de 5 clients en la base de datos.
    @Test
    public void lengthClients(){
        List<Client> clients = clientRepository.findAll();
        assertThat(clients.size(), is(lessThan(5)));
    }

    //Verifico que la pripiedad nombre de client sea String
    @Test
    public void clientNameType(){
        List<Client> clients = clientRepository.findAll();
        assertThat(clients, hasItem(hasProperty("firstName", isA(String.class))));
    }

    //Verifico si el amount de una transaction especifica es mayor a 5000
    @Test
    public void amount(){
        Transaction transaction = transactionRepository.findById(1l).orElse(null);
        if(transaction != null){
            assertThat(transaction.getAmount(), is(greaterThan(5000.0)));
        }
    }

    //Verifico que todas las descripciones en las transactions tengan "VIN" en ellas.
    @Test
    public void emptyDescription(){
        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions, hasItem(hasProperty("description", containsString("VIN"))));
    }

    @Test
    public void existLoans(){
        List<Loan> loans = loanRepository.findAll();
        assertThat(loans,is(not(empty())));
    }

    @Test
    public void existPersonalLoan(){
        List<Loan> loans = loanRepository.findAll();
        assertThat(loans, hasItem(hasProperty("name", is("Personal"))));

    }
}
