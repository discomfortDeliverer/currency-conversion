package ru.discomfortDeliverer.dao;

import ru.discomfortDeliverer.dto.CurrencyDto;
import ru.discomfortDeliverer.dto.ExchangePostDto;
import ru.discomfortDeliverer.dto.ExchangeDto;
import ru.discomfortDeliverer.dto.ExchangeUpdateDto;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.exceptions.QueryResultToCurrencyDtoParseException;
import ru.discomfortDeliverer.mappers.CurrencyMapper;
import ru.discomfortDeliverer.mappers.ExchangeMapper;
import ru.discomfortDeliverer.models.Currency;
import ru.discomfortDeliverer.models.Exchange;
import ru.discomfortDeliverer.models.ExchangeRate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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


    public ExchangeRate getExchangeRateByCurrencyPair(ExchangeUpdateDto exchangeUpdateDto)
            throws SQLException, DataBaseAccessException, QueryResultToCurrencyDtoParseException {

        Currency baseCurrency = findCurrencyByCurrencyCode(exchangeUpdateDto.getBaseCode());
        Currency targetCurrency = findCurrencyByCurrencyCode(exchangeUpdateDto.getTargetCode());


        ExchangeDto exchangeDto = findExchangeDtoByCurrenciesId(baseCurrency.getId(), targetCurrency.getId());
        // Здесь надо переснести DTO в Model
        return ExchangeMapper.exchangeDtoAndCurrenciesToExchange(exchangeDto, baseCurrency, targetCurrency);

   }

    private ExchangeDto findExchangeDtoByCurrenciesId(int baseCurrencyId, int targetCurrencyId) throws SQLException {
        String query = "SELECT *\n" +
                "FROM exchange_rates\n" +
                "WHERE base_currency_id= ? AND target_currency_id = ?";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     query)) {

            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);
            ResultSet resultSet = preparedStatement.executeQuery();

            return ExchangeMapper.queryResultToExchangeDto(resultSet);
        } catch (SQLException e){
            System.out.println("SQLException in ExchangeDao findExchangeRateByCurrenciesId()");
            throw e;
        }
    }

    public Currency currencyFromCurrencyDto (CurrencyDto currencyDto) {
        Currency currency = new Currency();

        currency.setId(currencyDto.getId());
        currency.setName(currencyDto.getFullName());
        currency.setCode(currencyDto.getCode());
        currency.setSign(currencyDto.getSign());

        return currency;
    }

    public Exchange addExchangeRate(ExchangePostDto exchangePostDto)
            throws DataBaseAccessException, QueryResultToCurrencyDtoParseException {
        String query = "INSERT INTO exchange_rates\n" +
                "(base_currency_id, target_currency_id, rate)\n" +
                "VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     query)) {
            Currency baseCurrency = findCurrencyByCurrencyCode(exchangePostDto.getBaseCurrencyCode());
            Currency targetCurrency = findCurrencyByCurrencyCode(exchangePostDto.getTargetCurrencyCode());

            preparedStatement.setInt(1, baseCurrency.getId());
            preparedStatement.setInt(2, targetCurrency.getId());
            preparedStatement.setDouble(3, exchangePostDto.getRate());

            int affectedRows = preparedStatement.executeUpdate();

            // Получаем последний вставленный id
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if(generatedKeys.next()){
                Integer lastInsertedId = generatedKeys.getInt(1);

                // Возвращаем только что вставленную запись
                ExchangeDto exchangeDto = findExchangeDtoById(lastInsertedId);
                Exchange exchange = new Exchange();
                exchange.setId(exchangeDto.getId());
                exchange.setBaseCurrency(baseCurrency);
                exchange.setTargetCurrency(targetCurrency);
                exchange.setRate(exchangeDto.getRate());

                return exchange;
            }
        } catch (SQLException e){
            System.out.println("SQLException in ExchangeDao findExchangeRateByCurrenciesId()");
        }
        throw new RuntimeException();
    }

    public ExchangeRate updateExchangeRate(Exchange exchange) throws DataBaseAccessException, QueryResultToCurrencyDtoParseException, SQLException {
        String query = "UPDATE exchange_rates *\n" +
                "SET rate = ?\n" +
                "WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    query)) {
            // Находим в бд Currency по CurrencyCode (RUB, EUR и т.д.)
            Currency baseCurrency = findCurrencyByCurrencyCode(exchange.getBaseCode());
            Currency targetCurrency = findCurrencyByCurrencyCode(exchange.getTargetCode());

            // Получаем по двум currency_id ExchangeDto
            ExchangeDto exchangeDto = findExchangeDtoByCurrenciesId(baseCurrency.getId(),
                    targetCurrency.getId());

            // Обновляем данные exchangeDto в таблице
            preparedStatement.setDouble(1, exchange.getRate());
            preparedStatement.setInt(2, exchangeDto.getId());

            preparedStatement.executeUpdate();

            // Получаем из БД обновленный exchangeDto
            ExchangeDto updatedExchangeDto = findExchangeDtoById(exchangeDto.getId());

            // Преобразовываем ExchangeDto и Currency в ExchangeRate, оторый быдем возвращать
            return ExchangeMapper.exchangeDtoAndCurrenciesToExchange(updatedExchangeDto, baseCurrency, targetCurrency);
        } catch (QueryResultToCurrencyDtoParseException e) {
            throw e;
        } catch (SQLException e) {
            throw e;
        }
    }

    private ExchangeDto findExchangeDtoById(Integer id) throws SQLException {
        String query = "SELECT *\n" +
                "FROM exchange_rates \n" +
                "WHERE id = ?";

        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    query)) {

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            return ExchangeMapper.queryResultToExchangeDto(resultSet);
        }
    }

    private Currency findCurrencyByCurrencyCode(String code)
            throws QueryResultToCurrencyDtoParseException, SQLException {
        String query = "SELECT *\n" +
                "FROM currency\n" +
                "WHERE code = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     query)) {

            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();

            CurrencyDto currencyDto = CurrencyMapper.queryResultToCurrencyDto(resultSet);

            return CurrencyMapper.currencyDtoToCurrency(currencyDto);

        } catch (SQLException e) {
            throw e;
        }
    }


}
