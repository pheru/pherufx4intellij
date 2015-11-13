package de.pheru.fx.intellij.createview;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.PsiNameHelper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CreateViewDialog extends DialogWrapper {

    private Project project;

    private JTextField inputField = new JTextField();
    private JCheckBox createCssBox = new JCheckBox("Create CSS");
    private JCheckBox createResourceBundleBox = new JCheckBox("Create ResourceBundle");
    private JCheckBox makePresenterInitializableBox = new JCheckBox("Make Presenter Initializable");
    private JCheckBox openViewInEditor = new JCheckBox("View");
    private JCheckBox openPresenterInEditor = new JCheckBox("Presenter", true);
    private JCheckBox openFXMLInEditor = new JCheckBox("FXML", true);
    private JCheckBox openCSSInEditor = new JCheckBox("CSS");
    private JCheckBox openResourceBundleInEditor = new JCheckBox("ResourceBundle");

    private List<JCheckBox> openBoxes = createOpenBoxesList();

    private boolean focusListenerInvoked = false;

    private List<JCheckBox> createOpenBoxesList() {
        List<JCheckBox> l = new ArrayList<>();
        l.add(openCSSInEditor);
        l.add(openFXMLInEditor);
        l.add(openPresenterInEditor);
        l.add(openResourceBundleInEditor);
        l.add(openViewInEditor);
        return l;
    }

    protected CreateViewDialog(Project project, String input) {
        super(project, true);
        this.project = project;
        inputField.setText(input);
        inputField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!focusListenerInvoked) {
                    inputField.setSelectionStart(inputField.getText().length());
                    inputField.setSelectionEnd(inputField.getText().length());
                    focusListenerInvoked = true;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                //nothing
            }
        });
        init();
        setTitle("PheruFXView");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        addToPanelAlignLeft(panel, inputField);
        addToPanelAlignLeft(panel, createCssBox);
        addToPanelAlignLeft(panel, createResourceBundleBox);
        addToPanelAlignLeft(panel, new JSeparator(JSeparator.HORIZONTAL));
        addToPanelAlignLeft(panel, makePresenterInitializableBox);
        addToPanelAlignLeft(panel, new JSeparator(JSeparator.HORIZONTAL));
        addToPanelAlignLeft(panel, createOpenFilesPanel());
        return panel;
    }

    private JPanel createOpenFilesPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(new JLabel("Open in Editor"));
        topPanel.add(Box.createRigidArea(new Dimension(40, 0)));
        topPanel.add(createSelectUnselectAllLabel("Select all", true));
        topPanel.add(Box.createRigidArea(new Dimension(25, 0)));
        topPanel.add(createSelectUnselectAllLabel("Unselect all", false));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(openViewInEditor);
        bottomPanel.add(openPresenterInEditor);
        bottomPanel.add(openFXMLInEditor);
        bottomPanel.add(openCSSInEditor);
        bottomPanel.add(openResourceBundleInEditor);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        addToPanelAlignLeft(panel, topPanel);
        addToPanelAlignLeft(panel, bottomPanel);
        return panel;
    }

    private JLabel createSelectUnselectAllLabel(String text, boolean select) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.BLUE);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (JCheckBox box : openBoxes) {
                    box.setSelected(select);
                }
            }
        });
        return label;
    }

    private void addToPanelAlignLeft(JPanel panel, JComponent component) {
        panel.add(component);
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String input = inputField.getText();
        if (input.isEmpty() || (input.length() > 0 && !PsiNameHelper.getInstance(project).isQualifiedName(input))) {
            return new ValidationInfo("This is not a valid Java qualified name!");
        }
        return null;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return inputField;
    }

    public String getInput() {
        return inputField.getText();
    }

    public boolean isCreateCssSelected() {
        return createCssBox.isSelected();
    }

    public boolean isCreateResourceBundleSelected() {
        return createResourceBundleBox.isSelected();
    }

    public boolean isMakePresenterInitializableSelected() {
        return makePresenterInitializableBox.isSelected();
    }

    public boolean isOpenViewInEditorSelected() {
        return openViewInEditor.isSelected();
    }

    public boolean isOpenPresenterInEditorSelected() {
        return openPresenterInEditor.isSelected();
    }

    public boolean isOpenFXMLInEditorSelected() {
        return openFXMLInEditor.isSelected();
    }

    public boolean isOpenCSSInEditorSelected() {
        return openCSSInEditor.isSelected();
    }

    public boolean isOpenResourceBundleInEditorSelected() {
        return openResourceBundleInEditor.isSelected();
    }
}
