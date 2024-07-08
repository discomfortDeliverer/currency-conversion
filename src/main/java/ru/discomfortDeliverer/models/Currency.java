package ru.discomfortDeliverer.models;

import ru.discomfortDeliverer.dto.CurrencyDto;

public class Currency {
    private Integer id;
    private String code;
    private String name;
    private String sign;

    public static Currency createFromCurrencyDto(CurrencyDto currencyDto){
        Currency currency = new Currency();
        currency.setId(currencyDto.getId());
        currency.setCode(currencyDto.getCode());
        currency.setName(currencyDto.getFullName());
        currency.setSign(currencyDto.getSign());
        return currency;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
