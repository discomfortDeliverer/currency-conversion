package ru.discomfortDeliverer.service;

import ru.discomfortDeliverer.dao.ExchangeDao;
import ru.discomfortDeliverer.dto.ConversionDto;
import ru.discomfortDeliverer.dto.ConvertedDto;
import ru.discomfortDeliverer.dto.ExchangePostDto;
import ru.discomfortDeliverer.dto.ExchangeUpdateDto;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.exceptions.ExchangeRateCalculationException;
import ru.discomfortDeliverer.exceptions.QueryResultToCurrencyDtoParseException;
import ru.discomfortDeliverer.mappers.ExchangeMapper;
import ru.discomfortDeliverer.models.Exchange;
import ru.discomfortDeliverer.models.ExchangeRate;

import java.sql.SQLException;
import java.util.List;

public class ExchangeService {
    private ExchangeDao exchangeDao = new ExchangeDao();
    public List<Exchange> getExchangeRates() throws SQLException {
        return exchangeDao.getExchangeRates();
    }

    public ExchangeRate getExchangeRateByCurrencyPair(ExchangeUpdateDto exchangeUpdateDto)
            throws SQLException, DataBaseAccessException, QueryResultToCurrencyDtoParseException {
        return exchangeDao.getExchangeRateByCurrencyPair(exchangeUpdateDto);
    }

    public Exchange addExchangeRate(ExchangePostDto exchangePostDto)
            throws DataBaseAccessException, QueryResultToCurrencyDtoParseException {
        return exchangeDao.addExchangeRate(exchangePostDto);
    }

    public ExchangeRate updateExchangeRate(ExchangeUpdateDto exchangeUpdateDto)
            throws DataBaseAccessException, SQLException, QueryResultToCurrencyDtoParseException {
        Exchange exchange = ExchangeMapper.exchangeUpdateDtoToExchange(exchangeUpdateDto);
        return exchangeDao.updateExchangeRate(exchange);
    }

    public ConvertedDto convert(ConversionDto conversionDto)
            throws SQLException, QueryResultToCurrencyDtoParseException, ExchangeRateCalculationException {
        return exchangeDao.convert(conversionDto);
    }
}
