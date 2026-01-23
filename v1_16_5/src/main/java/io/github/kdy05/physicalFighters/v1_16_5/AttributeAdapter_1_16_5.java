package io.github.kdy05.physicalFighters.v1_16_5;

import io.github.kdy05.physicalFighters.api.AttributeAdapter;
import io.github.kdy05.physicalFighters.util.ServerVersionDetector;
import org.bukkit.attribute.Attribute;

public class AttributeAdapter_1_16_5 implements AttributeAdapter {

    @Override
    public Attribute getMaxHealthAttribute() {
        return Attribute.GENERIC_MAX_HEALTH;
    }

    @Override
    public String getSupportedVersion() {
        return "1.16.5-1.21.2";
    }

    @Override
    public boolean isCompatible(String serverVersion) {
        return ServerVersionDetector.isBetween("1.16.5", "1.21.2");
    }
}
