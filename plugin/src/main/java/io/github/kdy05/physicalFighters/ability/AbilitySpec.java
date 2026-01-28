package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.ability.Ability.Rank;
import io.github.kdy05.physicalFighters.ability.Ability.ShowText;
import io.github.kdy05.physicalFighters.ability.Ability.Type;

/**
 * 능력 초기화 설정을 담는 불변 객체
 */
public final class AbilitySpec {
    private final String name;
    private final Type type;
    private final Rank rank;
    private final int cooldown;
    private final int duration;
    private final boolean runAbility;
    private final ShowText showText;
    private final String[] guide;

    private AbilitySpec(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.rank = builder.rank;
        this.cooldown = builder.cooldown;
        this.duration = builder.duration;
        this.runAbility = builder.runAbility;
        this.showText = builder.showText;
        this.guide = builder.guide;
    }

    public String name() { return name; }
    public Type type() { return type; }
    public Rank rank() { return rank; }
    public int cooldown() { return cooldown; }
    public int duration() { return duration; }
    public boolean runAbility() { return runAbility; }
    public ShowText showText() { return showText; }
    public String[] guide() { return guide; }

    public static Builder builder(String name, Type type, Rank rank) {
        return new Builder(name, type, rank);
    }

    public static final class Builder {
        // Required
        private final String name;
        private final Type type;
        private final Rank rank;

        // Optional with defaults
        private int cooldown = 0;
        private int duration = 0;
        private boolean runAbility = true;
        private ShowText showText = ShowText.All_Text;
        private String[] guide = new String[0];

        private Builder(String name, Type type, Rank rank) {
            this.name = name;
            this.type = type;
            this.rank = rank;
        }

        public Builder cooldown(int cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder runAbility(boolean runAbility) {
            this.runAbility = runAbility;
            return this;
        }

        public Builder showText(ShowText showText) {
            this.showText = showText;
            return this;
        }

        public Builder guide(String... guide) {
            this.guide = guide;
            return this;
        }

        public AbilitySpec build() {
            return new AbilitySpec(this);
        }
    }
}