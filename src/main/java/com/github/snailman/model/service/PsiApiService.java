package com.github.snailman.model.service;

import com.github.snailman.model.ApiService;
import com.github.snailman.model.HttpMethod;
import com.intellij.psi.NavigatablePsiElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@EqualsAndHashCode(callSuper = false)
public class PsiApiService extends ApiService {

    private NavigatablePsiElement psiElement;

    public PsiApiService(HttpMethod method, String path, NavigatablePsiElement psiElement) {
        this.psiElement = psiElement;
        setPath(path);
        setMethod(method);
    }

    private void setParent(@NotNull PsiApiService parent) {
        if ((this.getMethod() == null || this.getMethod() == HttpMethod.REQUEST) && parent.getMethod() != null) {
            this.setMethod(parent.getMethod());
        }
        String parentPath = parent.getPath();
        if (parentPath != null && parentPath.endsWith("/")) {
            // 去掉末尾的斜杠
            parentPath = parentPath.substring(0, parentPath.length() - 1);
        }
        this.setPath(parentPath + this.getPath());
    }

    @NotNull
    public PsiApiService copyWithParent(@Nullable PsiApiService parent) {
        PsiApiService apiService = new PsiApiService(this.getMethod(), this.getPath(), this.getPsiElement());
        if (parent != null) {
            apiService.setParent(parent);
        }
        return apiService;
    }

}
