/*
 * 01/15/2013
 *
 * LanguageListCellRenderer - Cell renderer for the Language option panel.
 * Copyright (C) 2013 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.optionsdialog;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import org.fife.rtext.optionsdialog.LanguageOptionPanel.IconTextInfo;
import org.fife.ui.WebLookAndFeelUtils;


/**
 * Cell renderer that knows how to display both an icon and text (as
 * <code>DefaultListCellRenderer</code> only knows how to display either/or).
 *
 * @author Robert Futrell
 * @version 1.0
 */
final class LanguageListCellRenderer extends DefaultListCellRenderer {

	/**
	 * For certain LAFs, we delegate to their custom renderer for simplicity.
	 */
	private final JLabel possibleDelegate;


	/**
	 * Private constructor to prevent direct instantiation.
	 */
	private LanguageListCellRenderer(JLabel delegate) {
		this.possibleDelegate = delegate;
	}


	/**
	 * Creates the cell renderer to use for the Language option panel.  Note
	 * that this may not be an instance of this class (or a subclass) for
	 * certain high-maintenance Look and Feels.
	 *
	 * @return The renderer to use.
	 */
	public static ListCellRenderer<Object> create() {

		JLabel delegate = null;

		if (WebLookAndFeelUtils.isWebLookAndFeelInstalled()) {
			delegate = (JLabel)UIManager.get("List.cellRenderer");
		}

		return new LanguageListCellRenderer(delegate);

	}


	@Override
	@SuppressWarnings("unchecked")
	public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean selected, boolean focused) {

		JLabel renderer;
		if (possibleDelegate instanceof ListCellRenderer) {
			renderer = possibleDelegate;
			((ListCellRenderer<Object>)possibleDelegate).getListCellRendererComponent(
					list, value, index, selected, focused);
		}
		else {
			renderer = this;
			super.getListCellRendererComponent(list, value, index, selected,
					focused);
		}

		IconTextInfo iti = (IconTextInfo)value;
		renderer.setIcon(iti.getIcon());
		renderer.setText(iti.getText());
		return renderer;

	}


}
