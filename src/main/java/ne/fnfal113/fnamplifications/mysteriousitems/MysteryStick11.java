package ne.fnfal113.fnamplifications.mysteriousitems;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import ne.fnfal113.fnamplifications.items.FNAmpItems;
import ne.fnfal113.fnamplifications.mysteriousitems.abstracts.AbstractStick;
import ne.fnfal113.fnamplifications.mysteriousitems.implementation.MainStick;
import ne.fnfal113.fnamplifications.utils.Keys;
import ne.fnfal113.fnamplifications.utils.Utils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

public class MysteryStick11 extends AbstractStick {

    public final MainStick mainStick;

    @ParametersAreNonnullByDefault
    public MysteryStick11(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        this.mainStick = new MainStick(Keys.STICK_11_EXP_LEVELS, Keys.STICK_11_DAMAGE, enchantments(), weaponLore(), stickLore(), 4, 25);
    }

    @Override
    public Map<Enchantment, Integer> enchantments(){
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        enchantments.put(Enchantment.DAMAGE_ARTHROPODS, 10);
        enchantments.put(Enchantment.DAMAGE_ALL, 8);
        enchantments.put(Enchantment.DAMAGE_UNDEAD, 8);
        enchantments.put(Enchantment.KNOCKBACK, 5);

        return enchantments;
    }

    @Override
    public String weaponLore(){
        return ChatColor.GOLD + "Behind your enemies awaits danger";
    }

    @Override
    public String stickLore(){
        return ChatColor.WHITE + "The stick of the nords";
    }

    @Override
    public void interact(PlayerInteractEvent e) {
        mainStick.onInteract(e, Material.DIAMOND_AXE);
    }

    @Override
    public void onSwing(EntityDamageByEntityEvent event){
        Player player = (Player) event.getDamager();
        ItemStack item = player.getInventory().getItemInMainHand();

        if(item.getType() != Material.DIAMOND_AXE){
            return;
        }

        if(mainStick.onSwing(item, FNAmpItems.FN_STICK_11, player, event.getDamage(), 13, 4))   {
            LivingEntity victim = (LivingEntity) event.getEntity();
            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 3, false, true, false));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 3, false, true, false));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 2, false, true, false));

            Location loc = victim.getLocation();
            loc.setYaw(loc.getYaw() + 180);
            victim.teleport(loc);
            victim.sendMessage(ChatColor.DARK_RED + "You have been disoriented! your opponent's mysterious stick is deadly");
            player.sendMessage(Utils.colorTranslator("&cMystery effects was applied to your enemy"));
        }

    }
}