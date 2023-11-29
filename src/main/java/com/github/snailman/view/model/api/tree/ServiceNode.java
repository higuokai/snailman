package com.github.snailman.view.model.api.tree;

import com.github.snailman.constant.Icons;
import com.github.snailman.model.ApiService;
import com.github.snailman.model.service.PsiApiService;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.PsiNavigateUtil;
import lombok.*;

import javax.swing.*;
import java.awt.event.InputEvent;

@Setter
@Getter
public class ServiceNode extends BaseNode {
    private static final Logger LOG = Logger.getInstance(ServiceNode.class);

    private ApiService apiService;

    public ServiceNode(SimpleNode aParent, ApiService apiService) {
        super(aParent);

        this.apiService = apiService;
        Icon icon = Icons.METHOD.get(apiService.getMethod());
        getTemplatePresentation().setIcon(icon);
    }

    @Override
    public String getName() {
        return apiService.getPath();
    }

    @Override
    public void handleDoubleClickOrEnter(SimpleTree tree, InputEvent inputEvent) {
        ServiceNode selectedNode = (ServiceNode) tree.getSelectedNode();
        if (selectedNode != null && selectedNode.apiService instanceof PsiApiService) {
            PsiElement psiElement = ((PsiApiService) selectedNode.apiService).getPsiElement();
            if (!psiElement.isValid()) {
                LOG.info("psiMethod is invalid: " + psiElement);
                return;
            }
            PsiNavigateUtil.navigate(psiElement);
        }
    }

    @Override
    public String getMenuId() {
        return "sm.apis.requestMenu";
    }

    @Override
    protected SimpleNode[] buildChildren() {
        return new SimpleNode[0];
    }

}
