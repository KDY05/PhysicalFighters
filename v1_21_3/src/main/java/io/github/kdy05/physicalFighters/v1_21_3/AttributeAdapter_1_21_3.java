package io.github.kdy05.physicalFighters.v1_21_3;

import io.github.kdy05.physicalFighters.api.AttributeAdapter;
import io.github.kdy05.physicalFighters.util.ServerVersionDetector;
import org.bukkit.attribute.Attribute;

public class AttributeAdapter_1_21_3 implements AttributeAdapter {

    @Override
    public Attribute getMaxHealthAttribute() {
        return Attribute.MAX_HEALTH;
    }

    @Override
    public String getSupportedVersion() {
        return "1.21.3+";
    }

    @Override
    public boolean isCompatible(String serverVersion) {
        return ServerVersionDetector.isAtLeast("1.21.3");
    }
}
