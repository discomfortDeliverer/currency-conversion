package ru.discomfortDeliverer.service;

import ru.discomfortDeliverer.dao.CurrencyDao;
import ru.discomfortDeliverer.exceptions.CurrencyNotFoundException;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.exceptions.FieldAlreadyExistException;
import ru.discomfortDeliverer.models.Currency;

import java.util.List;

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

}
