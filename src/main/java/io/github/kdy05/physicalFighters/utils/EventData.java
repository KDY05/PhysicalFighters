package io.github.kdy05.physicalFighters.utils;

import io.github.kdy05.physicalFighters.core.Ability;

public class EventData {
    public Ability ability;
    public int parameter;

    public EventData(Ability ability) {
        this(ability, 0);
    }

    public EventData(Ability ability, int parameter) {
        this.ability = ability;
        this.parameter = parameter;
    }
}
