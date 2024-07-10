package ru.discomfortDeliverer.servlets.exchange;

import com.google.gson.Gson;
import ru.discomfortDeliverer.dao.CurrencyDao;
import ru.discomfortDeliverer.exceptions.CurrencyNotFoundException;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.exceptions.ExchangeRateNotFoundException;
import ru.discomfortDeliverer.exceptions.QueryResultToCurrencyDtoParseException;
import ru.discomfortDeliverer.models.Currency;
import ru.discomfortDeliverer.models.ExchangeRate;
import ru.discomfortDeliverer.models.response.ErrorResponse;
import ru.discomfortDeliverer.servlets.HttpServletConfigurer;
import ru.discomfortDeliverer.util.Validator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ExchangeRateServlet extends AbstractExchangeServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpServletConfigurer.setEncode(req, resp);
        String pathInfo = req.getPathInfo();

        String currencyPairCodes = pathInfo.substring(1);

        if(!Validator.isValidCurrencyPairCodes(currencyPairCodes)){
            resp.setStatus(404);

            errorResponse = new ErrorResponse(404, "Ошибка, неверный код валютной пары");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        }


        String baseCurrencyCode = currencyPairCodes.substring(0, 3);
        String targetCurrencyCode = currencyPairCodes.substring(3);

        try {
            ExchangeRate exchangeRate = exchangeService.getExchangeRateByCurrencyPairCodes(baseCurrencyCode, targetCurrencyCode);
            String json = new Gson().toJson(exchangeRate);

            resp.setStatus(200);
            resp.getWriter().write(json);
            return;
        } catch (SQLException | ExchangeRateNotFoundException e) {
            resp.setStatus(404);
            errorResponse = new ErrorResponse(404, "Ошибка, обменный курс указанной пары не найден");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        } catch (DataBaseAccessException e) {
            resp.setStatus(500);
            errorResponse = new ErrorResponse(500, "Ошибка, база данных недоступна");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        } catch (QueryResultToCurrencyDtoParseException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String method = req.getMethod();
        if(method.equals("PATCH")){
            doPatch(req, res);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletConfigurer.setEncode(req, resp);
        String rateStr = req.getParameter("rate");

        String pathInfo = req.getPathInfo();
        String currencyPair = pathInfo.substring(1);
        String baseCurrencyCode = currencyPair.substring(0, 3);
        String targetCurrencyCode = currencyPair.substring(3);

        if(!Validator.isValidCurrencyCode(baseCurrencyCode) || !Validator.isValidCurrencyCode(targetCurrencyCode)){
            resp.setStatus(400);

            errorResponse = new ErrorResponse(400, "Ошибка, коды валют указаны в неверной форме");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
            return;
        }
        if(rateStr == null || rateStr.isEmpty()) {
            resp.setStatus(400);

            errorResponse = new ErrorResponse(400, "Ошибка, отсутсвует поле rate");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
            return;
        }

        Double rate;
        try{
            rate = Double.parseDouble(rateStr);
        } catch (NumberFormatException e){
            resp.setStatus(400);

            errorResponse = new ErrorResponse(400, "Ошибка, параметр rate указан в неверном формате");
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

            Integer updatedExchangeRateId = exchangeService.updateExchangeRate(baseCurrency.getId(), targetCurrency.getId(), rate);
            exchangeRate.setId(updatedExchangeRateId);

            resp.setStatus(200);
            resp.getWriter().write(jsonParser.toJson(exchangeRate));
        } catch (DataBaseAccessException e) {
            resp.setStatus(500);
            errorResponse = new ErrorResponse(500, "Ошибка, база данных недоступна");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        } catch (CurrencyNotFoundException e) {
            resp.setStatus(404);
            errorResponse = new ErrorResponse(404, "Ошибка, валютная пара отсутствует в БД");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        }

    }
}
