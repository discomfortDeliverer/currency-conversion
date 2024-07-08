package ru.discomfortDeliverer.dao;

import ru.discomfortDeliverer.dto.CurrencyDto;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.models.Currency;
import ru.discomfortDeliverer.models.Exchange;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ExchangeDao {
    private String url = "jdbc:sqlite:D:\\SQLite\\currencyConversion.db";

    public ExchangeDao() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Exchange> getExchangeRates() throws SQLException {
        List<Exchange> exchanges = new ArrayList<>();

        String query = "SELECT \n" +
                "    er.id,\n" +
                "    er.rate,\n" +
                "\tbc.id AS base_currency_id,\n" +
                "    bc.code AS base_currency_code,\n" +
                "    bc.full_name AS base_currency_name,\n" +
                "    bc.sign AS base_currency_sign,\n" +
                "\ttc.id AS target_currency_id,\n" +
                "    tc.code AS target_currency_code,\n" +
                "    tc.full_name AS target_currency_name,\n" +
                "    tc.sign AS target_currency_sign\n" +
                "FROM exchange_rates AS er\n" +
                "JOIN currency AS bc\n" +
                "ON er.base_currency_id = bc.id\n" +
                "JOIN currency AS tc\n" +
                "ON er.target_currency_id = tc.id";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     query)){

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                Currency baseCurrency = new Currency();
                baseCurrency.setId(resultSet.getInt("base_currency_id"));
                baseCurrency.setName(resultSet.getString("base_currency_name"));
                baseCurrency.setCode(resultSet.getString("base_currency_code"));
                baseCurrency.setSign(resultSet.getString("base_currency_sign"));

                Currency targetCurrency = new Currency();
                targetCurrency.setId(resultSet.getInt("target_currency_id"));
                targetCurrency.setName(resultSet.getString("target_currency_name"));
                targetCurrency.setCode(resultSet.getString("target_currency_code"));
                targetCurrency.setSign(resultSet.getString("target_currency_sign"));

                Exchange exchange = new Exchange();
                exchange.setId(resultSet.getInt("id"));
                exchange.setBaseCurrency(baseCurrency);
                exchange.setTargetCurrency(targetCurrency);
                exchange.setRate(resultSet.getDouble("rate"));

                exchanges.add(exchange);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка доступа к БД");
            throw e;
        }

        return exchanges;
    }


    public Exchange getExchangeRateByCurrencyPair(String currencyPair) throws SQLException, DataBaseAccessException {
        String baseCurrencyStr = currencyPair.substring(0, 3);
        String targetCurrencyStr = currencyPair.substring(3);

        CurrencyDao currencyDao = new CurrencyDao();
        Optional<CurrencyDto> baseCurrencyDtoOptional = currencyDao.findCurrencyByCode(baseCurrencyStr);
        Optional<CurrencyDto> targetCurrencyDtoOptional = currencyDao.findCurrencyByCode(targetCurrencyStr);

        CurrencyDto baseCurrencyDto = baseCurrencyDtoOptional.get();
        CurrencyDto targetCurrencyDto = targetCurrencyDtoOptional.get();


        Exchange exchange = findExchangeRateByCurrenciesId(baseCurrencyDto.getId(), targetCurrencyDto.getId());
        // Здесь надо переснести DTO в Model
        Currency baseCurrency = currencyFromCurrencyDto(baseCurrencyDto);
        Currency targetCurrency = currencyFromCurrencyDto(targetCurrencyDto);

        exchange.setBaseCurrency(baseCurrency);
        exchange.setTargetCurrency(targetCurrency);

        return exchange;
   }

    private Exchange findExchangeRateByCurrenciesId(int baseCurrencyId, int targetCurrencyId) {
        String query = "SELECT *\n" +
                "FROM exchange_rates\n" +
                "WHERE base_currency_id= ? AND target_currency_id = ? ;";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     query)) {

            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Exchange exchange = new Exchange();
                exchange.setId(resultSet.getInt("id"));
                exchange.setRate(resultSet.getDouble("rate"));

                return exchange;
            }
        } catch (SQLException e){
            System.out.println("SQLException in ExchangeDao findExchangeRateByCurrenciesId()");
        }
        throw new NoSuchElementException();
    }

    public Currency currencyFromCurrencyDto (CurrencyDto currencyDto) {
        Currency currency = new Currency();

        currency.setId(currencyDto.getId());
        currency.setName(currencyDto.getFullName());
        currency.setCode(currencyDto.getCode());
        currency.setSign(currencyDto.getSign());

        return currency;
    }
}
