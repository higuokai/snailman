package com.github.snailman.action.api;

import com.github.snailman.configuration.ApiSearchOption;
import com.github.snailman.configuration.SnailManOption;
import com.github.snailman.constant.Icons;
import com.github.snailman.view.SnailManWindowFactory;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.util.ElementsChooser;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ModuleFilterAction extends ToggleAction {

    private JBPopup myFilterPopup;

    public ModuleFilterAction() {
        super("Mdoule Filter", "", Icons.ApisModuleFilter);
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return myFilterPopup != null && !myFilterPopup.isDisposed();
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        if (state) {
            showPopup(e);
        } else {
            if (myFilterPopup != null && !myFilterPopup.isDisposed()) {
                myFilterPopup.cancel();
            }
        }
    }

    private void showPopup(AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        if (myFilterPopup != null) {
            return;
        }
        JBPopupListener popupCloseListener = new JBPopupListener() {
            @Override
            public void onClosed(@NotNull LightweightWindowEvent event) {
                myFilterPopup = null;
            }
        };
        myFilterPopup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(createFilterPanel(project), null)
                .setModalContext(false)
                .setFocusable(true)
                .setRequestFocus(true)
                .setResizable(true)
//                                      .setCancelOnClickOutside(true)
                .setMinSize(new Dimension(200, 200))
//                                      .setDimensionServiceKey(project, getDimensionServiceKey(), false)
                .addListener(popupCloseListener)
                .createPopup();
        Component anchor = e.getInputEvent().getComponent();
        if (anchor.isValid()) {
            myFilterPopup.showUnderneathOf(anchor);
        } else {
            Component component = e.getData(PlatformDataKeys.CONTEXT_COMPONENT);
            if (component != null) {
                myFilterPopup.showUnderneathOf(component);
            } else {
                myFilterPopup.showInFocusCenter();
            }
        }
    }

    private JComponent createFilterPanel(Project project) {
        ElementsChooser<?> chooser = createChooser(project);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(chooser);
        JPanel buttons = new JPanel();
        JButton all = new JButton(IdeBundle.message("big.popup.filter.button.all"));
        all.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                chooser.setAllElementsMarked(true);
            }
        });
        buttons.add(all);
        JButton none = new JButton(IdeBundle.message("big.popup.filter.button.none"));
        none.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                chooser.setAllElementsMarked(false);
            }
        });
        buttons.add(none);
        JButton invert = new JButton(IdeBundle.message("big.popup.filter.button.invert"));
        invert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                chooser.invertSelection();
            }
        });
        buttons.add(invert);
        panel.add(buttons);
        return panel;
    }

    private ElementsChooser<String> createChooser(@NotNull Project project) {
        SnailManOption.ModuleFilter moduleFilter = SnailManOption.getInstance(project).getModuleFilter();

        List<String> allModules = moduleFilter.getAllModules();
        ElementsChooser<String> res = new ElementsChooser<>(allModules, false) {
            @Override
            protected String getItemText(@NotNull String value) {
                return value;
            }
        };
        res.markElements(ContainerUtil.filter(allModules, moduleFilter::isVisible));

        ElementsChooser.ElementsMarkListener<String> listener = (element, isMarked) -> {
            moduleFilter.setModuleVisible(element, isMarked);
            SnailManWindowFactory.getApiPanel(project).refreshApiTree();
        };
        res.addElementsMarkListener(listener);
        return res;
    }


}
