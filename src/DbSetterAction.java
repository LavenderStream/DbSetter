import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.CollectionListModel;

import java.util.Arrays;
import java.util.List;

public class DbSetterAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromEvent(e);

        GenerateDialog dialog = new GenerateDialog(psiClass);
        dialog.show();

     /*    PsiField[] allFields = psiClass.getAllFields();
        PsiField[] fields = new PsiField[allFields.length];

        int i = 0;

        for (PsiField field : allFields) {
            if (!field.hasModifierProperty(PsiModifier.STATIC)) {
                fields[i++] = field;
            }
        }

        fields = Arrays.copyOfRange(fields, 0, i);
       CollectionListModel<PsiField> myFields  = new CollectionListModel<PsiField>(fields);;

        generateAccessors(psiClass, myFields.getItems());*/



        if (dialog.isOK()) {
            generateAccessors(psiClass, dialog.getSelectedFields());
        }
    }


    private void generateAccessors(final PsiClass psiClass, final List<PsiField> fields) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                new CodeGenerator(psiClass, fields).generate();
            }
        }.execute();
    }

    private PsiClass getPsiClassFromEvent(AnActionEvent event) {
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);

        if (psiFile == null || editor == null) {
            return null;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);

        return PsiTreeUtil.getParentOfType(element, PsiClass.class);
    }
}
