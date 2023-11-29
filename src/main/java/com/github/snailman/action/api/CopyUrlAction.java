package com.github.snailman.action.api;

import com.github.snailman.constant.Icons;
import com.github.snailman.model.ApiService;
import com.github.snailman.utils.IdeaUtils;
import com.github.snailman.utils.RestDataKey;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CopyUrlAction extends DumbAwareAction {

    public CopyUrlAction() {
        super("Copy Url", "", Icons.ApisCopy);
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
        IdeaUtils.copyToClipboard(apiService.getPath());
    }
}
