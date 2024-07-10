package ru.discomfortDeliverer.service;

import ru.discomfortDeliverer.dao.ExchangeDao;
import ru.discomfortDeliverer.exceptions.*;
import ru.discomfortDeliverer.models.ExchangeRate;
import ru.discomfortDeliverer.models.response.ExchangedRate;

import java.sql.SQLException;
import java.util.List;

public class ExchangeService {
    private ExchangeDao exchangeDao = new ExchangeDao();
    public List<ExchangeRate> findAllExchangeRates() throws DataBaseAccessException {
        return exchangeDao.findAllExchangeRates();
    }

    public ExchangeRate getExchangeRateByCurrencyPairCodes(String baseCurrencyCode, String targetCurrencyCode)
            throws SQLException, DataBaseAccessException, QueryResultToCurrencyDtoParseException, ExchangeRateNotFoundException {
        return exchangeDao.findExchangeRateByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
    }

    public Integer updateExchangeRate(int baseCurrencyId, int targetCurrencyId, Double rate)
            throws DataBaseAccessException {
        return exchangeDao.updateExchangeRate(baseCurrencyId, targetCurrencyId, rate);
    }

    public ExchangedRate convert(String codeFrom, String codeTo, Double amount)
            throws SQLException, QueryResultToCurrencyDtoParseException, ExchangeRateCalculationException, DataBaseAccessException, CurrencyNotFoundException {
        return exchangeDao.convert(codeFrom, codeTo, amount);
    }

    public Integer saveExchangeRate(Integer baseCurrencyId, Integer targetCurrencyId, double rate)
            throws DataBaseAccessException, FieldAlreadyExistException {
        return exchangeDao.saveExchangeRate(baseCurrencyId, targetCurrencyId, rate);
    }
}
