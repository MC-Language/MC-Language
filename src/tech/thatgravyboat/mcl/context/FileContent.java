package tech.thatgravyboat.mcl.context;

import java.io.File;
import java.nio.file.Files;

public record FileContent(String packagePath, String className, String id, String content) {

    public FileContent(ClassContext context, String id, String content) {
        this(context.pPackage(), context.pClass(), id, content);
    }

    public String toFile() {
        if (id == null) throw new IllegalStateException("Id cannot be null");
        return "data/" + packagePath + "/functions/" + className + "/" + id + ".mcfunction";
    }

    public void writeToFile(String path) {
        File file = new File(path + "/" + toFile());
        file.getParentFile().mkdirs();
        try {
            Files.write(file.toPath(), content.getBytes());
        }catch (Exception e) {
            throw new RuntimeException("Failed to write to file " + file.getAbsolutePath(), e);
        }
    }
}
