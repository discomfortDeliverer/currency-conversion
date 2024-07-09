package ru.discomfortDeliverer.servlets;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.UnsupportedEncodingException;

public class HttpServletConfigurer {
    public static void setEncode(ServletRequest servletRequest, ServletResponse servletResponse) {
        try{
            servletRequest.setCharacterEncoding("UTF-8");
            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.setContentType("application/json");
        } catch (UnsupportedEncodingException e){
            System.out.println("Указана неподдерживаемая кодировка");
        }

    }
}
