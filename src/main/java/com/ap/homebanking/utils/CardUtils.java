package com.ap.homebanking.utils;

import java.util.Random;

public final class CardUtils {

    private CardUtils(){}

    public static int getCVV() {
        Random random = new Random();
        int randomNumber3Digits = random.nextInt(900) + 100; //Numero random de 3 digitos
        return randomNumber3Digits;
    }

    public static String getCardNumber() {
        Random random = new Random();
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
        return cardNumber;
    }
}
