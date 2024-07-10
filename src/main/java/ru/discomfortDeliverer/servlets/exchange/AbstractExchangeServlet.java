package ru.discomfortDeliverer.servlets.exchange;

import com.google.gson.Gson;
import ru.discomfortDeliverer.models.response.ErrorResponse;
import ru.discomfortDeliverer.service.CurrencyService;
import ru.discomfortDeliverer.service.ExchangeService;

import javax.servlet.http.HttpServlet;

public abstract class AbstractExchangeServlet extends HttpServlet {
    protected ExchangeService exchangeService = new ExchangeService();
    protected Gson jsonParser = new Gson();
    protected ErrorResponse errorResponse;
}
