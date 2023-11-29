package com.github.snailman.action.api;

import com.github.snailman.utils.IdeaUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ExpandAllAction extends DumbAwareAction {

    public ExpandAllAction() {
        super("Expand All", "", AllIcons.Actions.Expandall);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        IdeaUtils.execute(e.getProject(), apisPanel -> apisPanel.expandAll(true));
    }
}
