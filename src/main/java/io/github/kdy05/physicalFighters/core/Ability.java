package io.github.kdy05.physicalFighters.core;

import io.github.kdy05.physicalFighters.PhysicalFighters;

import io.github.kdy05.physicalFighters.utils.AbilityInitializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import io.github.kdy05.physicalFighters.utils.TimerBase;

import java.util.UUID;

public abstract class Ability {
    protected static PhysicalFighters plugin;
    protected static CommandManager commandManager;
    protected static Material DefaultItem = Material.IRON_INGOT;

    private static int AbilityCount = 0;
    private CoolDownTimer CTimer;
    private DurationTimer DTimer;
    private int CoolDown = 0;
    private int Duration = 0;
    private UUID playerUuid = null;
    private String AbilityName;
    private Type type;
    private Rank rank;
    private String[] Guide;
    private boolean RunAbility = true;
    private ShowText showtext = ShowText.All_Text;

    public enum Type {
        Passive_AutoMatic, Passive_Manual, Active_Immediately, Active_Continue
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

    public enum Usage {
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

    public static void InitAbilityBase(PhysicalFighters plugin, CommandManager commandManager) {
        Ability.plugin = plugin;
        Ability.commandManager = commandManager;
    }

    protected final void InitAbility(String AbilityName, Type type, Rank rank, String... Manual) {
        this.AbilityName = AbilityName;
        this.type = type;
        this.Guide = new String[Manual.length];
        System.arraycopy(Manual, 0, this.Guide, 0, Manual.length);
        this.CTimer = new CoolDownTimer(this);
        this.DTimer = new DurationTimer(this, this.CTimer);
        this.rank = rank;
    }

    protected final void InitAbility(int CoolDown, int Duration, boolean RunAbility) {
        InitAbility(CoolDown, Duration, RunAbility, ShowText.All_Text);
    }

    protected final void InitAbility(int CoolDown, int Duration, boolean RunAbility, ShowText showtext) {
        this.CoolDown = (this.type == Type.Active_Continue || this.type == Type.Active_Immediately)
                ? CoolDown : -1;
        this.Duration = this.type == Type.Active_Continue ? Duration : -1;
        this.RunAbility = RunAbility;
        this.showtext = showtext;
        AbilityInitializer.AbilityList.add(this);
        AbilityCount += 1;
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

    // Common Utils

    public final void registerLeftClickEvent() {
        EventManager.LeftHandEvent.add(this);
    }

    public final void registerRightClickEvent() {
        EventManager.RightHandEvent.add(this);
    }

    public final boolean isValidItem(Material material) {
        if (getPlayer() == null) return false;
        return getPlayer().getInventory().getItemInMainHand().getType() == material;
    }

    public final boolean isOwner(Entity e) {
        return e instanceof Player && isOwner((Player) e);
    }

    public final boolean isOwner(Player p) {
        return this.playerUuid != null && p.getUniqueId().equals(this.playerUuid);
    }

    public final void sendMessage(String message) {
        Player player = getPlayer();
        if (player == null) return;
        player.sendMessage(message);
    }

    public final void setPlayer(Player p, boolean textout) {
        this.DTimer.stopTimer();
        this.CTimer.stopTimer();
        Player currentPlayer = getPlayer();
        if (currentPlayer != null) {
            if (textout) {
                currentPlayer.sendMessage(String.format(ChatColor.RED + "%s" +
                    ChatColor.WHITE + " 능력이 해제되었습니다.", getAbilityName()));
            }
            A_ResetEvent(currentPlayer);
        }
        if (p != null && this.RunAbility) {
            if (textout) {
                p.sendMessage(String.format(ChatColor.GREEN + "%s" +
                    ChatColor.WHITE + " 능력이 설정되었습니다.", getAbilityName()));
            }
            A_SetEvent(p);
        }
        this.playerUuid = p != null ? p.getUniqueId() : null;
    }

    public final void execute(Event event, int CustomData) {
        if (getPlayer() == null || !RunAbility) return;

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
                if (getShowText() != ShowText.No_CoolDownText) {
                    getPlayer().sendMessage(String.format(ChatColor.WHITE + "%d초"
                        + ChatColor.RED + " 후 능력을 다시 사용하실 수 있습니다.", this.CTimer.getCount()));
                }
                return;
            }
            // 능력 사용 알림
            if (getShowText() != ShowText.Custom_Text)
                getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "능력을 사용했습니다.");
        }

        switch (this.type) {
            // 지속 능력 시작
            case Type.Active_Continue ->
                    this.DTimer.startTimer(getDuration(), true);
            // 즉발 능력 사용 후 쿨타임 다시 시작
            case Type.Active_Immediately -> {
                A_Effect(event, data);
                if (getCoolDown() != 0)
                    this.CTimer.startTimer(getCoolDown(), true);
            }
            // 패시브 실행
            default -> A_Effect(event, data);
        }

    }

    // Timer Managing

    public final void cancelDTimer() {
        this.DTimer.stopTimer();
    }

    public final void cancelCTimer() {
        this.CTimer.stopTimer();
    }

    public int getCool() {
        return this.CTimer.getCount();
    }

    public void setCool(int i) {
        this.CTimer.setCount(i);
    }

    public final boolean getDurationState() {
        return this.DTimer.isRunning();
    }

    // Getter, Setter

    public static int getAbilityCount() {
        return AbilityCount;
    }

    public final int getCoolDown() {
        return this.CoolDown;
    }

    public final int getDuration() {
        return this.Duration;
    }

    public final Player getPlayer() {
        return this.playerUuid != null ? Bukkit.getPlayer(this.playerUuid) : null;
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

    public final boolean getRunAbility() {
        return this.RunAbility;
    }

    public final void setRunAbility(boolean RunAbility) {
        this.RunAbility = RunAbility;
    }

    public final ShowText getShowText() {
        return this.showtext;
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
            ShowText showText = ability.getShowText();
            if (count <= 3 && count >= 1 && showText != ShowText.No_CoolDownText && showText != ShowText.Custom_Text
                    && ability.getPlayer() != null) {
                ability.getPlayer().sendMessage(String.format(ChatColor.RED
                        + "%d초 뒤" + ChatColor.WHITE + " 능력사용이 가능합니다.", count));
            }
        }

        @Override
        public void onTimerEnd() {
            ability.A_CoolDownEnd();
            if (ability.getShowText() != ShowText.Custom_Text && ability.getPlayer() != null)
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
            ShowText showText = ability.getShowText();
            if (count <= 3 && count >= 1 && showText != ShowText.No_DurationText && showText != ShowText.Custom_Text
                    && ability.getPlayer() != null) {
                ability.getPlayer().sendMessage(String.format(ChatColor.GREEN
                        + "지속 시간" + ChatColor.WHITE + " %d초 전", count));
            }
        }

        @Override
        public void onTimerEnd() {
            ability.A_DurationEnd();
            if (ability.getShowText() != ShowText.Custom_Text && ability.getPlayer() != null)
                ability.getPlayer().sendMessage(ChatColor.GREEN + "능력 지속시간이 끝났습니다.");
            ctimer.startTimer(ability.getCoolDown(), true);
        }

        @Override
        public void onTimerFinalize() {
            ability.A_FinalDurationEnd();
        }
    }

}
