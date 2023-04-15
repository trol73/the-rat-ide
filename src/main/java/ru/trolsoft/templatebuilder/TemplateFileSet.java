package ru.trolsoft.templatebuilder;

import java.io.File;
import java.io.IOException;

public class TemplateFileSet extends TemplateItem {
	
	private String srcDir;
	private String outDir;
	public String getSrcDir() {
		return srcDir;
	}

	public void setSrcDir(String dir) {
		this.srcDir = dir;
	}
	
	public String getOutDir() {
		return outDir;
	}
	public void setOutDir(String dir) {
		this.outDir = dir;
	}
	private String getFullSrcName() {
		return getProperties().replaceProperties(srcDir);
	}
	private String getFullOutName() {
		return getProperties().getBasePath() + File.separatorChar + getProperties().replaceProperties(outDir);
	}

	@Override
	public void create() throws IOException {
		if ( !isIfConditionTrue() ) {
			return;
		}
		//TemplateBuilder.con.info("  Copy files ", "from " + getFullSrcName() + " to " + getFullOutName()).nl();

		File srcDir = new File(getFullSrcName());
		if ( !srcDir.exists() || !srcDir.isDirectory() ) {
			TemplateBuilder.error("directory isn't exist - " + getFullSrcName());
		}
		Utils.copyDir(getFullSrcName(), getFullOutName());
	}

}
