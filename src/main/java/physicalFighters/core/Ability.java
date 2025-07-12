package physicalFighters.core;

import org.bukkit.inventory.PlayerInventory;
import physicalFighters.PhysicalFighters;

import java.util.Arrays;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import physicalFighters.utils.TimerBase;

public abstract class Ability {

    protected static PhysicalFighters plugin;
    protected static CommandManager commandManager;
    protected static Material DefaultItem = Material.IRON_INGOT;

    public static int AbilityCount = 0;
    public static final RestrictionTimer restrictionTimer = new RestrictionTimer();
    private CoolDownTimer CTimer;
    private DurationTimer DTimer;
    private int CoolDown = 0;
    private int Duration = 0;
    private Player player;
    private String AbilityName;
    private Type type;
    private Rank rank;
    private String[] Guide;
    private boolean RunAbility = true;
    private ShowText showtext = ShowText.All_Text;

    public enum ShowText {
        All_Text, No_CoolDownText, No_DurationText, No_Text, Custom_Text
    }

    public enum Type {
        Passive_AutoMatic, Passive_Manual, Active_Immediately, Active_Continue
    }

    public enum Rank {
        SSS(ChatColor.GOLD + "Special Rank"), SS(ChatColor.GRAY + "SS Rank"),
        S(ChatColor.RED + "S Rank"), A(ChatColor.GREEN + "A Rank"),
        B(ChatColor.BLUE + "B Rank"), C(ChatColor.YELLOW + "C Rank"),
        F(ChatColor.BLACK + "F Rank"), GOD(ChatColor.WHITE + "신");

        private final String s;

        Rank(String s) {
            this.s = s;
        }

        public String getText() {
            return this.s;
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
        this.CoolDown = ((this.type == Type.Active_Continue) ||
                (this.type == Type.Active_Immediately) ? CoolDown : -1);
        this.Duration = (this.type == Type.Active_Continue ? Duration : -1);
        this.RunAbility = RunAbility;
        this.showtext = showtext;
        AbilityList.AbilityList.add(this);
        AbilityCount += 1;
    }

    public abstract int A_Condition(Event paramEvent, int paramInt);

    public abstract void A_Effect(Event paramEvent, int paramInt);

    public void A_CoolDownStart() {
    }

    public void A_CoolDownEnd() {
    }

    public void A_DurationStart() {
    }

    public void A_DurationEnd() {
    }

    public void A_FinalDurationEnd() {
    }

    public void A_SetEvent(Player p) {
    }

    public void A_ResetEvent(Player p) {
    }

    // Common Utils

    public final void registerLeftClickEvent() {
        EventManager.LeftHandEvent.add(this);
    }

    public final void registerRightClickEvent() {
        EventManager.RightHandEvent.add(this);
    }

    public boolean removeOneItem(Player player, Material material) {
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() == material) {
                int amount = item.getAmount();
                if (amount > 1) {
                    item.setAmount(amount - 1);
                } else {
                    inventory.setItem(i, null);
                }
                return true;
            }
        }
        return false;
    }

    public final boolean isValidItem(Material material) {
        return getPlayer().getInventory().getItemInMainHand().getType() == material;
    }

    public static Ability FindAbility(Player p) {
        for (Ability a : AbilityList.AbilityList)
            if (a.isOwner(p)) return a;
        return null;
    }


    public final void setPlayer(Player p, boolean textout) {
        this.DTimer.stopTimer();
        this.CTimer.stopTimer();
        if (this.player != null) {
            if (textout) {
                this.player.sendMessage(String.format(ChatColor.RED + "%s" +
                                ChatColor.WHITE + " 능력이 해제되었습니다.", getAbilityName()));
            }
            A_ResetEvent(this.player);
        }
        if (p != null && this.RunAbility) {
            if (textout) {
                p.sendMessage(String.format(ChatColor.GREEN + "%s" +
                                ChatColor.WHITE + " 능력이 설정되었습니다.", getAbilityName()));
            }
            A_SetEvent(p);
        }
        this.player = p;
    }

    public final boolean isOwner(Player p) {
        if ((this.player != null) &&
                (p.getName().equalsIgnoreCase(this.player.getName())) && (
                (this.player.isDead()) ||
                        (Bukkit.getServer().getPlayerExact(this.player.getName()) != null))) {
            this.player = p;
            return true;
        }
        return false;
    }

    public final boolean isOwner(Entity e) {
        return e instanceof Player && isOwner((Player) e);
    }

    // Timer Managing

    public final void cancelDTimer() {
        this.DTimer.stopTimer();
    }

    public final void cancelCTimer() {
        this.CTimer.stopTimer();
    }

    public void setCool(int i) {
        this.CTimer.setCount(i);
    }

    public int getCool() {
        return this.CTimer.getCount();
    }

    // Getter, Setter

    public final Player getPlayer() {
        return this.player;
    }

    public final boolean hasPlayer() {
        return this.player != null;
    }

    public final String getAbilityName() {
        return this.AbilityName;
    }

    public final Type getAbilityType() {
        return this.type == null ? null : this.type;
    }

    public final String[] getGuide() {
        return this.Guide;
    }

    public final LinkedList<String> getGuide2() {
        String[] arrayOfString;
        int j = (arrayOfString = this.Guide).length;
        return new LinkedList<>(Arrays.asList(arrayOfString).subList(0, j));
    }

    public final int getCoolDown() {
        return this.CoolDown;
    }

    public final Rank getRank() {
        return this.rank;
    }

    public final int getDuration() {
        return this.Duration;
    }

    public final boolean getDurationState() {
        return this.DTimer.isRunning();
    }

    public final void setRunAbility(boolean RunAbility) {
        this.RunAbility = RunAbility;
    }

    public final boolean getRunAbility() {
        return this.RunAbility;
    }

    public final ShowText getShowText() {
        return this.showtext;
    }

    public static int getAbilityCount() {
        return AbilityCount;
    }

    public final boolean AbilityExcute(Event event, int CustomData) {
        if ((this.player != null) && (this.RunAbility)) {
            int cd = A_Condition(event, CustomData);
            if (cd == -2) {
                return true;
            }
            if (cd != -1) {
                if ((this.type == Type.Active_Continue) ||
                        (this.type == Type.Active_Immediately)) {
                    if (this.DTimer.isRunning()) {
                        getPlayer().sendMessage(
                            String.format(ChatColor.WHITE + "%d초"
                                + ChatColor.GREEN + " 후 지속시간이 끝납니다.",
                                    this.DTimer.getCount()));
                        return true;
                    }
                    if (this.CTimer.isRunning()) {
                        if (getShowText() != ShowText.No_CoolDownText) {
                            getPlayer().sendMessage(
                                String.format(ChatColor.WHITE + "%d초" +
                                    ChatColor.RED + " 후 능력을 다시 사용하실 수 있습니다.",
                                    this.CTimer.getCount()));
                        }
                        return true;
                    }
                    if (getShowText() != ShowText.Custom_Text)
                        getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "능력을 사용했습니다.");
                }
                if (this.type == Type.Active_Immediately) {
                    A_Effect(event, cd);
                    if (getCoolDown() != 0)
                        this.CTimer.startTimer(getCoolDown(), true);
                } else if (this.type == Type.Active_Continue) {
                    this.DTimer.startTimer(getDuration(), true);
                } else {
                    A_Effect(event, cd);
                }
                return true;
            }
        }
        return false;
    }

    public final boolean AbilityDuratinEffect(Event event, int CustomData) {
        if ((this.player != null) && (this.DTimer.isRunning())) {
            A_Effect(event, CustomData);
            return true;
        }
        return false;
    }

    // Uncommon Utils (will be merged to specific ability)

    public final void goPlayerVelocity(Player p1, Player p2, int value) {
        p1.setVelocity(p1.getVelocity().add(
                p2.getLocation().toVector()
                        .subtract(p1.getLocation().toVector()).normalize()
                        .multiply(value)));
    }

    public static void goVelocity(Player p1, Location lo, int value) {
        p1.setVelocity(p1.getVelocity().add(
                lo.toVector().subtract(p1.getLocation().toVector()).normalize()
                        .multiply(value)));
    }

    public final boolean isSword(ItemStack item) {
        return (item.getType() == Material.WOODEN_SWORD) || (item.getType() == Material.STONE_SWORD)
                || (item.getType() == Material.GOLDEN_SWORD) || (item.getType() == Material.IRON_SWORD)
                || (item.getType() == Material.DIAMOND_SWORD);
    }

    public void ExplosionDMG(Player p, Location l, int distance, int dmg) {
        for (Player t : Bukkit.getOnlinePlayers()) {
            if ((t != p) && (l.distance(t.getLocation()) <= distance))
                t.damage(dmg, p);
        }
    }

    public void ExplosionDMGL(Player p, Location l, int distance, int dmg) {
        for (Player t : Bukkit.getOnlinePlayers())
            if ((t != p) && (l.distance(t.getLocation()) <= distance)) {
                t.damage(dmg, p);
                t.getWorld().strikeLightning(t.getLocation());
            }
    }

    public void ExplosionDMGPotion(Player p, Location l, int distance, int dmg, PotionEffectType pet, int dura, int amp) {
        for (Player t : Bukkit.getOnlinePlayers())
            if ((t != p) && (l.distance(t.getLocation()) <= distance)) {
                t.damage(dmg, p);
                t.addPotionEffect(new PotionEffect(pet, dura, amp));
            }
    }

    public void ExplosionDMG(Location l, int distance, int dmg) {
        for (Player t : Bukkit.getOnlinePlayers()) {
            if (t.getLocation().distance(l) <= distance)
                t.damage(dmg);
        }
    }

    // Timer Classes

    public static final class CoolDownTimer extends TimerBase {
        private final Ability ab;

        public CoolDownTimer(Ability ab) {
            this.ab = ab;
        }

        public void EventStartTimer() {
            this.ab.A_CoolDownStart();
        }

        public void EventRunningTimer(int count) {
            if (((count <= 3) && (count >= 1) && (this.ab.getShowText() == ShowText.All_Text))
                    || (this.ab.getShowText() == ShowText.No_DurationText)) {
                this.ab.getPlayer().sendMessage(String.format(ChatColor.RED + "%d초 뒤" + ChatColor.WHITE + " 능력사용이 가능합니다.", count));
            }
        }

        public void EventEndTimer() {
            this.ab.A_CoolDownEnd();
            if (this.ab.getShowText() != ShowText.Custom_Text) {
                this.ab.getPlayer().sendMessage(ChatColor.AQUA + "다시 능력을 사용할 수 있습니다.");
            }
        }
    }

    public static final class DurationTimer extends TimerBase {
        private final Ability ab;
        private final CoolDownTimer ctimer;

        public DurationTimer(Ability ab, CoolDownTimer ctimer) {
            this.ab = ab;
            this.ctimer = ctimer;
        }

        public void EventStartTimer() {
            this.ab.A_DurationStart();
        }

        public void EventRunningTimer(int count) {
            if (((count <= 3) && (count >= 1) && (this.ab.getShowText() == ShowText.All_Text))
                    || (this.ab.getShowText() == ShowText.No_CoolDownText)) {
                this.ab.getPlayer().sendMessage(String.format(ChatColor.GREEN + "지속 시간" + ChatColor.WHITE + " %d초 전", count));
            }
        }

        public void EventEndTimer() {
            this.ab.A_DurationEnd();
            if (this.ab.getShowText() != ShowText.Custom_Text)
                this.ab.getPlayer().sendMessage(ChatColor.GREEN + "능력 지속시간이 끝났습니다.");
            this.ctimer.startTimer(this.ab.getCoolDown(), true);
        }

        public void FinalEventEndTimer() {
            this.ab.A_FinalDurationEnd();
        }
    }

    public static final class RestrictionTimer extends TimerBase {
        public void EventStartTimer() {
        }

        public void EventRunningTimer(int count) {
        }

        public void EventEndTimer() {
            Bukkit.broadcastMessage(ChatColor.GREEN + "일부 능력의 제약이 해제되었습니다.");
        }
    }

}
