package ru.discomfortDeliverer.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ru.discomfortDeliverer.models.Exchange;
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
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try{
            List<Exchange> exchangeRates = exchangeService.getExchangeRates();

            String json = new Gson().toJson(exchangeRates);
            resp.setStatus(200);
            resp.getWriter().write(json);
        } catch (SQLException e){
            errorJsonObj = new JsonObject();
            errorJsonObj.addProperty("message", "Внутренняя ошибка");

            resp.setStatus(500);
            resp.getWriter().write(String.valueOf(errorJsonObj));
        }


    }
}
