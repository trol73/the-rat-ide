package ru.trolsoft.templatebuilder;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import org.xml.sax.SAXParseException;


/*

<template name="" description="">
    <property name="" value="">
    <property file="">

    <file source="" name="" binary="false" replaceProperties="true">
    </file>

    <dir name=""/>

<!--
    <option name="" description="" type="bool|int|enum|str">
        <enum>
            <item name="" value="" />
        <enum>
    </option>
-->
</template>

*/

public class TemplateBuilder {

    private static final String VERSION = "0.13";


    private static Properties properties = new Properties();
    private static TemplateBuilder instance;
    private static Templates templates = new Templates();

   // public static Console con = new Console();


    public static TemplateBuilder getInstance() {
        if (instance == null) {
            instance = new TemplateBuilder();
        }
        return instance;
    }


    public static Properties getProperties() {
        return properties;
    }


    public static void create(String basePath, String templateName, String projectName) throws IOException {
        properties.setBasePath(basePath);
        properties.setProjectName(projectName);

        // need to reload only selected template
        String templateFileName = templates.getTemplate(templateName).fileName;
        templates.clearList();
        try {
            templates.load(templateFileName, true);
        } catch (Exception e) {
            error("Can't reload template " + templateFileName);
        }

        properties.requestUndefinedProperties();
        templates.getTemplate(templateName).create(basePath, projectName);
    }


    public static void println(String s) {
        System.out.println(s);
    }


    public static void println() {
        println("");
    }


    public static void print(String s) {
        System.out.print(s);
    }


    public static void error(String s) {
        //con.error("ERROR: ", s);
        System.exit(1);
    }


    public static String input(String message) {
        print(message);
        System.out.flush();
        Scanner in = new Scanner(System.in);
        String result = "";
        do {
            result = in.nextLine();
            print(message);
        } while (result.trim().length() == 0);
        //in.close();
        return result;
    }


//    public static void about() {
//        con.nl();
//        con.color(Color.White, true).out("Template projects creator. ").reset().out("Version ").color(Color.Green, true).out(VERSION).reset().nl();
//        con.out("Copyright (c) Oleg Trifonov, 2010-2012").nl();
//        con.color(Color.White, true).out("Usage: ").reset().out("templater [options] [<template name>] [<project name>] [<project path>]").nl();
//        con.color(Color.White, true).out("Options:").nl();
//        con.color(Color.Cyan, true).out("      -D name1=value,name2='value 2'			- define properties").reset().nl();
//        con.nl();
//        System.exit(0);
//    }


    public static void loadTemplates(String path) throws Exception {
        File f = new File(path);
        File[] childs = f.listFiles();
        for (File child : childs) {
            if (child.isDirectory()) {
                loadTemplates(child.getPath());
            } else if ("xml".equalsIgnoreCase(Utils.getFileExtension(child.getName()))) {
                templates.load(child.getPath(), false);
            }

        }
    }


    public static void main(String[] args) throws Exception {
        String projectName;
        String projectPath;
        String templateName;
        // FIXME use utils library for args parsing

//        if (args.length == 0) {
//            about();
//        }
        try {
            loadTemplates(Utils.getHomePath());
        } catch (SAXParseException ex) {
            error(ex.getMessage());
        }

        int baseArgIndex;
        if (args.length >= 2 && "-d".equalsIgnoreCase(args[0])) {
            String[] defs = args[1].split(",");
            for (String def : defs) {
                String[] nv = def.split("=");
                String name = nv[0];
                String value = nv.length > 1 ? nv[1] : "";
                if (nv.length > 2) {
                    error("invlaid paramters option - " + args[1]);
                    System.exit(100);
                }
                properties.set(name, value);
                //println("   define "+ name + " = " + value);
            }
            baseArgIndex = 2;
        } else {
            baseArgIndex = 0;
        }


        if (args.length >= baseArgIndex + 1) {
            templateName = args[baseArgIndex];
        } else {
            Map<String, String> descriptions = templates.getDescriptions();

            println("Templates: ");
            for (String name : templates.getTemplatesMap().keySet()) {
                String description = descriptions.get(name);
                println("   " + name + ":\t" + description);
            }
            templateName = input("Enter template name: ");
        }

        if (!templates.isTemplateExist(templateName)) {
            error("Template not found - " + templateName);
        }

        projectName = args.length >= baseArgIndex + 2 ? args[baseArgIndex + 1] : input("Enter project name: ");
        projectPath = args.length >= baseArgIndex + 3 ? args[baseArgIndex + 2] : input("Enter project path: ");

        create(projectPath, templateName, projectName);
       // con.color(Color.White, true).out("Done.").reset().nl();
    }

}
