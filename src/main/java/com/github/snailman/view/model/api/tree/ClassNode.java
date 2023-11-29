package com.github.snailman.view.model.api.tree;

import com.github.snailman.constant.Icons;
import com.github.snailman.model.service.PsiApiClass;
import com.github.snailman.model.service.PsiApiService;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiClass;
import com.intellij.ui.treeStructure.SimpleNode;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class ClassNode extends BaseNode {

    private PsiApiClass apiClass;

    protected ClassNode(SimpleNode aParent, PsiApiClass apiClass) {
        super(aParent);
        this.apiClass = apiClass;

        getTemplatePresentation().setIcon(Icons.ApisClassNode);
        getTemplatePresentation().setTooltip(String.valueOf(apiClass.getApiServiceList().size()));

        updateServiceNode(apiClass.getApiServiceList());
    }

    protected ClassNode(SimpleNode aParent, PsiClass psiClass, List<PsiApiService> apiServices) {
        this(aParent, new PsiApiClass(psiClass, apiServices));
    }

    private void updateServiceNode(List<PsiApiService> apiServices) {
        childrenNodes = apiServices.stream().map(e -> new ServiceNode(this, e)).collect(Collectors.toList());

        SimpleNode parent = getParent();
        if (parent != null) {
            ((BaseNode) parent).cleanUpCache();
        }
        updateFrom(parent);
        childrenChanged();
        updateUpTo(this);
    }

    @Override
    public String getMenuId() {
        return "sm.apis.classMenu";
    }

    @Override
    protected SimpleNode[] buildChildren() {
        return childrenNodes.toArray(new SimpleNode[0]);
    }

    @Override
    public @NlsSafe String getName() {
        return apiClass.getClassName();
    }

    @Override
    public List<PsiApiService> getApiServices() {
        return apiClass.getApiServiceList();
    }
}
