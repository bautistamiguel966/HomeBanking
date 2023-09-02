package com.ap.homebanking.controllers;

import com.ap.homebanking.dtos.ClientDTO;
import com.ap.homebanking.models.Account;
import com.ap.homebanking.models.Client;
import com.ap.homebanking.services.AccountService;
import com.ap.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;


@RestController
@RequestMapping("/api")
public class ClientController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountService accountService;


    @RequestMapping("/clients")
    public List<ClientDTO> getClients(){
        return clientService.getClientsDTO();
    }

    @RequestMapping(path = "/clients", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email, @RequestParam String password) {

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (clientService.findByEmail(email) !=  null) {
            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);
        }

        Client client = new Client(firstName, lastName, email, passwordEncoder.encode(password));
        System.out.println("Nombre del cliente: " + client.getFirstName());
        int minDigits = 5;
        int maxDigits = 8;

        Random random = new Random();

        // Generar un número aleatorio entre 10000 y 99999999
        int randomNumber = random.nextInt((int)Math.pow(10, maxDigits) - (int)Math.pow(10, minDigits)) + (int)Math.pow(10, minDigits);
        String accountNumber = "VIN" + randomNumber;
        Account account = new Account(accountNumber, LocalDate.now(), 0);

        accountService.save(account);
        client.addAccount(account);
        clientService.save(client);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable long id){
        return clientService.getClientDTO(id);
    }

    @RequestMapping("/clients/current")
    public ClientDTO getClient(Authentication authentication) {

        return clientService.getCurrent(authentication.getName());

    }
}
