package com.ap.homebanking.controllers;

import com.ap.homebanking.dtos.CardDTO;
import com.ap.homebanking.models.*;
import com.ap.homebanking.services.ClientService;
import com.ap.homebanking.services.CardService;
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
    private ClientService clientService;
    @Autowired
    private CardService cardService;


    @RequestMapping(path = "/clients/current/cards", method = RequestMethod.GET)
    public ResponseEntity<Set<CardDTO>> getCards(Authentication authentication){
        Client client = clientService.findByEmail(authentication.getName());
        return ResponseEntity.ok(client.getCards().stream().map(card -> new CardDTO(card)).collect(Collectors.toSet()));
    }
    @RequestMapping(path = "/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> createAccount(Authentication authentication, @RequestParam CardColor cardColor, @RequestParam CardType cardType) {

        Client client = clientService.findByEmail(authentication.getName());

        if(cardService.exist(cardType, cardColor, client)){
            return new ResponseEntity<>("You already have a " +cardColor + " " +cardType + " card", HttpStatus.FORBIDDEN);
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
        Card card = new Card(cardType, cardColor, cardNumber, randomNumber3Digits, LocalDate.now(), LocalDate.now().plusYears(5));
        client.addCard(card);
        cardService.save(card);
        clientService.save(client);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
