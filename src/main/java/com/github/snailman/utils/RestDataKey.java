package com.github.snailman.utils;

import com.github.snailman.model.ApiService;
import com.github.snailman.model.service.PsiApiModule;
import com.intellij.openapi.actionSystem.DataKey;

import java.util.List;

public class RestDataKey {

    public static final DataKey<List<ApiService>> ALL_SERVICE = DataKey.create("ALL_SERVICE");

    public static final DataKey<List<ApiService>> SELECTED_SERVICE = DataKey.create("SELECTED_SERVICE");

    public static final DataKey<List<PsiApiModule>> SELECTED_MODULE_SERVICE = DataKey.create("SELECTED_MODULE_SERVICE");

    public static final DataKey<List<String>> ALL_MODULE = DataKey.create("ALL_MODULE");


}
