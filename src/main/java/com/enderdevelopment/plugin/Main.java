package com.enderdevelopment.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class Main extends JavaPlugin implements Listener {
    private static final double LAUNCH_HORIZONTAL_SCALE = 0.35D;
    private static final double LAUNCH_VERTICAL_SPEED = 1.45D;
    private static final long LAUNCH_COOLDOWN_TICKS = 30L;
    private static final double MAX_FALLING_BONUS = 28.0D;
    private static final double SPEED_DAMAGE_SCALE = 3.0D;

    private NamespacedKey weaponKey;
    private final Map<UUID, Long> launchCooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        weaponKey = new NamespacedKey(this, "spear_mace");
        Bukkit.getPluginManager().registerEvents(this, this);
        if (getCommand("spearmace") != null) {
            getCommand("spearmace").setExecutor((sender, command, label, args) -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Only players can use the Spear-Mace command.");
                    return true;
                }
                if (!player.hasPermission("spearmace.use")) {
                    player.sendMessage(Component.text("You do not have permission to use this weapon.", NamedTextColor.RED));
                    return true;
                }
                if (args.length == 1 && args[0].equalsIgnoreCase("launch")) {
                    launchPlayer(player);
                    return true;
                }
                player.getInventory().addItem(createSpearMace());
                player.sendMessage(Component.text("You received the ", NamedTextColor.GRAY)
                        .append(Component.text("Breeze Spear-Mace", NamedTextColor.AQUA))
                        .append(Component.text(". Bind G to /spearmace launch.", NamedTextColor.GRAY)));
                return true;
            });
        }
    }

    @Override
    public void onDisable() {
        launchCooldowns.clear();
    }

    private ItemStack createSpearMace() {
        ItemStack item = new ItemStack(Material.MACE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Breeze Spear-Mace", NamedTextColor.BLUE));
        meta.lore(List.of(
                Component.text("A long breeze-rod handle with a", NamedTextColor.DARK_AQUA),
                Component.text("blue and dark-blue spearhead.", NamedTextColor.DARK_AQUA),
                Component.text("Press G: launch into the air", NamedTextColor.AQUA),
                Component.text("Falling strikes gain mace damage.", NamedTextColor.AQUA)));
        meta.getPersistentDataContainer().set(weaponKey, PersistentDataType.BYTE, (byte) 1);
        addEnchantment(meta, "wind_burst", 3);
        addEnchantment(meta, "lunge", 3);
        addEnchantment(meta, "mending", 1);
        addEnchantment(meta, "unbreaking", 6);
        item.setItemMeta(meta);
        return item;
    }

    private void addEnchantment(ItemMeta meta, String key, int level) {
        Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(key));
        if (enchantment != null) {
            meta.addEnchant(enchantment, level, true);
        }
    }

    private boolean isSpearMace(ItemStack item) {
        if (item == null || item.getType() != Material.MACE || !item.hasItemMeta()) {
            return false;
        }
        Byte marker = item.getItemMeta().getPersistentDataContainer().get(weaponKey, PersistentDataType.BYTE);
        return marker != null && marker == (byte) 1;
    }

    private void launchPlayer(Player player) {
        if (!isSpearMace(player.getInventory().getItemInMainHand())) {
            player.sendMessage(Component.text("Hold the Breeze Spear-Mace in your main hand.", NamedTextColor.RED));
            return;
        }

        long now = player.getWorld().getGameTime();
        long availableAt = launchCooldowns.getOrDefault(player.getUniqueId(), 0L);
        if (now < availableAt) {
            long seconds = (availableAt - now + 19L) / 20L;
            player.sendMessage(Component.text("The launch is on cooldown for " + seconds + "s.", NamedTextColor.YELLOW));
            return;
        }

        Vector direction = player.getLocation().getDirection().setY(0).normalize();
        player.setVelocity(direction.multiply(LAUNCH_HORIZONTAL_SCALE).setY(LAUNCH_VERTICAL_SPEED));
        player.setFallDistance(0.0F);
        launchCooldowns.put(player.getUniqueId(), now + LAUNCH_COOLDOWN_TICKS);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_JUMP, 1.0F, 0.85F);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpearMaceHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player) || !isSpearMace(player.getInventory().getItemInMainHand())) {
            return;
        }
        Entity target = event.getEntity();
        if (!(target instanceof LivingEntity)) {
            return;
        }

        double speed = player.getVelocity().length();
        double speedBonus = Math.min(12.0D, speed * SPEED_DAMAGE_SCALE);
        double fallingBonus = Math.min(MAX_FALLING_BONUS, player.getFallDistance() * 2.5D);
        event.setDamage(event.getDamage() + speedBonus + fallingBonus);
        player.setFallDistance(0.0F);
    }
}
