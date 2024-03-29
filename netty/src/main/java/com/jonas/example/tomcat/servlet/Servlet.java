package com.jonas.example.tomcat.servlet;

import com.jonas.example.tomcat.http.Request;
import com.jonas.example.tomcat.http.Response;

public abstract class Servlet {

    public void service(Request request, Response response) throws Exception {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }

    public abstract void doGet(Request request, Response response) throws Exception;

    public abstract void doPost(Request request, Response response) throws Exception;
}
