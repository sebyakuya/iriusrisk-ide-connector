package com.iriusrisk.ide.connector.view;

import com.intellij.ui.components.JBTabbedPane;
import com.iriusrisk.ide.connector.controllers.IriusRiskClassController;
import com.iriusrisk.ide.connector.controllers.IriusRiskMainController;
import com.iriusrisk.ide.connector.models.IriusRiskClassModel;
import com.iriusrisk.ide.connector.models.IriusRiskMainModel;

import java.awt.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class IriusRiskMainView extends JFrame implements View {

    private JLabel statusLabel;
    private JPanel classView;
    private JPanel batchView;
    private JPanel helpView;
    private JTabbedPane tabbedPane;

    private final IriusRiskMainController controller;
    private final IriusRiskMainModel model;


    public IriusRiskMainView(IriusRiskMainController controller, IriusRiskMainModel model) {
        this.controller = controller;
        this.model = model;
        model.registerView(this);
        render();
    }

    public void render() {
        setTitle("IriusRisk IDE Connector");
        setLayout(new BorderLayout());
        Image image = null;
        try {
            image = ImageIO.read(getClass().getResource("/images/favicon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setIconImage(image);

        statusLabel = new JLabel("");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Class pane
        IriusRiskClassModel classModel = new IriusRiskClassModel();
        IriusRiskClassController classController = new IriusRiskClassController(classModel);
        classView = new IriusRiskClassView(controller, classController, classModel);

        // Batch pane
        // batchView = new IriusRiskBatchView(controller);

        // Help pane
        helpView = new IriusRiskHelpView(controller);

        // Tabbed pane
        tabbedPane = new JBTabbedPane();
        tabbedPane.addTab("Classes", null, classView, "To set the relation between classes and components");
        //tabbedPane.addTab("Batch", null, batchView, "To set a component to multiple classes at the same time");
        tabbedPane.addTab("Help", null, helpView, "Help guide");
        tabbedPane.setSelectedIndex(0);

        add(tabbedPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    @Override
    public void update() {
        statusLabel.setText(model.getStatusText());
        statusLabel.validate();
        statusLabel.repaint();

        if (statusLabel.getText().equals("-1")) {
            dispose();
        }

    }
}