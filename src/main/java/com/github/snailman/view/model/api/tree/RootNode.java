package com.github.snailman.view.model.api.tree;

import com.github.snailman.configuration.ApiSearchOption;
import com.github.snailman.constant.Icons;
import com.github.snailman.model.service.PsiApiModule;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleNode;

import java.util.List;

public class RootNode extends BaseNode {

    private int serviceCount;

    protected RootNode() {
        super(null);
        getTemplatePresentation().setIcon(Icons.ApisRootNode);
        getTemplatePresentation().setPresentableText(getName());
    }

    public RootNode(Project project, NodeDescriptor<String> nodeDescriptor) {
        super(project, null);
        getTemplatePresentation().setIcon(Icons.ApisRootNode);
        getTemplatePresentation().setPresentableText(getName());
    }

    @Override
    protected SimpleNode[] buildChildren() {
        return childrenNodes.toArray(new SimpleNode[0]);
    }

    @Override
    public String getName() {
        return serviceCount > 0 ? String.format("Found %d services", serviceCount) : "No service found";
    }

    @Override
    public String getMenuId() {
        return "sm.apis.rootMenu";
    }

    public void updateModuleNodes(List<PsiApiModule> apiModules) {
        cleanUpCache();
        childrenNodes.clear();

        boolean showClass = ApiSearchOption.getInstance(getProject()).isShowClassNode();
        for (PsiApiModule apiModule : apiModules) {
            ModuleNode moduleNode = ModuleNode.build(this, apiModule, showClass);
            if (moduleNode == null) {
                continue;
            }
            childrenNodes.add(moduleNode);
        }

        serviceCount = apiModules.stream().map(apiModule -> apiModule.getApiServices().size()).mapToInt(i -> i).sum();

        updateFrom(getParent());
        childrenChanged();
    }
}
