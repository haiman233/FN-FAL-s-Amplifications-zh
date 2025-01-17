package ne.fnfal113.fnamplifications.staffs;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.utils.WorldUtils;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import ne.fnfal113.fnamplifications.FNAmplifications;
import ne.fnfal113.fnamplifications.staffs.abstracts.AbstractStaff;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.*;

public class StaffOfMinerals extends AbstractStaff {

    private final NamespacedKey defaultUsageKey;

    private final MainStaff mainStaff;

    public StaffOfMinerals(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        this.defaultUsageKey = new NamespacedKey(FNAmplifications.getInstance(), "mineralstaff");
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
        lore.add(1, ChatColor.LIGHT_PURPLE + "Right click to receive mythical");
        lore.add(2, ChatColor.LIGHT_PURPLE + "information that awaits upon using");
        lore.add(3, ChatColor.LIGHT_PURPLE + "the staff");

        return lore;
    }

    @Override
    public void onRightClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Chunk chunk = player.getLocation().getChunk();

        Set<Material> materials = SlimefunTag.ORES.getValues();
        Map<String, Integer> MINERALS = new HashMap<>();
        List<String> contents = new ArrayList<>();
        List<String> firstPage = new ArrayList<>();

        int amount = 0;

        for(int y = WorldUtils.getMinHeight(chunk.getWorld()); y <= chunk.getWorld().getMaxHeight(); y++) {
            for(int x = 0; x <= 15; x++) {
                for(int z = 0; z <= 15; z++) {
                    Block itemStack = chunk.getBlock(x, y, z);

                    if(materials.contains(itemStack.getType())) {
                        MINERALS.put(itemStack.getType().name(), MINERALS.getOrDefault(itemStack.getType().name(), 0) + 1);
                        amount = amount + 1;
                    }
                }
            }
        }

        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
        if(bookMeta == null){
            return;
        }
        bookMeta.setTitle("Mineral Ores");
        bookMeta.setAuthor("FN_FAL113");

        if(amount != 0) {
            MINERALS.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(e -> contents.add(ChatColor.DARK_GREEN + e.getValue().toString() + "x " + ChatColor.GOLD + StringUtils.capitalize(e.getKey().toLowerCase(Locale.ROOT))));


            firstPage.add(ChatColor.BLUE + "    Staff of Minerals\n\n " + ChatColor.GRAY +
                    "  Through the power of the staff, you are bestowed with magical information written on this book containing the ores from the chunk you are standing at");

            bookMeta.addPage(firstPageBook(firstPage));
            for (int i = 0; i < contents.size(); i = i + 5) {
                bookMeta.addPage(contents.subList(i, Math.min(i + 5, contents.size())).toString()
                        .replace("[", "")
                        .replace("]", "")
                        .replace(":", ChatColor.GRAY + " =")
                        .replace(", ", "\n\n")
                        .replace("_", " "));
            }

        } else {
            firstPage.add(ChatColor.BLUE + "    Staff of Minerals\n\n " + ChatColor.GRAY +
                    "  There are no ores from your chunk location");
            bookMeta.addPage(firstPageBook(firstPage));
        }

        ItemMeta meta = item.getItemMeta();

        mainStaff.updateMeta(item, meta, player);

        writtenBook.setItemMeta(bookMeta);
        player.openBook(writtenBook);

        Objects.requireNonNull(player.getLocation().getWorld()).playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 1);

    }

    public String firstPageBook(List<String> firstPage){
        return firstPage.toString().replace("[", "").replace("]", "");
    }
}