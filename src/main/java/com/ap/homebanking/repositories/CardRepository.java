package com.ap.homebanking.repositories;

import com.ap.homebanking.models.Card;
import com.ap.homebanking.models.CardColor;
import com.ap.homebanking.models.CardType;
import com.ap.homebanking.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByTypeAndColorAndClient(CardType type, CardColor color, Client client);

    boolean existsByNumberAndClient(String number, Client client);

    Card findByNumber(String number);
}
