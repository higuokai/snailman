package com.github.snailman.configuration;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.SettingsCategory;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Setter
@Getter
@State(
        name = "RestfulApis",
        storages = @Storage("snailman/apis_options.xml"),
        category = SettingsCategory.CODE
)
public class SnailManOption implements PersistentStateComponent<SnailManOption> {

    public static SnailManOption getInstance(Project project) {
        return project.getService(SnailManOption.class);
    }

    private Map<String, Integer> modulePortMap = new HashMap<>();

    private Map<String, String> moduleContextMap = new HashMap<>();


    public Integer getModulePort(String moduleName) {
        return modulePortMap.get(moduleName);
    }

    public String getModuleContext(String moduleName) {
        return moduleContextMap.get(moduleName);
    }

    public void setModulePort(String moduleName, Integer port) {
        modulePortMap.put(moduleName, port);
    }

    public void setModuleContext(String moduleName, String moduleContext) {
        moduleContextMap.put(moduleName, moduleContext);
    }

    @Override
    public SnailManOption getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SnailManOption state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * 模块过滤
     */
    private final ModuleFilter moduleFilter = new ModuleFilter();

    public static class ModuleFilter {
        private final Set<String> disabledModules = new HashSet<>();
        private final Set<String> allModules = new HashSet<>();

        public List<String> getAllModules() {
            return new ArrayList<>(allModules);
        }

        public boolean isVisible(String module) {
            return !disabledModules.contains(module);
        }

        public void setModuleVisible(String element, boolean isMarked) {
            if (isMarked) {
                disabledModules.remove(element);
            } else {
                disabledModules.add(element);
            }
        }

        public void setAllModules(List<String> allModules) {
            this.allModules.clear();
            this.allModules.addAll(allModules);
        }
    }
    
}
