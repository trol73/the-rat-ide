package ru.trolsoft.ide.therat;

import ru.trolsoft.therat.RatKt;
import ru.trolsoft.therat.arch.avr.AvrDevParserKt;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AvrRatDevicesUtils {
    public static File getFolder() {
        return new File(RatKt.getRootPath() + "../devices/avr");
    }

    public static List<String> getAllDevices() {
        File[] files = getFolder().listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(files).map(File::getName).map((s)->s.substring(0, s.length()-4)).sorted().collect(Collectors.toList());
    }
    //AvrDevParserKt.parseAvrDeviceDefinitionFile()
}
