package com.iriusrisk.ide.connector.settings;

import com.intellij.openapi.options.Configurable;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "IriusRisk IDE Connector";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        boolean modified = !mySettingsComponent.getIriusRiskURLText().equals(settings.iriusrisk_url);
        modified |= !mySettingsComponent.getIriusRiskTokenText().equals(settings.iriusrisk_token);
        modified |= !mySettingsComponent.getIriusRiskProjectText().equals(settings.iriusrisk_project);
        return modified;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.iriusrisk_url = mySettingsComponent.getIriusRiskURLText();
        settings.iriusrisk_token = mySettingsComponent.getIriusRiskTokenText();
        settings.iriusrisk_project = mySettingsComponent.getIriusRiskProjectText();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setIriusRiskURLText(settings.iriusrisk_url);
        mySettingsComponent.setIriusRiskTokenText(settings.iriusrisk_token);
        mySettingsComponent.setIriusRiskProjectText(settings.iriusrisk_project);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}