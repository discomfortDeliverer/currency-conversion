package ru.discomfortDeliverer.servlets.exchange;

import ru.discomfortDeliverer.dao.CurrencyDao;
import ru.discomfortDeliverer.exceptions.CurrencyNotFoundException;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.exceptions.FieldAlreadyExistException;
import ru.discomfortDeliverer.models.Currency;
import ru.discomfortDeliverer.models.ExchangeRate;
import ru.discomfortDeliverer.models.response.ErrorResponse;
import ru.discomfortDeliverer.servlets.HttpServletConfigurer;
import ru.discomfortDeliverer.util.Validator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ExchangeRatesServlet extends AbstractExchangeServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletConfigurer.setEncode(req, resp);
        try {
            List<ExchangeRate> exchangeRates = exchangeService.findAllExchangeRates();

            resp.setStatus(200);
            resp.getWriter().write(jsonParser.toJson(exchangeRates));
        } catch (DataBaseAccessException e) {
            resp.setStatus(500);

            errorResponse = new ErrorResponse(500, "Ошибка, база данных недоступна");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpServletConfigurer.setEncode(req, resp);

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateStr = req.getParameter("rate");


        if(!Validator.isValidCurrencyCode(baseCurrencyCode) || !Validator.isValidCurrencyCode(targetCurrencyCode) ||
                baseCurrencyCode == null || targetCurrencyCode == null || rateStr == null ||
                baseCurrencyCode.isEmpty() || targetCurrencyCode.isEmpty() || rateStr.isEmpty()){
            resp.setStatus(400);

            errorResponse = new ErrorResponse(400, "Ошибка, отсутствует поле или поле в неправильной форме");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
            return;
        }
        double rate = 0.0;
        try{
            rate = Double.parseDouble(rateStr);
        } catch (NumberFormatException e){
            resp.setStatus(400);

            errorResponse = new ErrorResponse(400, "Ошибка, число в поле rate в неверном формате");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
            return;
        }

        try {
            CurrencyDao currencyDao = new CurrencyDao();
            Currency baseCurrency = currencyDao.findCurrencyByCode(baseCurrencyCode);
            Currency targetCurrency = currencyDao.findCurrencyByCode(targetCurrencyCode);

            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setBaseCurrency(baseCurrency);
            exchangeRate.setTargetCurrency(targetCurrency);
            exchangeRate.setRate(rate);

            Integer savedId = exchangeService.saveExchangeRate(baseCurrency.getId(), targetCurrency.getId(), rate);

            exchangeRate.setId(savedId);

            resp.setStatus(201);
            resp.getWriter().write(jsonParser.toJson(exchangeRate));
        } catch (DataBaseAccessException e) {
            resp.setStatus(500);
            errorResponse = new ErrorResponse(500, "Ошибка, база данных недоступна");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        } catch (CurrencyNotFoundException e) {
            resp.setStatus(404);
            errorResponse = new ErrorResponse(404, "Ошибка, одна или обе валюты не существуют в БД");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        } catch (FieldAlreadyExistException e) {
            resp.setStatus(409);
            errorResponse = new ErrorResponse(409, "Ошибка, такая валютная пара уже существует");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        }
    }
}


