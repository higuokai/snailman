package com.github.snailman.constant;

import com.github.snailman.model.HttpMethod;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class Icons {

    public static final Icon ApisRefresh = AllIcons.Actions.Refresh;
    public static final Icon ApisModuleFilter = load("/icons/system/moduleFilter.svg");

    public static final Icon ApisRootNode = AllIcons.Nodes.ModuleGroup;
    public static final Icon ApisModuleNode = AllIcons.Nodes.Module;
    public static final Icon ApisClassNode = AllIcons.FileTypes.JavaClass;

    public static final Icon ApisCopy = load("/icons/system/copy.svg");

    public static final Icon ApisCopyFull = load("/icons/system/copyFull.svg");

    public static class METHOD {
        public static Icon get(HttpMethod method) {
            if (method == null) {
                return UNDEFINED;
            }
            switch (method) {
                case GET:
                    return GET;
                case POST:
                    return POST;
                case PUT:
                    return PUT;
                case PATCH:
                    return PATCH;
                case DELETE:
                    return DELETE;
                case REQUEST:
                    return ALL;
                default:
                    return UNDEFINED;
            }
        }

        private static final Icon GET = load("/icons/method/GET.svg");
        private static final Icon PUT = load("/icons/method/PUT.svg");
        private static final Icon POST = load("/icons/method/POST.svg");
        private static final Icon PATCH = load("/icons/method/PATCH.svg");
        private static final Icon DELETE = load("/icons/method/DELETE.svg");
        public static final Icon ALL = load("/icons/method/REQUEST.svg");
        private static final Icon UNDEFINED = load("/icons/method/UNDEFINED.svg");
    }


    private static Icon load(String path) {
        return IconLoader.getIcon(path, Icons.class);
    }
}
