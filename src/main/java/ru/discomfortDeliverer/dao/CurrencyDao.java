package ru.discomfortDeliverer.dao;

import ru.discomfortDeliverer.dto.СurrencyDto;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;

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
    public List<СurrencyDto> getAllCurrency() {
        List<СurrencyDto> currencies = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM Currency")){
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                СurrencyDto сurrencyDto = new СurrencyDto();
                сurrencyDto.setId(resultSet.getInt("id"));
                сurrencyDto.setCode(resultSet.getString("code"));
                сurrencyDto.setFullName(resultSet.getString("fullname"));
                сurrencyDto.setSign(resultSet.getString("sign"));

                currencies.add(сurrencyDto);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка доступа к БД");
            e.printStackTrace();
        }

        return currencies;
    }

    public Optional<СurrencyDto> findCurrencyByCode(String code) throws DataBaseAccessException {
        String query = "select * from Currency where code = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     query)){
            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                СurrencyDto сurrencyDto = new СurrencyDto();
                сurrencyDto.setId(resultSet.getInt("id"));
                сurrencyDto.setCode(resultSet.getString("code"));
                сurrencyDto.setFullName(resultSet.getString("fullname"));
                сurrencyDto.setSign(resultSet.getString("sign"));

                return Optional.of(сurrencyDto);
            }
        } catch (SQLException e) {
            throw new DataBaseAccessException("Ошибка доступа к базе данных", e);
        }
        return Optional.empty();
    }
}

