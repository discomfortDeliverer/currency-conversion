package ru.discomfortDeliverer.servlets.currency;

import com.google.gson.JsonObject;
import ru.discomfortDeliverer.exceptions.CurrencyNotFoundException;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.models.Currency;
import ru.discomfortDeliverer.models.response.ErrorResponse;
import ru.discomfortDeliverer.servlets.HttpServletConfigurer;
import ru.discomfortDeliverer.util.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



public class CurrencyServlet extends AbstractCurrencyServlet {
    private JsonObject errorJsonObj;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpServletConfigurer.setEncode(req, resp);

        String code = req.getPathInfo().substring(1);
        if(!Validator.isValidCurrencyCode(code)){
            resp.setStatus(400);

            errorResponse = new ErrorResponse(400, "Ошибка, отсутствует код валюты");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
            return;
        }

        try {
            Currency currency = currencyService.getCurrencyByCode(code);

            resp.setStatus(200);
            resp.getWriter().write(jsonParser.toJson(currency));

        } catch (DataBaseAccessException e) {
            resp.setStatus(500);

            errorResponse = new ErrorResponse(500, "Ошибка, база данных недоступна");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        } catch (CurrencyNotFoundException e){
            resp.setStatus(404);

            errorResponse = new ErrorResponse(404, "Валюта не найдена");
            resp.getWriter().write(jsonParser.toJson(errorResponse));
        }
    }
}
