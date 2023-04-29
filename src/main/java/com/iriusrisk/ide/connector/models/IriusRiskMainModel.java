package com.iriusrisk.ide.connector.models;


import com.iriusrisk.ide.connector.view.View;

public class IriusRiskMainModel {

    private View view;
    private String statusText;

    public IriusRiskMainModel() {
        statusText = "";
    }

    public void registerView(View view) {
        this.view = view;
    }

    public void setStatusText(String s) {
        statusText = s;
        view.update();
    }

    public String getStatusText() {
        return statusText;
    }


}
