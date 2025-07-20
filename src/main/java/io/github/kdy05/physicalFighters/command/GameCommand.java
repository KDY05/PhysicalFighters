package io.github.kdy05.physicalFighters.command;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.utils.AbilityInitializer;
import io.github.kdy05.physicalFighters.core.GameManager;
import io.github.kdy05.physicalFighters.utils.AUC;
import io.github.kdy05.physicalFighters.PhysicalFighters;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import io.github.kdy05.physicalFighters.utils.CommandInterface;

public class GameCommand implements CommandInterface {

    private final PhysicalFighters plugin;
    private final GameManager gameManager;

    public GameCommand(PhysicalFighters plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    public boolean onCommandEvent(CommandSender sender, Command command, String label, String[] args) {
        // 유저 커맨드
        if (args[0].equalsIgnoreCase("check")) {
            handleCheck(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("yes")) {
            if (sender instanceof Player p) {
                this.gameManager.handleYes(p);
                return true;
            }
            sender.sendMessage("콘솔에서는 사용할 수 없습니다.");
        } else if (args[0].equalsIgnoreCase("no")) {
            if (sender instanceof Player p) {
                this.gameManager.handleNo(p);
                return true;
            }
            sender.sendMessage("콘솔에서는 사용할 수 없습니다.");
        }

        // 운영자 권한 필터
        if (!sender.hasPermission("va.operate")) {
            return false;
        }

        // 운영자 커맨드
        if (args[0].equalsIgnoreCase("start")) {
            if (sender instanceof Player p) {
                this.gameManager.gameReady(p);
                return true;
            }
            sender.sendMessage("프롬프트에서는 사용할 수 없는 명령입니다.");
        }
        else if (args[0].equalsIgnoreCase("stop")) {
            vastop(sender);
            return true;
        }
        else if (args[0].equalsIgnoreCase("skip")) {
            vaskip(sender);
            return true;
        }
        else if (args[0].equalsIgnoreCase("ob")) {
            if (sender instanceof Player p) {
                this.gameManager.handleObserve(p);
                return true;
            }
            sender.sendMessage("프롬프트에서는 사용할 수 없는 명령입니다.");
        }


        else if (args[0].equalsIgnoreCase("book")) {
            if (sender instanceof Player p) {
                vabook(p, args);
                return true;
            }
            sender.sendMessage("프롬프트에서는 사용할 수 없는 명령입니다.");
        }
        else if (args[0].equalsIgnoreCase("alist")) {
            vaalist(sender);
            return true;
        }
        else if (args[0].equalsIgnoreCase("elist")) {
            vaelist(sender);
            return true;
        }
        else if (args[0].equalsIgnoreCase("abi")) {
            vaabi(sender, args);
            return true;
        } else if (args[0].equalsIgnoreCase("ablist")) {
            vaablist(sender, args);
            return true;
        }

        return false;
    }

    public void handleCheck(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("콘솔에서는 사용할 수 없습니다.");
            return;
        }
        Ability ability;
        if (AbilityInitializer.assimilation.getPlayer() == player) {
            ability = AbilityInitializer.assimilation;
        } else {
            ability = AUC.findAbility(player);
        }
        if (ability == null) {
            player.sendMessage(ChatColor.RED + "능력이 없거나 옵저버입니다.");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "---------------");
        player.sendMessage(ChatColor.GOLD + "- 능력 정보 -");
        if (PhysicalFighters.AbilityOverLap)
            player.sendMessage(ChatColor.DARK_AQUA + "참고 : 능력 리스트중 가장 상단의 능력만 보여줍니다.");
        player.sendMessage(ChatColor.AQUA + ability.getAbilityName() + ChatColor.WHITE
                + " [" + getTypeText(ability) + "] " + ability.getRank());
        for (int l = 0; l < ability.getGuide().length; l++) {
            player.sendMessage(ability.getGuide()[l]);
        }
        player.sendMessage(getTimerText(ability));
        player.sendMessage(ChatColor.GREEN + "---------------");
    }

    private String getTypeText(Ability ability) {
        Ability.Type type = ability.getAbilityType();
        return switch (type) {
            case Active_Continue ->
                    ChatColor.GREEN + "액티브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "지속" + ChatColor.WHITE;
            case Active_Immediately ->
                    ChatColor.GREEN + "액티브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "즉발" + ChatColor.WHITE;
            case Passive_AutoMatic ->
                    ChatColor.GREEN + "패시브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "자동" + ChatColor.WHITE;
            case Passive_Manual ->
                    ChatColor.GREEN + "패시브 " + ChatColor.WHITE + "/ " + ChatColor.GOLD + "수동" + ChatColor.WHITE;
            case null -> "Unknown";
        };
    }

    private String getTimerText(Ability ability) {
        return switch (ability.getAbilityType()) {
            case Active_Continue -> String.format(ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "%d초 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "%d초", ability.getCoolDown(), ability.getDuration());
            case Active_Immediately -> String.format(ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "%d초 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음", ability.getCoolDown());
            case Passive_AutoMatic, Passive_Manual -> ChatColor.RED + "쿨타임 : " + ChatColor.WHITE + "없음 / "
                    + ChatColor.RED + "지속시간 : " + ChatColor.WHITE + "없음";
            case null -> "None";
        };
    }

    public final void vaablist(CommandSender sender, String[] d) {
        if (d.length != 2) {
            sender.sendMessage(ChatColor.RED + "명령이 올바르지 않습니다. [/va ablist [0~10]");
            return;
        }
        try {
            int page = Integer.parseInt(d[1]);
            if (page < 0) {
                sender.sendMessage(ChatColor.RED + "페이지가 올바르지 않습니다.");
                return;
            }
            sender.sendMessage(ChatColor.GOLD + "==== 능력 목록 및 코드 ====");
            sender.sendMessage(String.format(ChatColor.AQUA + "페이지 %d...[0~10]", page));
            for (int code = page * 8; code < (page + 1) * 8; code++) {
                if (code < AbilityInitializer.AbilityList.size()) {
                    Ability ability = AbilityInitializer.AbilityList.get(code);
                    sender.sendMessage(String.format(
                            ChatColor.GREEN + "[%d] " + ChatColor.WHITE + "%s",
                            code, ability.getAbilityName()));
                }
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "페이지가 올바르지 않습니다.");
        }
    }

    public final void vaabi(CommandSender sender, String[] d) {
        // 예외 처리
        if (d.length != 3) {
            sender.sendMessage(ChatColor.RED + "명령이 올바르지 않습니다. [/va abi [플레이어] [명령코드]]");
            return;
        }
        Player target = Bukkit.getServer().getPlayerExact(d[1]);
        if (target == null && !d[1].equalsIgnoreCase("null")) {
            sender.sendMessage(ChatColor.RED + "존재하지 않는 플레이어입니다.");
            return;
        }
        int abicode;
        try {
            abicode = Integer.parseInt(d[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "능력 코드가 올바르지 않습니다.");
            return;
        }
        if (abicode < -1 || abicode >= AbilityInitializer.AbilityList.size()) {
            sender.sendMessage(ChatColor.RED + "능력 코드가 올바르지 않습니다.");
            return;
        }

        // 특정 플레이어 능력 해제
        if (abicode == -1) {
            for (Ability ab : AbilityInitializer.AbilityList) {
                if (ab.isOwner(target)) {
                    ab.setPlayer(null, true);
                }
            }
            if (target != null) {
                target.sendMessage(ChatColor.RED + "당신의 능력이 모두 해제되었습니다.");
                sender.sendMessage(String.format(ChatColor.GREEN + "%s" +
                        ChatColor.WHITE + "님의 능력을 모두 해제했습니다.", target.getName()));
            }
            Bukkit.broadcastMessage(String.format(ChatColor.GOLD +
                    "%s님이 누군가의 능력을 모두 해제했습니다.", sender.getName()));
            return;
        }

        // 특정 능력 소유자 능력 해제
        Ability ability = AbilityInitializer.AbilityList.get(abicode);
        if (d[1].equalsIgnoreCase("null")) {
            ability.setPlayer(null, true);
            sender.sendMessage(String.format("%s 능력 초기화 완료", ability.getAbilityName()));
            return;
        }

        // 실제 능력 적용 로직
        // 기존 능력 해제
        if (PhysicalFighters.AbilityOverLap) {
            // 중복 모드에서 액티브 능력 중복은 불가함.
            if (ability.getAbilityType() == Ability.Type.Active_Continue ||
                    ability.getAbilityType() == Ability.Type.Active_Immediately) {
                for (Ability ab : AbilityInitializer.AbilityList) {
                    if (ab.isOwner(target) &&
                            (ab.getAbilityType() == Ability.Type.Active_Continue)||
                                ab.getAbilityType() == Ability.Type.Active_Immediately) {
                        ab.setPlayer(null, true);
                    }
                }
            }
        } else {
            for (Ability ab : AbilityInitializer.AbilityList) {
                if (ab.isOwner(target)) {
                    ab.setPlayer(null, true);
                }
            }
        }

        // 새로운 능력 적용
        ability.setPlayer(target, true);
        ability.setRunAbility(true);
        sender.sendMessage(String.format(ChatColor.GREEN + "%s" + ChatColor.WHITE + "님에게 " +
                            ChatColor.GREEN + "%s" + ChatColor.WHITE + " 능력 할당이 완료되었습니다.",
                            Objects.requireNonNull(target).getName(), ability.getAbilityName()));
        Bukkit.broadcastMessage(String.format(ChatColor.GOLD +
                        "%s님이 누군가에게 능력을 강제로 할당했습니다.",sender.getName()));
        String senderName = sender instanceof Player ? sender.getName() : "Console";
        plugin.getLogger().info(String.format("%s님이 %s님에게 %s 능력을 할당했습니다.",
                senderName, target.getName(), ability.getAbilityName()));
    }

    public final void vabook(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "명령이 올바르지 않습니다. [/va book [능력코드]]");
            return;
        }

        int abicode;
        try {
            abicode = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "능력 코드가 올바르지 않습니다.");
            return;
        }
        if (abicode < 0 || abicode >= AbilityInitializer.AbilityList.size() - 1) {
            player.sendMessage(ChatColor.RED + "능력 코드가 올바르지 않습니다.");
            return;
        }

        Ability ability = AbilityInitializer.AbilityList.get(abicode);
        ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return;

        meta.setDisplayName(ChatColor.GOLD + "[능력서]" + ChatColor.WHITE + abicode + ". " + ability.getAbilityName());
        meta.setLore(new LinkedList<>(Arrays.asList(ability.getGuide())));
        stack.setItemMeta(meta);

        player.getInventory().addItem(stack);
        player.sendMessage("능력서를 만들었습니다. " + ChatColor.GOLD + ability.getAbilityName());
    }

    public final void vaskip(CommandSender sender) {
        if (GameManager.getScenario() == GameManager.ScriptStatus.AbilitySelect) {
            Bukkit.broadcastMessage(String.format(ChatColor.GRAY +
                            "관리자 %s님이 능력을 강제로 확정시켰습니다.", sender.getName()));
            this.gameManager.getOKSign().clear();
            this.gameManager.getOKSign().addAll(GameManager.getPlayerList());
            this.gameManager.gameStart();
        } else {
            sender.sendMessage(ChatColor.RED + "능력 추첨중이 아닙니다.");
        }
    }


    public final void vaelist(CommandSender p) {
        if (GameManager.getScenario() != GameManager.ScriptStatus.AbilitySelect) {
            p.sendMessage(ChatColor.RED + "능력 추첨중에만 가능합니다.");
            return;
        }
        p.sendMessage(ChatColor.GOLD + "- 확정하지 않은 사람 -");
        p.sendMessage(ChatColor.GREEN + "---------------");
        List<Ability> pl = AbilityInitializer.AbilityList;
        int count = 0;
        for (Ability ability : pl) {
            if (ability.getPlayer() == null) continue;
            if (!this.gameManager.getOKSign().contains(ability.getPlayer())) {
                p.sendMessage(String.format(ChatColor.GREEN +
                        "%d. " + ChatColor.WHITE + "%s",
                        count, ability.getPlayer().getName()));
                count++;
            }
        }
        p.sendMessage(ChatColor.GREEN + "---------------");
    }

    public final void vaalist(CommandSender sender) {
        Bukkit.broadcastMessage(String.format(ChatColor.GREEN +
                "%s님이 플레이어들의 능력을 확인했습니다.", sender.getName()));
        sender.sendMessage(ChatColor.GOLD + "- 능력을 스캔했습니다. -");
        sender.sendMessage(ChatColor.GREEN + "---------------");
        List<Ability> pl = AbilityInitializer.AbilityList;
        int count = 0;
        for (Ability ability : pl) {
            if (ability.getPlayer() == null) continue;
            Player temp = Bukkit.getServer().getPlayer(ability.getPlayer().getName());
            if (temp == null) continue;
            sender.sendMessage(String.format(ChatColor.GREEN + "%d. " + ChatColor.WHITE +
                            "%s : " + ChatColor.RED + "%s " + ChatColor.WHITE +
                            "[" + getTypeText(ability) + "]",
                    count, temp.getName(), ability.getAbilityName()));
            count++;
        }
        if (count == 0)
            sender.sendMessage("아직 능력자가 없습니다.");
        sender.sendMessage(ChatColor.GREEN + "---------------");
    }

    public final void vastop(CommandSender sender) {
        if (GameManager.getScenario() == GameManager.ScriptStatus.NoPlay) {
            sender.sendMessage(ChatColor.RED + "아직 게임을 시작하지 않았습니다.");
            return;
        }
        GameManager.setScenario(GameManager.ScriptStatus.NoPlay);
        this.gameManager.gameReadyStop();
        this.gameManager.gameStartStop();
        this.gameManager.gameProgressStop();
        this.gameManager.gameWarningStop();
        this.gameManager.getOKSign().clear();
        PhysicalFighters.DamageGuard = false;
        for (int l = 0; l < AbilityInitializer.AbilityList.size(); l++) {
            AbilityInitializer.AbilityList.get(l).cancelDTimer();
            AbilityInitializer.AbilityList.get(l).cancelCTimer();
            AbilityInitializer.AbilityList.get(l).setRunAbility(false);
            AbilityInitializer.AbilityList.get(l).setPlayer(null, false);
        }
        GameManager.getPlayerList().clear();
        Bukkit.broadcastMessage(ChatColor.GRAY + "------------------------------");
        Bukkit.broadcastMessage(String.format(ChatColor.YELLOW +
                "관리자 %s님이 게임 카운터를 중단시켰습니다.", sender.getName()));
        Bukkit.broadcastMessage(ChatColor.GRAY + "모든 설정이 취소됩니다.");
        Bukkit.broadcastMessage(ChatColor.GRAY + "옵저버 설정은 초기화 되지 않습니다.");
    }

}
