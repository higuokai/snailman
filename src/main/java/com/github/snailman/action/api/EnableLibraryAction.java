package com.github.snailman.action.api;

import com.github.snailman.configuration.ApiSearchOption;
import com.github.snailman.view.SnailManWindowFactory;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class EnableLibraryAction extends ToggleAction {

    public EnableLibraryAction() {
        super("Enable Library", "", AllIcons.ObjectBrowser.ShowLibraryContents);
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return false;
        }
        return ApiSearchOption.getInstance(project).isScanServiceWithLib();
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        ApiSearchOption.getInstance(project).setScanServiceWithLib(state);
        SnailManWindowFactory.getApiPanel(project).refreshApiTree();
    }
}
