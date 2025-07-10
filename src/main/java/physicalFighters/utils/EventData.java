package physicalFighters.utils;

import physicalFighters.core.Ability;

public class EventData {
    public Ability ab;
    public int parameter;

    public EventData(Ability ab) {
        this(ab, 0);
    }

    public EventData(Ability ab, int parameter) {
        this.ab = ab;
        this.parameter = parameter;
    }
}
