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
package net.reflxction.commandaliaser.game;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.commons.lang3.StringUtils;

/**
 * Fired when the player sends a message to the server (when {@link net.minecraft.network.play.client.C01PacketChatMessage}
 * is being sent)
 */
@Cancelable
public class SendMessageEvent extends Event {

    // The message that is being sent
    private String message;

    private String commandText;

    public SendMessageEvent(String message) {
        this.message = message;
        commandText = StringUtils.replaceOnce(message, "/", "");
    }

    /**
     * The message that is being sent
     *
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the text of the command. This will equal {@link #getMessage()} if the
     * sent message isn't a command.
     *
     * @return The command
     */
    public String getCommandText() {
        return commandText;
    }
}
