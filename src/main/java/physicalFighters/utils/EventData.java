package physicalFighters.utils;

import physicalFighters.core.AbilityBase;

public class EventData {
    public AbilityBase ab;
    public int parameter;

    public EventData(AbilityBase ab) {
        this(ab, 0);
    }

    public EventData(AbilityBase ab, int parameter) {
        this.ab = ab;
        this.parameter = parameter;
    }
}
