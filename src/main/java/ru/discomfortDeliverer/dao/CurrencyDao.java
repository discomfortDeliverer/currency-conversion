package ru.discomfortDeliverer.dao;

import ru.discomfortDeliverer.dto.CurrencyDto;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.exceptions.FieldAlreadyExistException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao {
    private String url = "jdbc:sqlite:D:\\SQLite\\currencyConversion.db";

    public CurrencyDao(){
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public List<CurrencyDto> getAllCurrency() {
        List<CurrencyDto> currencies = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM currency")){
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){

                CurrencyDto currencyDto = new CurrencyDto();
                currencyDto.setId(resultSet.getInt("id"));
                currencyDto.setCode(resultSet.getString("code"));
                currencyDto.setFullName(resultSet.getString("full_name"));
                currencyDto.setSign(resultSet.getString("sign"));

                currencies.add(currencyDto);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка доступа к БД");
            e.printStackTrace();
        }

        return currencies;
    }

    public Optional<CurrencyDto> findCurrencyByCode(String code) throws DataBaseAccessException {
        String query = "select * from currency where code = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     query)){
            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                CurrencyDto currencyDto = new CurrencyDto();
                currencyDto.setId(resultSet.getInt("id"));
                currencyDto.setCode(resultSet.getString("code"));
                currencyDto.setFullName(resultSet.getString("full_name"));
                currencyDto.setSign(resultSet.getString("sign"));

                return Optional.of(currencyDto);
            }
        } catch (SQLException e) {
            throw new DataBaseAccessException("Ошибка доступа к базе данных", e);
        }
        return Optional.empty();
    }

    public Optional<CurrencyDto> addCurrencyIntoDataBase(CurrencyDto currencyDto) throws DataBaseAccessException, FieldAlreadyExistException {
        String query = "INSERT INTO currency (full_name, code, sign) VALUES (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     query)){
            preparedStatement.setString(1, currencyDto.getFullName());
            preparedStatement.setString(2, currencyDto.getCode());
            preparedStatement.setString(3, currencyDto.getSign());

            int resultSet = preparedStatement.executeUpdate();

            System.out.println("Результат добавления в бд: " + resultSet);

            // Берем из базы данных вновь созданный объект
            return findCurrencyByCode(currencyDto.getCode());

        } catch (SQLException e) {
            String errorMessage = e.getMessage();
            System.out.println(errorMessage);
            if(errorMessage.contains("SQL error or missing database")){
                throw new DataBaseAccessException(e.getMessage(), e);
            } else if (errorMessage.contains("A UNIQUE constraint failed")) {
                throw new FieldAlreadyExistException(e.getMessage(), e);
            }
            throw new DataBaseAccessException(e.getMessage(), e);
        }
    }
}

