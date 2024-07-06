package ru.discomfortDeliverer.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ru.discomfortDeliverer.dto.CurrencyDto;
import ru.discomfortDeliverer.exceptions.DataBaseAccessException;
import ru.discomfortDeliverer.exceptions.FieldAlreadyExistException;
import ru.discomfortDeliverer.service.CurrencyService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class CurrencyServlet extends HttpServlet {
    private CurrencyService currencyService = new CurrencyService();
    private JsonObject errorJsonObj;
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
            List<CurrencyDto> allCurrencies;
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
                Optional<CurrencyDto> currencyByCode = currencyService.getCurrencyByCode(code);

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String name =req.getParameter("name");
        String code =req.getParameter("code");
        String sign =req.getParameter("sign");

        // Проверяем заполнены ли поля
        if(name == null || name.trim().isEmpty()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Name is required");
            return;
        }
        if(code == null || code.trim().isEmpty()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Code is require");
            return;
        }
        if(sign == null || sign.trim().isEmpty()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sign is require");
            return;
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            Optional<CurrencyDto> currency = currencyService.createCurrency(name, code, sign);

            String sql = new Gson().toJson(currency.get());
            resp.setStatus(201);
            resp.getWriter().write(sql);
        } catch (DataBaseAccessException e){
            errorJsonObj = new JsonObject();
            errorJsonObj.addProperty("message", "База данных недоступна");

            resp.setStatus(500);
            resp.getWriter().write(String.valueOf(errorJsonObj));
        } catch (FieldAlreadyExistException e){
            errorJsonObj = new JsonObject();
            errorJsonObj.addProperty("message", "Валюта с таким кодом уже существует");

            resp.setStatus(409);
            resp.getWriter().write(String.valueOf(errorJsonObj));
        }

    }
}
