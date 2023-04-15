package ru.trolsoft.templatebuilder;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Template {
    public String name;
    public String description;
    public String fileName;

    private final List<TemplateItem> items = new ArrayList<TemplateItem>();


    public void addItem(TemplateItem item) {
        items.add(item);
    }

    public void setProperty(String name, String value) {
        TemplateBuilder.getProperties().set(name, value);
    }

    public void loadProperties(String fileName) throws IOException {
        TemplateBuilder.getProperties().load(fileName);
    }

    public void create(String basePath, String projectName) throws IOException {
        for (TemplateItem item : items) {
            item.create();
        }
    }

}
