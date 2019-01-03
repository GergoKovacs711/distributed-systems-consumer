package hu.iit.uni.miskolc.kovacsgergo.distributedsystemconsumer.util;

import hu.iit.uni.miskolc.kovacsgergo.distributedsystemconsumer.model.generated.ExchangeRate;

public class Util {

    public static String toString(ExchangeRate exchangeRate){
        return "Rate {" +
                "name = " + exchangeRate.getName() +
                ", code = " + exchangeRate.getCode() +
                ", rate = " + exchangeRate.getRate() +
                '}';
    }
}
