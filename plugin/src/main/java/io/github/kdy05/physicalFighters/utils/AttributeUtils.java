package io.github.kdy05.physicalFighters.utils;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.api.AttributeAdapter;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

public class AttributeUtils {

    private static final AttributeAdapter adapter = PhysicalFighters.getAttributeAdapter();

    public static double getMaxHealth(LivingEntity entity) {
        AttributeInstance instance = entity.getAttribute(adapter.getMaxHealthAttribute());
        if (instance == null) {
            return 20.0;
        }
        return instance.getValue();
    }

}
