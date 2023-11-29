package com.github.snailman.action.api;

import com.github.snailman.model.ApiService;
import com.github.snailman.model.service.PsiApiService;
import com.github.snailman.utils.RestDataKey;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.util.PsiNavigateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JumpToSourceAction extends DumbAwareAction {

    public JumpToSourceAction() {
        super("Jump to Source", "", AllIcons.CodeWithMe.CwmJump);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        List<ApiService> items = RestDataKey.SELECTED_SERVICE.getData(e.getDataContext());
        boolean match = items != null && items.stream()
                .allMatch(restItem -> restItem instanceof PsiApiService);
        e.getPresentation().setVisible(match);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<ApiService> itemList = RestDataKey.SELECTED_SERVICE.getData(e.getDataContext());
        if (CollectionUtils.isNotEmpty(itemList)) {
            itemList.stream().filter(item -> item instanceof PsiApiService)
                    .findFirst()
                    .ifPresent(item -> PsiNavigateUtil.navigate(((PsiApiService) item).getPsiElement()));
        }
    }
}
