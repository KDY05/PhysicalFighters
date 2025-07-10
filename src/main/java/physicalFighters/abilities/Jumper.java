package physicalFighters.abilities;

import physicalFighters.core.Ability;
import physicalFighters.utils.Vector;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class Jumper
        extends Ability {
    public Jumper() {
        InitAbility("점퍼", Type.Active_Immediately, Rank.B, new String[]{
                "최대 40칸 거리를 순간이동 할수 있습니다.",
                "단, 벽은 통과할수 없고 낙사 데미지도 받습니다.",
                "자신이 갈 장소의 바닥 블럭을 클릭해야 텔포가 됩니다.",
                "사용시 유의하세요. 허공엔 사용이 안됩니다!"});
        InitAbility(20, 0, true);
        registerLeftClickEvent();
    }

    public int A_Condition(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        if ((isOwner(Event.getPlayer())) && (isValidItem(Ability.DefaultItem))) {
            Player p = Event.getPlayer();
            Block b = p.getTargetBlock(null, 0);
            Location loc = b.getLocation();
            Location ploc = p.getLocation();
            Vector targetvec;
            if ((b.getRelative(0, 1, 0).getType() == Material.AIR) && (b.getRelative(0, 2, 0).getType() == Material.AIR)) {
                targetvec = new Vector(loc.getX(), loc.getY(), loc.getZ());
            } else
                targetvec = new Vector(loc.getX(), loc.getY(), loc.getZ());
            Vector playervec = new Vector(ploc.getX(), ploc.getY(), ploc.getZ());
            if ((playervec.distance(targetvec) <= 40.0D) && (b.getY() != 0)) {
                return 0;
            }
            p.sendMessage(String.format(ChatColor.RED + "거리가 너무 멉니다.", new Object[0]));
        }
        return -1;
    }

    public void A_Effect(Event event, int CustomData) {
        PlayerInteractEvent Event = (PlayerInteractEvent) event;
        Player p = Event.getPlayer();
        Block b = p.getTargetBlock(null, 0);
        Location loc = b.getLocation();
        Vector targetvec;
        if ((b.getRelative(0, 1, 0).getType() == Material.AIR) && (b.getRelative(0, 2, 0).getType() == Material.AIR)) {
            targetvec = new Vector(loc.getX(), loc.getY(), loc.getZ());
        } else {
            targetvec = new Vector(loc.getX(), b.getWorld().getHighestBlockYAt(b.getX(), b.getZ()), loc.getZ());
        }
        loc.setX(targetvec.getX() + 0.5D);
        loc.setY(targetvec.getY() + 1.0D);
        loc.setZ(targetvec.getZ() + 0.5D);
        loc.setPitch(p.getLocation().getPitch());
        loc.setYaw(p.getLocation().getYaw());
        p.teleport(loc);
    }
}


/* Location:              E:\플러그인\1.7.10모드능력자(95개).jar!\Physical\Fighters\AbilityList\Jumper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */