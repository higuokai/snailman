package com.github.snailman.model;

import lombok.Data;

@Data
public class ApiService {

    private String path;

    private HttpMethod method;

    private String moduleName;
    
}
