package com.iriusrisk.ide.connector.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.iriusrisk.ide.connector.controllers.IriusRiskMainController;
import com.iriusrisk.ide.connector.models.IriusRiskMainModel;
import com.iriusrisk.ide.connector.view.IriusRiskMainView;

import org.jetbrains.annotations.NotNull;

public class IriusRiskAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        IriusRiskMainModel model = new IriusRiskMainModel();
        IriusRiskMainController controller = new IriusRiskMainController(model);
        IriusRiskMainView view = new IriusRiskMainView(controller, model);
        view.setVisible(true);
    }


}