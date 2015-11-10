package de.pheru.fx.intellij.createview;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.help.UnsupportedOperationException;
import javax.swing.*;

public class CreateViewDialog extends DialogWrapper {

    private JTextField viewNameField;
    private JCheckBox createCssBox;
    private JCheckBox createResourceBundleBox;
    private JCheckBox makePresenterInitializableBox;

    protected CreateViewDialog(@Nullable Project project) {
        super(project, true);
        init();
        setTitle("PheruFXView");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return new JPanel();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return super.doValidate(); //TODO
    }

    public String getViewName(){
        return "TestViewName";
//        throw new UnsupportedOperationException();
    }

    public boolean isCreateCssSelected(){
        throw new UnsupportedOperationException();
    }

    public boolean isCreateResourceBundleSelected(){
        throw new UnsupportedOperationException();
    }

    public boolean isMakePresenterInitializableSelected(){
        throw new UnsupportedOperationException();
    }
}
