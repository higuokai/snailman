package com.github.snailman.view.request;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;

public class RequestPanel extends SimpleToolWindowPanel {
    
    private final Project project;
    
    public RequestPanel(Project project) {
        super(true);
        this.project = project;
    }
}
