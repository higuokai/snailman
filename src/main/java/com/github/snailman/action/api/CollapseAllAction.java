package com.github.snailman.action.api;

import com.github.snailman.utils.IdeaUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class CollapseAllAction extends DumbAwareAction {

    public CollapseAllAction() {
        super("Collapse All", "", AllIcons.Actions.Collapseall);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        IdeaUtils.execute(e.getProject(), apisPanel -> apisPanel.expandAll(false));
    }
}
