package com.iriusrisk.ide.connector.settings;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.iriusrisk.ide.connector.services.IriusRiskAPI;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JBTextField myIriusRiskURL = new JBTextField();
    private final JBTextField myIriusRiskToken = new JBTextField();
    private final JBTextField myIriusRiskProject = new JBTextField();
    private final JButton testButton = new JButton("Test connection");
    private final JButton testAPI = new JButton("Test API token");
    private final JLabel testResult = new JLabel();

    public AppSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Enter IriusRisk URL: "), myIriusRiskURL, 1, false)
                .addLabeledComponent(new JBLabel("Enter IriusRisk API token: "), myIriusRiskToken, 1, false)
                .addLabeledComponent(new JBLabel("Enter IriusRisk Project ID: "), myIriusRiskProject, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .addSeparator()
                .addComponent(testButton)
                .addComponent(testAPI)
                .addComponent(testResult)
                .getPanel();


        testButton.addActionListener(e -> {
            try {
                IriusRiskAPI api = new IriusRiskAPI();
                String result = api.testConnection();
                testResult.setText(result);
            } catch (Exception ex) {
                ex.printStackTrace();
                testResult.setText("Failed: " + ex.getClass().getSimpleName() + " -> " + ex.getMessage());
            }
        });

        testAPI.addActionListener(e -> {
            try {
                IriusRiskAPI api = new IriusRiskAPI();
                String result = api.testAPIToken();
                testResult.setText(result);
            } catch (Exception ex) {
                ex.printStackTrace();
                testResult.setText("Failed: " + ex.getClass().getSimpleName() + " -> " + ex.getMessage());
            }
        });

    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return myIriusRiskURL;
    }

    @NotNull
    public String getIriusRiskURLText() {
        return myIriusRiskURL.getText();
    }

    public void setIriusRiskURLText(@NotNull String newText) {
        myIriusRiskURL.setText(newText);
    }

    @NotNull
    public String getIriusRiskTokenText() {
        return myIriusRiskToken.getText();
    }

    public void setIriusRiskTokenText(@NotNull String newText) {
        myIriusRiskToken.setText(newText);
    }

    @NotNull
    public String getIriusRiskProjectText() {
        return myIriusRiskProject.getText();
    }

    public void setIriusRiskProjectText(@NotNull String newText) {
        myIriusRiskProject.setText(newText);
    }


}
