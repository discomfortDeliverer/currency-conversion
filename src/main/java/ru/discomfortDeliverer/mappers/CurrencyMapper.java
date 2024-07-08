package ru.discomfortDeliverer.mappers;

import ru.discomfortDeliverer.dto.CurrencyDto;
import ru.discomfortDeliverer.exceptions.QueryResultToCurrencyDtoParseException;
import ru.discomfortDeliverer.models.Currency;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyMapper {
    public static CurrencyDto queryResultToCurrencyDto(ResultSet resultSet)
            throws SQLException, QueryResultToCurrencyDtoParseException {

        while (resultSet.next()) {
            CurrencyDto currencyDto = new CurrencyDto();
            currencyDto.setId(resultSet.getInt("id"));
            currencyDto.setCode(resultSet.getString("code"));
            currencyDto.setSign(resultSet.getString("sign"));
            currencyDto.setFullName(resultSet.getString("full_name"));
            return currencyDto;
        }
        throw new QueryResultToCurrencyDtoParseException("Ошибка в создании CurrencyDto из ResultSet");
    }

    public static Currency currencyDtoToCurrency(CurrencyDto currencyDto){
        Currency currency = new Currency();
        currency.setId(currencyDto.getId());
        currency.setName(currencyDto.getFullName());
        currency.setCode(currencyDto.getCode());
        currency.setSign(currencyDto.getSign());
        return currency;
    }
}
