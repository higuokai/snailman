package com.github.snailman.view.model.api.tree;

import com.github.snailman.configuration.ApiSearchOption;
import com.github.snailman.configuration.SnailManOption;
import com.github.snailman.constant.Icons;
import com.github.snailman.model.service.PsiApiModule;
import com.github.snailman.model.service.PsiApiService;
import com.intellij.psi.PsiClass;
import com.intellij.ui.treeStructure.SimpleNode;
import lombok.*;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Setter
@Getter
public class ModuleNode extends BaseNode {

    private PsiApiModule apiModule;

    public static ModuleNode build(SimpleNode aParent, PsiApiModule apiModule, boolean showClass) {
        // 模块过滤
        if (!SnailManOption.getInstance(aParent.getProject()).getModuleFilter().isVisible(apiModule.getModuleName())) {
            return null;
        }
        return new ModuleNode(aParent, apiModule, showClass);
    }


    private ModuleNode(SimpleNode aParent, PsiApiModule apiModule, boolean showClass) {
        super(aParent);
        this.apiModule = apiModule;

        getTemplatePresentation().setIcon(Icons.ApisModuleNode);

        List<PsiApiService> apiServices = apiModule.getApiServices();

        // URL过滤
        Function<PsiApiService, Boolean> apiFunction;
        String urlFilterString = ApiSearchOption.getInstance(aParent.getProject()).getPathFiltingString();
        if (StringUtils.isEmpty(urlFilterString)) {
            apiFunction = e -> Boolean.TRUE;
        } else {
            apiFunction = e -> e.getPath().toLowerCase().contains(urlFilterString.toLowerCase());
        }

        apiServices = apiServices.stream().filter(apiFunction::apply).toList();

        getTemplatePresentation().setTooltip(String.valueOf(apiServices));

        updateChildrenNode(apiServices, showClass);
    }

    private void updateChildrenNode(List<PsiApiService> apiServices, boolean showClass) {
        this.childrenNodes.clear();
        if (showClass) {
            apiServices.stream()
                    .collect(Collectors.groupingBy(e -> (PsiClass) e.getPsiElement().getParent()))
                    .entrySet().stream().map(e -> new ClassNode(this, e.getKey(), e.getValue()))
                    .forEach(childrenNodes::add);
        } else {
            this.childrenNodes = apiServices.stream().map(e -> new ServiceNode(this, e)).collect(Collectors.toList());
        }

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
        return "sm.apis.moduleMenu";
    }

    @Override
    protected SimpleNode[] buildChildren() {
        return childrenNodes.toArray(new SimpleNode[0]);
    }

    @Override
    public String getName() {
        return apiModule.getModuleName();
    }

    @Override
    public List<PsiApiService> getApiServices() {
        if (childrenNodes.isEmpty()) {
            return Collections.emptyList();
        } else if (childrenNodes.get(0) instanceof ClassNode) {
            return childrenNodes.stream().flatMap(e -> e.getApiServices().stream()).toList();
        } else {
            return childrenNodes.stream().map(e -> (PsiApiService)((ServiceNode)e).getApiService()).toList();
        }
    }
}
