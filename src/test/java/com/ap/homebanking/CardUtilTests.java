package com.ap.homebanking;

import com.ap.homebanking.utils.CardUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class CardUtilTests {

    @Test
    public void cardNumberIsCreated(){
        String cardNumber = CardUtils.getCardNumber();
        assertThat(cardNumber,is(not(emptyOrNullString())));
    }
    //Verifico si la longitud de cardNumber es menor que 20, se supone que deberia ser de 16 numeros + 3 "-".
    @Test
    public void cardNumberLength(){
        String cardNumber = CardUtils.getCardNumber();
        assertThat(cardNumber.length(), is(lessThan(20)));
    }

    @Test
    public void cvvIsCreater(){
        int cvv = CardUtils.getCVV();
        assertThat(cvv, is(notNullValue()) );
    }

    //Verifico que el cvv tenga exactamente longitud 3
    @Test
    public void cvvLength(){
        int cvv = CardUtils.getCVV();
        assertThat(String.valueOf(cvv), hasLength(3));
    }
}
