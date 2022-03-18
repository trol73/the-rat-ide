package org.fife.rtext.plugins.project.model;

public enum ProjectType {
    AVR_RAT,
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
