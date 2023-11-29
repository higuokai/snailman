package com.github.snailman.model.service;

import lombok.Data;

import java.util.List;

@Data
public class PsiApiModule {

    private String moduleName;

    private List<PsiApiService> apiServices;

    public PsiApiModule(String moduleName, List<PsiApiService> apiServices) {
        this.moduleName = moduleName;
        this.apiServices = apiServices;
    }
    
}
