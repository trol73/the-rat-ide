package ru.trolsoft.ide.config.history;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FileList {
    private final List<String> list = new ArrayList<>();
    private boolean enabled = true;

    public void add(String fileName) {
        if (enabled && !list.contains(fileName)) {
            list.add(fileName);
        }
    }

    public void remove(String fileName) {
        if (enabled) {
            list.remove(fileName);
        }
    }

    public void load(String path) {
        list.clear();
        try (Stream<String> lines = Files.lines(Paths.get(path), Charset.defaultCharset())) {
            lines.forEachOrdered(list::add);
        } catch (NoSuchFileException ignore) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(String path) {
        try (FileWriter writer = new FileWriter(path)) {
            for (String s : list) {
                writer.write(s);
                writer.write('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void forEach(Consumer<String> consumer) {
        enabled = false;
        list.forEach(consumer);
        enabled = true;
    }
}
