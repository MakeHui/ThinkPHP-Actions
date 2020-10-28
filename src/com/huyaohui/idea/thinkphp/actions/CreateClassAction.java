package com.huyaohui.idea.thinkphp.actions;

import com.huyaohui.idea.thinkphp.forms.CreateClassForm;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.util.Set;


public class CreateClassAction extends AnAction {
    private Project mProject;
    private VirtualFile mSelectedFile;
    private String mClassName;
    private String mProductName;
    private final Set<String> mFolders;

    /**
     * 创建类型枚举
     */
    private enum  CodeType {
        Controller, Error, Exception, Validate
    }

    public CreateClassAction() {
        mFolders = Set.of(CodeType.Controller.toString().toLowerCase(), CodeType.Error.toString().toLowerCase(),
                CodeType.Exception.toString().toLowerCase(), CodeType.Validate.toString().toLowerCase()
        );
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        mProject = e.getData(PlatformDataKeys.PROJECT);
        init();
    }

    @Override
    public void update(AnActionEvent e) {
        setSelectedFile(e.getData(PlatformDataKeys.VIRTUAL_FILE));
        checkFolders();
        e.getPresentation().setEnabled(mSelectedFile != null);
    }

    /**
     * 初始化Dialog
     */
    private void init(){
        CreateClassForm myDialog = new CreateClassForm(new CreateClassForm.DialogCallBack() {
            @Override
            public void ok(String className) {
                mClassName = className;
                createClassFiles();
            }
        });
        myDialog.setVisible(true);
    }

    /**
     * 生成类文件
     */
    private void createClassFiles() {
        VirtualFile[] files = mSelectedFile.getChildren();

        if (!existFolders(files)) {
            Messages.showInfoMessage(mProject, "Please check subfolders is contains","Error");
            return;
        }

        createClassFile(CodeType.Controller);
        createClassFile(CodeType.Error);
        createClassFile(CodeType.Exception);
        createClassFile(CodeType.Validate);
        mSelectedFile.refresh(true, true);
    }

    /**
     * 生成mvp框架代码
     * @param codeType 类型
     */
    private void createClassFile(CodeType codeType) {
        String fileName = codeType + ".txt";
        String content = readTemplateFile(fileName);
        content = dealTemplateContent(content);
        writeToFile(content, mSelectedFile.getPath() + "/" + codeType.toString().toLowerCase() + "/", mClassName + codeType + ".php");
    }

    /**
     * 生成
     * @param content 类中的内容
     * @param classPath 类文件路径
     * @param className 类文件名称
     */
    private void writeToFile(String content, String classPath, String className) {
        try {
            File floder = new File(classPath);
            if (!floder.exists()){
                boolean b = floder.mkdirs();
            }

            File file = new File(classPath + "/" + className);
            if (!file.exists()) {
                boolean b = file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean existFolders(VirtualFile[] files) {
        int count = 0;
        for (VirtualFile children : files) {
            String filePath = children.getPath();
            if (mFolders.contains(getLastPath(filePath))) {
                count += 1;
            }
        }
        return count == mFolders.size();
    }

    private void checkFolders() {
        VirtualFile[] files = mSelectedFile.getChildren();
        if (existFolders(files)) {
            return;
        }
        if (mFolders.contains(mProductName)) {
            files = mSelectedFile.getParent().getChildren();
            if (existFolders(files)) {
                setSelectedFile(mSelectedFile.getParent());
                return;
            }
        }
        mSelectedFile = null;
    }

    private void setSelectedFile(VirtualFile file) {
        mSelectedFile = file;
        assert mSelectedFile != null;
        mProductName = getLastPath(mSelectedFile.getPath());
    }

    private String getLastPath(String filePath) {
        String[] splitList = filePath.split("/");
        if (splitList.length == 0) {
            return "";
        }
        return splitList[splitList.length - 1];
    }


    /**
     * 替换模板中字符
     * @param content
     * @return
     */
    private String dealTemplateContent(String content) {
        content = content.replace("$className", mClassName);
        content = content.replace("$productName", mProductName);
        return content;
    }

    /**
     * 读取模板文件中的字符内容
     * @param fileName 模板文件名
     */
    private String readTemplateFile(String fileName) {
        InputStream in = null;
        in = this.getClass().getResourceAsStream("/com/huyaohui/idea/thinkphp/templates/" + fileName);
        String content = "";
        try {
            content = new String(readStream(in));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 读取数据
     * @param inputStream
     */
    private byte[] readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (inputStream; outputStream) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }


}
