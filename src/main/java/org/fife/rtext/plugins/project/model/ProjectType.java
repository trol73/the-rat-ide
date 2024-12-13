package org.fife.rtext.plugins.project.model;

public enum ProjectType {
    RAT,
    I8085_RAT,
    BUILDER,
    MAKEFILE,
    CUSTOM;

    public static ProjectType fromStr(String s) {
        try {
            return valueOf(s);
        } catch (IllegalArgumentException e) {
            return CUSTOM;
        }
    }
}
