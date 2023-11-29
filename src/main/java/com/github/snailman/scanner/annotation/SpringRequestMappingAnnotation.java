package com.github.snailman.scanner.annotation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum SpringRequestMappingAnnotation {

    REQUEST_MAPPING("RequestMapping", "org.springframework.web.bind.annotation.RequestMapping", null),
    GET_MAPPING("GetMapping", "org.springframework.web.bind.annotation.GetMapping", "GET"),
    POST_MAPPING("PostMapping", "org.springframework.web.bind.annotation.PostMapping", "POST"),
    PUT_MAPPING("PutMapping", "org.springframework.web.bind.annotation.PutMapping", "PUT"),
    DELETE_MAPPING("DeleteMapping", "org.springframework.web.bind.annotation.DeleteMapping", "DELETE"),
    PATCH_MAPPING("PatchMapping", "org.springframework.web.bind.annotation.PatchMapping", "PATCH");

    private final String shortName;
    private final String qualifiedName;
    private final String method;

    private static final Map<String, SpringRequestMappingAnnotation> ANNO_MAP = new HashMap<>(SpringRequestMappingAnnotation.values().length);

    static {
        for (SpringRequestMappingAnnotation anno : SpringRequestMappingAnnotation.values()) {
            ANNO_MAP.put(anno.shortName, anno);
            ANNO_MAP.put(anno.qualifiedName, anno);
        }
    }

    public static SpringRequestMappingAnnotation getByQualifiedName(String qualifiedName) {
        return ANNO_MAP.get(qualifiedName);
    }

    public static SpringRequestMappingAnnotation getByShortName(String shortName) {
        return ANNO_MAP.get(shortName);
    }

}
