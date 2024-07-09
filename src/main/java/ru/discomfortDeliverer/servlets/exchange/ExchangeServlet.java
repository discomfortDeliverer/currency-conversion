package ru.discomfortDeliverer.servlets.exchange;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ru.discomfortDeliverer.dto.*;
import ru.discomfortDeliverer.exceptions.ExchangeRateCalculationException;
import ru.discomfortDeliverer.exceptions.QueryResultToCurrencyDtoParseException;
import ru.discomfortDeliverer.service.ExchangeService;
import ru.discomfortDeliverer.servlets.HttpServletConfigurer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ExchangeServlet extends HttpServlet {
    private ExchangeService exchangeService = new ExchangeService();
    private JsonObject errorJsonObj;
    private ExceptionDto exceptionDto;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServletConfigurer.setEncode(req, resp);

        String pathInfo = req.getPathInfo(); // /currencies/HERE
        String servletPath = req.getServletPath(); // /currencies


        if(servletPath.equals("/exchange")){
            String from = req.getParameter("from");
            String to = req.getParameter("to");
            Double amount = Double.valueOf(req.getParameter("amount"));

            ConversionDto conversionDto = new ConversionDto();
            conversionDto.setFrom(from);
            conversionDto.setTo(to);
            conversionDto.setAmount(amount);

            try {
                ConvertedDto convert = exchangeService.convert(conversionDto);

                String json = new Gson().toJson(convert);

                resp.setStatus(200);
                resp.getWriter().write(json);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (QueryResultToCurrencyDtoParseException e) {
                throw new RuntimeException(e);
            } catch (ExchangeRateCalculationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
