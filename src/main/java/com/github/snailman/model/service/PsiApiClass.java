package com.github.snailman.model.service;

import com.intellij.psi.PsiClass;
import lombok.Data;

import java.util.List;

@Data
public class PsiApiClass {

    private final PsiClass psiClass;

    private List<PsiApiService> apiServiceList;

    private String className;

    public PsiApiClass(PsiClass psiClass, List<PsiApiService> apiServiceList) {
        this.psiClass = psiClass;
        this.apiServiceList = apiServiceList;
        this.className = psiClass.getName();
    }
    
}
