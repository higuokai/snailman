package com.github.snailman.action.api;

import com.github.snailman.configuration.SnailManOption;
import com.github.snailman.model.service.PsiApiModule;
import com.github.snailman.utils.RestDataKey;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModuleServiceContextEditAction extends DumbAwareAction {

    public ModuleServiceContextEditAction() {
        super("Edit Service Context", "", AllIcons.Modules.EditFolder);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        List<PsiApiModule> apiModules = RestDataKey.SELECTED_MODULE_SERVICE.getData(e.getDataContext());
        if (CollectionUtils.isEmpty(apiModules)) {
            return;
        }
        PsiApiModule apiModule = apiModules.get(0);

        SnailManOption apisOptions = SnailManOption.getInstance(project);

        String oldContext = apisOptions.getModuleContext(apiModule.getModuleName());

        String inputContext = Messages.showInputDialog(project, "Input module server.context", "Edit Server Context", null, StringUtils.isEmpty(oldContext)?null:oldContext, new InputValidator() {
            @Override
            public boolean checkInput(String inputString) {
                return StringUtils.isNotBlank(inputString);
            }
            @Override
            public boolean canClose(String inputString) {
                return true;
            }
        });
        if (StringUtils.isEmpty(inputContext)) {
            return;
        }
        apisOptions.setModuleContext(apiModule.getModuleName(), inputContext);
    }
}
