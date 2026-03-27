package com.caiorosadev.graph.ui;

import java.util.regex.Pattern;

/** IDs de nó: apenas letras e dígitos (sem espaços). */
public final class NodeIdValidator {

    private static final Pattern ALPHANUMERIC = Pattern.compile("[a-zA-Z0-9]+");

    private NodeIdValidator() {
    }

    public static boolean isValid(String id) {
        return id != null && !id.isBlank() && ALPHANUMERIC.matcher(id.trim()).matches();
    }

    public static String normalize(String id) {
        return id == null ? "" : id.trim();
    }
}
