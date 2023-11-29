package com.github.snailman.action.api;

import com.github.snailman.configuration.SnailManOption;
import com.github.snailman.constant.Icons;
import com.github.snailman.model.ApiService;
import com.github.snailman.utils.IdeaUtils;
import com.github.snailman.utils.PathUtil;
import com.github.snailman.utils.RestDataKey;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CopyFullUrlAction extends DumbAwareAction {

    public CopyFullUrlAction() {
        super("Copy Full Url", "", Icons.ApisCopyFull);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        List<ApiService> serviceItems = RestDataKey.SELECTED_SERVICE.getData(e.getDataContext());
        if (CollectionUtils.isEmpty(serviceItems)) {
            return;
        }
        ApiService apiService = serviceItems.get(0);
        // 获取模块名
        String moduleName = apiService.getModuleName();
        // 获取port和ContextPath
        SnailManOption snailManOption = SnailManOption.getInstance(project);

        Integer port = snailManOption.getModulePort(moduleName);
        String context = snailManOption.getModuleContext(moduleName);

        String url = PathUtil.buildUrl("http", port, context, apiService.getPath());
        IdeaUtils.copyToClipboard(url);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
    }
}
