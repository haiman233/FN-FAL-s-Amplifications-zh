package ne.fnfal113.fnamplifications.mysteriousitems;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import ne.fnfal113.fnamplifications.items.FNAmpItems;
import ne.fnfal113.fnamplifications.mysteriousitems.abstracts.AbstractStick;
import ne.fnfal113.fnamplifications.utils.Keys;
import ne.fnfal113.fnamplifications.utils.Utils;
import ne.fnfal113.fnamplifications.mysteriousitems.implementation.MainStick;
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

public class MysteryStick2 extends AbstractStick {

    public final MainStick mainStick;

    @ParametersAreNonnullByDefault
    public MysteryStick2(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        this.mainStick = new MainStick(Keys.STICK_2_EXP_LEVELS, Keys.STICK_2_DAMAGE, enchantments(), weaponLore(), stickLore(), 1, 5);
    }

    @Override
    public Map<Enchantment, Integer> enchantments(){
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        enchantments.put(Enchantment.DAMAGE_ARTHROPODS, 3);
        enchantments.put(Enchantment.DAMAGE_ALL, 2);
        enchantments.put(Enchantment.DAMAGE_UNDEAD, 3);

        return enchantments;
    }

    @Override
    public String weaponLore(){
        return ChatColor.GOLD + "Another stick of wrecking";
    }

    @Override
    public String stickLore(){
        return ChatColor.WHITE + "Another stick of no matter what is it";
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

        if(mainStick.onSwing(item, FNAmpItems.FN_STICK_2, player, event.getDamage(), 27, 1))  {
            LivingEntity victim = (LivingEntity) event.getEntity();
            victim.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 80, 0, false, false, false));
            player.sendMessage(Utils.colorTranslator("&cMystery effects was applied to your enemy"));
        }

    }
}