package com.github.snailman.action.api;

import com.github.snailman.configuration.SnailManOption;
import com.github.snailman.model.service.PsiApiService;
import com.github.snailman.utils.ApiServiceUtil;
import com.github.snailman.utils.IdeaUtils;
import com.github.snailman.utils.PathUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EditTextCopyFullUrlAction extends DumbAwareAction {

    public EditTextCopyFullUrlAction() {
        super("SnailMan:  Copy Full Path", "", null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project == null) {
            return;
        }

        PsiMethod psiMethod = getPsiMethod(e);
        if (psiMethod == null) {
            return;
        }

        Optional<PsiApiService> any = ApiServiceUtil
                .getApisForEditor(project)
                .stream()
                .filter(element -> element.getPsiElement().equals(psiMethod))
                .findAny();
        
        if (any.isEmpty()) {
            return;
        }

        PsiApiService apiService = any.get();

        // 获取模块名
        String moduleName = apiService.getModuleName();
        // 获取port和ContextPath
        SnailManOption apisOptions = SnailManOption.getInstance(project);

        Integer port = apisOptions.getModulePort(moduleName);
        String context = apisOptions.getModuleContext(moduleName);
        String url = PathUtil.buildUrl("http", port, context, apiService.getPath());
        IdeaUtils.copyToClipboard(url);
    }

    @Nullable
    public static PsiElement getCurrentEditorElement(@NotNull AnActionEvent e) {
        Editor editor = e.getData(LangDataKeys.EDITOR);
        if (editor == null) {
            return null;
        }
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null) {
            return null;
        }
        return psiFile.findElementAt(editor.getCaretModel().getOffset());
    }

    @Nullable
    public static PsiMethod getPsiMethod(@NotNull AnActionEvent event) {
        PsiElement currentEditorElement = getCurrentEditorElement(event);
        if (currentEditorElement == null) {
            return null;
        }
        // 如果右键处为当前方法其中的 注解末尾 或 方法体中
        PsiElement editorElementContext = currentEditorElement.getContext();
        if (editorElementContext instanceof PsiMethod) {
            return ((PsiMethod) editorElementContext);
        }
        return null;
    }
}
