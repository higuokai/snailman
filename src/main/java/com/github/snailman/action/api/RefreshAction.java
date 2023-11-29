package com.github.snailman.action.api;

import com.github.snailman.constant.Icons;
import com.github.snailman.view.SnailManWindowFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class RefreshAction extends DumbAwareAction {

    public RefreshAction() {
        super("Refresh", "", Icons.ApisRefresh);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        SnailManWindowFactory.getApiPanel(project).refreshApiTree();
    }
}
