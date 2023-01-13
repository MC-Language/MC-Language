package tech.thatgravyboat.mcl.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FetchedFile {

    private final Path path;
    private final String content;
    private final String name;

    public FetchedFile(Path path) throws IOException {
        this.path = path;
        this.content = Files.readString(path);
        this.name = path.getFileName().toString().replaceFirst("[.][^.]+$", "");
    }

    public Path getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }
}
