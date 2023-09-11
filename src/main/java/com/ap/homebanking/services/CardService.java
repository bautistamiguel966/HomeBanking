package com.ap.homebanking.services;

import com.ap.homebanking.dtos.CardDTO;
import com.ap.homebanking.models.Card;
import com.ap.homebanking.models.CardColor;
import com.ap.homebanking.models.CardType;
import com.ap.homebanking.models.Client;

import java.util.List;

public interface CardService {

    void save(Card card);

    boolean exist(CardType type, CardColor color, Client client);

    void delete(long cardId);

    boolean existByNumberAndClient(String number, Client client);

    Card findByNumber(String number);
}
