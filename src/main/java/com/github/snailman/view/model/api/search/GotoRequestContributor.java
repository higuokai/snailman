package com.github.snailman.view.model.api.search;

import com.github.snailman.model.service.PsiApiService;
import com.github.snailman.utils.ApiServiceUtil;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GotoRequestContributor implements ChooseByNameContributor {

    private transient final List<RestSearchItem> cacheSearchItemList = new ArrayList<>();

    public GotoRequestContributor(Module module) {

    }

    @Override
    public String @NotNull [] getNames(Project project, boolean includeNonProjectItems) {

        Map<String, List<PsiApiService>> apiServices = ApiServiceUtil.getApis(project);

        cacheSearchItemList.clear();
        // 从缓存中获取接口数据
        List<String> resultList = apiServices
                .entrySet().stream()
                .flatMap(i -> i.getValue().stream())
                .peek(e -> cacheSearchItemList.add(new RestSearchItem(e)))
                .map(PsiApiService::getPath)
                .toList();
        return resultList.toArray(new String[0]);
    }

    @Override
    public NavigationItem @NotNull [] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        List<RestSearchItem> patternList = cacheSearchItemList.stream().filter(e -> e.getName().contains(name)).toList();
        return patternList.toArray(new NavigationItem[0]);
    }
}
