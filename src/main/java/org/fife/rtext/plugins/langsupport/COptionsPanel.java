/*
 * 05/26/2010
 *
 * COptionsPanel.java - Options for C language support.
 * Copyright (C) 2010 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.fife.rtext.plugins.langsupport;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.border.Border;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rtext.RText;
import org.fife.ui.OptionsDialogPanel;
import org.fife.ui.UIUtil;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;


/**
 * Options panel for C language support.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class COptionsPanel extends OptionsDialogPanel {

	private final Listener listener;
	private final JCheckBox enabledCB;
	private final JCheckBox paramAssistanceCB;
	private final JCheckBox showDescWindowCB;
	private final JCheckBox foldingEnabledCB;
	private final JButton rdButton;


	/**
	 * Constructor.
	 *
	 * @param app The parent application.
	 */
	COptionsPanel(RText app) {

		ResourceBundle msg = Plugin.MSG;
		setName(msg.getString("Options.C.Name"));
		listener = new Listener();
		setIcon(app.getIconGroup().getIcon("fileTypes/c"));
		app.addPropertyChangeListener(RText.ICON_STYLE_PROPERTY, e -> {
			setIcon(app.getIconGroup().getIcon("fileTypes/c"));
		});

		ComponentOrientation o = ComponentOrientation.
											getOrientation(getLocale());

		setLayout(new BorderLayout());
		Border empty5Border = UIUtil.getEmpty5Border();
		setBorder(empty5Border);

		Box cp = Box.createVerticalBox();
		cp.setBorder(null);
		add(cp, BorderLayout.NORTH);

		Box box = Box.createVerticalBox();
		box.setBorder(new OptionPanelBorder(msg.
				getString("Options.General.Section.General")));
		cp.add(box);
		cp.add(Box.createVerticalStrut(5));

		enabledCB = createCB("Options.General.EnableCodeCompletion");
		addLeftAligned(box, enabledCB, 5);

		Box box2 = Box.createVerticalBox();
		if (o.isLeftToRight()) {
			box2.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		}
		else {
			box2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
		}
		box.add(box2);

		showDescWindowCB = createCB("Options.General.ShowDescWindow");
		addLeftAligned(box2, showDescWindowCB, 5);

		paramAssistanceCB = createCB("Options.General.ParameterAssistance");
		addLeftAligned(box2, paramAssistanceCB, 5);

		box2.add(Box.createVerticalGlue());

		box = Box.createVerticalBox();
		box.setBorder(new OptionPanelBorder(msg.
				getString("Options.General.Section.Folding")));
		cp.add(box);
		cp.add(Box.createVerticalStrut(5));

		foldingEnabledCB = createCB("Options.General.EnableCodeFolding");
		addLeftAligned(box, foldingEnabledCB, 5);

		cp.add(Box.createVerticalStrut(5));
		rdButton = new JButton(msg.getString("Options.General.RestoreDefaults"));
		rdButton.addActionListener(listener);
		addLeftAligned(cp, rdButton);

		cp.add(Box.createVerticalGlue());

		applyComponentOrientation(o);

	}


	private JCheckBox createCB(String key) {
		if (key.indexOf('.')==-1) {
			key = "Options.C." + key;
		}
		JCheckBox cb = new JCheckBox(Plugin.MSG.getString(key));
		cb.addActionListener(listener);
		return cb;
	}


	@Override
	protected void doApplyImpl(Frame owner) {

		LanguageSupportFactory lsf = LanguageSupportFactory.get();
		LanguageSupport ls=lsf.getSupportFor(SyntaxConstants.SYNTAX_STYLE_C);

		// Options dealing with code completion.
		ls.setAutoCompleteEnabled(enabledCB.isSelected());
		ls.setParameterAssistanceEnabled(paramAssistanceCB.isSelected());
		ls.setShowDescWindow(showDescWindowCB.isSelected());

	}


	@Override
	protected OptionsPanelCheckResult ensureValidInputsImpl() {
		return null;
	}


	@Override
	public JComponent getTopJComponent() {
		return enabledCB;
	}


	private void setEnabledCBSelected(boolean selected) {
		enabledCB.setSelected(selected);
		paramAssistanceCB.setEnabled(selected);
		showDescWindowCB.setEnabled(selected);
	}


	@Override
	protected void setValuesImpl(Frame owner) {

		LanguageSupportFactory lsf = LanguageSupportFactory.get();
		LanguageSupport ls=lsf.getSupportFor(SyntaxConstants.SYNTAX_STYLE_C);

		// Options dealing with code completion
		setEnabledCBSelected(ls.isAutoCompleteEnabled());
		paramAssistanceCB.setSelected(ls.isParameterAssistanceEnabled());
		showDescWindowCB.setSelected(ls.getShowDescWindow());

		// Code folding options
		//foldingEnabledCB.setSelected()

	}


	/**
	 * Listens for events in this options panel.
	 */
	private class Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			Object source = e.getSource();

			if (enabledCB==source) {
				// Trick related components to toggle enabled states
				setEnabledCBSelected(enabledCB.isSelected());
				setDirty(true);
			}

			else if (showDescWindowCB==source) {
				setDirty(true);
			}

			else if (foldingEnabledCB==source) {
				setDirty(true);
			}

			else if (rdButton==source) {
				if (!enabledCB.isSelected() ||
						!foldingEnabledCB.isSelected() ||
						!paramAssistanceCB.isSelected() ||
						!showDescWindowCB.isSelected()) {
					setEnabledCBSelected(true);
					foldingEnabledCB.setSelected(true);
					paramAssistanceCB.setSelected(true);
					showDescWindowCB.setSelected(true);
					setDirty(true);
				}
			}

		}

	}


}
