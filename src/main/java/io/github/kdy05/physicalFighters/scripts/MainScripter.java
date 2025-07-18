package io.github.kdy05.physicalFighters.scripts;

import io.github.kdy05.physicalFighters.core.Ability;
import io.github.kdy05.physicalFighters.core.AbilityList;
import io.github.kdy05.physicalFighters.core.EventManager;
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

public class MainScripter implements CommandInterface {

    private final PhysicalFighters plugin;
    private final GameManager gameManager;

    public MainScripter(PhysicalFighters plugin) {
        this.plugin = plugin;
        this.gameManager = new GameManager(this, plugin);
    }

    public boolean onCommandEvent(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equalsIgnoreCase("check")) {
            if (sender instanceof Player p) {
                handleCheck(p);
                return true;
            }
            sender.sendMessage("프롬프트에서는 사용할 수 없는 명령입니다.");
        } else if (args[0].equalsIgnoreCase("start")) {
            if (sender instanceof Player p) {
                this.gameManager.setGameWorld(p.getWorld());
                this.gameManager.gameReady(p);
                return true;
            }
            sender.sendMessage("프롬프트에서는 사용할 수 없는 명령입니다.");
        } else if (args[0].equalsIgnoreCase("ob")) {
            if (sender instanceof Player p) {
                this.gameManager.handleObserve(p);
                return true;
            }
            sender.sendMessage("프롬프트에서는 사용할 수 없는 명령입니다.");
        } else if (args[0].equalsIgnoreCase("yes")) {
            if (sender instanceof Player p) {
                this.gameManager.handleYes(p);
                return true;
            }
            sender.sendMessage("프롬프트에서는 사용할 수 없는 명령입니다.");
        } else if (args[0].equalsIgnoreCase("no")) {
            if (sender instanceof Player p) {
                this.gameManager.handleNo(p);
                return true;
            }
            sender.sendMessage("프롬프트에서는 사용할 수 없는 명령입니다.");
        } else if (args[0].equalsIgnoreCase("book")) {
            if (sender instanceof Player p) {
                vabook(p, args);
                return true;
            }
            sender.sendMessage("프롬프트에서는 사용할 수 없는 명령입니다.");
        } else if (args[0].equalsIgnoreCase("stop")) {
            vastop(sender);
        } else if (args[0].equalsIgnoreCase("alist")) {
            vaalist(sender);
        } else if (args[0].equalsIgnoreCase("elist")) {
            vaelist(sender);
        } else if (args[0].equalsIgnoreCase("tc")) {
            vatc(sender);
        } else if (args[0].equalsIgnoreCase("kill")) {
            vakill(sender, args);
        } else if (args[0].equalsIgnoreCase("debug")) {
            vadebug(sender);
        } else if (args[0].equalsIgnoreCase("skip")) {
            vaskip(sender);
        } else if (args[0].equalsIgnoreCase("uti")) {
            vauti(sender);
        } else if (args[0].equalsIgnoreCase("abi")) {
            vaabi(sender, args);
        } else if (args[0].equalsIgnoreCase("ablist")) {
            vaablist(sender, args);
        } else if (args[0].equalsIgnoreCase("inv")) {
            vainv(sender);
        } else if (args[0].equalsIgnoreCase("go")) {
            vago(sender);
        } else if (args[0].equalsIgnoreCase("hung")) {
            vahungry(sender);
        } else if (args[0].equalsIgnoreCase("dura")) {
            vadura(sender);
        }

        return true;
    }

    public void handleCheck(Player p) {
        Ability ability;
        if (AbilityList.assimilation.getPlayer() == p) {
            ability = AbilityList.assimilation;
        } else {
            ability = AUC.findAbility(p);
        }
        if (ability == null) {
            p.sendMessage(ChatColor.RED + "능력이 없거나 옵저버입니다.");
            return;
        }
        p.sendMessage(ChatColor.GREEN + "---------------");
        p.sendMessage(ChatColor.GOLD + "- 능력 정보 -");
        if (PhysicalFighters.AbilityOverLap)
            p.sendMessage(ChatColor.DARK_AQUA + "참고 : 능력 리스트중 가장 상단의 능력만 보여줍니다.");
        p.sendMessage(ChatColor.AQUA + ability.getAbilityName() + ChatColor.WHITE
                + " [" + getTypeText(ability) + "] " + ability.getRank());
        for (int l = 0; l < ability.getGuide().length; l++) {
            p.sendMessage(ability.getGuide()[l]);
        }
        p.sendMessage(getTimerText(ability));
        p.sendMessage(ChatColor.GREEN + "---------------");
    }

    private String getTypeText(Ability ability) {
        Ability.Type type = ability.getAbilityType();
        if (!ability.getRunAbility()) return ChatColor.RED + "능력 비활성화됨" + ChatColor.WHITE;
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
        if (!sender.isOp()) return;
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
                if (code < AbilityList.AbilityList.size()) {
                    Ability ability = AbilityList.AbilityList.get(code);
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
        if (!sender.isOp()) return;
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
        if (abicode < -1 || abicode >= AbilityList.AbilityList.size()) {
            sender.sendMessage(ChatColor.RED + "능력 코드가 올바르지 않습니다.");
            return;
        }

        // 특정 플레이어 능력 해제
        if (abicode == -1) {
            for (Ability ab : AbilityList.AbilityList) {
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
        Ability ability = AbilityList.AbilityList.get(abicode);
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
                for (Ability ab : AbilityList.AbilityList) {
                    if (ab.isOwner(target) &&
                            (ab.getAbilityType() == Ability.Type.Active_Continue)||
                                ab.getAbilityType() == Ability.Type.Active_Immediately) {
                        ab.setPlayer(null, true);
                    }
                }
            }
        } else {
            for (Ability ab : AbilityList.AbilityList) {
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

    public final void vabook(Player p, String[] d) {
        if (!p.isOp()) return;
        if (d.length != 2) {
            p.sendMessage(ChatColor.RED + "명령이 올바르지 않습니다. [/va book [능력코드]]");
            return;
        }
        int abicode;
        try {
            abicode = Integer.parseInt(d[1]);
        } catch (NumberFormatException e) {
            p.sendMessage(ChatColor.RED + "능력 코드가 올바르지 않습니다.");
            return;
        }
        if (abicode < 0 || abicode >= AbilityList.AbilityList.size()) {
            p.sendMessage(ChatColor.RED + "능력 코드가 올바르지 않습니다.");
            return;
        }
        Ability ability = AbilityList.AbilityList.get(abicode);
        ItemStack is = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            im.setDisplayName(ChatColor.GOLD + "[능력서]" + ChatColor.WHITE + abicode + "." + ChatColor.AQUA + ability.getAbilityName());
            im.setLore(new LinkedList<>(Arrays.asList(ability.getGuide())));
        }
        is.setItemMeta(im);
        p.getInventory().addItem(is);
        p.sendMessage("능력서를 만들었습니다. " + ChatColor.GOLD + ability.getAbilityName());
    }

    public final void vauti(CommandSender p) {
        if (!p.isOp()) return;
        p.sendMessage(ChatColor.DARK_RED + "Physical Fighters 명령어 목록");
        p.sendMessage(ChatColor.GRAY + "명령어는 /va [명령어]로 사용합니다.");
        p.sendMessage(ChatColor.RED + "alist : " + ChatColor.WHITE + "능력자 목록을 봅니다.");
        p.sendMessage(ChatColor.RED + "elist : " + ChatColor.WHITE + "능력 확정이 안된 사람들을 보여줍니다.");
        p.sendMessage(ChatColor.RED + "ablist [페이지(0~2)] : " + ChatColor.WHITE + "능력 목록 및 능력 코드를 보여줍니다.");
        p.sendMessage(ChatColor.RED + "abi [닉네임] [능력 코드] : " +
                ChatColor.WHITE + "특정 플레이어에게 능력을 강제로 할당합니다. 같은 능력을 " +
                "여럿이서 가질수는 없으며 이미 할당된 능력을 타인에게 " +
                "주면 기존에 갖고있던 사람의 능력은 사라지게 됩니다. " +
                "액티브 능력은 두 종류 이상 중복해서 줄 수 없습니다. " +
                "게임을 시작하지 않더라도 사용이 가능한 명령입니다. " +
                "닉네임칸에 null을 쓰면 해당 능력에 등록된 플레이어가 " +
                "해제되며 명령 코드에 -1을 넣으면 해당 플레이어가 가진" + "모든 능력이 해제됩니다.");
    }

    public final void vago(CommandSender p) {
        if (!p.isOp()) return;
        Bukkit.broadcastMessage(ChatColor.GREEN +
                "OP에 의해 초반 무적이 해제되었습니다. 이제 데미지를 입습니다.");
        EventManager.DamageGuard = false;
    }

    public final void vainv(CommandSender p) {
        if (!p.isOp()) return;
        Bukkit.broadcastMessage(ChatColor.GREEN +
                "OP에 의해 초반 무적이 설정되었습니다. 이제 데미지를 입지않습니다.");
        EventManager.DamageGuard = true;
    }

    public final void vahungry(CommandSender p) {
        if (!p.isOp()) return;
        if (!PhysicalFighters.NoFoodMode) {
            PhysicalFighters.NoFoodMode = true;
            Bukkit.broadcastMessage(ChatColor.GREEN +
                    "OP에 의해 배고픔무한이 설정되었습니다.");
        } else {
            PhysicalFighters.NoFoodMode = false;
            Bukkit.broadcastMessage(ChatColor.RED +
                    "OP에 의해 배고픔무한이 해제되었습니다.");
        }
    }

    public final void vadura(CommandSender p) {
        if (!p.isOp()) return;
        if (!PhysicalFighters.InfinityDur) {
            PhysicalFighters.InfinityDur = true;
            Bukkit.broadcastMessage(ChatColor.GREEN +
                    "OP에 의해 내구도무한이 설정되었습니다.");
        } else {
            PhysicalFighters.InfinityDur = false;
            Bukkit.broadcastMessage(ChatColor.RED +
                    "OP에 의해 내구도무한이 해제되었습니다.");
        }
    }

    public final void vadebug(CommandSender p) {
        if (!p.isOp()) return;
        p.sendMessage(ChatColor.DARK_RED + "Physical Fighters Debug");
        p.sendMessage(ChatColor.RED + "tc : " + ChatColor.WHITE +
                "[Debug] 모든 능력의 지속 효과 및 쿨타임을 초기화 합니다.");
        p.sendMessage(ChatColor.RED + "kill 닉네임 : " + ChatColor.WHITE +
                "[Debug] 플러그인 내에서 이 플레이어를 사망 처리합니다.");
        p.sendMessage(ChatColor.RED + "skip : " + ChatColor.WHITE +
                "[Debug] 모든 능력을 강제로 확정시킵니다.");
    }

    public final void vaskip(CommandSender p) {
        if (!p.isOp()) return;
        if (GameManager.getScenario() == GameManager.ScriptStatus.AbilitySelect) {
            Bukkit.broadcastMessage(String.format(ChatColor.GRAY +
                            "관리자 %s님이 능력을 강제로 확정시켰습니다.", p.getName()));
            this.gameManager.getOKSign().clear();
            this.gameManager.getOKSign().addAll(GameManager.getPlayerList());
            this.gameManager.gameStart();
        } else {
            p.sendMessage(ChatColor.RED + "능력 추첨중이 아닙니다.");
        }
    }

    public final void vatc(CommandSender p) {
        if (!p.isOp()) return;
        for (Ability a : AbilityList.AbilityList) {
            a.cancelDTimer();
            a.cancelCTimer();
        }
        Bukkit.broadcastMessage(String.format(ChatColor.GRAY +
                        "관리자 %s님이 쿨타임 및 지속시간을 초기화했습니다.", p.getName()));
    }

    public final void vakill(CommandSender p, String[] d) {
        if (!p.isOp()) return;
        if (d.length != 2) {
            p.sendMessage("명령이 올바르지 않습니다.");
            return;
        }
        Player pn = Bukkit.getServer().getPlayerExact(d[1]);
        if (pn == null)  return;
        Ability a = AUC.findAbility(pn);
        if (a != null) {
            a.cancelDTimer();
            a.cancelCTimer();
        }
        pn.damage(5000.0);
        pn.kickPlayer("관리자가 당신의 의지를 꺾었습니다.");
        Bukkit.broadcastMessage(String.format(ChatColor.GRAY +
                "%s님이 %s님을 사망처리했습니다.", p.getName(), pn.getName()));
    }

    public final void vaelist(CommandSender p) {
        if (!p.isOp()) return;
        if (GameManager.getScenario() != GameManager.ScriptStatus.AbilitySelect) {
            p.sendMessage(ChatColor.RED + "능력 추첨중에만 가능합니다.");
            return;
        }
        p.sendMessage(ChatColor.GOLD + "- 확정하지 않은 사람 -");
        p.sendMessage(ChatColor.GREEN + "---------------");
        List<Ability> pl = AbilityList.AbilityList;
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

    public final void vaalist(CommandSender p) {
        if (!p.isOp()) return;
        Bukkit.broadcastMessage(String.format(ChatColor.GREEN +
                "%s님이 플레이어들의 능력을 확인했습니다.", p.getName()));
        p.sendMessage(ChatColor.GOLD + "- 능력을 스캔했습니다. -");
        p.sendMessage(ChatColor.GREEN + "---------------");
        List<Ability> pl = AbilityList.AbilityList;
        int count = 0;
        for (Ability ability : pl) {
            if (ability.getPlayer() == null) continue;
            Player temp = Bukkit.getServer().getPlayer(ability.getPlayer().getName());
            if (temp == null) continue;
            p.sendMessage(String.format(ChatColor.GREEN + "%d. " + ChatColor.WHITE +
                            "%s : " + ChatColor.RED + "%s " + ChatColor.WHITE +
                            "[" + getTypeText(ability) + "]",
                    count, temp.getName(), ability.getAbilityName()));
            count++;
        }
        if (count == 0)
            p.sendMessage("아직 능력자가 없습니다.");
        p.sendMessage(ChatColor.GREEN + "---------------");
    }

    public final void vastop(CommandSender p) {
        if (!p.isOp()) return;
        if (GameManager.getScenario() == GameManager.ScriptStatus.NoPlay) {
            p.sendMessage(ChatColor.RED + "아직 게임을 시작하지 않았습니다.");
            return;
        }
        GameManager.PlayDistanceBuffer = 0;
        Bukkit.broadcastMessage(ChatColor.GRAY + "------------------------------");
        Bukkit.broadcastMessage(String.format(ChatColor.YELLOW +
                        "%s님이 게임 카운터를 중단시켰습니다.", p.getName()));
        GameManager.setScenario(GameManager.ScriptStatus.NoPlay);
        this.gameManager.gameReadyStop();
        this.gameManager.gameStartStop();
        this.gameManager.gameProgressStop();
        this.gameManager.gameWarningStop();
        Bukkit.broadcastMessage(ChatColor.GRAY + "모든 설정이 취소됩니다.");
        Bukkit.broadcastMessage(ChatColor.GREEN + "옵저버 설정은 초기화 되지 않습니다.");
        this.gameManager.getOKSign().clear();
        EventManager.DamageGuard = false;
        for (int l = 0; l < AbilityList.AbilityList.size(); l++) {
            AbilityList.AbilityList.get(l).cancelDTimer();
            AbilityList.AbilityList.get(l).cancelCTimer();
            AbilityList.AbilityList.get(l).setRunAbility(false);
            AbilityList.AbilityList.get(l).setPlayer(null, false);
        }
        GameManager.getPlayerList().clear();
    }

}
