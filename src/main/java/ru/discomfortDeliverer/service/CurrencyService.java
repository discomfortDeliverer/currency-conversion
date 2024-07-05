package ru.discomfortDeliverer.service;

import com.google.gson.Gson;
import ru.discomfortDeliverer.dao.CurrencyDao;
import ru.discomfortDeliverer.dto.СurrencyDto;

import java.util.List;

public class CurrencyService {
    private CurrencyDao currencyDao = new CurrencyDao();
    public String getAllCurrencies(){
        List<СurrencyDto> allCurrency = null;
        try {
            allCurrency = currencyDao.getAllCurrency();
        } catch (ClassNotFoundException e) {
            System.out.println("Ошибка в CurrencyService");
        }

        String json = new Gson().toJson(allCurrency);

        return json;
    }

}
