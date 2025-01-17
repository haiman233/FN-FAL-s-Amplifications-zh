package ne.fnfal113.fnamplifications.staffs;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import ne.fnfal113.fnamplifications.FNAmplifications;
import ne.fnfal113.fnamplifications.staffs.abstracts.AbstractStaff;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StaffOfMuster extends AbstractStaff {

    private final NamespacedKey defaultUsageKey;

    private final MainStaff mainStaff;

    public StaffOfMuster(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        this.defaultUsageKey = new NamespacedKey(FNAmplifications.getInstance(), "musterstaff");
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
        lore.add(1, ChatColor.LIGHT_PURPLE + "Right click a target block to teleport");
        lore.add(2, ChatColor.LIGHT_PURPLE + "nearby entities that are on ground");
        lore.add(3, ChatColor.LIGHT_PURPLE + "within 50 block radius");

        return lore;
    }

    @Override
    public void onRightClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Block block = event.getPlayer().getTargetBlockExact(50);

        if(block == null || item.getType() == Material.AIR){
            return;
        }

        if (!Slimefun.getProtectionManager().hasPermission(
                Bukkit.getOfflinePlayer(player.getUniqueId()),
                block,
                Interaction.BREAK_BLOCK)
        ) {
            player.sendMessage(ChatColor.DARK_RED + "You don't have permission to cast muster there!");
            return;
        }

        int amount = 0;
        for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(), 50, 50, 50)) {
            if(Slimefun.getProtectionManager().hasPermission
                    (Bukkit.getOfflinePlayer(player.getUniqueId()),
                            entity.getLocation(),
                            Interaction.INTERACT_ENTITY)) {

                if (entity instanceof LivingEntity && !(entity instanceof ArmorStand)
                        && !((LivingEntity) entity).isLeashed() && !entity.isInWater() && !(entity instanceof Player)) {
                    entity.teleport(block.getLocation().clone().add(0.5, 1, 0.5));
                    amount = amount + 1;
                } // instanceof check
            } // permission check
        } // for each

        player.sendMessage(ChatColor.GREEN + "Mustered " + amount + " entities");

        block.getWorld().playEffect(block.getLocation().clone().add(0.5, 1, 0.5), Effect.ENDER_SIGNAL, 1);

        ItemMeta meta = item.getItemMeta();

        mainStaff.updateMeta(item, meta, player);

        Objects.requireNonNull(player.getLocation().getWorld()).playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 1);

    }

}