package com.iriusrisk.ide.connector.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.iriusrisk.ide.connector.models.IriusRiskClassModel;
import com.iriusrisk.ide.connector.models.IriusRiskRelation;
import com.iriusrisk.ide.connector.services.IriusRiskAPI;
import com.iriusrisk.ide.connector.settings.AppSettingsState;
import com.iriusrisk.ide.connector.settings.Constants;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IriusRiskClassController {

    private final IriusRiskClassModel classModel;
    private final IriusRiskAPI api;
    private Gson gson;
    private Type listOfMyClassObject;

    public IriusRiskClassController(IriusRiskClassModel model) {
        this.classModel = model;
        api = new IriusRiskAPI();
        gson = new Gson();
        listOfMyClassObject = new TypeToken<TreeSet<IriusRiskRelation>>() {
        }.getType();
    }

    public void detectClasses() {
        // TODO: Improve this method, maybe appSettingsState.relations should be a map instead of a set
        AppSettingsState appSettingsState = AppSettingsState.getInstance();

        // Read the files in the current project
        Project openProject = ProjectManager.getInstance().getOpenProjects()[0];

        Set<Path> detectedClasses = new HashSet<>();
        try {
            Stream<Path> stream = Files.walk(Paths.get(Objects.requireNonNull(openProject.getBasePath())));

            detectedClasses = stream
                    .filter(path -> path.getFileName().toString().contains("."))
                    .filter(path -> Constants.ALLOWED_TYPES.contains(path.getFileName().toString().substring(path.getFileName().toString().lastIndexOf(".") + 1)))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            System.err.println("Error reading project files");
        }

        // Include new detected classes into the relation list
        Set<String> existingClasses = appSettingsState.relations.stream().map(IriusRiskRelation::getClassName).collect(Collectors.toSet());
        for (Path classname : detectedClasses) {
            if (!existingClasses.contains(classname.getFileName().toString())) {
                appSettingsState.relations.add(new IriusRiskRelation(openProject.getBasePath(), classname.getFileName().toString(), classname.toAbsolutePath().toString(), "", ""));
            }
        }
    }

    public void saveRelations() throws Exception {

        Map<String, IriusRiskRelation> map = classModel.getRelations().stream().collect(Collectors.toMap(IriusRiskRelation::getClassName, x -> x));

        for (int i = 0; i < classModel.getDtm().getRowCount(); i++) {
            String classname = (String) classModel.getDtm().getValueAt(i, 0);
            String component = (String) classModel.getDtm().getValueAt(i, 1);
            String parent = (String) classModel.getDtm().getValueAt(i, 2);

            if (!"".equals(parent) && "".equals(component)) {
                throw new Exception("Component " + classname + " has a parent but doesn't have component. Threat model has not been saved");
            }

            IriusRiskRelation rel = map.get(classname);
            if (!rel.getComponent().equals(component)) {
                rel.setComponent(component);
            }
            if (!rel.getParent().equals(parent)) {
                rel.setParent(parent);
            }
        }

        AppSettingsState appSettingsState = AppSettingsState.getInstance();
        appSettingsState.relations = new HashSet<>(map.values());
    }

    public void restoreOperation() {
        classModel.getDtm().setRowCount(0);
        addDataToTable();
    }


    public void getRelationsFromStorage() {
        AppSettingsState appSettingsState = AppSettingsState.getInstance();
        String jsonStr = gson.toJson(appSettingsState.relations);
        classModel.setRelations(gson.fromJson(jsonStr, listOfMyClassObject));
        addDataToTable();
    }

    public String updateThreatModel(IriusRiskMainController controller) {
        return api.updateThreatModel(controller);
    }

    public void addDataToTable() {
        Project openProject = ProjectManager.getInstance().getOpenProjects()[0];
        for (IriusRiskRelation r : classModel.getRelations()) {
            if (r.getProject().equals(openProject.getBasePath())) {
                classModel.getDtm().addRow(new Object[]{r.getClassName(), r.getComponent(), r.getParent()});
            }
        }
    }

    public void setCombobox() {
        classModel.getComboModel().addAll(api.getFunctionalComponentsOrdered());
    }
}
