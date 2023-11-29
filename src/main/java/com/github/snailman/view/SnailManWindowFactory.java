package com.github.snailman.view;

import com.github.snailman.constant.Constant;
import com.github.snailman.utils.CommonUtil;
import com.github.snailman.view.apis.ApiPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SnailManWindowFactory implements ToolWindowFactory {

    private static final Map<String, ApiPanel> APIS_PANEL_MAP = new HashMap<>();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.getInstance();
        ContentManager contentManager = toolWindow.getContentManager();

        ApiPanel apiPanel = new ApiPanel(project);
        Content apisContent = contentFactory.createContent(apiPanel, "Apis", false);
        contentManager.addContent(apisContent);
        APIS_PANEL_MAP.put(CommonUtil.getProjectKey(project), apiPanel);
    }

    public static ToolWindow getToolWindow(Project project) {
        return Objects.requireNonNull(ToolWindowManager.getInstance(project).getToolWindow(Constant.TOOLWINDOW_ID));
    }

    public static ApiPanel getApiPanel(Project project) {
        return APIS_PANEL_MAP.get(CommonUtil.getProjectKey(project));
    }
}
