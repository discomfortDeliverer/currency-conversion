package ru.discomfortDeliverer.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ru.discomfortDeliverer.dto.ExceptionDto;
import ru.discomfortDeliverer.dto.ExchangePostDto;
import ru.discomfortDeliverer.dto.ExchangeUpdateDto;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.exceptions.QueryResultToCurrencyDtoParseException;
import ru.discomfortDeliverer.models.Exchange;
import ru.discomfortDeliverer.models.ExchangeRate;
import ru.discomfortDeliverer.service.ExchangeService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ExchangeServlet extends HttpServlet {
    private ExchangeService exchangeService = new ExchangeService();
    private JsonObject errorJsonObj;
    private ExceptionDto exceptionDto;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo(); // /currencies/HERE
        String servletPath = req.getServletPath(); // /currencies

        if(servletPath.equals("/exchangeRates")){
            try{
                List<Exchange> exchangeRates = exchangeService.getExchangeRates();

                String json = new Gson().toJson(exchangeRates);
                resp.setStatus(200);
                resp.getWriter().write(json);
                return;
            } catch (SQLException e){
                errorJsonObj = new JsonObject();
                errorJsonObj.addProperty("message", "Внутренняя ошибка");

                resp.setStatus(500);
                resp.getWriter().write(String.valueOf(errorJsonObj));
                return;
            }
        }

        if(servletPath.equals("/exchangeRate")){
            String currencyPair = pathInfo.substring(1);

            ExchangeUpdateDto exchangeUpdateDto = new ExchangeUpdateDto();
            exchangeUpdateDto.setBaseCode(currencyPair.substring(0, 3));
            exchangeUpdateDto.setTargetCode(currencyPair.substring(3));

            try {
                ExchangeRate exchangeRate = exchangeService.getExchangeRateByCurrencyPair(exchangeUpdateDto);
                String json = new Gson().toJson(exchangeRate);

                resp.setStatus(200);
                resp.getWriter().write(json);
                return;
            } catch (SQLException e) {
                exceptionDto.setStatusCode(404);
                exceptionDto.setMessage("Обменный курс указанной пары не найден");

                resp.setStatus(404);
                resp.getWriter().write(String.valueOf(exceptionDto));
            } catch (DataBaseAccessException e) {
                throw new RuntimeException(e);
            } catch (QueryResultToCurrencyDtoParseException e) {
                throw new RuntimeException(e);
            }
        }

        if(servletPath.equals("/exchange")){
            String from = req.getParameter("from");
            String to = req.getParameter("to");
            Double amount = Double.valueOf(req.getParameter("amount"));
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
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

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Double rate = Double.valueOf(req.getParameter("rate"));

        String pathInfo = req.getPathInfo();
        String currencyPair = pathInfo.substring(1);
        String baseCode = currencyPair.substring(0, 3);
        String targetCode = currencyPair.substring(3);

        ExchangeUpdateDto exchangeUpdateDto = new ExchangeUpdateDto();
        exchangeUpdateDto.setBaseCode(baseCode);
        exchangeUpdateDto.setTargetCode(targetCode);
        exchangeUpdateDto.setRate(rate);

        exceptionDto = new ExceptionDto();
        try {
            ExchangeRate exchangeRate = exchangeService.updateExchangeRate(exchangeUpdateDto);

            String json = new Gson().toJson(exchangeRate);

            resp.setStatus(200);
            resp.getWriter().write(json);
        } catch (DataBaseAccessException e) {
            exceptionDto.setStatusCode(500);
            exceptionDto.setMessage("База данных недоступна");

            resp.setStatus(500);
            resp.getWriter().write(String.valueOf(exceptionDto));
        } catch (SQLException e) {
            exceptionDto.setStatusCode(500);
            exceptionDto.setMessage("Ошибка в SQL запросе");

            resp.setStatus(500);
            resp.getWriter().write(String.valueOf(exceptionDto));
        } catch (QueryResultToCurrencyDtoParseException e) {
            exceptionDto.setStatusCode(500);
            exceptionDto.setMessage("Ошибка парсинга данных из БД в CurrencyDto");

            resp.setStatus(500);
            resp.getWriter().write(String.valueOf(exceptionDto));
        }

    }
}
