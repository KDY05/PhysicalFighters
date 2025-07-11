package physicalFighters.scripts;

import physicalFighters.core.Ability;
import physicalFighters.core.AbilityList;
import physicalFighters.core.EventManager;
import physicalFighters.utils.TimerBase;
import physicalFighters.PhysicalFighters;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class S_GameStart {
    private final MainScripter ms;
    private final S_ScriptTimer stimer = new S_ScriptTimer();
    private final PhysicalFighters plugin;
    public static int PlayDistanceBuffer = 0;

    public S_GameStart(MainScripter ms, PhysicalFighters plugin) {
        this.ms = ms;
        this.plugin = plugin;
    }

    public void GameStart() {
        this.stimer.startTimer(15, false);
    }

    public void GameStartStop() {
        this.stimer.stopTimer();
        Ability.restrictionTimer.stopTimer();
    }

    private void RespawnTeleport() {
        Location l = this.ms.gameworld.getSpawnLocation();
        l.setY(this.ms.gameworld.getHighestBlockYAt((int) l.getX(),
                (int) l.getZ()));
        for (Player p : MainScripter.PlayerList) {
            p.setFoodLevel(20);
            p.setLevel(0);
            p.setExhaustion(0.0F);
            p.setExp(0.0F);
            p.setHealth((int) 20.0D);
            p.setSaturation(10.0F);
            p.setLevel(PhysicalFighters.Setlev);

            if (PhysicalFighters.ClearInventory) {
                p.getInventory().clear();
            }
            if (PhysicalFighters.Respawn) {
                p.teleport(l);
            }
            if (PhysicalFighters.DefaultArmed) {
                p.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
                p.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
                p.getInventory().setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS, 1));
                p.getInventory().setBoots(new ItemStack(Material.GOLDEN_BOOTS, 1));
                p.getInventory().addItem(new ItemStack(Material.GOLDEN_SWORD, 1));
                p.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 64));
                p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 64));
                Bukkit.broadcastMessage(ChatColor.GREEN + "기본 무장이 제공됩니다.");
            }
            if (PhysicalFighters.TableGive) {
                p.getInventory().addItem(new ItemStack(Material.ENCHANTING_TABLE, 1));
                p.getInventory().addItem(new ItemStack(Material.BOOKSHELF, 64));
            }
            if (PhysicalFighters.WoodGive) {
                p.getInventory().addItem(new ItemStack(Material.OAK_LOG, 64));
            }
            if (PhysicalFighters.Specialability) {
                Bukkit.broadcastMessage(ChatColor.GREEN + "인기있는 능력만 적용됩니다.");
            }
        }
        for (Player p : this.ms.ExceptionList) {
            p.teleport(l);
        }
    }

    public final class S_ScriptTimer extends TimerBase {
        public S_ScriptTimer() {
        }

        public void EventStartTimer() {
            MainScripter.Scenario = MainScripter.ScriptStatus.GameStart;
        }

        public void EventRunningTimer(int count) {
            switch (count) {
                case 0:
                    S_GameStart.this.ms.s_GameWarning.GameWarnningStop();
                    break;
                case 3:
                    Bukkit.broadcastMessage(ChatColor.WHITE + "모든 플레이어들의 능력을 확정했습니다.");
                    break;
                case 5:
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "잠시 후 게임이 시작됩니다.");
                    break;
                case 10:
                    Bukkit.broadcastMessage(ChatColor.GOLD + "5초 전 ");
                    break;
                case 11:
                    Bukkit.broadcastMessage(ChatColor.GOLD + "4초 전");
                    break;
                case 12:
                    Bukkit.broadcastMessage(ChatColor.GOLD + "3초 전 ");
                    break;
                case 13:
                    Bukkit.broadcastMessage(ChatColor.GOLD + "2초 전");
                    break;
                case 14:
                    Bukkit.broadcastMessage(ChatColor.GOLD + "1초 전");
                    break;
                case 15:
                    Bukkit.broadcastMessage(ChatColor.GREEN + "게임이 시작되었습니다. ");
                    int c = 0;
                    plugin.getLogger().info("플레이어들의 능력");
                    for (Ability a : AbilityList.AbilityList) {
                        if (a.getPlayer() != null) {
                            plugin.getLogger().info(String.format("%d. %s - %s",
                                    c, a.getPlayer().getName(), a.getAbilityName()));
                            c++;
                        }
                    }
                    plugin.getLogger().info("-------------------------");
                    if (PhysicalFighters.EarlyInvincibleTime != 0) {
                        Bukkit.broadcastMessage("시작 직후 " + PhysicalFighters.EarlyInvincibleTime
                                + "분간은 무적입니다.");
                        EventManager.DamageGuard = true;
                    } else {
                        Bukkit.broadcastMessage(ChatColor.RED + "초반 무적은 작동하지 않습니다.");
                    }
                    if (PhysicalFighters.RestrictionTime != 0) {
                        Ability.restrictionTimer.startTimer(PhysicalFighters.RestrictionTime * 60, false);
                    } else {
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "제약 카운트는 동작하지 않습니다.");
                    }
                    S_GameStart.this.RespawnTeleport();
                    S_GameStart.PlayDistanceBuffer = MainScripter.PlayerList.size() * 50;
                    java.util.List<World> w = Bukkit.getWorlds();
                    for (World wl : w) {
                        wl.setTime(1L);
                        wl.setStorm(false);
                        wl.setWeatherDuration(0);
                        wl.setPVP(true);
                    }
                    for (Ability b : AbilityList.AbilityList) {
                        b.setRunAbility(true);
                        b.setPlayer(b.getPlayer(), false);
                    }
                    S_GameStart.this.ms.s_GameProgress.GameProgress();
            }
        }

        public void EventEndTimer() {
        }
    }
}
