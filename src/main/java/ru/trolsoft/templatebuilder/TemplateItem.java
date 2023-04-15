package ru.trolsoft.templatebuilder;

import java.io.IOException;

public abstract class TemplateItem {

	private String ifCondition;

	public abstract void create() throws IOException;


	protected static Properties getProperties() {
		return TemplateBuilder.getProperties();
	}


	public String getIfCondition() {
		return ifCondition;
	}


	public void setIfCondition(String ifCondition) {
		this.ifCondition = ifCondition;
	}


	protected boolean isIfConditionTrue() {
		if ( ifCondition == null ) {
			return true;
		}
		String operator;
		if ( ifCondition.indexOf("==") > 0 ) {
			operator = "==";
		} else if ( ifCondition.indexOf("!=") > 0 ) {
			operator = "!=";
		} else {
			throw new RuntimeException("invalid 'if' condition: " + ifCondition);
		}
		String s[] = ifCondition.split(operator);
		if ( s.length != 2 ) {
			throw new RuntimeException("invalid 'if' condition: " + ifCondition);
		}
		String name = s[0].trim();
		String value = s[1].trim();
		if ( "==".equals(operator) ) {
			return value.equals(getProperties().get(name));
		} else {
			return !value.equals(getProperties().get(name));
		}
	}


}
