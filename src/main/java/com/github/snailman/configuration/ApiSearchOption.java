package com.github.snailman.configuration;

import com.github.snailman.utils.CommonUtil;
import com.intellij.openapi.project.Project;
import lombok.Data;

import java.util.*;

@Data
public class ApiSearchOption {

    private final static Map<String, ApiSearchOption> INSTANCE_MAP = new HashMap<>();

    public static ApiSearchOption getInstance(Project project) {
        return INSTANCE_MAP.computeIfAbsent(CommonUtil.getProjectKey(project), k -> new ApiSearchOption());
    }

    /**
     * 是否显示Class
     */
    private boolean showClassNode = false;
    /**
     * 是否扫描lib
     */
    private boolean scanServiceWithLib = false;
    /**
     * URL过滤
     */
    private String pathFiltingString;

}
