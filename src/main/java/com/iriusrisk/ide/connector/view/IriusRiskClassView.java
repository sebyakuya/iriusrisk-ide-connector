package com.iriusrisk.ide.connector.view;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.iriusrisk.ide.connector.controllers.IriusRiskClassController;
import com.iriusrisk.ide.connector.controllers.IriusRiskMainController;
import com.iriusrisk.ide.connector.models.IriusRiskClassModel;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.TableColumn;

public class IriusRiskClassView extends JPanel implements View {

    private final IriusRiskMainController controller;
    private final IriusRiskClassController classController;
    private final IriusRiskClassModel classModel;

    JBTable table;

    public IriusRiskClassView(IriusRiskMainController controller, IriusRiskClassController classController, IriusRiskClassModel classModel) {
        this.controller = controller;
        this.classController = classController;
        this.classModel = classModel;
        classModel.registerView(this);
        render();
    }

    @Override
    public void render() {
        setLayout(new BorderLayout());

        classController.detectClasses();
        classController.getRelationsFromStorage();

        //First pane:
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table

        table = new JBTable();
        table.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        table.setAutoResizeMode(JBTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setModel(classModel.getDtm());
        table.setAutoCreateRowSorter(true);

        classController.setCombobox();

        ComboBox<String> componentCombobox = new ComboBox<>(classModel.getComboModel());
        componentCombobox.setSelectedItem("");
        TableColumn componentComboboxCell = table.getColumnModel().getColumn(1);
        componentComboboxCell.setCellEditor(new DefaultCellEditor(componentCombobox));
        TableColumn parentTextfieldCell = table.getColumnModel().getColumn(2);
        parentTextfieldCell.setCellEditor(new DefaultCellEditor(new JBTextField()));
        JBScrollPane scrollPane = new JBScrollPane(table);

        table.getModel().addTableModelListener(e -> controller.setStatusText(""));


        // Buttons

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> controller.setStatusText("-1"));

        JButton restoreButton = new JButton("Restore");
        restoreButton.addActionListener(e -> {
            classController.restoreOperation();
            controller.setStatusText("Restored to previous settings");

        });

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String message = "Threat model saved!";
            try{
                classController.saveRelations();
            } catch (Exception ex){
                message = ex.getMessage();
            }
            controller.setStatusText(message);

        });

        JButton sendToIriusRiskButton = new JButton("Send to IriusRisk");
        sendToIriusRiskButton.addActionListener(e -> controller.setStatusText(classController.updateThreatModel(controller)));


        JPanel buttons = new JPanel(new BorderLayout());
        JPanel leftButtons = new JPanel(new GridLayout(1, 3));
        leftButtons.add(cancelButton);
        leftButtons.add(restoreButton);
        leftButtons.add(saveButton);
        JPanel rightButtons = new JPanel(new GridLayout(1, 1));
        rightButtons.add(sendToIriusRiskButton);
        buttons.add(leftButtons, BorderLayout.WEST);
        buttons.add(rightButtons, BorderLayout.EAST);


        // Main panel

        add(scrollPane, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }


    @Override
    public void update() {

    }
}
