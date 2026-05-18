package com.sun.net.httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public abstract class HttpExchange {
    public abstract URI getRequestURI();
    public abstract void sendResponseHeaders(int rCode, long responseLength) throws IOException;
    public abstract OutputStream getResponseBody();
}
