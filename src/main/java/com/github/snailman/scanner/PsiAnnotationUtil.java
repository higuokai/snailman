package com.github.snailman.scanner;

import com.intellij.lang.jvm.annotation.*;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PsiAnnotationUtil {

    /**
     * 查找类上的指定注解（包括超类和接口）
     *
     * @param psiClass      PsiClass
     * @param qualifiedName 注解全限定名
     * @return annotation
     */
    @Nullable
    public static PsiAnnotation getClassAnnotation(@NotNull PsiClass psiClass, @NotNull String... qualifiedName) {
        if (qualifiedName.length < 1) {
            return null;
        }
        PsiAnnotation annotation;
        for (String name : qualifiedName) {
            annotation = psiClass.getAnnotation(name);
            if (annotation != null) {
                return annotation;
            }
        }
        List<PsiClass> classes = new ArrayList<>();
        classes.add(psiClass.getSuperClass());
        classes.addAll(Arrays.asList(psiClass.getInterfaces()));
        for (PsiClass superPsiClass : classes) {
            if (superPsiClass == null) {
                continue;
            }
            PsiAnnotation classAnnotation = getClassAnnotation(superPsiClass, qualifiedName);
            if (classAnnotation != null) {
                return classAnnotation;
            }
        }
        return null;
    }

    public static PsiAnnotation getInheritedAnnotation(PsiClass psiClass, String fqn) {
        if (psiClass == null) {
            return null;
        }
        PsiAnnotation annotation = psiClass.getAnnotation(fqn);
        if (annotation != null) {
            return annotation;
        }
        for (PsiClass aSuper : psiClass.getSupers()) {
            if (!"java.lang.Object".equals(aSuper.getQualifiedName())) {
                PsiAnnotation superClassAnno = getInheritedAnnotation(aSuper, fqn);
                if (superClassAnno != null) {
                    return superClassAnno;
                }
            }
        }
        return null;
    }

    public static List<String> getAnnotationAttributeValues(PsiAnnotation annotation, String attr) {
        List<String> values = new ArrayList<>();
        PsiAnnotationMemberValue value = annotation.findDeclaredAttributeValue(attr);
        if (value == null) {
            return values;
        }

        //只有注解
        //一个值 class com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl
        //多个值  class com.intellij.psi.impl.source.tree.java.PsiArrayInitializerMemberValueImpl
        if (value instanceof PsiReferenceExpression) {
            PsiReferenceExpression expression = (PsiReferenceExpression) value;
            values.add(expression.getText());
        } else if (value instanceof PsiLiteralExpression) {
//            values.add(psiNameValuePair.getLiteralValue());
            values.add(((PsiLiteralExpression) value).getValue().toString());
        } else if (value instanceof PsiArrayInitializerMemberValue) {
            PsiAnnotationMemberValue[] initializers = ((PsiArrayInitializerMemberValue) value).getInitializers();

            for (PsiAnnotationMemberValue initializer : initializers) {
                values.add(initializer.getText().replaceAll("\"", ""));
            }
        } else if (value instanceof PsiPolyadicExpression) {
            String s = "";
            for (PsiElement child : value.getChildren()) {
                if (child instanceof PsiLiteralExpression) {
                    s += ((PsiLiteralExpression) child).getValue().toString();
                    continue;
                }
                PsiFile containingFile = child.getContainingFile();
                if (child instanceof PsiReferenceExpression && containingFile instanceof PsiJavaFile) {
                    PsiClass aClass = ((PsiJavaFile) containingFile).getClasses()[0];
                    for (PsiField field : aClass.getFields()) {
                        if (child.getText().endsWith(field.getName())) {
                            s += field.computeConstantValue();
                        }
                    }
                }
            }
            values.add(s);
        }

        return values;
    }

    /**
     * 获取方法的所有注解（包括父类）
     *
     * @param psiMethod psiMethod
     * @return annotations
     */
    @NotNull
    public static List<PsiAnnotation> getMethodAnnotations(@NotNull PsiMethod psiMethod) {
        List<PsiAnnotation> annotations = new ArrayList<>(Arrays.asList(psiMethod.getModifierList().getAnnotations()));
        for (PsiMethod superMethod : psiMethod.findSuperMethods()) {
            getMethodAnnotations(superMethod)
                    .stream()
                    // 筛选：子类中方法定义了父类中方法存在的注解时只保留最上层的注解（即实现类的方法注解
                    .filter(annotation -> !annotations.contains(annotation))
                    .forEach(annotations::add);
        }
        return annotations;
    }

    @Nullable
    public static PsiAnnotation getQualifiedAnnotation(PsiAnnotation psiAnnotation, @NotNull String qualifiedName) {
        final String targetAnn = "java.lang.annotation.Target";
        final String documentedAnn = "java.lang.annotation.Documented";
        final String retentionAnn = "java.lang.annotation.Retention";
        if (psiAnnotation == null) {
            return null;
        }
        String annotationQualifiedName = psiAnnotation.getQualifiedName();
        if (qualifiedName.equals(annotationQualifiedName)) {
            return psiAnnotation;
        }
        if (targetAnn.equals(annotationQualifiedName) || documentedAnn.equals(annotationQualifiedName) || retentionAnn.equals(annotationQualifiedName)) {
            return null;
        }
        PsiJavaCodeReferenceElement element = psiAnnotation.getNameReferenceElement();
        if (element == null) {
            return null;
        }
        PsiElement resolve = element.resolve();
        if (!(resolve instanceof PsiClass)) {
            return null;
        }
        PsiClass psiClass = (PsiClass) resolve;
        if (!psiClass.isAnnotationType()) {
            return null;
        }
        PsiAnnotation annotation = psiClass.getAnnotation(qualifiedName);
        if (annotation != null && qualifiedName.equals(annotation.getQualifiedName())) {
            return annotation;
        }
        for (PsiAnnotation classAnnotation : psiClass.getAnnotations()) {
            PsiAnnotation qualifiedAnnotation = getQualifiedAnnotation(classAnnotation, qualifiedName);
            if (qualifiedAnnotation != null) {
                return qualifiedAnnotation;
            }
        }
        return null;
    }

    /**
     * 获取属性值
     *
     * @param attributeValue Psi属性
     * @return {Object | List}
     */
    @Nullable
    public static Object getAttributeValue(JvmAnnotationAttributeValue attributeValue) {
        if (attributeValue == null) {
            return null;
        }
        if (attributeValue instanceof JvmAnnotationConstantValue) {
            return ((JvmAnnotationConstantValue) attributeValue).getConstantValue();
        } else if (attributeValue instanceof JvmAnnotationEnumFieldValue) {
            return ((JvmAnnotationEnumFieldValue) attributeValue).getFieldName();
        } else if (attributeValue instanceof JvmAnnotationArrayValue) {
            List<JvmAnnotationAttributeValue> values = ((JvmAnnotationArrayValue) attributeValue).getValues();
            List<Object> list = new ArrayList<>(values.size());
            for (JvmAnnotationAttributeValue value : values) {
                Object o = getAttributeValue(value);
                if (o != null) {
                    list.add(o);
                } else {
                    // 如果是jar包里的JvmAnnotationConstantValue则无法正常获取值
                    try {
                        Class<? extends JvmAnnotationAttributeValue> clazz = value.getClass();
                        Field myElement = clazz.getSuperclass().getDeclaredField("myElement");
                        myElement.setAccessible(true);
                        Object elObj = myElement.get(value);
                        if (elObj instanceof PsiExpression) {
                            PsiExpression expression = (PsiExpression) elObj;
                            list.add(expression.getText());
                        }
                    } catch (Exception ignore) {
                    }
                }
            }
            return list;
        } else if (attributeValue instanceof JvmAnnotationClassValue) {
            return ((JvmAnnotationClassValue) attributeValue).getQualifiedName();
        }
        return null;
    }
}
