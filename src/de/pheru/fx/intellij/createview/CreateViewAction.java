package de.pheru.fx.intellij.createview;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNameHelper;
import com.intellij.psi.PsiPackage;

import java.util.Properties;

/**
 * Created by Philipp on 10.11.2015.
 */
public class CreateViewAction extends AnAction {

    //                NotificationGroup GROUP_DISPLAY_ID_INFO = new NotificationGroup("Group", NotificationDisplayType.BALLOON, true);
//                Notification notification = GROUP_DISPLAY_ID_INFO.createNotification("Title", "Test",
//                        NotificationType.INFORMATION, null);
//                Notifications.Bus.notify(notification, e.getProject());

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
    public void actionPerformed(AnActionEvent event) {
        PsiDirectory targetDirectory = PsiManager.getInstance(event.getProject()).findDirectory(DataKeys.VIRTUAL_FILE.getData(event.getDataContext()));
        VirtualFile sourceRootFile = ProjectFileIndex.SERVICE.getInstance(event.getProject()).getSourceRootForFile(DataKeys.VIRTUAL_FILE.getData(event.getDataContext()));
        PsiDirectory sourceRootDirectory = PsiManager.getInstance(event.getProject()).findDirectory(sourceRootFile);

        String qualifiedPackageForTargetDirectory = getQualifiedPackageForTargetDirectory(sourceRootDirectory, targetDirectory);
        CreateViewDialog dialog = new CreateViewDialog(event.getProject(), qualifiedPackageForTargetDirectory.isEmpty() ? "" : qualifiedPackageForTargetDirectory + ".");
        dialog.show();
        if (dialog.getExitCode() != 0) {
            return;
        }
        String input = dialog.getInput();
        String viewName;
        String qualifiedPackageName;
        if (input.contains(".")) {
            viewName = input.substring(input.lastIndexOf('.') + 1);
            qualifiedPackageName = input.substring(0, input.lastIndexOf('.'));
        } else {
            viewName = input;
            qualifiedPackageName = "";
        }

        Properties properties = FileTemplateManager.getDefaultInstance().getDefaultProperties();
        properties.setProperty("VIEWNAME", viewName);
        properties.setProperty("INITIALIZABLE", String.valueOf(dialog.isMakePresenterInitializableSelected()));

        WriteCommandAction.runWriteCommandAction(event.getProject(), () -> {
            try {
                PsiDirectory directory = createDirectories(sourceRootDirectory, qualifiedPackageName, viewName.toLowerCase());
                PsiElement fileToSelect = createFiles(event.getProject(), directory, properties, viewName, dialog);
                ProjectView.getInstance(event.getProject()).selectPsiElement(fileToSelect, false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getQualifiedPackageForTargetDirectory(PsiDirectory sourceRootDirectory, PsiDirectory targetDirectory) {
        if (sourceRootDirectory == targetDirectory) {
            return "";
        }
        PsiDirectory currentDirectory = targetDirectory;
        StringBuilder qualifiedPackageNameBuilder = new StringBuilder(currentDirectory.getName());
        while (currentDirectory.getParentDirectory() != sourceRootDirectory) {
            currentDirectory = currentDirectory.getParentDirectory();
            qualifiedPackageNameBuilder.insert(0, ".");
            qualifiedPackageNameBuilder.insert(0, currentDirectory.getName());
        }
        return qualifiedPackageNameBuilder.toString();
    }

    private PsiDirectory createDirectories(PsiDirectory sourceRootDirectory, String qualifiedPackageName, String viewPackageName) {
        PsiDirectory currentDirectory = sourceRootDirectory;
        for (String s : qualifiedPackageName.split("\\.")) {
            if (!s.isEmpty()) {
                PsiDirectory subdirectory = currentDirectory.findSubdirectory(s);
                if (subdirectory == null) {
                    subdirectory = currentDirectory.createSubdirectory(s);
                }
                currentDirectory = subdirectory;
            }
        }
        currentDirectory = currentDirectory.createSubdirectory(viewPackageName);
        return currentDirectory;
    }

    private PsiElement createFiles(Project project, PsiDirectory directory, Properties properties, String viewName,
                                   CreateViewDialog dialog) throws Exception {
        FileTemplate viewTemplate = FileTemplateManager.getInstance(project).getTemplate("PheruFXView");
        FileTemplate presenterTemplate = FileTemplateManager.getInstance(project).getTemplate("PheruFXPresenter");
        FileTemplate fxmlTemplate = FileTemplateManager.getInstance(project).getTemplate("PheruFXML");
        FileTemplate cssTemplate = FileTemplateManager.getInstance(project).getTemplate("PheruFXCss");
        FileTemplate resourceBundleTemplate = FileTemplateManager.getInstance(project).getTemplate("PheruFXResourceBundle");

        if (dialog.isCreateResourceBundleSelected()) {
            createFile(resourceBundleTemplate, viewName.toLowerCase(), properties, directory, dialog.isOpenResourceBundleInEditorSelected());
        }
        if (dialog.isCreateCssSelected()) {
            createFile(cssTemplate, viewName.toLowerCase(), properties, directory, dialog.isOpenCSSInEditorSelected());
        }
        PsiElement view = createFile(viewTemplate, viewName + "View", properties, directory, dialog.isOpenViewInEditorSelected());
        createFile(fxmlTemplate, viewName.toLowerCase(), properties, directory, dialog.isOpenFXMLInEditorSelected());
        createFile(presenterTemplate, viewName + "Presenter", properties, directory, dialog.isOpenPresenterInEditorSelected());
        return view;
    }

    private PsiElement createFile(FileTemplate template, String name, Properties properties, PsiDirectory directory, boolean openInEditor) throws Exception {
        PsiElement file = FileTemplateUtil.createFromTemplate(template, name, properties, directory);
        if (openInEditor) {
            file.getContainingFile().navigate(true);
        }
        return file;
    }

}
