package com.iriusrisk.ide.connector.view;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.iriusrisk.ide.connector.controllers.IriusRiskMainController;

import java.awt.*;

import javax.swing.*;

public class IriusRiskHelpView extends JPanel implements View {

    public IriusRiskHelpView(IriusRiskMainController controller) {
        super(new BorderLayout());
        render();
    }


    @Override
    public void render() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JBTextArea textArea = new JBTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        String sb = "Use the Class tab to link classes to functional components. Parent classes must be written manually. \n" +
                "Remember to save before sending to IriusRisk. Only new components will be created in the threat model.";
        textArea.setText(sb);
        textArea.setEnabled(false);

        add(textArea, BorderLayout.CENTER);

    }

    @Override
    public void update() {

    }
}