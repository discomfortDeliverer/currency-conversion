package ru.discomfortDeliverer.util;

public class Validator {
    public static boolean isValidCurrencyCode(String currencyCode){
        if(currencyCode.length() > 3) return false;

        return currencyCode.matches("[A-Z]+");
    }

    public static boolean isValidCurrencyPairCodes(String currencyPairCodes){
        if(currencyPairCodes.length() > 6) return false;

        return currencyPairCodes.matches("[A-Z]+");
    }
}
