package com.iriusrisk.ide.connector.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.iriusrisk.ide.connector.models.IriusRiskRelation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@State(
        name = "org.intellij.sdk.settings.AppSettingsState",
        storages = @Storage("iriusrisk-ide-connector.xml")
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    public String iriusrisk_url = "<Set your IriusRisk URL here>";
    public String iriusrisk_token = "<Set your IriusRisk API token here>";
    public String iriusrisk_project = "<Set your IriusRisk project ID here>";
    public Set<IriusRiskRelation> relations = new HashSet<>();

    public static AppSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsState.class);
    }

    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
