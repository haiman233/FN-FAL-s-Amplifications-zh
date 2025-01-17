package ne.fnfal113.fnamplifications.staffs;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import ne.fnfal113.fnamplifications.FNAmplifications;
import ne.fnfal113.fnamplifications.staffs.abstracts.AbstractStaff;
import ne.fnfal113.fnamplifications.staffs.implementations.AirStriderTask;
import ne.fnfal113.fnamplifications.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StaffOfAirStrider extends AbstractStaff {

    private final NamespacedKey defaultUsageKey;

    private final Map<UUID, BukkitTask> taskMap = new HashMap<>();

    private final MainStaff mainStaff;

    public StaffOfAirStrider(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        this.defaultUsageKey = new NamespacedKey(FNAmplifications.getInstance(), "airstriderstaff");
        this.mainStaff = new MainStaff(lore(), 10, getStorageKey(), this.getItem(), this.getId());
    }

    protected @Nonnull
    NamespacedKey getStorageKey() {
        return defaultUsageKey;
    }

    @Override
    public List<String> lore(){
        List<String> lore = new ArrayList<>();
        lore.add(0, "");
        lore.add(1, ChatColor.LIGHT_PURPLE + "Right click to gain the ability to");
        lore.add(2, ChatColor.LIGHT_PURPLE + "walk on the air for 10 seconds");

        return lore;
    }

    @Override
    public void onRightClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if(taskMap.containsKey(player.getUniqueId())) {
            if(!taskMap.get(player.getUniqueId()).isCancelled()){
                player.sendMessage(Utils.colorTranslator("&6Air strider is not yet expired!"));
            }
            return;
        } else {
            if(Slimefun.getProtectionManager().hasPermission
                    (Bukkit.getOfflinePlayer(player.getUniqueId()), player.getLocation(), Interaction.PLACE_BLOCK)) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "You can now walk on the air for 10 seconds");
                taskMap.put(player.getUniqueId(), new AirStriderTask(player).runTaskTimer(FNAmplifications.getInstance(), 0, 1L));
            } else{
                player.sendMessage(ChatColor.RED  + "You have no permission to cast air strider on this land claim!");
                return;
            }
        }

        ItemMeta meta = item.getItemMeta();

        mainStaff.updateMeta(item, meta, player);

        AtomicInteger i = new AtomicInteger(10);
        Bukkit.getScheduler().runTaskTimer(FNAmplifications.getInstance(), task -> {
            if(i.get() <= 5){
                player.sendMessage(Utils.colorTranslator("&dAir strider will expire in ") + i + " seconds");
            }
            if(i.get() == 0){
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Air Strider has expired!");
                taskMap.get(player.getUniqueId()).cancel();
                taskMap.remove(player.getUniqueId());
                task.cancel();
            }
            i.getAndDecrement();
        },0L, 23L);

        Objects.requireNonNull(player.getLocation().getWorld()).playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 1);

    }
}