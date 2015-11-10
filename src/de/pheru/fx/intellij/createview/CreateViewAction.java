package de.pheru.fx.intellij.createview;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNameHelper;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.impl.file.PsiDirectoryFactory;

import java.util.Properties;

/**
 * Created by Philipp on 10.11.2015.
 */
public class CreateViewAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Project project = DataKeys.PROJECT.getData(e.getDataContext());
        VirtualFile[] files = DataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
        if (project != null && files != null && files.length == 1) {
            PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(files[0]);
            e.getPresentation().setVisible(psiDirectory != null && checkPackageExists(psiDirectory));
        } else {
            e.getPresentation().setVisible(false);
        }
    }

    private boolean checkPackageExists(PsiDirectory directory) {
        PsiPackage pkg = JavaDirectoryService.getInstance().getPackage(directory);
        if (pkg == null) {
            return false;
        } else {
            String name = pkg.getQualifiedName();
            return StringUtil.isEmpty(name) || PsiNameHelper.getInstance(directory.getProject()).isQualifiedName(name);
        }
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        CreateViewDialog dialog = new CreateViewDialog(e.getProject());
        dialog.show();
        if (dialog.getExitCode() != 0) {
            return;
        }
        Properties properties = FileTemplateManager.getDefaultInstance().getDefaultProperties();
        properties.setProperty("VIEWNAME", dialog.getViewName());
        properties.setProperty("INITIALIZABLE", String.valueOf(dialog.isMakePresenterInitializableSelected()));

        PsiDirectory directory = PsiManager.getInstance(e.getProject()).findDirectory(DataKeys.VIRTUAL_FILE.getData(e.getDataContext()));

        FileTemplate viewTemplate = FileTemplateManager.getInstance(e.getProject()).getTemplate("PheruFXView");
        FileTemplate presenterTemplate = FileTemplateManager.getInstance(e.getProject()).getTemplate("PheruFXPresenter");
        FileTemplate fxmlTemplate = FileTemplateManager.getInstance(e.getProject()).getTemplate("PheruFXML");
        FileTemplate cssTemplate = FileTemplateManager.getInstance(e.getProject()).getTemplate("PheruFXCss");
        FileTemplate resourceBundleTemplate = FileTemplateManager.getInstance(e.getProject()).getTemplate("PheruFXResourceBundle");

        WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
            try {
                PsiDirectory subdirectory = directory.createSubdirectory(dialog.getViewName().toLowerCase());
                FileTemplateUtil.createFromTemplate(viewTemplate, dialog.getViewName() + "View", properties, subdirectory);
                FileTemplateUtil.createFromTemplate(presenterTemplate, dialog.getViewName() + "Presenter", properties, subdirectory);
                FileTemplateUtil.createFromTemplate(fxmlTemplate, dialog.getViewName().toLowerCase(), properties, subdirectory);
                if (dialog.isCreateCssSelected()) {
                    FileTemplateUtil.createFromTemplate(cssTemplate, dialog.getViewName().toLowerCase(), properties, subdirectory);
                }
                if(dialog.isCreateResourceBundleSelected()){
                    FileTemplateUtil.createFromTemplate(resourceBundleTemplate, dialog.getViewName().toLowerCase(), properties, subdirectory);
                }
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
        });
    }
}
