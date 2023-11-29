package com.github.snailman.scanner;

import com.github.snailman.model.HttpMethod;
import com.github.snailman.model.service.PsiApiService;
import com.github.snailman.scanner.annotation.SpringControllerAnnotation;
import com.github.snailman.scanner.annotation.SpringRequestMappingAnnotation;
import com.github.snailman.utils.PathUtil;
import com.intellij.lang.jvm.annotation.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class JavaSpringScanner implements ScannerHelper {

    @Getter
    private static final JavaSpringScanner instance = new JavaSpringScanner();

    @Override
    public Collection<PsiApiService> getService(@NotNull Project project, @NotNull Module module) {
        List<PsiApiService> apiServices = new ArrayList<>();
        GlobalSearchScope moduleScope = ScannerHelper.getModuleScope(project, module);

        String moduleName = module.getName();

        // java: 标注了 Controller 和 Controller的类
        SpringControllerAnnotation[] supportedAnnotations = SpringControllerAnnotation.values();
        for (SpringControllerAnnotation controllerAnnotation : supportedAnnotations) {
            Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(controllerAnnotation.getShortName(), module.getProject(), moduleScope);
            if (CollectionUtils.isEmpty(psiAnnotations)) {
                continue;
            }
            for (PsiAnnotation psiAnnotation : psiAnnotations) {
                PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
                PsiElement psiElement = psiModifierList.getParent();
                if (psiElement instanceof PsiClass) {
                    Collection<PsiApiService> service = getService((PsiClass) psiElement, module);
                    if (CollectionUtils.isEmpty(service)) {
                        continue;
                    }
                    service.forEach(e -> {
                        e.setModuleName(moduleName);
                        apiServices.add(e);
                    });
                }
            }
        }
        return apiServices;
    }

    public Collection<PsiApiService> getService(@NotNull PsiClass psiClass, Module module) {
        List<PsiApiService> apiServices = new ArrayList<>();
        List<PsiApiService> parentApiServices = new ArrayList<>();
        List<PsiApiService> childrenApiServices = new ArrayList<>();

        // 类上的RequestMapping注解
        PsiAnnotation classAnnotation = PsiAnnotationUtil.getClassAnnotation(psiClass, SpringRequestMappingAnnotation.REQUEST_MAPPING.getQualifiedName(),
                SpringRequestMappingAnnotation.REQUEST_MAPPING.getShortName());
        if (classAnnotation != null) {
            parentApiServices = getRequests(classAnnotation, null);
        }

        PsiMethod[] psiMethods = psiClass.getAllMethods();
        for (PsiMethod psiMethod : psiMethods) {
            childrenApiServices.addAll(getRequests(psiMethod));
        }
        if (parentApiServices.isEmpty()) {
            apiServices.addAll(childrenApiServices);
        } else {
            parentApiServices.forEach(parentRequest -> childrenApiServices.forEach(childrenRequest -> {
                PsiApiService apiService = childrenRequest.copyWithParent(parentRequest);
                apiServices.add(apiService);
            }));
        }
        return apiServices;
    }

    /**
     * 获取注解中的参数，生成RequestBean
     *
     * @param annotation annotation
     * @return list
     * @see JavaSpringScanner#getRequests(PsiMethod)
     */
    @NotNull
    private List<PsiApiService> getRequests(@NotNull PsiAnnotation annotation, @Nullable PsiMethod psiMethod) {
        SpringRequestMappingAnnotation spring = SpringRequestMappingAnnotation.getByQualifiedName(
                annotation.getQualifiedName()
        );
        if (annotation.getResolveScope().isSearchInLibraries()) {
            spring = SpringRequestMappingAnnotation.getByShortName(annotation.getQualifiedName());
        }

        Set<HttpMethod> methods = new HashSet<>();
        List<String> paths = new ArrayList<>();
        CustomRefAnnotation refAnnotation = null;
        if (spring == null) {
            refAnnotation = findCustomAnnotation(annotation);
            if (refAnnotation == null) {
                return Collections.emptyList();
            }
            methods.addAll(refAnnotation.getMethods());
        } else {
            methods.add(HttpMethod.fromMethod(spring.getMethod()));
        }

        // 是否为隐式的path（未定义value或者path）
        boolean hasImplicitPath = true;
        List<JvmAnnotationAttribute> attributes = annotation.getAttributes();
        for (JvmAnnotationAttribute attribute : attributes) {
            String name = attribute.getAttributeName();

            if (methods.contains(HttpMethod.REQUEST) && "method".equals(name)) {
                // method可能为数组
                Object value = PsiAnnotationUtil.getAttributeValue(attribute.getAttributeValue());
                if (value instanceof String) {
                    methods.add(HttpMethod.fromMethod(Objects.toString(value)));
                } else if (value instanceof List) {
                    //noinspection unchecked,rawtypes
                    List<String> list = (List) value;
                    for (String item : list) {
                        if (item != null) {
                            item = item.substring(item.lastIndexOf(".") + 1);
                            methods.add(HttpMethod.fromMethod(item));
                        }
                    }
                }
            }

            boolean flag = false;
            for (String path : new String[]{"value", "path"}) {
                if (path.equals(name)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                continue;
            }
            Object value = PsiAnnotationUtil.getAttributeValue(attribute.getAttributeValue());
            if (value instanceof String) {
                paths.add(PathUtil.formatPath(value));
            } else if (value instanceof List) {
                //noinspection unchecked,rawtypes
                List<Object> list = (List) value;
                list.forEach(item -> paths.add(PathUtil.formatPath(item)));
            } else {
                throw new IllegalArgumentException(String.format(
                        "Scan api: %s\n" +
                                "Class: %s",
                        value,
                        value != null ? value.getClass() : null
                ));
            }
            hasImplicitPath = false;
        }
        if (hasImplicitPath && psiMethod != null) {
            List<String> loopPaths;
            if (refAnnotation != null && !(loopPaths = refAnnotation.getPaths()).isEmpty()) {
                paths.addAll(loopPaths);
            } else {
                paths.add("/");
            }
        }

        List<PsiApiService> apiServices = new ArrayList<>(paths.size());

        paths.forEach(path -> {
            for (HttpMethod method : methods) {
                if (method.equals(HttpMethod.REQUEST) && methods.size() > 1) {
                    continue;
                }
                apiServices.add(new PsiApiService(
                        method,
                        path,
                        psiMethod
                ));
            }
        });
        return apiServices;
    }

    @NotNull
    private List<PsiApiService> getRequests(@NotNull PsiMethod method) {
        List<PsiApiService> apiServices = new ArrayList<>();
        for (PsiAnnotation annotation : PsiAnnotationUtil.getMethodAnnotations(method)) {
            apiServices.addAll(getRequests(annotation, method));
        }
        return apiServices;
    }

    @Nullable
    private CustomRefAnnotation findCustomAnnotation(@NotNull PsiAnnotation psiAnnotation) {
        PsiAnnotation qualifiedAnnotation = PsiAnnotationUtil.getQualifiedAnnotation(
                psiAnnotation,
                SpringRequestMappingAnnotation.REQUEST_MAPPING.getQualifiedName()
        );
        if (qualifiedAnnotation == null) {
            return null;
        }
        CustomRefAnnotation otherAnnotation = new CustomRefAnnotation();

        for (JvmAnnotationAttribute attribute : qualifiedAnnotation.getAttributes()) {
            Object methodValues = getAnnotationValue(attribute, "method");
            if (methodValues != null) {
                List<?> methods = methodValues instanceof List ? ((List<?>) methodValues) : Collections.singletonList(methodValues);
                if (methods.isEmpty()) {
                    continue;
                }
                for (Object method : methods) {
                    if (method == null) {
                        continue;
                    }
                    otherAnnotation.addMethods(HttpMethod.fromMethod(Objects.toString(method)));
                }
                continue;
            }

            Object pathValues = getAnnotationValue(attribute, "path", "value");
            if (pathValues != null) {
                List<?> paths = pathValues instanceof List ? ((List<?>) pathValues) : Collections.singletonList(pathValues);
                if (!paths.isEmpty()) {
                    for (Object path : paths) {
                        if (path == null) {
                            continue;
                        }
                        otherAnnotation.addPath((String) path);
                    }
                }
            }
        }
        return otherAnnotation;
    }

    private static class CustomRefAnnotation {

        private final List<String> paths;
        private final List<HttpMethod> methods;

        public CustomRefAnnotation() {
            this.paths = new ArrayList<>();
            this.methods = new ArrayList<>();
        }

        public void addPath(@NotNull String... paths) {
            if (paths.length < 1) {
                return;
            }
            this.paths.addAll(Arrays.asList(paths));
        }

        public void addMethods(@NotNull HttpMethod... methods) {
            if (methods.length < 1) {
                return;
            }
            this.methods.addAll(Arrays.asList(methods));
        }

        public List<String> getPaths() {
            return paths;
        }

        public List<HttpMethod> getMethods() {
            return methods;
        }
    }

    @Nullable
    private Object getAnnotationValue(@NotNull JvmAnnotationAttribute attribute, @NotNull String... attrNames) {
        String attributeName = attribute.getAttributeName();
        if (attrNames.length < 1) {
            return null;
        }
        boolean matchAttrName = false;
        for (String attrName : attrNames) {
            if (attributeName.equals(attrName)) {
                matchAttrName = true;
                break;
            }
        }
        if (!matchAttrName) {
            return null;
        }
        JvmAnnotationAttributeValue attributeValue = attribute.getAttributeValue();
        return getAttributeValue(attributeValue);
    }

    private Object getAttributeValue(@Nullable JvmAnnotationAttributeValue attributeValue) {
        if (attributeValue == null) {
            return null;
        }
        if (attributeValue instanceof JvmAnnotationConstantValue) {
            Object constantValue = ((JvmAnnotationConstantValue) attributeValue).getConstantValue();
            return constantValue == null ? null : constantValue.toString();
        } else if (attributeValue instanceof JvmAnnotationEnumFieldValue) {
            return ((JvmAnnotationEnumFieldValue) attributeValue).getFieldName();
        } else if (attributeValue instanceof JvmAnnotationArrayValue) {
            List<String> values = new ArrayList<>();
            for (JvmAnnotationAttributeValue value : ((JvmAnnotationArrayValue) attributeValue).getValues()) {
                values.add((String) getAttributeValue(value));
            }
            return values;
        }
        return null;
    }

}
