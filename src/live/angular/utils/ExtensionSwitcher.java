package live.angular.utils;

import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.impl.EditorHistoryManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ExtensionSwitcher {
    private final AnActionEvent event;

    public ExtensionSwitcher(AnActionEvent event) {
        this.event = event;
    }

    private final List<String> templateExtensions = Lists.newArrayList("html", "svg");
    private final List<String> componentExtensions = Lists.newArrayList("ts");
    private final List<String> testExtensions = Lists.newArrayList("spec.ts");
    private final List<String> styleExtensions = Lists.newArrayList("css", "scss", "less", "styl");
    private final List<List<String>> groups = Lists.newArrayList(templateExtensions, componentExtensions, testExtensions, styleExtensions);

    public void switchToTemplate() {
        switchToGroup(templateExtensions);
    }

    public void switchToComponent() {
        switchToGroup(componentExtensions);
    }

    public void switchToStyle() {
        switchToGroup(styleExtensions);
    }

    public void switchToTest() {
        switchToGroup(testExtensions);
    }

    private void switchToGroup(List<String> nextGroup) {
        String basePath = getCurrentBasePath();
        for (String extension : nextGroup) {
            Optional<VirtualFile> file = findFileByPath(basePath + "." + extension);
            if (file.isPresent()) {
                openFile(file.get());
                return;
            }
        }
    }

    @NotNull
    private String getCurrentBasePath() {
        return getCurrentFile().map(VirtualFile::getCanonicalPath).map(this::getNameWithoutExtension).orElseThrow(BasePathNotFoundException::new);
    }

    String getExtension(String filename) {
        if (noExtension(filename)) {
            return "";
        }
        List<String> types = groups.stream()
                .flatMap(Collection::stream)
                // 把字符串反转后再倒序排序，确保 spec.ts 排在 ts 前面
                .sorted(Comparator.comparing(this::reverseText).reversed())
                .collect(Collectors.toList());
        for (String type : types) {
            if (filename.endsWith("." + type)) {
                return type;
            }
        }
        return "";
    }

    @NotNull
    private String reverseText(String text) {
        return new StringBuilder(text).reverse().toString();
    }

    String getNameWithoutExtension(String filename) {
        if (noExtension(filename)) {
            return filename;
        }
        return filename.replaceAll("." + getExtension(filename) + "$", "");
    }

    private boolean noExtension(String filename) {
        return !filename.contains(".");
    }

    private Project getProject() {
        return event.getData(PlatformDataKeys.PROJECT);
    }

    private Optional<VirtualFile> findFileByPath(String path) {
        return Optional.ofNullable(LocalFileSystem.getInstance().findFileByPath(path));
    }

    private List<VirtualFile> getRecentFiles() {
        VirtualFile[] files = EditorHistoryManager.getInstance(getProject()).getFiles();
        return Lists.reverse(Arrays.asList(files));
    }

    private Optional<VirtualFile> getCurrentFile() {
        try {
            return Optional.ofNullable(getRecentFiles().get(0));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    private void openFile(@NotNull VirtualFile file) {
        new OpenFileDescriptor(getProject(), file).navigate(true);
    }
}
