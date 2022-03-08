/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE file for details.
 */
package org.fife.ui.rtextarea;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for the {@link RTextAreaEditorKit.ClipboardHistoryAction} class.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class RTextAreaEditorKitClipboardHistoryActionTest {


	@Test
	void testGetMacroID() {
		Assertions.assertEquals(RTextAreaEditorKit.clipboardHistoryAction,
			new RTextAreaEditorKit.ClipboardHistoryAction().getMacroID());
	}
}
