package com.iriusrisk.ide.connector.models;

import org.jetbrains.annotations.NotNull;

public class IriusRiskRelation implements Comparable<IriusRiskRelation> {
    private String project;
    private String className;
    private String fullPath;
    private String component;
    private String parent;

    public IriusRiskRelation() {

    }

    public IriusRiskRelation(String project, String className, String fullPath, String component, String parent) {
        this.project = project;
        this.className = className;
        this.fullPath = fullPath;
        this.component = component;
        this.parent = parent;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "IriusRiskRelation{" +
                "project='" + project + '\'' +
                ", className='" + className + '\'' +
                ", fullPath='" + fullPath + '\'' +
                ", component='" + component + '\'' +
                ", parent='" + parent + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NotNull IriusRiskRelation o) {
        return this.getClassName().compareTo(o.getClassName());
    }


}
