package ru.trolsoft.templatebuilder;

import java.io.File;

public class TemplateDir extends TemplateItem {
	private String name;

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public String getFullName() {
		return getProperties().getBasePath() + File.separatorChar + getProperties().replaceProperties(name);
		// return TemplateBuilder.getProperties().replaceProperties(name);
	}

	public void create() {
		if ( !isIfConditionTrue() ) {
			return;
		}
		//TemplateBuilder.con.info("  Create dir: ", getFullName()).nl();
		new File(getFullName()).mkdirs();
	}
	
	
	
}
