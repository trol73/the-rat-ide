/*
 * 12/18/2010
 *
 * ViewConsoleAction.java - Toggles visibility of the console dockable window.
 * Copyright (C) 2010 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.buildoutput;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import org.fife.rtext.RText;
import org.fife.ui.app.AppAction;


/**
 * Toggles visibility of the console dockable window.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class ViewBuildOutputAction extends AppAction<RText> {

	/**
	 * The parent plugin.
	 */
	private final Plugin plugin;


	/**
	 * Constructor.
	 *
	 * @param owner The parent RText instance.
	 * @param msg The resource bundle to use for localization.
	 * @param plugin The parent plugin.
	 */
	ViewBuildOutputAction(RText owner, ResourceBundle msg, Plugin plugin) {
		super(owner, msg, "Action.ViewBuilderOutput");
		this.plugin = plugin;
	}


	/**
	 * Called when this action is performed.
	 *
	 * @param e The event.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		plugin.setBuildOutputWindowVisible(!plugin.isBuildOutputWindowVisible());
	}


}