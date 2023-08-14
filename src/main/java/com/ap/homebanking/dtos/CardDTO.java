package com.ap.homebanking.dtos;

import com.ap.homebanking.models.Card;
import com.ap.homebanking.models.CardType;
import com.ap.homebanking.models.ColorType;

import java.time.LocalDate;

public class CardDTO {
    private long id;
    private String cardHolder;
    private CardType type;
    private ColorType color;
    private long number;
    private long cvv;
    private LocalDate fromDate;
    private LocalDate thruDate;

    public CardDTO(Card card){
        this.id = card.getId();
        this.cardHolder = card.getCardHolder();
        this.type = card.getType();
        this.color = card.getColor();
        this.number = card.getNumber();
        this.cvv = card.getCvv();
        this.fromDate = card.getFromDate();
        this.thruDate = card.getThruDate();
    }

    public long getId() {
        return id;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public CardType getType() {
        return type;
    }

    public ColorType getColor() {
        return color;
    }

    public long getNumber() {
        return number;
    }

    public long getCvv() {
        return cvv;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getThruDate() {
        return thruDate;
    }
}
