package ru.discomfortDeliverer.servlets.exchange;

import ru.discomfortDeliverer.dao.CurrencyDao;
import ru.discomfortDeliverer.exceptions.CurrencyNotFoundException;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.exceptions.ExchangeRateCalculationException;
import ru.discomfortDeliverer.exceptions.QueryResultToCurrencyDtoParseException;
import ru.discomfortDeliverer.models.Currency;
import ru.discomfortDeliverer.models.response.ErrorResponse;
import ru.discomfortDeliverer.models.response.ExchangedRate;
import ru.discomfortDeliverer.servlets.HttpServletConfigurer;
import ru.discomfortDeliverer.util.Validator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ExchangeServlet extends AbstractExchangeServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServletConfigurer.setEncode(req, resp);

        String codeFrom = req.getParameter("from");
        String codeTo = req.getParameter("to");
        String amountStr = req.getParameter("amount");


        if(codeFrom == null || codeFrom.isEmpty()
                || codeTo == null || codeTo.isEmpty()
                || amountStr == null || amountStr.isEmpty()){
            resp.setStatus(400);

            errorResponse = new ErrorResponse(400, "Ошибка, отсутствует поле");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
            return;
        }
        if(!Validator.isValidCurrencyCode(codeFrom) || !Validator.isValidCurrencyCode(codeTo)){
            resp.setStatus(400);

            errorResponse = new ErrorResponse(400, "Ошибка, отсутствует поле или поле в неправильной форме");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
            return;
        }
        Double amount;
        try{
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e){
            resp.setStatus(400);

            errorResponse = new ErrorResponse(400, "Ошибка, число в поле amount в неверном формате");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
            return;
        }

        try {
            ExchangedRate exchangedRate = new ExchangedRate();
            CurrencyDao currencyDao = new CurrencyDao();
            Currency baseCurrency = currencyDao.findCurrencyByCode(codeFrom);
            Currency targetCurrency = currencyDao.findCurrencyByCode(codeTo);

            exchangedRate.setBaseCurrency(baseCurrency);
            exchangedRate.setTargetCurrency(targetCurrency);

            ExchangedRate converted = exchangeService.convert(codeFrom, codeTo, amount);

            resp.setStatus(200);
            resp.getWriter().write(jsonParser.toJson(converted));

        } catch (DataBaseAccessException | SQLException e) {
            resp.setStatus(500);
            errorResponse = new ErrorResponse(500, "Ошибка, база данных недоступна");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        } catch (QueryResultToCurrencyDtoParseException e) {
            throw new RuntimeException(e);
        } catch (ExchangeRateCalculationException e) {
            resp.setStatus(500);
            errorResponse = new ErrorResponse(500, "Ошибка, конвертации");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        } catch (CurrencyNotFoundException e) {
            resp.setStatus(404);
            errorResponse = new ErrorResponse(404, "Ошибка, валюта не найдена");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        }

    }
}
