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

/**
 * 右键模块弹出编辑框
 */
public class ModulePortEditAction extends DumbAwareAction {

    public ModulePortEditAction() {
        super("edit port", "", AllIcons.CodeWithMe.CwmPermissionEdit);
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

        Integer oldPort = apisOptions.getModulePort(apiModule.getModuleName());

        String inputPort = Messages.showInputDialog(project, "Input module server.port", "Edit Server Port", null, oldPort==null? "8080":String.valueOf(oldPort), new InputValidator() {
            @Override
            public boolean checkInput(String inputString) {
                return StringUtils.isNotBlank(inputString);
            }
            @Override
            public boolean canClose(String inputString) {
                return true;
            }
        });
        if (StringUtils.isEmpty(inputPort)) {
            return;
        }
        try {
            int portInteger = Integer.parseInt(inputPort);
            if (portInteger < 1 || portInteger > 65535) {
                throw new IllegalArgumentException();
            }
            apisOptions.setModulePort(apiModule.getModuleName(), portInteger);
        } catch (Exception ex) {
            // 非数字或 1-65535, 不动
        }
    }
}
