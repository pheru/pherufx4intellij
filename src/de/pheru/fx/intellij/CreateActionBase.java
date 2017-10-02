package de.pheru.fx.intellij;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNameHelper;
import com.intellij.psi.PsiPackage;

public abstract class CreateActionBase extends AnAction {

    public abstract void execute(final Project project, final String qualifiedTargetPackage);

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
        final PsiDirectory targetDirectory = PsiManager.getInstance(event.getProject()).findDirectory(DataKeys.VIRTUAL_FILE.getData(event.getDataContext()));
        final VirtualFile sourceRootFile = ProjectFileIndex.SERVICE.getInstance(event.getProject()).getSourceRootForFile(DataKeys.VIRTUAL_FILE.getData(event.getDataContext()));
        final PsiDirectory sourceRootDirectory = PsiManager.getInstance(event.getProject()).findDirectory(sourceRootFile);
        final String qualifiedPackageForTargetDirectory = getQualifiedPackageForTargetDirectory(sourceRootDirectory, targetDirectory);
        execute(event.getProject(), qualifiedPackageForTargetDirectory);
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

}

