package ru.trolsoft.ide.therat;

import ru.trolsoft.therat.RatKt;
import ru.trolsoft.therat.arch.avr.AvrDevParserKt;
import ru.trolsoft.therat.arch.avr.AvrDevice;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

public class AvrRatDevicesUtils {

    private static final WeakHashMap<String, AvrDevice> devicesCache = new WeakHashMap<>();

    public static File getDevFolder() {
        return new File(RatKt.getRootPath() + "../devices/avr");
    }

    public static File getEncodingsFolder() {
        return new File(RatKt.getRootPath() + "../encodings");
    }

    public static List<String> getAllDevices() {
        File[] files = getDevFolder().listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(files).map(File::getName).map((s)->s.substring(0, s.length()-4)).sorted().collect(Collectors.toList());
    }

    public static AvrDevice loadDevice(String name) {
        if ("<none>".equals(name) || name.isEmpty()) {
            return null;
        }
        if (devicesCache.containsKey(name)) {
            return devicesCache.get(name);
        }
        String path = getDevFolder().getAbsolutePath() + File.separatorChar + name + ".dev";
        AvrDevice device = AvrDevParserKt.parseAvrDeviceDefinitionFile(path, null);
        devicesCache.put(name, device);
        return device;
    }

}
