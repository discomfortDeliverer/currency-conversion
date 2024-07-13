package ru.discomfortDeliverer.dao;

import ru.discomfortDeliverer.exceptions.*;
import ru.discomfortDeliverer.models.Currency;
import ru.discomfortDeliverer.models.ExchangeRate;
import ru.discomfortDeliverer.models.response.ExchangedRate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeDao {
    private String url = "jdbc:sqlite::resource:currencyConversion.db";

    public ExchangeDao() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ExchangeRate> findAllExchangeRates() throws DataBaseAccessException {
        List<ExchangeRate> exchanges = new ArrayList<>();

        String query = "SELECT \n" +
                "    er.id,\n" +
                "    er.rate,\n" +
                "    bc.id AS base_currency_id,\n" +
                "    bc.code AS base_currency_code,\n" +
                "    bc.full_name AS base_currency_name,\n" +
                "    bc.sign AS base_currency_sign,\n" +
                "    tc.id AS target_currency_id,\n" +
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

                ExchangeRate exchangeRate = new ExchangeRate();
                exchangeRate.setId(resultSet.getInt("id"));
                exchangeRate.setBaseCurrency(baseCurrency);
                exchangeRate.setTargetCurrency(targetCurrency);
                exchangeRate.setRate(resultSet.getDouble("rate"));

                exchanges.add(exchangeRate);
            }
        } catch (SQLException e) {
            throw new DataBaseAccessException(e.getMessage(), e);
        }
        return exchanges;
    }

   public ExchangeRate findExchangeRateByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode)
           throws DataBaseAccessException, ExchangeRateNotFoundException {
       String query = "SELECT \n" +
               "    er.id,\n" +
               "    er.rate,\n" +
               "    bc.id AS base_currency_id,\n" +
               "    bc.code AS base_currency_code,\n" +
               "    bc.full_name AS base_currency_name,\n" +
               "    bc.sign AS base_currency_sign,\n" +
               "    tc.id AS target_currency_id,\n" +
               "    tc.code AS target_currency_code,\n" +
               "    tc.full_name AS target_currency_name,\n" +
               "    tc.sign AS target_currency_sign\n" +
               "FROM exchange_rates AS er\n" +
               "JOIN currency AS bc\n" +
               "ON er.base_currency_id = bc.id\n" +
               "JOIN currency AS tc\n" +
               "ON er.target_currency_id = tc.id\n" +
               "WHERE bc.code = ? AND tc.code = ?";

       try (Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    query)){
           preparedStatement.setString(1, baseCurrencyCode);
           preparedStatement.setString(2, targetCurrencyCode);

           ResultSet resultSet = preparedStatement.executeQuery();
           if(resultSet.next()){
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

               ExchangeRate exchangeRate = new ExchangeRate();
               exchangeRate.setId(resultSet.getInt("id"));
               exchangeRate.setBaseCurrency(baseCurrency);
               exchangeRate.setTargetCurrency(targetCurrency);
               exchangeRate.setRate(resultSet.getDouble("rate"));

               return exchangeRate;
           }

            throw new ExchangeRateNotFoundException();
       } catch (SQLException e) {
           throw new DataBaseAccessException(e.getMessage(), e);
       }
   }

    public Integer updateExchangeRate(int baseCurrencyId, int targetCurrencyId, Double rate)
            throws DataBaseAccessException {
        String query = "UPDATE exchange_rates\n" +
                "SET rate = ?\n" +
                "WHERE base_currency_id = ? AND target_currency_id = ?";

        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setDouble(1, rate);
            preparedStatement.setDouble(2, baseCurrencyId);
            preparedStatement.setDouble(3, targetCurrencyId);

            int resultSet = preparedStatement.executeUpdate();

            ResultSet savedExchangeRate = preparedStatement.getGeneratedKeys();
            savedExchangeRate.next();

            return savedExchangeRate.getInt(1);

        } catch (SQLException e) {
            throw new DataBaseAccessException(e.getMessage(), e);
        }
    }

    public ExchangedRate convert(String codeFrom, String codeTo, Double amount)
            throws SQLException, ExchangeRateCalculationException, DataBaseAccessException, CurrencyNotFoundException {
        CurrencyDao currencyDao = new CurrencyDao();

        Currency from = currencyDao.findCurrencyByCode(codeFrom);
        Currency to = currencyDao.findCurrencyByCode(codeTo);

        String query = "SELECT *\n" +
                "FROM exchange_rates \n" +
                "WHERE base_currency_id = ? AND target_currency_id = ?";

        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    query)) {

            preparedStatement.setInt(1, from.getId());
            preparedStatement.setInt(2, to.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            ExchangedRate exchangedRate = new ExchangedRate();
            exchangedRate.setAmount(amount);

            // Если в таблице существует валютная пара AB
            if(resultSet.next()){
                exchangedRate.setBaseCurrency(from);
                exchangedRate.setTargetCurrency(to);
                exchangedRate.setRate(resultSet.getDouble("rate"));

                Double convertedAmount = exchangedRate.getRate() * exchangedRate.getAmount();
                Double roundedConvertedAmount = Math.round(convertedAmount * 100.0) / 100.0;
                exchangedRate.setConvertedAmount(roundedConvertedAmount);
                return exchangedRate;
            }

            // Если в таблице существует валютная пара BA
            preparedStatement.setInt(1, to.getId());
            preparedStatement.setInt(2, from.getId());

            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                exchangedRate.setBaseCurrency(from);
                exchangedRate.setTargetCurrency(to);
                Double rate = resultSet.getDouble("rate");

                // Округляем
                rate = 1 / rate;
                Double roundedRate = Math.round(rate * 100.0) / 100.0;
                exchangedRate.setRate(rate);


                Double convertedAmount = exchangedRate.getRate() * exchangedRate.getAmount();
                Double roundedConvertedAmount = Math.round(convertedAmount * 100.0) / 100.0;
                exchangedRate.setConvertedAmount(roundedConvertedAmount);
                return exchangedRate;
            }

            // Если в таблице существуют валютные пары USD-A и USD-B
            Currency usd = currencyDao.findCurrencyByCode("USD");
            preparedStatement.setInt(1, usd.getId());
            preparedStatement.setInt(2, from.getId());

            resultSet = preparedStatement.executeQuery();
            Double fromRate = null;
            if (resultSet.next()){
                fromRate = resultSet.getDouble("rate");
            }

            preparedStatement.setInt(1, usd.getId());
            preparedStatement.setInt(2, to.getId());

            resultSet = preparedStatement.executeQuery();
            Double toRate = null;
            if (resultSet.next()){
                toRate = resultSet.getDouble("rate");
            }

            if(fromRate != null && toRate != null){
                exchangedRate.setBaseCurrency(from);
                exchangedRate.setTargetCurrency(to);

                Double roundedRate = Math.round((toRate / fromRate) * 1000.0) / 1000.0;
                exchangedRate.setRate(roundedRate);

                Double convertedAmount = exchangedRate.getRate() * exchangedRate.getAmount();
                Double roundedConvertedAmount = Math.round(convertedAmount * 100.0) / 100.0;
                exchangedRate.setConvertedAmount(roundedConvertedAmount);
                return exchangedRate;
            }

            throw new ExchangeRateCalculationException();
        }
    }

    public Integer saveExchangeRate(Integer baseCurrencyId, Integer targetCurrencyId, double rate)
            throws DataBaseAccessException, FieldAlreadyExistException {
        String query = "INSERT INTO exchange_rates\n" +
                "(base_currency_id, target_currency_id, rate)\n" +
                "VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     query)) {

            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);
            preparedStatement.setDouble(3, rate);

            int affectedRows = preparedStatement.executeUpdate();

            // Получаем последний вставленный id
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if(generatedKeys.next()){
                Integer lastInsertedId = generatedKeys.getInt(1);

                // Возвращаем только id вставленной записи
                return lastInsertedId;
            } else throw new SQLException();
        } catch (SQLException e){
            if(e.getMessage().contains("A UNIQUE constraint failed")){
                throw new FieldAlreadyExistException(e.getMessage(), e);
            }
            throw new DataBaseAccessException(e.getMessage(), e);
        }
    }
}
