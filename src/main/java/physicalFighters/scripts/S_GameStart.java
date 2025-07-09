package physicalFighters.scripts;

import physicalFighters.core.AbilityBase;
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
    public static int PlayDistanceBuffer = 0;

    public S_GameStart(MainScripter ms) {
        this.ms = ms;
    }

    public void GameStart() {
        this.stimer.StartTimer(15);
    }

    public void GameStartStop() {
        this.stimer.StopTimer();
        AbilityBase.restrictionTimer.StopTimer();
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
            if (!PhysicalFighters.NoClearInventory)
                p.getInventory().clear();
            if (PhysicalFighters.Respawn) {
                p.teleport(l);
            }
            if (PhysicalFighters.DefaultArmed) {
                p.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1));
                p.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
                p.getInventory().setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS, 1));
                p.getInventory().setBoots(new ItemStack(Material.GOLDEN_BOOTS, 1));
                p.getInventory().setItem(0, new ItemStack(Material.GOLDEN_SWORD, 1));
                p.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 64));
                p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 64));
            } else if (!PhysicalFighters.NoClearInventory) {
                p.getInventory().setHelmet(null);
                p.getInventory().setChestplate(null);
                p.getInventory().setLeggings(null);
                p.getInventory().setBoots(null);
            }
            if (PhysicalFighters.MaxLevelSurvival) {
                p.setLevel(PhysicalFighters.Setlev);
            }
            if (PhysicalFighters.Kimiedition) {
                p.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET, 1));
                p.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
                p.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
                p.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS, 1));
                p.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
                p.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 64));
                p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 64));
                p.setLevel(500);
            }
            if (PhysicalFighters.Specialability) {
                Bukkit.broadcastMessage(ChatColor.GREEN + "인기있는 능력만 적용됩니다.");
                PhysicalFighters.Specialability = true;
            }
            if (PhysicalFighters.TableGive) {
                p.getInventory().addItem(new ItemStack(Material.ENCHANTING_TABLE, 1));
                p.getInventory().addItem(new ItemStack(Material.BOOKSHELF, 64));
            }
            if (PhysicalFighters.WoodGive) {
                p.getInventory().addItem(new ItemStack(Material.OAK_LOG, 64));
            }
        }
        if (PhysicalFighters.DefaultArmed) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "기본 무장이 제공됩니다.");
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "기본 무장이 제공되지 않습니다.");
        }
        if (PhysicalFighters.MaxLevelSurvival) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "만렙 서바이벌 모드입니다. 아이템 제공.");
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
                    S_GameStart.this.ms.s_GameWarnning.GameWarnningStop();
                    break;
                case 3:
                    Bukkit.broadcastMessage(ChatColor.WHITE +
                            "모든 플레이어들의 능력을 확정했습니다.");
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
                    PhysicalFighters.log.info("플레이어들의 능력");
                    for (AbilityBase a : AbilityList.AbilityList) {
                        if (a.getPlayer() != null) {
                            PhysicalFighters.log.info(String.format("%d. %s - %s",
                                    c, a.getPlayer().getName(), a.getAbilityName()));
                            c++;
                        }
                    }
                    PhysicalFighters.log.info("-------------------------");
                    if (PhysicalFighters.Invincibility) {
                        Bukkit.broadcastMessage("시작 직후 " +
                                PhysicalFighters.EarlyInvincibleTime +
                                "분간은 무적입니다.");
                        EventManager.DamageGuard = true;
                    } else {
                        Bukkit.broadcastMessage(ChatColor.RED + "초반 무적은 작동하지 않습니다.");
                    }
                    if (PhysicalFighters.RestrictionTime != 0) {
                        AbilityBase.restrictionTimer.StartTimer(PhysicalFighters.RestrictionTime * 60);
                    } else {
                        Bukkit.broadcastMessage(ChatColor.YELLOW +
                                "제약 카운트는 동작하지 않습니다.");
                    }
                    S_GameStart.this.RespawnTeleport();
                    S_GameStart.PlayDistanceBuffer = MainScripter.PlayerList.size() * 50;
                    java.util.List<World> w = Bukkit.getWorlds();
                    for (World wl : w) {
                        wl.setTime(1L);
                        wl.setStorm(false);
                        if (PhysicalFighters.AutoDifficultySetting)
                            wl.setDifficulty(org.bukkit.Difficulty.EASY);
                        wl.setWeatherDuration(0);
                        wl.setSpawnFlags(wl.getAllowMonsters(),
                                !PhysicalFighters.NoAnimal);
                        wl.setPVP(true);
                    }
                    for (AbilityBase b : AbilityList.AbilityList) {
                        b.setRunAbility(true);
                        b.setPlayer(b.getPlayer(), false);
                    }
                    S_GameStart.this.ms.s_GameProgress.GameProgress();
                    if (!PhysicalFighters.AutoCoordinateOutput) {
                        break;
                    }
            }
        }

        public void EventEndTimer() {
        }
    }
}
