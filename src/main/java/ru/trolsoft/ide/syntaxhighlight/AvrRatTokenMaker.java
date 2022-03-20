package ru.trolsoft.ide.syntaxhighlight;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import ru.trolsoft.ide.therat.AvrRatDevicesUtils;

public class AvrRatTokenMaker extends org.fife.ui.rsyntaxtextarea.modes.AvrRatTokenMaker {
    private static String device;
    private static IdentifierTokenReplacer replacer;

    @Override
    public void addToken(char[] array, int start, int end, int tokenType, int startOffset, boolean hyperlink) {
        if (tokenType == TokenTypes.IDENTIFIER && replacer != null) {
            tokenType = replacer.getTokenType(array, start, end);
//System.out.println(" > " + new String(array, start, end-start+1));
        }
        super.addToken(array, start, end, tokenType, startOffset, hyperlink);
    }

    public static void setDevice(String deviceName) {
        if (deviceName == null || deviceName.equalsIgnoreCase(device)) {
            return;
        }
        var device = AvrRatDevicesUtils.loadDevice(deviceName);
        if (device == null) {
            System.out.println("Device not found " + deviceName);
            replacer = null;
            return;
        }
        AvrRatTokenMaker.device = deviceName;
        var builder = new IdentifierTokenReplacer.Builder();
        for (var port : device.getRegisters()) {
            var portName = port.getName();
            builder.add(portName, Token.IO_PORT);
            for (var bit : port.getBits()) {
                var bitName = bit.getName();
                //builder.add(bitName, Token.IO_PORT_BIT);
            }
        }
        replacer = builder.build();
    }
}
