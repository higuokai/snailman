package com.github.snailman.scanner;

import com.github.snailman.configuration.ApiSearchOption;
import com.github.snailman.model.service.PsiApiService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public interface ScannerHelper {

    static List<ScannerHelper> getJavaHelpers() {
        return Arrays.asList(JavaSpringScanner.getInstance());
    }

    Collection<PsiApiService> getService(@NotNull Project project, @NotNull Module module);

    static GlobalSearchScope getModuleScope(@NotNull Project project, @NotNull Module module) {
        boolean scanServiceWithLib = ApiSearchOption.getInstance(project).isScanServiceWithLib();
        if (scanServiceWithLib) {
            return module.getModuleWithLibrariesScope();
        } else {
            return module.getModuleScope();
        }
    }

}
