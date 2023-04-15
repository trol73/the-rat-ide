package ru.trolsoft.templatebuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Templates {

    private Map<String, Template> templates = new HashMap<String, Template>();
    private static String currentFileName = "";


    public Templates() {

    }


    private static String getTagAttribute(Node node, String name) {
        Node nAttr = node.getAttributes().getNamedItem(name);
        return nAttr == null ? null : nAttr.getTextContent();
    }


    private static boolean getTagBoolAttribute(Node node, String name, boolean defaultVal) {
        String str = getTagAttribute(node, name);
        if (str == null) {
            return defaultVal;
        }
        if ("true".equalsIgnoreCase(str)) {
            return true;
        }
        if ("false".equalsIgnoreCase(str)) {
            return false;
        }
        error("invalid boolean value - " + str + " (<" + node.getNodeName() + "> attribute '" + name + "')");
        return defaultVal;
    }

    /*
    private static String getTagValue(Element eElement, String sTag) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);

        return nValue.getNodeValue();
    }*/


    private static void error(String msg) {
        String fs = currentFileName.length() > 0 ? "ERROR in " + currentFileName + ": " : "ERROR: ";
        //TemplateBuilder.con.error(fs, msg);
        System.exit(1);
    }


    private static void warning(String msg) {
        String fs = currentFileName.length() > 0 ? "WARNING in " + currentFileName + ": " : "WARNING: ";
        //TemplateBuilder.con.warning(fs, msg);
    }


    public void load(String fileName, boolean setProperties) throws Exception {
        currentFileName = fileName;
        File fXmlFile = new File(fileName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        doc.getDocumentElement().normalize();
        NodeList nTemplates = doc.getElementsByTagName("template");

        for (int iTemplate = 0; iTemplate < nTemplates.getLength(); iTemplate++) {
            Node nTemplate = nTemplates.item(iTemplate);
            Template template = new Template();
            String name = getTagAttribute(nTemplate, "name");
            String description = getTagAttribute(nTemplate, "description");
            template.name = name != null ? name : Utils.getFileNameWithoutExtension(fileName);
            template.description = description;
            template.fileName = fileName;
            if (templates.containsKey(name)) {
                warning("Template is already exist: " + name + " (file " + templates.get(name).fileName + ")");
            }
            templates.put(name, template);
            NodeList nChilds = nTemplate.getChildNodes();

            for (int iChild = 0; iChild < nChilds.getLength(); iChild++) {
                Node nChild = nChilds.item(iChild);

                String tagName = nChild.getNodeName();
                if ("file".equals(tagName)) {
                    TemplateFile tFile = new TemplateFile();
                    tFile.setName(getTagAttribute(nChild, "name"));
                    if (tFile.getName() == null) {
                        error("The 'name' attribute expected for <dir> tag");
                    }
                    tFile.setSource(getTagAttribute(nChild, "source"));
                    tFile.setContent(nChild.getTextContent());
                    if (tFile.getSource() == null && tFile.getContent() == null) {
                        error("The 'source' attribute or body must be defined for <file> tag");
                    }
                    tFile.setBinary(getTagBoolAttribute(nChild, "binary", true));
                    tFile.setReplaceProperties(getTagBoolAttribute(nChild, "replaceProperties", false));
                    tFile.setPreprocess(getTagBoolAttribute(nChild, "preprocess", false));
                    tFile.setIfCondition(getTagAttribute(nChild, "if"));
                    template.addItem(tFile);
                } else if ("dir".equals(tagName)) {
                    TemplateDir tDir = new TemplateDir();
                    tDir.setName(getTagAttribute(nChild, "name"));
                    if (tDir.getName() == null) {
                        error("The 'name' attribute expected for <dir> tag");
                    }
                    tDir.setIfCondition(getTagAttribute(nChild, "if"));
                    template.addItem(tDir);
                } else if ("fileset".equals(tagName)) {
                    TemplateFileSet tFS = new TemplateFileSet();
                    tFS.setSrcDir(getTagAttribute(nChild, "sourceDir"));
                    if (tFS.getSrcDir() == null) {
                        error("The 'sourceDir' attribute expected for <fileset> tag");
                    }
                    tFS.setOutDir(getTagAttribute(nChild, "outDir"));
                    if (tFS.getOutDir() == null) {
                        error("The 'outDir' attribute expected for <fileset> tag");
                    }
                    tFS.setIfCondition(getTagAttribute(nChild, "if"));
                    template.addItem(tFS);
                } else if ("property".equals(tagName)) {
                    String pName = getTagAttribute(nChild, "name");
                    String pValue = getTagAttribute(nChild, "value");
                    String pFile = getTagAttribute(nChild, "file");
                    String pTitle = getTagAttribute(nChild, "title");

                    if (pName == null && pFile == null) {
                        error("The 'name' or 'file' attribute expected for <property> tag");
                    }
                    if (pValue != null && pFile != null) {
                        error("to many attributes for <troperty> tag - 'value' and 'file'");
                    }
                    if (setProperties) {
                        if (pFile != null) {
                            template.loadProperties(TemplateBuilder.getProperties().replaceProperties(pFile));
                        } else {
                            template.setProperty(pName, pValue);
                        }
                    }

                    if (pTitle != null) {
                        TemplateBuilder.getProperties().setTitle(pName, pTitle);
                    } else if (pValue == null && pFile == null) {
                        error("'value' or 'title' or 'file' attribute must be defined for <property> tag");
                    }
                } else if (!"#text".equals(tagName) && !"#comment".equals(tagName)) {
                    warning("unknown tag - <" + tagName + ">");
                }
            }
        }
        currentFileName = "";
    }


    public boolean isTemplateExist(String name) {
        return templates.containsKey(name);
    }


    public Template getTemplate(String name) {
        return templates.get(name);
    }

    public Map<String, Template> getTemplatesMap() {
        return templates;
    }


    public void clearList() {
        templates.clear();
    }


    public Map<String, String> getDescriptions() {
        Map<String, String> map = new HashMap<>();

        for (String name : templates.keySet()) {
            String description = templates.get(name).description;
            map.put(name, description);
        }
        return map;
    }


}
