package Physical.Fighters.Script;

import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.CommandManager;
import Physical.Fighters.MajorModule.AbilityList;
import Physical.Fighters.MinerModule.AUC;
import Physical.Fighters.PhysicalFighters;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MainScripter implements Physical.Fighters.MinerModule.CommandInterface {
    public static ScriptStatus Scenario = ScriptStatus.NoPlay;
    public LinkedList<Player> ExceptionList = new LinkedList<>();
    public static ArrayList<Player> PlayerList = new ArrayList<>();
    public ArrayList<Player> OKSign = new ArrayList<>();
    public PhysicalFighters va;
    public org.bukkit.World gameworld;
    public S_GameReady s_GameReady;
    public S_GameStart s_GameStart;
    public S_GameProgress s_GameProgress;
    public S_GameWarnning s_GameWarnning;

    public MainScripter(PhysicalFighters va, CommandManager cm) {
        this.va = va;
        cm.RegisterCommand(this);
        this.s_GameReady = new S_GameReady(this);
        this.s_GameStart = new S_GameStart(this);
        this.s_GameProgress = new S_GameProgress(this);
        this.s_GameWarnning = new S_GameWarnning(this);
    }

    public boolean onCommandEvent(CommandSender sender, Command command, String label, String[] data) {
        if ((sender instanceof Player p)) {
            if (PhysicalFighters.canstart) {
                if (data[0].equalsIgnoreCase("help")) {
                    AUC.InfoTextOut(p);
                } else if (data[0].equalsIgnoreCase("start")) {
                    this.gameworld = p.getWorld();
                    this.s_GameReady.GameReady(p);
                } else if (data[0].equalsIgnoreCase("ob")) {
                    vaob(p);
                } else if (data[0].equalsIgnoreCase("yes")) {
                    vayes(p);
                } else if (data[0].equalsIgnoreCase("no")) {
                    vano(p);
                } else if (data[0].equalsIgnoreCase("stop")) {
                    vastop(p);
                } else if (data[0].equalsIgnoreCase("alist")) {
                    vaalist(p);
                } else if (data[0].equalsIgnoreCase("elist")) {
                    vaelist(p);
                } else if (data[0].equalsIgnoreCase("tc")) {
                    vatc(p);
                } else if (data[0].equalsIgnoreCase("kill")) {
                    vakill(p, data);
                } else if (data[0].equalsIgnoreCase("debug")) {
                    vadebug(p);
                } else if (data[0].equalsIgnoreCase("skip")) {
                    vaskip(p);
                } else if (data[0].equalsIgnoreCase("maker")) {
                    vamaker(p);
                } else if (data[0].equalsIgnoreCase("uti")) {
                    vauti(p);
                } else if (data[0].equalsIgnoreCase("abi")) {
                    vaabi(p, data);
                } else if (data[0].equalsIgnoreCase("book")) {
                    vabook(p, data);
                } else if (data[0].equalsIgnoreCase("ablist")) {
                    vaablist(p, data);
                } else if (data[0].equalsIgnoreCase("go")) {
                    vago(p);
                } else if (data[0].equalsIgnoreCase("inv")) {
                    vainv(p);
                } else if (data[0].equalsIgnoreCase("hung")) {
                    vahungry(p);
                } else if (data[0].equalsIgnoreCase("dura")) {
                    vadura(p);
                } else if (data[0].equalsIgnoreCase("염료")) {
                    try {
                        vaeasteregg(p);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else
                sender.sendMessage("플러그인이 개조되었습니다. 게임을 진행할 수 없습니다.");
            return true;
        }
        if (PhysicalFighters.canstart) {
            if (data[0].equalsIgnoreCase("help")) {
                sender.sendMessage("프롬프트에서는 사용할수 없는 명령입니다.");
            } else if (data[0].equalsIgnoreCase("start")) {
                sender.sendMessage("프롬프트에서는 사용할수 없는 명령입니다.");
            } else if (data[0].equalsIgnoreCase("ob")) {
                sender.sendMessage("프롬프트에서는 사용할수 없는 명령입니다.");
            } else if (data[0].equalsIgnoreCase("yes")) {
                sender.sendMessage("프롬프트에서는 사용할수 없는 명령입니다.");
            } else if (data[0].equalsIgnoreCase("no")) {
                sender.sendMessage("프롬프트에서는 사용할수 없는 명령입니다.");
            } else if (data[0].equalsIgnoreCase("stop")) {
                vastop(sender);
            } else if (data[0].equalsIgnoreCase("alist")) {
                vaalist(sender);
            } else if (data[0].equalsIgnoreCase("elist")) {
                vaelist(sender);
            } else if (data[0].equalsIgnoreCase("tc")) {
                vatc(sender);
            } else if (data[0].equalsIgnoreCase("kill")) {
                vakill(sender, data);
            } else if (data[0].equalsIgnoreCase("debug")) {
                vadebug(sender);
            } else if (data[0].equalsIgnoreCase("skip")) {
                vaskip(sender);
            } else if (data[0].equalsIgnoreCase("uti")) {
                vauti(sender);
            } else if (data[0].equalsIgnoreCase("maker")) {
                vamaker(sender);
            } else if (data[0].equalsIgnoreCase("abi")) {
                vaabi(sender, data);
            } else if (data[0].equalsIgnoreCase("ablist")) {
                vaablist(sender, data);
            } else if (data[0].equalsIgnoreCase("inv")) {
                vainv(sender);
            } else if (data[0].equalsIgnoreCase("go")) {
                vago(sender);
            }
        } else
            sender.sendMessage("플러그인이 개조되었습니다. 게임을 진행할 수 없습니다.");
        return true;
    }

    public final void vaablist(CommandSender p, String[] d) {
        if (p.isOp()) {
            int page;
            if (d.length == 2) {
                try {
                    page = Integer.parseInt(d[1]);
                    if (page >= 0) {
                        p.sendMessage(ChatColor.GOLD + "==== 능력 목록 및 코드 ====");
                        p.sendMessage(String.format(ChatColor.AQUA +
                                        "페이지 %d...[0~10]",
                                page));
                        for (int code = page * 8; code < (page + 1) * 8; code++) {
                            if (code < AbilityList.AbilityList.size()) {
                                AbilityBase a = AbilityList.AbilityList.get(code);
                                p.sendMessage(String.format(
                                        ChatColor.GREEN + "[%d] " + ChatColor.WHITE + "%s",
                                        code, a.GetAbilityName()));
                            }
                        }
                        p.sendMessage(ChatColor.GOLD + "================");
                    } else {
                        p.sendMessage(ChatColor.RED + "페이지가 올바르지 않습니다.");
                    }
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "페이지가 올바르지 않습니다.");
                }
            } else
                p.sendMessage(ChatColor.RED +
                        "명령이 올바르지 않습니다. [/va ablist [0~10]");
        }
    }

    public final void vaabi(CommandSender p, String[] d) {
        if (p.isOp()) {
            if (d.length == 3) {
                Player pn = Bukkit.getServer().getPlayerExact(d[1]);
                if ((pn != null) || (d[1].equalsIgnoreCase("null"))) {
                    int abicode;
                    try {
                        abicode = Integer.parseInt(d[2]);
                    } catch (NumberFormatException e) {
                        p.sendMessage(ChatColor.RED + "능력 코드가 올바르지 않습니다.");
                        return;
                    }
                    if (abicode == -1) {
                        for (AbilityBase ab : AbilityList.AbilityList) {
                            if (ab.PlayerCheck(pn)) {
                                ab.SetPlayer(null, true);
                            }
                        }
                        if (pn != null) {
                            pn.sendMessage(ChatColor.RED + "당신의 능력이 모두 해제되었습니다.");
                        }
                        if (pn != null) {
                            p.sendMessage(String.format(ChatColor.GREEN + "%s" +
                                            ChatColor.WHITE + "님의 능력을 모두 해제했습니다.",
                                    pn.getName()));
                        }
                        Bukkit.broadcastMessage(String.format(ChatColor.GOLD +
                                        "%s님이 누군가의 능력을 모두 해제했습니다.",
                                p.getName()));
                        return;
                    }
                    if ((abicode >= 0) &&
                            (abicode < AbilityList.AbilityList.size())) {
                        AbilityBase a = AbilityList.AbilityList.get(abicode);
                        if (d[1].equalsIgnoreCase("null")) {
                            a.SetPlayer(null, true);
                            p.sendMessage(String.format("%s 능력 초기화 완료",
                                    a.GetAbilityName()));
                            return;
                        }
                        if (PhysicalFighters.AbilityOverLap) {
                            if ((a.GetAbilityType() == AbilityBase.Type.Active_Continue) ||
                                    (a.GetAbilityType() == AbilityBase.Type.Active_Immediately)) {
                                for (AbilityBase ab : AbilityList.AbilityList) {
                                    if ((ab.PlayerCheck(pn)) && (
                                            (ab.GetAbilityType() == AbilityBase.Type.Active_Continue) ||
                                                    (ab.GetAbilityType() == AbilityBase.Type.Active_Immediately))) {
                                        ab.SetPlayer(null, true);
                                    }
                                }
                            }
                        } else {
                            for (AbilityBase ab : AbilityList.AbilityList) {
                                if (ab.PlayerCheck(pn)) {
                                    ab.SetPlayer(null, true);
                                }
                            }
                        }
                        a.SetPlayer(pn, true);
                        a.SetRunAbility(true);
                        p.sendMessage(
                                String.format(ChatColor.GREEN + "%s" +
                                                ChatColor.WHITE + "님에게 " +
                                                ChatColor.GREEN + "%s" +
                                                ChatColor.WHITE + " 능력 할당이 완료되었습니다.",
                                        Objects.requireNonNull(pn).getName(),
                                        a.GetAbilityName()));
                        Bukkit.broadcastMessage(String.format(ChatColor.GOLD +
                                        "%s님이 누군가에게 능력을 강제로 할당했습니다.",
                                p.getName()));
                        String s;
                        if ((p instanceof Player)) {
                            s = p.getName();
                        } else
                            s = "서버 개설자";
                        PhysicalFighters.log.info(String.format(
                                "%s님이 %s님에게 %s 능력을 할당했습니다.", s,
                                pn.getName(), a.GetAbilityName()));
                    } else {
                        p.sendMessage(ChatColor.RED + "능력 코드가 올바르지 않습니다.");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "존재하지 않는 플레이어입니다.");
                }
            } else {
                p.sendMessage(ChatColor.RED +
                        "명령이 올바르지 않습니다. [/va abi [플레이어] [명령코드]]");
            }
        }
    }

    public final void vabook(Player p, String[] d) {
        if (p.isOp())
            if (d.length == 2) {
                int abicode;
                try {
                    abicode = Integer.parseInt(d[1]);
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "능력 코드가 올바르지 않습니다.");
                    return;
                }
                if ((abicode >= 0) &&
                        (abicode < AbilityList.AbilityList.size())) {
                    AbilityBase a = AbilityList.AbilityList.get(abicode);
                    p.sendMessage("능력서를 만들었습니다. " + ChatColor.GOLD + a.GetAbilityName());
                    ItemStack is = new ItemStack(Material.ENCHANTED_BOOK);
                    ItemMeta im = is.getItemMeta();
                    if (im != null) {
                        im.setDisplayName(ChatColor.GOLD + "[능력서]" + ChatColor.WHITE + abicode + "." + ChatColor.AQUA + a.GetAbilityName());
                    }
                    if (im != null) {
                        im.setLore(a.GetGuide2());
                    }
                    is.setItemMeta(im);
                    p.getInventory().addItem(is);
                } else {
                    p.sendMessage(ChatColor.RED + "능력 코드가 올바르지 않습니다.");
                }
            } else {
                p.sendMessage(ChatColor.RED +
                        "명령이 올바르지 않습니다. [/va book [능력코드]]");
            }
    }

    public final void vauti(CommandSender p) {
        if (p.isOp()) {
            p.sendMessage(ChatColor.DARK_RED + "Physical Fighters 명령어 목록");
            p.sendMessage(ChatColor.GRAY + "명령어는 /va [명령어]로 사용합니다.");
            p.sendMessage(ChatColor.RED + "alist : " + ChatColor.WHITE +
                    "능력자 목록을 봅니다. 옵 전용.");
            p.sendMessage(ChatColor.RED + "elist : " + ChatColor.WHITE +
                    "능력 확정이 안된 사람들을 보여줍니다. 옵 전용.");
            p.sendMessage(ChatColor.RED + "ablist [페이지(0~2)] : " +
                    ChatColor.WHITE + "능력 목록 및 능력 코드를 보여줍니다. 옵 전용.");
            p.sendMessage(ChatColor.RED + "abi [닉네임] [능력 코드] : " +
                    ChatColor.WHITE + "특정 플레이어에게 능력을 강제로 할당합니다. 같은 능력을 " +
                    "여럿이서 가질수는 없으며 이미 할당된 능력을 타인에게 " +
                    "주면 기존에 갖고있던 사람의 능력은 사라지게 됩니다. " +
                    "액티브 능력은 두 종류 이상 중복해서 줄수 없습니다. " +
                    "게임을 시작하지 않더라도 사용이 가능한 명령입니다. " +
                    "닉네임칸에 null을 쓰면 해당 능력에 등록된 플레이어가 " +
                    "해제되며 명령 코드에 -1을 넣으면 해당 플레이어가 가진" + "모든 능력이 해제됩니다.");
        }
    }

    public final void vago(CommandSender p) {
        if (p.isOp()) {
            Bukkit.broadcastMessage(ChatColor.GREEN +
                    "OP에 의해 초반 무적이 해제되었습니다. 이제 데미지를 입습니다.");
            Physical.Fighters.MainModule.EventManager.DamageGuard = false;
        }
    }

    public final void vainv(CommandSender p) {
        if (p.isOp()) {
            Bukkit.broadcastMessage(ChatColor.GREEN +
                    "OP에 의해 초반 무적이 설정되었습니다. 이제 데미지를 입지않습니다.");
            Physical.Fighters.MainModule.EventManager.DamageGuard = true;
        }
    }

    public final void vahungry(CommandSender p) {
        if (p.isOp())
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
        if (p.isOp())
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
        if (p.isOp()) {
            p.sendMessage(ChatColor.DARK_RED + "Physical Fighters Debug");
            p.sendMessage(ChatColor.RED + "tc : " + ChatColor.WHITE +
                    "[Debug] 모든 능력의 지속 효과 및 쿨타임을 초기화 합니다.");
            p.sendMessage(ChatColor.RED + "kill 닉네임 : " + ChatColor.WHITE +
                    "[Debug] 플러그인 내에서 이 플레이어를 사망 처리합니다.");
            p.sendMessage(ChatColor.RED + "skip : " + ChatColor.WHITE +
                    "[Debug] 모든 능력을 강제로 확정시킵니다.");
        }
    }

    public final void vamaker(CommandSender p) {
        if (p.isOp()) {
            p.sendMessage(ChatColor.DARK_RED + "Physical Fighters 제작자");
            p.sendMessage(ChatColor.RED +
                    " 본 모드는 '제온'님이 배포하신 'VisualAbility'의 모듈을 사용하고있습니다. " +
                    ChatColor.WHITE);
        }
    }

    public final void vaskip(CommandSender p) {
        if (p.isOp())
            if (Scenario == ScriptStatus.AbilitySelect) {
                Bukkit.broadcastMessage(String.format(ChatColor.GRAY +
                                "관리자 %s님이 능력을 강제로 확정시켰습니다.", p.getName()));
                this.OKSign.clear();
                this.OKSign.addAll(PlayerList);
                this.s_GameStart.GameStart();
            } else {
                p.sendMessage(ChatColor.RED + "능력 추첨중이 아닙니다.");
            }
    }

    public final void vatc(CommandSender p) {
        if (p.isOp()) {
            for (AbilityBase a : AbilityList.AbilityList) {
                a.AbilityDTimerCancel();
                a.AbilityCTimerCancel();
            }
            Bukkit.broadcastMessage(String.format(ChatColor.GRAY +
                            "관리자 %s님이 쿨타임및 지속시간을 초기화했습니다.", p.getName()));
        }
    }

    public final void vakill(CommandSender p, String[] d) {
        if (p.isOp())
            if (d.length == 2) {
                Player pn = Bukkit.getServer().getPlayerExact(d[1]);
                if (pn != null) {
                    AbilityBase a = AbilityBase.FindAbility(pn);
                    if (a != null) {
                        a.AbilityDTimerCancel();
                        a.AbilityCTimerCancel();
                    }
                    pn.damage((int) 5000.0D);
                    pn.kickPlayer("관리자가 당신의 의지를 꺾었습니다.");
                    Bukkit.broadcastMessage(String.format(ChatColor.GRAY +
                            "%s님이 %s님을 사망처리했습니다.", p.getName(), pn.getName()));
                }
            } else {
                p.sendMessage("명령이 올바르지 않습니다.");
            }
    }

    public final void vaeasteregg(Player p) throws IOException {
        File f = new File("yeomryo.love");
        if (!f.exists()) {
            if (!PhysicalFighters.easteregg) {
                p.sendMessage("이스터에그의 히든능력이 적용되었습니다.");
                PhysicalFighters.easteregg = true;
                f.createNewFile();
                Bukkit.reload();
            } else {
                p.sendMessage("이미 히든능력이 사용되고 있습니다.(없애려면 폴더의 yeomryo.love파일을 제거해주세요.)");
            }
        } else
            p.sendMessage("하이!");
    }

    public final void vaelist(CommandSender p) {
        if (p.isOp()) {
            if (Scenario == ScriptStatus.AbilitySelect) {
                p.sendMessage(ChatColor.GOLD + "- 확정하지 않은 사람 -");
                p.sendMessage(ChatColor.GREEN + "---------------");
                List<AbilityBase> pl = AbilityList.AbilityList;
                int count = 0;
                for (AbilityBase abilityBase : pl) {
                    if (abilityBase.GetPlayer() != null) {
                        if (!this.OKSign.contains(abilityBase.GetPlayer())) {
                            p.sendMessage(String.format(ChatColor.GREEN +
                                    "%d. " + ChatColor.WHITE + "%s", count,
                                    abilityBase.GetPlayer().getName()));
                            count++;
                        }
                    }
                }
                p.sendMessage(ChatColor.GREEN + "---------------");
            } else {
                p.sendMessage(ChatColor.RED + "능력 추첨중에만 가능합니다.");
            }
        } else {
            p.sendMessage(ChatColor.RED + "당신은 권한이 없습니다. 관리자에게 OP 권한을 요청하세요.");
        }
    }

    public final void vaalist(CommandSender p) {
        if (p.isOp()) {
            Bukkit.broadcastMessage(String.format(ChatColor.GREEN +
                    "%s님이 플레이어들의 능력을 확인했습니다.", p.getName()));
            p.sendMessage(ChatColor.GOLD + "- 능력을 스캔했습니다. -");
            p.sendMessage(ChatColor.GREEN + "---------------");
            List<AbilityBase> pl = AbilityList.AbilityList;
            int count = 0;
            for (AbilityBase abilityBase : pl) {
                if (abilityBase.GetPlayer() != null) {
                    Player temp = Bukkit.getServer().getPlayer(
                            abilityBase.GetPlayer().getName());
                    if (temp != null) {
                        p.sendMessage(String.format(
                                ChatColor.GREEN + "%d. " + ChatColor.WHITE +
                                        "%s : " + ChatColor.RED + "%s " +
                                        ChatColor.WHITE + "[" +
                                        AUC.TypeTextOut(abilityBase) + "]",
                                count, temp.getName(), abilityBase.GetAbilityName()));
                        count++;
                    }
                }
            }
            if (count == 0)
                p.sendMessage("아직 능력자가 없습니다.");
            p.sendMessage(ChatColor.GREEN + "---------------");
        } else {
            p.sendMessage(ChatColor.RED + "당신은 권한이 없습니다. 관리자에게 OP 권한을 요청하세요.");
        }
    }

    public final void vastop(CommandSender p) {
        if (p.isOp()) {
            if (Scenario != ScriptStatus.NoPlay) {
                S_GameStart.PlayDistanceBuffer = 0;
                Bukkit.broadcastMessage(ChatColor.GRAY +
                        "------------------------------");
                Bukkit.broadcastMessage(String.format(ChatColor.YELLOW +
                                "%s님이 게임 카운터를 중단시켰습니다.", p.getName()));
                Scenario = ScriptStatus.NoPlay;
                this.s_GameReady.GameReadyStop();
                this.s_GameStart.GameStartStop();
                this.s_GameProgress.GameProgressStop();
                this.s_GameWarnning.GameWarnningStop();
                Bukkit.broadcastMessage(ChatColor.GRAY + "모든 설정이 취소됩니다.");
                Bukkit.broadcastMessage(ChatColor.GREEN +
                        "옵저버 설정은 초기화 되지 않습니다.");
                this.OKSign.clear();
                Physical.Fighters.MainModule.EventManager.DamageGuard = false;
                for (int l = 0; l < AbilityList.AbilityList.size(); l++) {
                    AbilityList.AbilityList.get(l).AbilityDTimerCancel();
                    AbilityList.AbilityList.get(l)
                            .AbilityCTimerCancel();
                    AbilityList.AbilityList.get(l)
                            .SetRunAbility(false);
                    AbilityList.AbilityList.get(l).SetPlayer(
                            null, false);
                }
                PlayerList.clear();
            } else {
                p.sendMessage(ChatColor.RED + "아직 게임을 시작하지 않았습니다.");
            }
        } else
            p.sendMessage(ChatColor.RED + "당신은 권한이 없습니다. 관리자에게 OP 권한을 요청하세요.");
    }

    public final void vaob(Player p) {
        if (Scenario == ScriptStatus.NoPlay) {
            if (this.ExceptionList.contains(p)) {
                PlayerList.add(p);
                this.ExceptionList.remove(p);
                p.sendMessage(ChatColor.GREEN + "게임 예외처리가 해제되었습니다.");
            } else {
                this.ExceptionList.add(p);
                PlayerList.remove(p);
                p.sendMessage(ChatColor.GOLD + "게임 예외처리가 완료되었습니다.");
                p.sendMessage(ChatColor.GREEN + "/va ob을 다시 사용하시면 해제됩니다.");
            }
        } else
            p.sendMessage(ChatColor.RED + "게임 시작 이후는 옵저버 처리가 불가능합니다.");
    }

    public final void vayes(Player p) {
        if ((Scenario == ScriptStatus.AbilitySelect) &&
                (!this.ExceptionList.contains(p)) &&
                (!this.OKSign.contains(p))) {
            this.OKSign.add(p);
            p.sendMessage(ChatColor.GOLD + "능력이 확정되었습니다. 다른 사람을 기다리세요.");
            Bukkit.broadcastMessage(String.format(ChatColor.YELLOW + "%s" +
                            ChatColor.WHITE + "님이 능력을 확정했습니다.", p.getName()));
            Bukkit.broadcastMessage(String.format(
                    ChatColor.GREEN + "남은 인원 : " + ChatColor.WHITE + "%d명",
                    PlayerList.size() - this.OKSign.size()));
            if (this.OKSign.size() == PlayerList.size())
                this.s_GameStart.GameStart();
        }
    }

    public final void vano(Player p) {
        if ((Scenario == ScriptStatus.AbilitySelect) &&
                (!this.ExceptionList.contains(p)) &&
                (!this.OKSign.contains(p))) {
            if (reRandomAbility(p) == null) {
                p.sendMessage(ChatColor.RED + "경고, 능력의 갯수가 부족합니다.");
                return;
            }
            AUC.InfoTextOut(p);
            this.OKSign.add(p);
            p.sendMessage(ChatColor.GOLD + "능력이 자동으로 확정되었습니다. 다른 사람을 기다리세요.");
            Bukkit.broadcastMessage(String.format(ChatColor.YELLOW + "%s" +
                            ChatColor.WHITE + "님이 능력을 확정했습니다.", p.getName()));
            Bukkit.broadcastMessage(String.format(
                    ChatColor.GREEN + "남은 인원 : " + ChatColor.WHITE + "%d명",
                    PlayerList.size() - this.OKSign.size()));
            if (this.OKSign.size() == PlayerList.size())
                this.s_GameStart.GameStart();
        }
    }

    private AbilityBase reRandomAbility(Player p) {
        ArrayList<AbilityBase> Alist = new ArrayList<>();
        Random r = new Random();
        int Findex = r.nextInt(AbilityList.AbilityList.size() - 1);
        int saveindex;
        if (Findex == 0) {
            saveindex = AbilityList.AbilityList.size();
        } else {
            saveindex = Findex - 1;
        }
        for (int i = 0; i < AbilityList.AbilityList.size(); i++) {
            if (AbilityList.AbilityList.get(Findex).PlayerCheck(p)) {
                AbilityList.AbilityList.get(Findex).SetPlayer(null, false);
            } else if ((AbilityList.AbilityList.get(Findex).GetPlayer() == null) && (
                    (PlayerList.size() > 6) ||
                            (AbilityList.AbilityList.get(Findex) != AbilityList.mirroring))) {
                Alist.add(AbilityList.AbilityList.get(Findex));
            }
            Findex++;
            if (Findex == saveindex)
                break;
            if (Findex == AbilityList.AbilityList.size())
                Findex = 0;
        }
        if (Alist.isEmpty()) {
            return null;
        }
        if (Alist.size() == 1) {
            Alist.getFirst().SetPlayer(p, false);
            return Alist.getFirst();
        }
        int ran2 = r.nextInt(Alist.size() - 1);
        Alist.get(ran2).SetPlayer(p, false);
        return Alist.get(ran2);
    }

    public enum ScriptStatus {
        NoPlay, ScriptStart, AbilitySelect, GameStart
    }
}
