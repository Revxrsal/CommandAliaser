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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.reflxction.commandaliaser.CommandAliaser;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Class which manages aliases
 */
public class AliasManager {

    // A constant map of aliases
    public static final Map<String, String> ALIASES = Maps.newHashMap();

    private CommandAliaser m;

    public AliasManager(CommandAliaser m) {
        this.m = m;
    }

    /**
     * Creates the alias
     *
     * @param alias Alias to create
     */
    public void create(String alias, String command) {
        ALIASES.put(alias, command);
    }

    /**
     * Removes the alias
     *
     * @param alias Alias to remove
     */
    boolean remove(String alias) {
        boolean changed = ALIASES.get(alias) != null;
        ALIASES.remove(alias);
        return changed;
    }

    /**
     * Saves all aliases into the config. This will be invoked as a shutdown hook.
     */
    public void save() {
        if (ALIASES.isEmpty()) {
            LogManager.getLogger().info("No aliases to save.");
            return;
        }
        LogManager.getLogger().info("Saving alises...");
        try {
            List<String> aliases = Lists.newArrayList();
            ALIASES.forEach((alias, command) -> aliases.add(build(alias, command)));
            m.getAliasesStorage().set("Aliases", aliases);
            m.getAliasesStorage().save(CommandAliaser.ALIASES_STORAGE);
            LogManager.getLogger().info("Aliases saved successfully");
        } catch (IOException e) {
            LogManager.getLogger().error("Failed to save aliases");
            e.printStackTrace();
        }
    }

    /**
     * Returns the command that the alias has
     *
     * @param alias The alias to retrieve from
     * @return The actual command from the alias
     */
    public String getCommand(String alias) {
        return ALIASES.get(alias);
    }

    /**
     * Whether the given string represents an alias or not
     *
     * @param alias Alias to check
     * @return True if it is an alias, false if otherwise.
     */
    public boolean isAlias(String alias) {
        return ALIASES.get(alias) != null;
    }

    private String build(String alias, String command) {
        return alias + "_<<<>>>_" + command;
    }

}
