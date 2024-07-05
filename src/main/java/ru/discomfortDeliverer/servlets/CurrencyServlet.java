package ru.discomfortDeliverer.servlets;

import com.google.gson.Gson;
import ru.discomfortDeliverer.service.CurrencyService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CurrencyServlet extends HttpServlet {
    private CurrencyService currencyService = new CurrencyService();
    // Получаем список всех валют
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getPathInfo();

        System.out.println(req.getServletPath());

        resp.getWriter().write(currencyService.getAllCurrencies());
    }
}
