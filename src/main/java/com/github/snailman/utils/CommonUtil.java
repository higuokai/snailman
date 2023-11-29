package com.github.snailman.utils;

import com.intellij.openapi.project.Project;

public class CommonUtil {

    public static String getProjectKey(Project project) {
        return project.getLocationHash();
    }
    
}
