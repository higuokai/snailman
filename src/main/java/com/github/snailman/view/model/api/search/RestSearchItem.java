package com.github.snailman.view.model.api.search;

import com.github.snailman.constant.Icons;
import com.github.snailman.model.service.PsiApiService;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.PsiNavigateUtil;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 搜索框
 */
public class RestSearchItem implements NavigationItem {

    @Getter
    private PsiApiService apiService;

    public RestSearchItem(PsiApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public @Nullable String getName() {
        return apiService.getPath();
    }

    @Override
    public @Nullable ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public @Nullable String getPresentableText() {
                return getName();
            }

            @Override
            public @Nullable String getLocationString() {
                String location = null;
                NavigatablePsiElement psiElement = apiService.getPsiElement();
                if (psiElement instanceof PsiMethod) {
                    PsiMethod psiMethod = ((PsiMethod) psiElement);
                    PsiClass psiClass = psiMethod.getContainingClass();
                    if (psiClass != null) {
                        location = psiClass.getName();
                    }
                    location += "#" + psiMethod.getName();
                    location = "Java: (" + location + ")";
                }
                if (psiElement != null) {
                    location += " in " + psiElement.getResolveScope().getDisplayName();
                }
                return location;
            }

            @Override
            public @Nullable Icon getIcon(boolean unused) {
                return Icons.METHOD.get(apiService.getMethod());
            }
        };
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (apiService.getPsiElement() != null) {
            PsiNavigateUtil.navigate(apiService.getPsiElement());
        }
    }

    @Override
    public boolean canNavigate() {
        return apiService.getPsiElement().canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }
}
