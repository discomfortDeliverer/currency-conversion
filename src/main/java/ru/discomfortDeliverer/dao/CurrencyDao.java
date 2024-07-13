package ru.discomfortDeliverer.dao;

import ru.discomfortDeliverer.exceptions.CurrencyNotFoundException;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.exceptions.FieldAlreadyExistException;
import ru.discomfortDeliverer.models.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {
    private String url = "jdbc:sqlite::resource:currencyConversion.db";

    public CurrencyDao(){
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Currency> getAllCurrency() throws DataBaseAccessException {
        List<Currency> currencies = new ArrayList<>();
        String sql = "SELECT * FROM currency";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     sql)){
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                currencies.add(getCurrency(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new DataBaseAccessException("Ошибка доступа к базе данных", e);
        }
    }

    public Currency findCurrencyByCode(String code)
            throws DataBaseAccessException, CurrencyNotFoundException {
        String query = "select * from currency where code = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     query)){
            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return getCurrency(resultSet);
            }
            throw new CurrencyNotFoundException();
        } catch (SQLException e) {
            throw new DataBaseAccessException("Ошибка доступа к базе данных", e);
        }
    }

    public Integer addCurrencyIntoDataBase(Currency currencyDto)
            throws DataBaseAccessException, FieldAlreadyExistException {
        String query = "INSERT INTO currency (full_name, code, sign) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     query, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, currencyDto.getName());
            preparedStatement.setString(2, currencyDto.getCode());
            preparedStatement.setString(3, currencyDto.getSign());

            int resultSet = preparedStatement.executeUpdate();

            ResultSet savedCurrency = preparedStatement.getGeneratedKeys();
            savedCurrency.next();
            Integer savedCurrencyId = savedCurrency.getInt(1);
            return savedCurrencyId;

        } catch (SQLException e) {
            String errorMessage = e.getMessage();

            if (errorMessage.contains("A UNIQUE constraint failed")) {
                throw new FieldAlreadyExistException(e.getMessage(), e);
            }
            throw new DataBaseAccessException(e.getMessage(), e);
        }
    }

    private Currency getCurrency(ResultSet resultSet) throws SQLException {
        Currency currency = new Currency();
        currency.setId(resultSet.getInt("id"));
        currency.setName(resultSet.getString("full_name"));
        currency.setCode(resultSet.getString("code"));
        currency.setSign(resultSet.getString("sign"));
        return currency;
    }
}

