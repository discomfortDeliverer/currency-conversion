package ru.discomfortDeliverer.servlets;

import com.google.gson.Gson;
import ru.discomfortDeliverer.dto.СurrencyDto;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.service.CurrencyService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


public class CurrencyServlet extends HttpServlet {
    private CurrencyService currencyService = new CurrencyService();
    // Получаем список всех валют
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getPathInfo();
        System.out.println("getPathInfo: " + req.getPathInfo());
        System.out.println("getServletPath: " + req.getServletPath());

        String pathInfo = req.getPathInfo(); // /currencies/HERE
        String servletPath = req.getServletPath(); // /currencies

        if(servletPath.equals("/currencies")){
            List<СurrencyDto> allCurrencies;
            try {
                allCurrencies = currencyService.getAllCurrencies();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                resp.setStatus(500);
                resp.getWriter().write("Ошибка, база данных недоступна");
                return;
            }

            String json = new Gson().toJson(allCurrencies);
            resp.setStatus(200);
            resp.getWriter().write(json);
            return;
        }

        if(servletPath.equals("/currency")){
            if(pathInfo == null){
                resp.setStatus(400);
                resp.getWriter().write("Ошибка, отсутствует код валюты");
                return;
            }
            // Убираем '/'
            String code = pathInfo.substring(1);

            try {
                Optional<СurrencyDto> currencyByCode = currencyService.getCurrencyByCode(code);

                if (currencyByCode.isPresent()){
                    resp.setStatus(200);
                    String json = new Gson().toJson(currencyByCode.get());
                    resp.getWriter().write(json);
                } else {
                    resp.setStatus(404);
                    resp.getWriter().write("Валюта не найдена");
                }

            } catch (DataBaseAccessException e) {
                resp.setStatus(500);
                resp.getWriter().write("Ошибка, база данных недоступна");
            }
        }
    }
}
