package ru.trolsoft.ide.config.history;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class OpenFileList {
    private final List<String> list = new ArrayList<>();

    public void add(String fileName) {
        if (!list.contains(fileName)) {
            list.add(fileName);
        }
    }

    public void remove(String fileName) {
        list.remove(fileName);
    }

    public void load(String path) {
        list.clear();
        try (Stream<String> lines = Files.lines(Paths.get(path), Charset.defaultCharset())) {
            lines.forEachOrdered(list::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(String path) {
        try (FileWriter writer = new FileWriter(path)) {
            for (String s : list) {
                writer.write(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void forEach(Consumer<String> consumer) {
        list.forEach(consumer);
    }
}
