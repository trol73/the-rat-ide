package ru.trolsoft.templatebuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Properties {

    private static final String FORMAT_TIME = "HH:mm:ss";
    private static final String FORMAT_DATE = "MM-dd-yyyy";
    private static final String FORMAT_DATETIME = "MM-dd-yyyy HH:mm:ss";


    private final Map<String, String> properties = new HashMap<>();
    private final Map<String, String> titles = new HashMap<>();

    private String processedFileName = "";

    public Properties() {
        set("home.path", Utils.getHomePath());
        set("date", getDateStr());
        set("time", getTimeStr());
        set("datetime", getDateTimeStr());
    }


    public void set(String name, String value) {
        properties.put(name, value);
    }

    public String get(String name) {
        if ("file.name".equals(name)) {
            return Utils.getFileName(processedFileName);
        } else if ("file.path".equals(name)) {
            return Utils.getFilePath(processedFileName);
        } else if ("file.fullpath".equals(name)) {
            return processedFileName;
        }
        return properties.get(name);
    }


    public boolean exist(String name) {
        if (
//			"date".equals(name) ||
//			"time".equals(name) ||
//			"datetime".equals(name) ||
                "file.name".equals(name) ||
                        "file.path".equals(name) ||
                        "file.fullpath".equals(name)
        ) {
            return true;
        }
        return properties.containsKey(name);
    }


    public void load(String fileName) throws IOException {
        try (BufferedReader f = new BufferedReader(new FileReader(fileName))) {
            String s;
            int line = 0;
            while ((s = f.readLine()) != null) {
                line++;
                s = new String(s.getBytes());

                if (s.length() == 0 || s.charAt(0) == '#') {
                    continue;
                }
                int pos = s.indexOf('=');
                if (pos < 0) {
                    pos = s.indexOf("  ");
                }
                if (pos < 0) {
                    pos = s.indexOf("\t");
                }
                if (pos < 0) {
                    pos = s.length();
                }
                String name = s.substring(0, pos).trim();
                String value = s.substring(pos + 1).trim();
                set(name, value);
            } //
        }
    }


    public String replaceProperties(String src) {
        String result = src;
        int pos = 0;
        while (true) {
            pos = result.indexOf("${", pos);
            if (pos < 0) {
                break;
            }
            pos += 2;
            int posEnd = result.indexOf("}", pos);
            if (posEnd < 0) {
                break;
            }
            String key = result.substring(pos, posEnd);
            pos = posEnd;
            if (exist(key)) {
                result = result.replace("${" + key + "}", get(key));
                pos = 0;
            }
        }
        return result;
    }


    public String getBasePath() {
        return get("base.path");
    }

    public void setBasePath(String path) {
        set("base.path", path);
    }

    public void setProjectName(String name) {
        set("project.name", name);
    }

    public void setProcessedFileName(String fileName) {
        this.processedFileName = fileName;
    }

    private String getDateStr() {
        String fmt = get("format.date");
        if (fmt == null) {
            fmt = FORMAT_DATE;
        }
        return new SimpleDateFormat(fmt).format(new Date());
    }

    private String getTimeStr() {
        String fmt = get("format.time");
        if (fmt == null) {
            fmt = FORMAT_TIME;
        }
        return new SimpleDateFormat(fmt).format(new Date());
    }


    private String getDateTimeStr() {
        String fmt = get("format.datetime");
        if (fmt == null) {
            fmt = FORMAT_DATETIME;
        }
        return new SimpleDateFormat(fmt).format(new Date());
    }


    public void setTitle(String name, String title) {
        titles.put(name, title);
    }


    public String getTitle(String name) {
        return titles.get(name);
    }


    public void requestUndefinedProperties() {
        for (String name : properties.keySet()) {
            String value = properties.get(name);
            if (value == null) {
                String title = getTitle(name);
                value = TemplateBuilder.input("Enter " + title + ": ");
                set(name, value);
            }
        }
    }


}
