package ru.discomfortDeliverer.servlets.exchange;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ru.discomfortDeliverer.dto.ExceptionDto;
import ru.discomfortDeliverer.dto.ExchangePostDto;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.exceptions.QueryResultToCurrencyDtoParseException;
import ru.discomfortDeliverer.models.Exchange;
import ru.discomfortDeliverer.service.ExchangeService;
import ru.discomfortDeliverer.servlets.HttpServletConfigurer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeService exchangeService = new ExchangeService();
    private JsonObject errorJsonObj;
    private ExceptionDto exceptionDto;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServletConfigurer.setEncode(req, resp);
        try {
            List<Exchange> exchangeRates = exchangeService.getExchangeRates();

            String json = new Gson().toJson(exchangeRates);
            resp.setStatus(200);
            resp.getWriter().write(json);
            return;
        } catch (SQLException e) {
            errorJsonObj = new JsonObject();
            errorJsonObj.addProperty("message", "Внутренняя ошибка");

            resp.setStatus(500);
            resp.getWriter().write(String.valueOf(errorJsonObj));
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpServletConfigurer.setEncode(req, resp);
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        ExchangePostDto exchangePostDto = new ExchangePostDto();
        exchangePostDto.setBaseCurrencyCode(baseCurrencyCode);
        exchangePostDto.setTargetCurrencyCode(targetCurrencyCode);
        exchangePostDto.setRate(Double.valueOf(rate));

        try {
            Exchange insertedExchange = exchangeService.addExchangeRate(exchangePostDto);

            String json = new Gson().toJson(insertedExchange);

            resp.setStatus(200);
            resp.getWriter().write(json);
        } catch (DataBaseAccessException e) {
            throw new RuntimeException(e);
        } catch (QueryResultToCurrencyDtoParseException e) {
            throw new RuntimeException(e);
        }
    }
}

