package de.pheru.fx.intellij.createview;

import com.intellij.openapi.compiler.Validator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiNameHelper;
import org.jetbrains.annotations.Nullable;

import javax.help.UnsupportedOperationException;
import javax.swing.*;

public class CreateViewDialog extends DialogWrapper {

    private Project project;

    private JTextField viewNameField;
    private JCheckBox createCssBox;
    private JCheckBox createResourceBundleBox;
    private JCheckBox makePresenterInitializableBox;

    protected CreateViewDialog(Project project) {
        super(project, true);
        this.project = project;
        viewNameField = new JTextField();
        createCssBox = new JCheckBox("Create CSS");
        createResourceBundleBox = new JCheckBox("Create ResourceBundle");
        makePresenterInitializableBox = new JCheckBox("Make Presenter Initializable");
        init();
        setTitle("PheruFXView");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(viewNameField);
        panel.add(createCssBox);
        panel.add(createResourceBundleBox);
        panel.add(new JSeparator(JSeparator.HORIZONTAL));
        panel.add(makePresenterInitializableBox);
        return panel;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String viewName = viewNameField.getText();
        if (viewName.length() > 0 && !PsiNameHelper.getInstance(project).isQualifiedName(viewName)) {
            return new ValidationInfo("This is not a valid Java qualified name!");
        }
        return null;

    }

    public String getViewName() {
        return viewNameField.getText();
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
}
