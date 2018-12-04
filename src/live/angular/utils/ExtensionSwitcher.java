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
    private AnActionEvent event;

    public ExtensionSwitcher(AnActionEvent event) {
        this.event = event;
    }

    private final List<List<String>> typeGroups = Lists.newArrayList(
            Lists.newArrayList("html"),
            Lists.newArrayList("ts"),
            Lists.newArrayList("spec.ts"),
            Lists.newArrayList("css", "scss", "less", "styl")
    );

    public void switchToNext(int step) {
        // 找到当前扩展名的组号
        String currentExtension = getCurrentFile().map(VirtualFile::getCanonicalPath).map(this::getExtension).get();
        int groupIndex = groupIndexOf(currentExtension);
        if (groupIndex == -1) {
            return;
        }
        String basePath = getCurrentFile().map(VirtualFile::getCanonicalPath).map(this::getNameWithoutExtension).get();
        // 切换到下一组，如果任何一组没有文件，则跳过
        int nextIndex = groupIndex;
        // 最多循环 typeGroup.size() 次，以免不小心出现死循环
        for (int i = 0; i < typeGroups.size(); ++i) {
            nextIndex = nextIndexOf(nextIndex, step);
            List<String> nextGroup = typeGroups.get(nextIndex);
            if (switchToGroup(nextGroup, basePath)) {
                break;
            }
        }
    }

    int nextIndexOf(int nextIndex, int step) {
        return (nextIndex + step + typeGroups.size()) % typeGroups.size();
    }

    private boolean switchToGroup(List<String> nextGroup, String basePath) {
        for (String extension : nextGroup) {
            Optional<VirtualFile> file = findFileByPath(basePath + "." + extension);
            if (file.isPresent()) {
                openFile(file.get());
                return true;
            }
        }
        return false;
    }

    int groupIndexOf(String extension) {
        int groupIndex = -1;
        for (int i = 0; i < typeGroups.size(); ++i) {
            if (typeGroups.get(i).contains(extension)) {
                groupIndex = i;
            }
        }
        return groupIndex;
    }

    String getExtension(String filename) {
        if (noExtension(filename)) {
            return "";
        }
        List<String> types = typeGroups.stream()
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
