package com.ap.homebanking.controllers;

import com.ap.homebanking.dtos.CardDTO;
import com.ap.homebanking.models.*;
import com.ap.homebanking.services.ClientService;
import com.ap.homebanking.services.CardService;
import com.ap.homebanking.utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CardController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private CardService cardService;


    @GetMapping("/clients/current/cards")
    public ResponseEntity<Set<CardDTO>> getCards(Authentication authentication){
        Client client = clientService.findByEmail(authentication.getName());
        return ResponseEntity.ok(client.getCards().stream().map(card -> new CardDTO(card)).collect(Collectors.toSet()));
    }
    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> createAccount(Authentication authentication, @RequestParam CardColor cardColor, @RequestParam CardType cardType) {

        Client client = clientService.findByEmail(authentication.getName());

        if(cardService.exist(cardType, cardColor, client)){
            return new ResponseEntity<>("You already have a " +cardColor + " " +cardType + " card", HttpStatus.FORBIDDEN);
        }
        int cvv = CardUtils.getCVV();
        String cardNumber = CardUtils.getCardNumber();
        Card card = new Card(cardType, cardColor, cardNumber, cvv, LocalDate.now(), LocalDate.now().plusYears(5));
        client.addCard(card);
        cardService.save(card);
        clientService.save(client);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/clients/current/cards/{id}")
    public ResponseEntity<Object> deleteCard(Authentication authentication, @PathVariable long id){
        Client client = clientService.findByEmail(authentication.getName());
        for(Card card : client.getCards()){
            if(card.getId() == id){
                System.out.println("ID DE CARD ELIMINADA: " + id);
                cardService.delete(id);
                return ResponseEntity.noContent().build();
            }
        }
        return new ResponseEntity<>("The card doesn´t belong to this client ",HttpStatus.FORBIDDEN);
    }

}
