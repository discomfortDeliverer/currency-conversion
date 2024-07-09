package ru.discomfortDeliverer.service;

import ru.discomfortDeliverer.dao.CurrencyDao;
import ru.discomfortDeliverer.dto.CurrencyDto;
import ru.discomfortDeliverer.exceptions.CurrencyNotFoundException;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.exceptions.FieldAlreadyExistException;
import ru.discomfortDeliverer.models.Currency;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CurrencyService {
    private CurrencyDao currencyDao = new CurrencyDao();
    public List<Currency> getAllCurrencies() throws DataBaseAccessException {
        return currencyDao.getAllCurrency();
    }

    public Currency getCurrencyByCode(String code)
            throws DataBaseAccessException, CurrencyNotFoundException {
        return currencyDao.findCurrencyByCode(code);
    }

    public Integer createCurrency(Currency currency)
            throws DataBaseAccessException, FieldAlreadyExistException {
        return currencyDao.addCurrencyIntoDataBase(currency);
    }

    private Map<String, String> parseRequestBody(String requestBody){
        return Arrays.stream(requestBody.split("&"))
                .map(field -> field.split("="))
                .collect(Collectors.toMap(
                        pair -> pair[0],
                        pair -> URLDecoder.decode(pair[1])
                ));
    }

}
