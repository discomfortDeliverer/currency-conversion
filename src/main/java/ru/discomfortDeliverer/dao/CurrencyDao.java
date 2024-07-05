package ru.discomfortDeliverer.dao;

import ru.discomfortDeliverer.dto.СurrencyDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {
    private String url = "jdbc:sqlite:D:\\SQLite\\currencyConversion.db";
    public List<СurrencyDto> getAllCurrency() throws ClassNotFoundException {
        List<СurrencyDto> currencies = new ArrayList<>();

        Class.forName("org.sqlite.JDBC");

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
}

