package com.ap.homebanking.controllers;

import com.ap.homebanking.dtos.AccountDTO;
import com.ap.homebanking.dtos.CardDTO;
import com.ap.homebanking.models.*;
import com.ap.homebanking.repositories.CardRepository;
import com.ap.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CardController {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CardRepository cardRepository;


    @RequestMapping(path = "/clients/current/cards", method = RequestMethod.GET)
    public ResponseEntity<Set<CardDTO>> getCards(Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        return ResponseEntity.ok(client.getCards().stream().map(card -> new CardDTO(card)).collect(Collectors.toSet()));
    }
    @RequestMapping(path = "/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> createAccount(Authentication authentication, @RequestParam CardColor cardColor, @RequestParam CardType cardType) {

        Client client = clientRepository.findByEmail(authentication.getName());

        //System.out.println("Nombre del cliente: " + client.getFirstName());

        int typeCard = 0;
        Set<Card> cards = client.getCards();
        for(Card card : cards){
            if(card.getType() == cardType){
                typeCard ++;
            }
        }
        if(typeCard >= 3){
            return new ResponseEntity<>("You already have 3 " +cardType + " cards", HttpStatus.FORBIDDEN);
        }

        Random random = new Random();

        int randomNumber3Digits = random.nextInt(900) + 100; //Numero random de 3 digitos
        String cardNumber = "";
        for(int i = 0; i<4; i++){
            if(i == 3){
                int randomNumber4Digits = random.nextInt(9000) + 1000;
                cardNumber = cardNumber + randomNumber4Digits;
            }else{
                int randomNumber4Digits = random.nextInt(9000) + 1000;
                cardNumber = cardNumber + randomNumber4Digits + "-";
            }
        }
        //System.out.println("ID: " + account.getId() + " NUMERO: " + account.getNumber());
        Card card = new Card(cardType, cardColor, cardNumber, randomNumber3Digits, LocalDate.now(), LocalDate.now().plusYears(5));
        client.addCard(card);
        cardRepository.save(card);
        clientRepository.save(client);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
