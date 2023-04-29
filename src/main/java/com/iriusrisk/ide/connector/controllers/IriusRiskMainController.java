package com.iriusrisk.ide.connector.controllers;

import com.iriusrisk.ide.connector.models.IriusRiskMainModel;

public class IriusRiskMainController {

    private final IriusRiskMainModel model;

    public IriusRiskMainController(IriusRiskMainModel model) {
        this.model = model;
    }

    public void setStatusText(String s) {
        model.setStatusText(s);
    }

}
