package com.iriusrisk.ide.connector.models;


import com.iriusrisk.ide.connector.view.View;

import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class IriusRiskClassModel {

    private View view;
    private Set<IriusRiskRelation> relations;
    private DefaultTableModel dtm;
    DefaultComboBoxModel<String> comboModel;

    public IriusRiskClassModel() {
        relations = new HashSet<>();
        dtm = new DefaultTableModel(0, 0);
        dtm.setColumnIdentifiers(new String[]{"Class", "Component", "Parent"});
        comboModel = new DefaultComboBoxModel<>();
    }

    public void registerView(View view) {
        this.view = view;
    }

    public DefaultTableModel getDtm() {
        return dtm;
    }

    public Set<IriusRiskRelation> getRelations() {
        return relations;
    }

    public void setRelations(Set<IriusRiskRelation> relations) {
        this.relations = relations;
        view.update();
    }

    public DefaultComboBoxModel<String> getComboModel() {
        return comboModel;
    }

    public void setComboModel(DefaultComboBoxModel<String> comboModel) {
        this.comboModel = comboModel;
        view.update();
    }

    public void refresh() {
        view.update();
    }
}
