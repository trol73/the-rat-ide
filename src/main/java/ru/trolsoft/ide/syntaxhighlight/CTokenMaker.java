package ru.trolsoft.ide.syntaxhighlight;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import ru.trolsoft.ide.therat.AvrRatDevicesUtils;

public class CTokenMaker extends org.fife.ui.rsyntaxtextarea.modes.CTokenMaker {
    private static String device;
    private static IdentifierTokenReplacer replacer;

    @Override
    public void addToken(char[] array, int start, int end, int tokenType, int startOffset, boolean hyperlink) {
        if (tokenType == TokenTypes.IDENTIFIER && replacer != null) {
            tokenType = replacer.getTokenType(array, start, end);
        }
        super.addToken(array, start, end, tokenType, startOffset, hyperlink);
    }

    public static void setDevice(String deviceName) {
        if (deviceName == null || "<none>".equals(deviceName) || deviceName.equalsIgnoreCase(device)) {
            return;
        }
        var device = AvrRatDevicesUtils.loadDevice(deviceName);
        if (device == null) {
            System.out.println("Device not found " + deviceName);
            replacer = null;
            return;
        }
        CTokenMaker.device = deviceName;
        var builder = new IdentifierTokenReplacer.Builder();
        for (var port : device.getRegisters()) {
            var portName = port.getName();
            builder.add(portName, Token.IO_PORT);
            if (port.getSize() == 2) {
                builder.add(portName + 'H', Token.IO_PORT);
                builder.add(portName + 'L', Token.IO_PORT);
            }
            for (var bit : port.getBits()) {
                var bitName = bit.getName();
                builder.add(bitName, Token.IO_PORT_BIT);
            }
        }
        for (var vector : device.getInterrupts().getVectors()) {
            builder.add(vector.getName() + "_vect", Token.INTERRUPT_VECTOR);
        }
        replacer = builder.build();
    }
}
