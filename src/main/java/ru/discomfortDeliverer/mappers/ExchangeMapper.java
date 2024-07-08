package ru.discomfortDeliverer.mappers;

import ru.discomfortDeliverer.dto.ExchangeDto;
import ru.discomfortDeliverer.dto.ExchangeUpdateDto;
import ru.discomfortDeliverer.models.Currency;
import ru.discomfortDeliverer.models.Exchange;
import ru.discomfortDeliverer.models.ExchangeRate;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeMapper {
    public static Exchange exchangeUpdateDtoToExchange(ExchangeUpdateDto exchangeUpdateDto){
        Exchange exchange = new Exchange();
        exchange.setBaseCode(exchangeUpdateDto.getBaseCode());
        exchange.setTargetCode(exchangeUpdateDto.getTargetCode());
        exchange.setRate(exchangeUpdateDto.getRate());

        return exchange;
    }

    public static ExchangeDto queryResultToExchangeDto(ResultSet resultSet) throws SQLException {
        ExchangeDto exchangeDto = new ExchangeDto();

        resultSet.next();
        exchangeDto.setId(resultSet.getInt("id"));
        exchangeDto.setBaseCurrencyId(resultSet.getInt("base_currency_id"));
        exchangeDto.setTargetCurrencyId(resultSet.getInt("target_currency_id"));
        exchangeDto.setRate(resultSet.getDouble("rate"));

        return exchangeDto;
    }

    public static ExchangeRate exchangeDtoAndCurrenciesToExchange(ExchangeDto exchangeDto,
                                                              Currency baseCurrency, Currency targetCurrency){
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setId(exchangeDto.getId());
        exchangeRate.setBaseCurrency(baseCurrency);
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRate.setRate(exchangeDto.getRate());

        return exchangeRate;
    }
}
