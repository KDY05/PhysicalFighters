package io.github.kdy05.physicalFighters.util;

import io.github.kdy05.physicalFighters.api.AdapterRegistry;
import io.github.kdy05.physicalFighters.api.AttributeAdapter;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

public class AttributeUtils {

    private static AttributeAdapter adapter() {
        return AdapterRegistry.attribute();
    }

    public static double getMaxHealth(LivingEntity entity) {
        AttributeInstance instance = entity.getAttribute(adapter().getMaxHealthAttribute());
        if (instance == null) {
            return 20.0;
        }
        return instance.getValue();
    }

}
