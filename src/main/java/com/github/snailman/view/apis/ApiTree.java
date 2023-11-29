package com.github.snailman.view.apis;

import com.github.snailman.model.ApiService;
import com.github.snailman.model.service.PsiApiModule;
import com.github.snailman.model.service.PsiApiService;
import com.github.snailman.utils.ApiServiceUtil;
import com.github.snailman.utils.RestDataKey;
import com.github.snailman.view.model.api.tree.*;
import com.github.snailman.utils.IdeaUtils;
import com.intellij.ide.util.treeView.AbstractTreeStructure;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AppUIUtil;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.tree.AsyncTreeModel;
import com.intellij.ui.tree.StructureTreeModel;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.SimpleTreeStructure;
import com.intellij.util.ui.tree.TreeUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ApiTree extends JPanel implements DataProvider, Disposable {
    private final Project project;

    private final SimpleTree apisTree;

    private final StructureTreeModel<AbstractTreeStructure> treeModel;
    
    private final RootNode rootNode;
    
    public ApiTree(Project project) {
        this.project = project;

        rootNode = new RootNode(project, null);

        treeModel = new StructureTreeModel<>(new SimpleTreeStructure() {
            @Override
            public @NotNull Object getRootElement() {
                return rootNode;
            }
        }, null, this);

        apisTree = new SimpleTree(new AsyncTreeModel(treeModel, this));
        apisTree.setRootVisible(true);
        apisTree.setShowsRootHandles(true);
        apisTree.getEmptyText().clear();
        apisTree.setBorder(BorderFactory.createEmptyBorder());
        apisTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        setLayout(new BorderLayout());
        // 带滚动条的panel
        add(ScrollPaneFactory.createScrollPane(apisTree), BorderLayout.CENTER);

        // 树列表点击事件
        initEvent();
    }

    private void initEvent() {
        
        apisTree.addMouseListener(new PopupHandler() {
            @Override
            public void invokePopup(Component comp, int x, int y) {
                final String menuId = getMenuId(getSelectedNodes());
                if (menuId != null) {
                    final ActionManager actionManager = ActionManager.getInstance();
                    final ActionGroup actionGroup = (ActionGroup) actionManager.getAction(menuId);
                    if (actionGroup != null) {
                        JPopupMenu component = actionManager.createActionPopupMenu(ActionPlaces.TOOLWINDOW_CONTENT, actionGroup).getComponent();
                        component.show(comp, x, y);
                    }
                }
            }
        });
    }

    /**
     * 返回节点数据
     */
    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {
        if (RestDataKey.ALL_SERVICE.is(dataId)) {
            return rootNode.getChildrenNodes();
        }
        if (RestDataKey.ALL_MODULE.is(dataId)) {
            return rootNode.getChildrenNodes().stream()
                    .map(moduleNode -> ((ModuleNode)moduleNode).getApiModule().getModuleName())
                    .distinct()
                    .collect(Collectors.toList());
        }
        // getMenu保证了不能选择不同类型节点
        if (RestDataKey.SELECTED_SERVICE.is(dataId)) {
            List<ApiService> list = new ArrayList<>();
            for (BaseNode node : getSelectedNodes()) {
                if (node instanceof RootNode) {
                    return rootNode.getApiServices();
                } else if (node instanceof ModuleNode) {
                    list.addAll(node.getApiServices());
                } else if (node instanceof ClassNode) {
                    list.addAll(((ClassNode) node).getApiClass().getApiServiceList());
                } else if (node instanceof ServiceNode) {
                    list.add(((ServiceNode)node).getApiService());
                }
            }
            return list;
        }
        if (RestDataKey.SELECTED_MODULE_SERVICE.is(dataId)) {
            return getSelectedNodes().stream()
                    .filter(node -> node instanceof ModuleNode)
                    .map(node -> ((ModuleNode)node).getApiModule())
                    .toList();
        }
        return null;
    }
    
    private String getMenuId(Collection<? extends BaseNode> nodes) {
        return Optional.ofNullable(nodes)
                .orElse(Collections.emptyList())
                .stream()
                .filter(e -> StringUtils.isNotBlank(e.getMenuId()))
                .findFirst()
                .map(BaseNode::getMenuId)
                .orElse(null);
    }
    
    @Override
    public void dispose() {
        
    }

    /**
     * 获取选中的节点
     */
    private List<BaseNode> getSelectedNodes() {
        final List<BaseNode> filtered = new ArrayList<>();
        TreePath[] treePaths = apisTree.getSelectionPaths();
        if (treePaths != null) {
            for (TreePath treePath : treePaths) {
                SimpleNode nodeFor = apisTree.getNodeFor(treePath);
                if (!(nodeFor instanceof BaseNode)) {
                    filtered.clear();
                    break;
                }
                filtered.add((BaseNode) nodeFor);
            }
        }
        return filtered;
    }

    public void refreshApiTree() {
        Runnable backgroundTask = () -> {
            List<PsiApiModule> restModules = IdeaUtils.runRead(project, () -> {
                Map<String, List<PsiApiService>> apis = ApiServiceUtil.getApis(project);
                return apis
                        .entrySet()
                        .stream()
                        .map(entry -> new PsiApiModule(entry.getKey(), entry.getValue()))
                        .toList();
            });
            // 重新渲染树
            AppUIUtil.invokeOnEdt(() -> rootNode.updateModuleNodes(restModules));
        };

        IdeaUtils.backBackgroundTask("[SnailMan] Search restful apis", project, backgroundTask);
    }

    public void expandAll(boolean expand) {
        if (expand) {
            TreeUtil.expandAll(apisTree);
        } else {
            TreeUtil.collapseAll(apisTree, false, 1);
        }
    }
}
