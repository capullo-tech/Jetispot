package com.sun.net.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;

public abstract class HttpServer {
    public static HttpServer create(InetSocketAddress addr, int backlog) throws IOException {
        throw new UnsupportedOperationException(
            "com.sun.net.httpserver.HttpServer is not available on Android");
    }

    public abstract HttpContext createContext(String path, HttpHandler handler);
    public abstract void start();
    public abstract void stop(int delay);
    public abstract InetSocketAddress getAddress();
}
