# Minecraft Plugin: Galeheart Spearmace

Galeheart Spearmace is a Minecraft Paper plugin that adds a custom blue Breeze Spear-Mace weapon with mace combat, launch movement, falling damage, speed-based damage, custom enchantments, cooldown handling, and permission-controlled commands.

## Install

1. Build the plugin with Java 21 and Maven:

   ```bash
   mvn clean package
   ```

2. Copy the generated `plugin.jar` file from the `target` directory into your Paper server's `plugins` directory.

3. Start or restart the server.

4. Confirm that the server is running Paper 1.21 or a compatible version using the Paper API supported by this project.

## Use

1. Give yourself permission to use the plugin:

   ```text
   /op <player>
   ```

   The required permission is `spearmace.use`.

2. Run the command below to receive the weapon:

   ```text
   /spearmace
   ```

3. Hold the Breeze Spear-Mace in your main hand.

4. Launch yourself into the air with:

   ```text
   /spearmace launch
   ```

   The launch applies horizontal movement and a strong upward boost. It has a 30-tick cooldown and requires the weapon to be held in the main hand.

5. Attack a living entity while holding the weapon. The plugin adds damage based on your current movement speed and falling distance. Falling strikes receive additional mace-style damage, and the fall distance is cleared after a successful hit.

6. The weapon is created as a mace item with the name “Breeze Spear-Mace.” It has descriptive lore and attempts to apply Wind Burst III, Lunge III, Mending I, and Unbreaking VI when those enchantments are available on the server.

7. The plugin does not register a crafting recipe. The weapon is obtained through `/spearmace`.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/42928fdb-12f7-4484-a91a-f94a955c6d86?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=42928fdb-12f7-4484-a91a-f94a955c6d86
