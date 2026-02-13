package io.github.kdy05.physicalFighters.ability;

import io.github.kdy05.physicalFighters.PhysicalFighters;
import io.github.kdy05.physicalFighters.command.CommandInterface;
import io.github.kdy05.physicalFighters.game.EventManager;
import io.github.kdy05.physicalFighters.util.BaseItem;
import io.github.kdy05.physicalFighters.util.EventData;
import io.github.kdy05.physicalFighters.util.TimerBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class Ability {
    protected static final PhysicalFighters plugin = PhysicalFighters.getPlugin();
    protected static final Material DefaultItem = Material.IRON_INGOT;

    private final int CoolDown;
    private final int Duration;
    private final CoolDownTimer CTimer;
    private final DurationTimer DTimer;
    private final String AbilityName;
    private final Type type;
    private final Rank rank;
    private final String[] Guide;
    private final ShowText showtext;

    private final UUID playerUuid;

    public enum Type {
        Passive_AutoMatic(new String[]{"패시브", "자동"}),
        Passive_Manual(new String[]{"패시브", "수동"}),
        Active_Immediately(new String[]{"액티브", "즉발"}),
        Active_Continue(new String[]{"액티브", "지속"});

        private final String[] text;

        Type(String[] text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return ChatColor.GREEN + text[0] + ChatColor.WHITE + " / " + ChatColor.GOLD + text[1] + ChatColor.WHITE;
        }
    }

    public enum Rank {
        SSS(ChatColor.DARK_PURPLE + "Special Rank"), SS(ChatColor.GOLD + "SS Rank"),
        S(ChatColor.RED + "S Rank"), A(ChatColor.GREEN + "A Rank"),
        B(ChatColor.BLUE + "B Rank"), C(ChatColor.YELLOW + "C Rank"),
        F(ChatColor.BLACK + "F Rank"), GOD(ChatColor.WHITE + "신");

        private final String s;

        Rank(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return this.s + ChatColor.WHITE;
        }
    }

    public enum ShowText {
        All_Text, No_CoolDownText, No_DurationText, Custom_Text
    }

    protected enum Usage {
        IronLeft("철괴 좌클릭"), IronRight("철괴 우클릭"),
        IronAttack("철괴 타격"), GoldRight("금괴 우클릭"),
        GoldLeft("금괴 좌클릭"), Passive("패시브");

        private final String s;

        Usage(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return String.format(ChatColor.GRAY + "(%s) " + ChatColor.WHITE, this.s);
        }
    }

    protected Ability(AbilitySpec spec, UUID playerUuid) {
        this.AbilityName = spec.name();
        this.type = spec.type();
        this.rank = spec.rank();
        this.Guide = spec.guide().clone();
        this.showtext = spec.showText();

        this.CoolDown = (type == Type.Active_Continue || type == Type.Active_Immediately)
                ? spec.cooldown() : -1;
        this.Duration = type == Type.Active_Continue ? spec.duration() : -1;

        this.CTimer = new CoolDownTimer(this);
        this.DTimer = new DurationTimer(this, this.CTimer);

        this.playerUuid = playerUuid;
    }

    public void registerEvents() {}

    public void unregisterEvents() {
        EventManager.unregisterAll(this);
    }

    // Common Utils

    public final void registerLeftClickEvent() {
        EventManager.registerLeftClick(this);
    }

    public final void registerRightClickEvent() {
        EventManager.registerRightClick(this);
    }

    public final boolean isValidItem(Material material) {
        if (getPlayer() == null) return false;
        return getPlayer().getInventory().getItemInMainHand().getType() == material;
    }

    public final boolean isOwner(Entity e) {
        return e instanceof Player && isOwner((Player) e);
    }

    public final boolean isOwner(Player p) {
        return this.playerUuid != null && p != null && p.getUniqueId().equals(this.playerUuid);
    }

    @Nullable
    public final Player getPlayer() {
        return Bukkit.getPlayer(this.playerUuid);
    }

    public final void sendMessage(String message) {
        Player player = getPlayer();
        if (player == null) return;
        player.sendMessage(message);
    }

    public final void activate(boolean textout) {
        registerEvents();
        if (this instanceof BaseItem) {
            EventManager.registerPlayerDropItem(new EventData(this, BaseItem.ITEM_DROP_EVENT));
            EventManager.registerPlayerRespawn(new EventData(this, BaseItem.ITEM_RESPAWN_EVENT));
            EventManager.registerEntityDeath(new EventData(this, BaseItem.ITEM_DEATH_EVENT));
        }
        if (this instanceof CommandInterface) {
            AbilityRegistry.registerCommand((CommandInterface) this);
        }
        Player player = getPlayer();
        if (player != null) {
            if (textout) {
                player.sendMessage(String.format(ChatColor.GREEN + "%s" +
                    ChatColor.WHITE + " 능력이 설정되었습니다.", getAbilityName()));
            }
            A_SetEvent(player);
            if (this instanceof BaseItem) {
                ((BaseItem) this).giveBaseItem(player);
            }
        }
    }

    public final void deactivate(boolean textout) {
        cancelDTimer();
        cancelCTimer();
        Player player = getPlayer();
        if (player != null) {
            if (textout) {
                player.sendMessage(String.format(ChatColor.RED + "%s" +
                    ChatColor.WHITE + " 능력이 해제되었습니다.", getAbilityName()));
            }
            if (this instanceof BaseItem) {
                ((BaseItem) this).removeBaseItem(player);
            }
            A_ResetEvent(player);
        }
        unregisterEvents();
        if (this instanceof CommandInterface) {
            AbilityRegistry.unregisterCommand((CommandInterface) this);
        }
    }

    public final void execute(Event event, int CustomData) {
        if (getPlayer() == null) return;

        // BaseItem 이벤트는 A_Condition/A_Effect를 거치지 않고 직접 처리
        if (this instanceof BaseItem) {
            BaseItem baseItem = (BaseItem) this;
            if (CustomData == BaseItem.ITEM_DROP_EVENT) {
                baseItem.handleItemDrop(event);
                return;
            } else if (CustomData == BaseItem.ITEM_RESPAWN_EVENT) {
                baseItem.handleItemRespawn(event);
                return;
            } else if (CustomData == BaseItem.ITEM_DEATH_EVENT) {
                baseItem.handleItemDeath(event);
                return;
            }
        }

        int data = A_Condition(event, CustomData);
        if (data < 0) return;

        if (this.type == Type.Active_Continue || this.type == Type.Active_Immediately) {
            // 지속 시간 알림 후 종료
            if (this.DTimer.isRunning()) {
                getPlayer().sendMessage(String.format(ChatColor.WHITE + "%d초"
                        + ChatColor.GREEN + " 후 지속시간이 끝납니다.", this.DTimer.getCount()));
                return;
            }
            // 쿨타임 알림 후 종료
            if (this.CTimer.isRunning()) {
                if (showtext != ShowText.No_CoolDownText) {
                    getPlayer().sendMessage(String.format(ChatColor.WHITE + "%d초"
                        + ChatColor.RED + " 후 능력을 다시 사용하실 수 있습니다.", this.CTimer.getCount()));
                }
                return;
            }
            // 능력 사용 알림
            if (showtext != ShowText.Custom_Text)
                getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "능력을 사용했습니다.");
        }

        if (this.type == Type.Active_Continue) {
            // 지속 능력 시작
            this.DTimer.startTimer(getDuration(), true);
        } else if (this.type == Type.Active_Immediately) {
            // 즉발 능력 사용 후 쿨타임 다시 시작
            A_Effect(event, data);
            if (getCoolDown() != 0)
                this.CTimer.startTimer(getCoolDown(), true);
        } else {
            // 패시브 실행
            A_Effect(event, data);
        }

    }

    // Events

    public abstract int A_Condition(Event paramEvent, int paramInt);

    public abstract void A_Effect(Event paramEvent, int paramInt);

    public void A_SetEvent(Player p) {}

    public void A_ResetEvent(Player p) {}

    public void A_CoolDownStart() {}

    public void A_CoolDownEnd() {}

    public void A_DurationStart() {}

    public void A_DurationEnd() {}

    public void A_FinalDurationEnd() {}

    // Hooks — 서브클래스에서 필요 시 오버라이드

    /** 사망 시 페널티를 면제받는 능력인지 여부 (예: 불사조) */
    public boolean isDeathExempt() { return false; }

    /** showInfo에서 우선 표시되는 능력인지 여부 (예: 흡수) */
    public boolean isInfoPrimary() { return false; }

    /** 랜덤 분배 시 필요한 최소 플레이어 수 (기본 0 = 제한 없음) */
    public int getMinimumPlayers() { return 0; }

    // Timer Managing

    public final void cancelDTimer() {
        this.DTimer.stopTimer();
    }

    public final void cancelCTimer() {
        this.CTimer.stopTimer();
    }

    public final boolean getDurationState() {
        return this.DTimer.isRunning();
    }

    // Getters
    public final int getCoolDown() {
        return this.CoolDown;
    }

    public final int getDuration() {
        return this.Duration;
    }

    public final String getAbilityName() {
        return this.AbilityName;
    }

    public final Type getAbilityType() {
        return this.type;
    }

    public final Rank getRank() {
        return this.rank;
    }

    public final String[] getGuide() {
        return this.Guide;
    }

    // Timer Classes

    private static final class CoolDownTimer extends TimerBase {
        private final Ability ability;

        public CoolDownTimer(Ability ability) {
            this.ability = ability;
        }

        @Override
        public void onTimerStart() {
            ability.A_CoolDownStart();
        }

        @Override
        public void onTimerRunning(int count) {
            ShowText showText = ability.showtext;
            if (count <= 3 && count >= 1 && showText != ShowText.No_CoolDownText && showText != ShowText.Custom_Text
                    && ability.getPlayer() != null) {
                ability.getPlayer().sendMessage(String.format(ChatColor.RED
                        + "%d초 뒤" + ChatColor.WHITE + " 능력사용이 가능합니다.", count));
            }
        }

        @Override
        public void onTimerEnd() {
            ability.A_CoolDownEnd();
            if (ability.showtext != ShowText.Custom_Text && ability.getPlayer() != null)
                ability.getPlayer().sendMessage(ChatColor.AQUA + "다시 능력을 사용할 수 있습니다.");
        }
    }

    private static final class DurationTimer extends TimerBase {
        private final Ability ability;
        private final CoolDownTimer ctimer;

        public DurationTimer(Ability ability, CoolDownTimer ctimer) {
            this.ability = ability;
            this.ctimer = ctimer;
        }

        @Override
        public void onTimerStart() {
            ability.A_DurationStart();
        }

        @Override
        public void onTimerRunning(int count) {
            if (ability.getPlayer() == null) return;
            ShowText showText = ability.showtext;
            if (count <= 3 && count >= 1 && showText != ShowText.No_DurationText && showText != ShowText.Custom_Text
                    && ability.getPlayer() != null) {
                ability.getPlayer().sendMessage(String.format(ChatColor.GREEN
                        + "지속 시간" + ChatColor.WHITE + " %d초 전", count));
            }
        }

        @Override
        public void onTimerEnd() {
            ability.A_DurationEnd();
            if (ability.showtext != ShowText.Custom_Text && ability.getPlayer() != null)
                ability.getPlayer().sendMessage(ChatColor.GREEN + "능력 지속시간이 끝났습니다.");
            ctimer.startTimer(ability.getCoolDown(), true);
        }

        @Override
        public void onTimerFinalize() {
            ability.A_FinalDurationEnd();
        }
    }

}
