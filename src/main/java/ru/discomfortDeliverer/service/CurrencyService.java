package ru.discomfortDeliverer.service;

import com.google.gson.Gson;
import ru.discomfortDeliverer.dao.CurrencyDao;
import ru.discomfortDeliverer.dto.СurrencyDto;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;

import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private CurrencyDao currencyDao = new CurrencyDao();
    public List<СurrencyDto> getAllCurrencies() throws ClassNotFoundException {
        List<СurrencyDto> allCurrency = null;

        allCurrency = currencyDao.getAllCurrency();

        return allCurrency;
    }

    public Optional<СurrencyDto> getCurrencyByCode(String code) throws DataBaseAccessException {
        return currencyDao.findCurrencyByCode(code);
    }
}
