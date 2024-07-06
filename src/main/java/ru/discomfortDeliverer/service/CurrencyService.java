package ru.discomfortDeliverer.service;

import ru.discomfortDeliverer.dao.CurrencyDao;
import ru.discomfortDeliverer.dto.CurrencyDto;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.exceptions.FieldAlreadyExistException;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrencyService {
    private CurrencyDao currencyDao = new CurrencyDao();
    public List<CurrencyDto> getAllCurrencies() throws ClassNotFoundException {
        List<CurrencyDto> allCurrency = null;

        allCurrency = currencyDao.getAllCurrency();

        return allCurrency;
    }

    public Optional<CurrencyDto> getCurrencyByCode(String code) throws DataBaseAccessException {
        return currencyDao.findCurrencyByCode(code);
    }

    public Optional<CurrencyDto> createCurrency(String name, String code, String sign)
            throws DataBaseAccessException, FieldAlreadyExistException {

        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setFullName(name);
        currencyDto.setCode(code);
        currencyDto.setSign(sign);

        // Пытаемся добавить объект в базу данных
        return currencyDao.addCurrencyIntoDataBase(currencyDto);


    }

    private Map<String, String> parseRequestBody(String requestBody){
        return Arrays.stream(requestBody.split("&"))
                .map(field -> field.split("="))
                .collect(Collectors.toMap(
                        pair -> pair[0],
                        pair -> URLDecoder.decode(pair[1])
                ));
    }

}
