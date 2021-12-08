package com.jonas.example.tomcat.servlet;

import com.jonas.example.tomcat.http.Request;
import com.jonas.example.tomcat.http.Response;

public class FirstServlet extends Servlet {

    @Override
    public void doGet(Request request, Response response) throws Exception {
        this.doPost(request, response);
    }

    @Override
    public void doPost(Request request, Response response) throws Exception {
        response.write("This is First Servlet!");
    }
}
