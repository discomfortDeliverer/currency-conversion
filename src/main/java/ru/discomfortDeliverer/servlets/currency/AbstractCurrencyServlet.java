package ru.discomfortDeliverer.servlets.currency;

import com.google.gson.Gson;
import ru.discomfortDeliverer.models.response.ErrorResponse;
import ru.discomfortDeliverer.service.CurrencyService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractCurrencyServlet extends HttpServlet {
    protected CurrencyService currencyService = new CurrencyService();
    protected Gson jsonParser = new Gson();
    protected ErrorResponse errorResponse;
}
