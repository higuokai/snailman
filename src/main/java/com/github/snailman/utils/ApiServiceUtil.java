package com.github.snailman.utils;

import com.github.snailman.configuration.ApiSearchOption;
import com.github.snailman.configuration.SnailManOption;
import com.github.snailman.model.service.PsiApiService;
import com.github.snailman.scanner.ScannerHelper;
import com.google.common.collect.Maps;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ApiServiceUtil {

    public static Map<String, List<PsiApiService>> getApis(@NotNull Project project) {
        return getApis(project, ModuleManager.getInstance(project).getModules());
    }

    public static List<PsiApiService> getApisForEditor(@NotNull Project project) {
        return Arrays.stream(ModuleManager.getInstance(project).getModules())
                .flatMap(module -> getModuleApis(project, module).stream())
                .collect(Collectors.toList());
    }
    
    public static Map<String, List<PsiApiService>> getApis(@NotNull Project project, @NotNull Module[] modules) {
        // 初始化模块过滤
        SnailManOption.ModuleFilter moduleFilter = SnailManOption.getInstance(project).getModuleFilter();
        moduleFilter.setAllModules(Arrays.stream(modules).map(Module::getName).collect(Collectors.toList()));
        
        Map<String, List<PsiApiService>> resultMap = Maps.newHashMapWithExpectedSize(modules.length);
        
        for (Module module : modules) {
            if (!moduleFilter.isVisible(module.getName())) {
                continue;
            }
            List<PsiApiService> moduleApis = getModuleApis(project, module);
            if (CollectionUtils.isEmpty(moduleApis)) {
                continue;
            }
            resultMap.put(module.getName(), moduleApis);
        }
        return resultMap;
    }

    public static List<PsiApiService> getModuleApis(@NotNull Project project, @NotNull Module module) {
        List<PsiApiService> resultList = new ArrayList<>();
        List<ScannerHelper> javaHelpers = ScannerHelper.getJavaHelpers();
        for (ScannerHelper javaHelper : javaHelpers) {
            Collection<PsiApiService> services = javaHelper.getService(project, module);
            if (CollectionUtils.isEmpty(services)) {
                continue;
            }
            resultList.addAll(services);
        }
        return resultList;
    }

    public static String getCombinedPath(@NotNull String typePath, @NotNull String methodPath) {
        if (typePath.isEmpty()) {
            typePath = "/";
        } else if (!typePath.startsWith("/")) {
            typePath = "/".concat(typePath);
        }

        if (!methodPath.isEmpty()) {
            if (!methodPath.startsWith("/") && !typePath.endsWith("/")) {
                methodPath = "/".concat(methodPath);
            }
        }

        return (typePath + methodPath).replace("//", "/");
    }

}
