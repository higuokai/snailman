package com.github.snailman.model;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum HttpMethod {

    GET, POST, PUT, DELETE, PATCH, HEAD, REQUEST, UNDEFINED;

    private static final Map<String, HttpMethod> methodMap = Maps.newHashMapWithExpectedSize(8);
    static {
        for (HttpMethod httpMethod : values()) {
            methodMap.put(httpMethod.name(), httpMethod);
        }
    }

    public static HttpMethod fromMethod(String method) {
        if (method == null || method.isEmpty()) {
            return REQUEST;
        }
        return methodMap.getOrDefault(method.toUpperCase(), REQUEST);
    }

    public static HttpMethod nameOf(String method) {
        HttpMethod httpMethod = methodMap.get(method.toUpperCase());
        if (httpMethod == null) {
            throw new IllegalArgumentException("Not found enum constant in" + Arrays.toString(values()));
        }
        return httpMethod;
    }

}
