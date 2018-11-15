/*
 * * Copyright 2018 github.com/ReflxctionDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.reflxction.commandaliaser;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.reflxction.commandaliaser.alias.AliasManager;
import net.reflxction.commandaliaser.alias.AliaserCommand;
import net.reflxction.commandaliaser.commons.Multithreading;
import net.reflxction.commandaliaser.commons.Settings;
import net.reflxction.commandaliaser.game.SendMessageListener;
import net.reflxction.commandaliaser.updater.NotificationSender;
import net.reflxction.commandaliaser.updater.UpdateManager;
import net.reflxction.commandaliaser.updater.VersionChecker;
import net.reflxction.commandaliaser.utils.FileUtil;
import net.reflxction.commandaliaser.utils.Reference;
import net.reflxction.commandaliaser.utils.TimerUtils;
import org.apache.commons.lang3.StringUtils;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * CommandAliaser: A mod for quickly creating aliases for commands
 */
@Mod(
        modid = Reference.MOD_ID,
        name = Reference.NAME,
        version = Reference.VERSION,
        acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS
)
public class CommandAliaser {

    public static File DATA_DIRECTORY;

    public static File ALIASES_STORAGE;

    private AliasManager aliasManager = new AliasManager(this);

    // Config for saving data
    private static Configuration config = new Configuration(new File("config" + File.separator + "command-aliaser.cfg"));

    // The aliases storage
    private FileConfiguration aliasesStorage;

    // The update manager
    private UpdateManager updateManager = new UpdateManager(true, this);

    // The version checker
    private VersionChecker checker = new VersionChecker();

    /**
     * Called before the mod is fully initialized
     * <p>
     * Registries: Initiate variables and client command registries
     *
     * @param event Forge's pre-init event
     */
    @EventHandler
    public void onFMLPreInitialization(FMLPreInitializationEvent event) {
        CommandAliaser.DATA_DIRECTORY = new File(Minecraft.getMinecraft().mcDataDir, "aliaser");
        FileUtil.forceMkdir(CommandAliaser.DATA_DIRECTORY);
        CommandAliaser.ALIASES_STORAGE = new File(CommandAliaser.DATA_DIRECTORY, "aliases.yml");
        setAliasesStorage(FileUtil.loadFile("aliases.yml"));
        if (Settings.SEND_UPDATES.get()) {
            Multithreading.runAsync(() -> getChecker().updateState());
        }
        ClientCommandHandler.instance.registerCommand(new AliaserCommand(this));
    }

    /**
     * Called when the mod has been fully initialized
     * <p>
     * Registries: Events and client-server command registries
     *
     * @param event Forge's init event
     */
    @EventHandler
    public void onFMLInitialization(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new NotificationSender(this));
        MinecraftForge.EVENT_BUS.register(new SendMessageListener(this));
    }

    /**
     * Called after the mod has been successfully initialized
     * <p>
     * Registries: Nothing
     *
     * @param event Forge's post init event
     */
    @EventHandler
    public void onFMLPostInitialization(FMLPostInitializationEvent event) {
        load();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            getAliasManager().save();
            Multithreading.SERVICE.shutdown();
            Multithreading.SCHEDULED_EXECUTOR_SERVICE.shutdown();
        }));
        TimerUtils.runEvery(() -> getAliasManager().save(), 10, TimeUnit.MINUTES);
    }

    /**
     * Called when the Netty thread is starting. Useful to register server-side commands
     *
     * @param event Forge's server-starting event
     */
    @EventHandler
    public void onFMLServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new AliaserCommand(this));
    }

    /**
     * The mod config
     *
     * @return The config file used to store all the mod data and HTTP caches if any
     */
    public static Configuration getConfig() {
        return config;
    }

    /**
     * The mod update manager
     *
     * @return An instance of the mod update manager
     */
    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    /**
     * The mod version checker
     *
     * @return An instance of the mod version checker
     */
    public VersionChecker getChecker() {
        return checker;
    }

    /**
     * The YAML file which stores all the aliases
     *
     * @return The YAML file
     */
    public FileConfiguration getAliasesStorage() {
        return aliasesStorage;
    }

    /**
     * Sets the alias storage. Called on {@link #onFMLPreInitialization(FMLPreInitializationEvent)}
     *
     * @param aliasesStorage New value to set
     */
    private void setAliasesStorage(FileConfiguration aliasesStorage) {
        this.aliasesStorage = aliasesStorage;
    }

    /**
     * Returns the {@link #aliasManager) which manages aliases
     *
     * @return The alias manager
     */
    public AliasManager getAliasManager() {
        return aliasManager;
    }

    /**
     * Loads all the aliases and assigns them appropriately to {@link AliasManager#ALIASES}
     */
    private void load() {
        for (String s : aliasesStorage.getStringList("Aliases")) {
            String[] slices = StringUtils.split(s, "_<<<>>>_");
            AliasManager.ALIASES.put(slices[0], slices[1]);
        }
    }
}