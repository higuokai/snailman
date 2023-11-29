package com.github.snailman.view.apis;

import com.github.snailman.action.api.EnableClassNodeAction;
import com.github.snailman.action.api.UrlFilterAction;
import com.github.snailman.utils.IdeaUtils;
import com.github.snailman.view.SnailManWindowFactory;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import lombok.Getter;

@Getter
public class ApiPanel extends SimpleToolWindowPanel {
    
    private final Project project;

    private ApiTree apiTree;
    
    public ApiPanel(Project project) {
        super(true);
        this.project = project;
        initUI();
    }
    
    private void initUI() {
        // 工具条
        ActionToolbar actionToolbar = initActionToolbar();
        setToolbar(actionToolbar.getComponent());
        
        // api树
        apiTree = new ApiTree(this.project);
        actionToolbar.setTargetComponent(apiTree);
        setContent(apiTree);
        
        // 渲染API树
        this.refreshApiTree();
    }
    
    private ActionToolbar initActionToolbar() {
        ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        actionGroup.addAction(actionManager.getAction("sm.apis.refresh"));
        actionGroup.addAction(actionManager.getAction("sm.apis.searchEverywhere"));
        actionGroup.addAction(new UrlFilterAction(project));

        actionGroup.addSeparator("");
        actionGroup.addAction(actionManager.getAction("sm.apis.expandAll"));
        actionGroup.addAction(actionManager.getAction("sm.apis.collapseAll"));
        actionGroup.addSeparator();

        actionGroup.addAction(actionManager.getAction("sm.apis.moduleFilter"));
        actionGroup.addAction(actionManager.getAction("sm.apis.enableLibrary"));
        actionGroup.addAction(new EnableClassNodeAction());
        
        return actionManager.createActionToolbar(ActionPlaces.TOOLWINDOW_TOOLBAR_BAR,
                actionGroup,
                true);
    }

    public void refreshApiTree() {
        // 更新api树
        IdeaUtils.smartInvokeLater(project, () -> {
            ToolWindow toolWindow = SnailManWindowFactory.getToolWindow(project);
            if (toolWindow.isDisposed() || !toolWindow.isVisible()) {
                toolWindow.show(() -> {
                    apiTree.refreshApiTree();
                });
            } else {
                apiTree.refreshApiTree();
            }
        });
    }

    public void expandAll(boolean expand) {
        apiTree.expandAll(expand);
    }
}
