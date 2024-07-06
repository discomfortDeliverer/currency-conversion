package ru.discomfortDeliverer.service;

import ru.discomfortDeliverer.dao.ExchangeDao;
import ru.discomfortDeliverer.models.Exchange;

import java.sql.SQLException;
import java.util.List;

public class ExchangeService {
    private ExchangeDao exchangeDao = new ExchangeDao();
    public List<Exchange> getExchangeRates() throws SQLException {
        return exchangeDao.getExchangeRates();
    }
}
