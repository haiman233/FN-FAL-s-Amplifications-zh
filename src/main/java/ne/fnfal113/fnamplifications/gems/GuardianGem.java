package ne.fnfal113.fnamplifications.gems;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import lombok.Getter;
import ne.fnfal113.fnamplifications.FNAmplifications;
import ne.fnfal113.fnamplifications.gems.implementation.Gem;
import ne.fnfal113.fnamplifications.gems.abstracts.AbstractGem;
import ne.fnfal113.fnamplifications.gems.handlers.OnDamageHandler;
import ne.fnfal113.fnamplifications.utils.Keys;
import ne.fnfal113.fnamplifications.gems.implementation.GuardianTask;
import ne.fnfal113.fnamplifications.gems.implementation.WeaponArmorEnum;
import ne.fnfal113.fnamplifications.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("ConstantConditions")
public class GuardianGem extends AbstractGem implements OnDamageHandler {

    @Getter
    private final int chance;

    private final Map<UUID, Zombie> entityUUIDMap = new HashMap<>();
    private final Map<UUID, BukkitTask> runnableMap = new HashMap<>();

    public GuardianGem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, 8);

        this.chance = FNAmplifications.getInstance().getConfigManager().getValueById(this.getId() + "-percent-chance");
    }

    @Override
    public void onDrag(InventoryClickEvent event, Player player){
        if(event.getCursor() == null){
            return;
        }

        ItemStack currentItem = event.getCurrentItem();

        SlimefunItem slimefunItem = SlimefunItem.getByItem(event.getCursor());

        if(slimefunItem != null && currentItem != null) {
            if ((WeaponArmorEnum.CHESTPLATE.isTagged(currentItem.getType()))) {
                new Gem(slimefunItem, currentItem, player).onDrag(event, false);
            } else {
                player.sendMessage(Utils.colorTranslator("&eInvalid item to socket! Gem works on chestplate only"));
            }
        }
    }

    @Override
    public void onDamage(EntityDamageByEntityEvent event){
        if(event.isCancelled()){
            return;
        }

        Player player = (Player) event.getEntity();

        if(event.getDamager() instanceof Phantom || event.getDamager() instanceof Flying || event.getDamager() instanceof EnderDragon){
            return;
        } // prevent guardian from attacking flying entities

        if(runnableMap.containsKey(player.getUniqueId())){
            if(runnableMap.get(player.getUniqueId()).isCancelled()){
                entityUUIDMap.remove(player.getUniqueId());
                runnableMap.remove(player.getUniqueId());
                return;
            } // remove the map when the runnable associated with the UUID is cancelled
        }

        if(!entityUUIDMap.containsKey(player.getUniqueId())) {
            if(ThreadLocalRandom.current().nextInt(100) < getChance()) {
                GuardianTask guardianTask = new GuardianTask(player, event);

                runnableMap.put(player.getUniqueId(),
                        guardianTask.runTaskTimer(FNAmplifications.getInstance(), 5L, 3L));
                entityUUIDMap.put(player.getUniqueId(), guardianTask.getZombie());
            } // make a guardian runnable for the player using UUID and a new instance of the guardian task
        } else {
            Zombie zombie = entityUUIDMap.get(player.getUniqueId());

            if(zombie.getTarget() != null && !zombie.getTarget().isDead()){
                return;
            } // don't change target if the current target is not yet dead

            if(event.getDamager().getPersistentDataContainer().has(Keys.GUARDIAN_KEY, PersistentDataType.STRING)){
                zombie.setTarget(Bukkit.getPlayer(event.getDamager().getPersistentDataContainer().get(Keys.GUARDIAN_KEY, PersistentDataType.STRING)));
            } // if the damager has a guardian, attack the owner of the guardian instead
            else {
                zombie.setTarget(event.getDamager() instanceof Projectile ?
                        (LivingEntity) ((Projectile) event.getDamager()).getShooter() : (LivingEntity) event.getDamager());
            } // target the damager, will attack projectile shooter

        } // guardian targets the entity that damaged the player
    }
}