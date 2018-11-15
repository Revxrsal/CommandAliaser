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
package net.reflxction.commandaliaser.alias;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.reflxction.commandaliaser.CommandAliaser;
import net.reflxction.commandaliaser.commons.Multithreading;
import net.reflxction.commandaliaser.commons.Settings;
import net.reflxction.commandaliaser.game.AliasGUI;
import net.reflxction.commandaliaser.utils.SimpleSender;
import net.reflxction.commandaliaser.utils.TimerUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Class which handles command input for "/commandaliaser"
 */
public class AliaserCommand implements ICommand {

    private CommandAliaser m;

    public AliaserCommand(CommandAliaser m) {
        this.m = m;
    }

    /**
     * Gets the name of the command
     */
    @Override
    public String getCommandName() {
        return "commandaliaser";
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender The command sender that executed the command
     */
    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/commandaliaser <toggle / check / update / alias / remove>";
    }

    @Override
    public List<String> getCommandAliases() {
        return ImmutableList.of("ca", "alias");
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The command sender that executed the command
     * @param args   The arguments that were passed
     */
    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        switch (args.length) {
            case 0:
                SimpleSender.send("&cIncorrect command usage. Try " + getCommandUsage(sender));
                break;
            case 1:
                switch (args[0]) {
                    case "toggle":
                        Settings.ENABLED.set(!Settings.ENABLED.get());
                        SimpleSender.send(Settings.ENABLED.get() ? "&aCommandAliaser has been enabled" : "&cCommandAliaser has been disabled");
                        break;
                    case "update":
                        if (m.getChecker().isUpdateAvailable()) {
                            Multithreading.runAsync(() -> {
                                if (m.getUpdateManager().updateMod()) {
                                    SimpleSender.send("&aSuccessfully updated the mod! Restart your game to see changes.");
                                } else {
                                    SimpleSender.send("&cFailed to update mod! To avoid any issues, delete the mod jar and install it manually again.");
                                }
                            });
                        } else {
                            SimpleSender.send("&cNo updates found. You're up to date!");
                        }
                        break;
                    case "check":
                        Settings.SEND_UPDATES.set(!Settings.SEND_UPDATES.get());
                        SimpleSender.send(Settings.SEND_UPDATES.get() ? "&aYou will be notified on updates" : "&cYou will no longer be notified on updates");
                        break;
                    case "config":
                    case "gui":
                    case "alias":
                    case "aliaser":
                        TimerUtils.runAfter(() -> Minecraft.getMinecraft().displayGuiScreen(new AliasGUI(m)), 50);
                        break;
                    case "save":
                        m.getAliasManager().save();
                        SimpleSender.send("&aSuccessfully saved aliases.");
                        break;
                    default:
                        SimpleSender.send("&cIncorrect command usage. Try " + getCommandUsage(sender));
                        break;
                }
                break;
            case 2:
                switch (args[0]) {
                    case "remove":
                        if (m.getAliasManager().remove(args[1]))
                            SimpleSender.send("&aSuccessfully removed alias: &e" + args[1]);
                        else
                            SimpleSender.send("&cCouldn't find an alias with that name!");

                        break;
                }
                break;
        }
    }

    /**
     * Returns true if the given command sender is allowed to use this command.
     *
     * @param sender The command sender that executed the command
     */
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return Arrays.asList("toggle", "check", "update", "alias", "gui", "remove");
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     *
     * @param args  The arguments that were passed
     * @param index Argument index to check
     */
    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

}
