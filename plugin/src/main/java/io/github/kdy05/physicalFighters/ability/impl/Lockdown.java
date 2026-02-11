package io.github.kdy05.physicalFighters.ability.impl;

import io.github.kdy05.physicalFighters.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import io.github.kdy05.physicalFighters.ability.Ability;
import io.github.kdy05.physicalFighters.ability.AbilitySpec;
import io.github.kdy05.physicalFighters.ability.AbilityUtils;
import io.github.kdy05.physicalFighters.command.CommandInterface;

public class Lockdown extends Ability implements CommandInterface {
    // 능력 설정 상수
    private static final double MAX_RANGE = 60.0;          // 최대 사용 거리 (60블록)
    private static final int LOCKDOWN_DURATION = 60;      // 봉인 지속시간 (60초)
    // 임시 저장 필드
    private Ability targetAbility = null;
    private Player caster = null;
    private String targetName = null;

    public Lockdown(Player player) {
        super(AbilitySpec.builder("봉인", Type.Active_Continue, Rank.B)
                .cooldown(80)
                .duration(LOCKDOWN_DURATION)
                .guide("특정 플레이어의 능력을 1분간 봉인하며 배고픔 수치를 0으로 만듭니다.",
                        "\"/va lock <nickname>\" 명령어로 작동하며 대상이 60칸 이내에 있어야 합니다.",
                        "게임 시작 후 제약 시간 동안 능력 사용이 제한됩니다.")
                .build(), player);
    }

    @Override
    public int A_Condition(Event event, int CustomData) {
        if (caster == null || !isOwner(caster)) {
            if (caster != null) caster.sendMessage(ChatColor.RED + "이 명령은 사용할 수 없습니다.");
            clearTempData();
            return -1;
        }

        int currentTime = plugin.getGameManager().getGameTime();
        int restrictionTime = plugin.getConfigManager().getRestrictionTime();
        GameManager.ScriptStatus status = plugin.getGameManager().getScenario();
        if (status == GameManager.ScriptStatus.GameStart && currentTime <= restrictionTime * 60) {
            caster.sendMessage(ChatColor.RED + "아직 능력 제한 시간입니다. " + String.format("(%d/%d)",
                    currentTime / 60, restrictionTime));
            return -1;
        }

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            caster.sendMessage(ChatColor.RED + "존재하지 않는 플레이어입니다.");
            clearTempData();
            return -1;
        }

        if (caster.getName().equals(target.getName())) {
            caster.sendMessage(ChatColor.RED + "자기 자신에게 능력을 사용할 수 없습니다.");
            clearTempData();
            return -1;
        }

        targetAbility = AbilityUtils.findAbility(target);
        if (targetAbility == null) {
            caster.sendMessage(ChatColor.RED + "옵저버입니다.");
            clearTempData();
            return -1;
        }

        double distance = caster.getLocation().distance(target.getLocation());
        if (distance > MAX_RANGE) {
            caster.sendMessage(ChatColor.RED + String.format(
                    "거리가 너무 멉니다. (현재: %.1f블록, 최대: %.0f블록)", distance, MAX_RANGE));
            clearTempData();
            return -1;
        }

        return 0;
    }

    @Override
    public void A_Effect(Event event, int CustomData) {
        // A_DurationStart에서 실제 효과 처리
    }

    @Override
    public void A_DurationStart() {
        if (caster == null || targetAbility == null) {
            return;
        }

        Player target = targetAbility.getPlayer();
        if (target == null) return;

        caster.sendMessage(ChatColor.YELLOW +
                String.format("%s님의 능력을 %d초간 봉인합니다.", target.getName(), LOCKDOWN_DURATION));
        target.sendMessage(ChatColor.RED +
                String.format("경고, %s님이 당신에게 봉인 능력을 사용했습니다.", caster.getName()));
        target.sendMessage(ChatColor.RED +
                "지속 효과가 해제되고 1분간 능력 효과가 봉인됩니다.");

        targetAbility.cancelDTimer();
        targetAbility.cancelCTimer();
        targetAbility.unregisterEvents();

        if (!plugin.getConfigManager().isNoFoodMode()) {
            targetAbility.getPlayer().setFoodLevel(0);
        }

        clearTempData();
    }

    @Override
    public void A_FinalDurationEnd() {
        if (targetAbility != null) {
            Player target = targetAbility.getPlayer();
            if (target != null) {
                target.sendMessage(ChatColor.GREEN + "봉인이 해제되었습니다.");
                targetAbility.registerEvents();
            }
            targetAbility = null;
        }
    }

    @Override
    public boolean onCommandEvent(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !args[0].equalsIgnoreCase("lock")) {
            return false;
        }

        this.caster = (Player) sender;
        this.targetName = args[1];

        this.execute(null, 0);
        return true;
    }

    private void clearTempData() {
        this.caster = null;
        this.targetName = null;
    }
}
