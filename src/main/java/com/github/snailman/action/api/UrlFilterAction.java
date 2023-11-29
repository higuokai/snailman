package com.github.snailman.action.api;

import com.github.snailman.configuration.ApiSearchOption;
import com.github.snailman.view.SnailManWindowFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.project.Project;
import com.intellij.ui.SearchTextField;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.NamedColorUtil;
import com.intellij.util.ui.StartupUiUtil;
import com.intellij.util.ui.UIUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class UrlFilterAction extends AnAction implements CustomComponentAction {

    private final JPanel component;
    private final SearchTextField searchTextField;

    private Project project;

    public UrlFilterAction(Project project) {
        super("Find...");
        this.project = project;

        searchTextField = new SearchTextField(true) {
            @Override
            protected boolean preprocessEventForTextField(KeyEvent e) {
                if ((KeyEvent.VK_ENTER == e.getKeyCode()) || ('\n' == e.getKeyChar())) {
                    e.consume();
                    addCurrentTextToHistory();
                    perform();
                }
                return super.preprocessEventForTextField(e);
            }

            @Override
            protected void onFocusLost() {
                searchTextField.addCurrentTextToHistory();
                perform();
            }

            @Override
            protected void onFieldCleared() {
                perform();
            }
        };
        Border border = searchTextField.getBorder();
        Border emptyBorder = JBUI.Borders.empty(3, 0, 2, 0);
        if (border instanceof CompoundBorder) {
            if (!StartupUiUtil.isUnderDarcula()) {
                searchTextField.setBorder(new CompoundBorder(emptyBorder, ((CompoundBorder)border).getInsideBorder()));
            }
        }
        else {
            searchTextField.setBorder(emptyBorder);
        }
        searchTextField.getTextEditor().getEmptyText().setText("Enter Url...");

        component = new JPanel();
        final BoxLayout layout = new BoxLayout(component, BoxLayout.X_AXIS);
        component.setLayout(layout);
        component.add(searchTextField);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String text = getText();
        // 判断有没有变化
        ApiSearchOption apisTempOptions = ApiSearchOption.getInstance(project);
        String urlFilterString = apisTempOptions.getPathFiltingString();
        if (Objects.equals(urlFilterString, text)) {
            return;
        }
        // 有变化的话, 设置且更新
        apisTempOptions.setPathFiltingString(text);
        SnailManWindowFactory.getApiPanel(project).refreshApiTree();
        if (StringUtils.isNotBlank(text)) {
            // 搜索后展开
            SnailManWindowFactory.getApiPanel(project).expandAll(true);
        }
    }

    private void perform() {
        actionPerformed(ActionUtil.createEmptyEvent());
    }

    public String getText() {
        return searchTextField.getText();
    }

    @NotNull
    @Override
    public JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        return component;
    }

    public void setTextFieldFg(boolean inactive) {
        searchTextField.getTextEditor().setForeground(inactive ? NamedColorUtil.getInactiveTextColor() : UIUtil.getActiveTextColor());
    }
}
