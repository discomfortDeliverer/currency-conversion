package ru.discomfortDeliverer.servlets.currency;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ru.discomfortDeliverer.dto.CurrencyDto;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.exceptions.FieldAlreadyExistException;
import ru.discomfortDeliverer.models.Currency;
import ru.discomfortDeliverer.models.response.ErrorResponse;
import ru.discomfortDeliverer.service.CurrencyService;
import ru.discomfortDeliverer.servlets.HttpServletConfigurer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CurrenciesServlet extends AbstractCurrencyServlet {
    private JsonObject errorJsonObj;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpServletConfigurer.setEncode(req, resp);

        try {
            List<Currency> allCurrencies = currencyService.getAllCurrencies();

            resp.setStatus(200);
            resp.getWriter().write(jsonParser.toJson(allCurrencies));
        } catch (DataBaseAccessException e) {
            resp.setStatus(500);

            errorResponse = new ErrorResponse(500, "Ошибка, база данных недоступна");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpServletConfigurer.setEncode(req, resp);

        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        // Проверяем заполнены ли поля
        if(name == null || name.trim().isEmpty()){
            resp.setStatus(400);

            errorResponse = new ErrorResponse(400, "Ошибка, отсутствует поле name");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
            return;
        }
        if(code == null || code.trim().isEmpty()){
            resp.setStatus(400);

            errorResponse = new ErrorResponse(400, "Ошибка, отсутствует поле code");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
            return;
        }
        if(sign == null || sign.trim().isEmpty()){
            resp.setStatus(400);

            errorResponse = new ErrorResponse(400, "Ошибка, отсутствует поле sign");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
            return;
        }

        try {
            Currency currency = new Currency();
            currency.setName(name);
            currency.setCode(code);
            currency.setSign(sign);

            int generatedId = currencyService.createCurrency(currency);
            currency.setId(generatedId);

            resp.setStatus(201);
            resp.getWriter().write(jsonParser.toJson(currency));
        } catch (DataBaseAccessException e){
            resp.setStatus(500);

            errorResponse = new ErrorResponse(500, "Ошибка, база данных недоступна");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        } catch (FieldAlreadyExistException e){
            resp.setStatus(409);

            errorResponse = new ErrorResponse(409, "Ошибка, валюта с таким кодом уже существует");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        }

    }
}
