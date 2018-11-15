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
package net.reflxction.commandaliaser.mixins;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraftforge.common.MinecraftForge;
import net.reflxction.commandaliaser.game.SendMessageEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {

    @Shadow
    @Final
    public NetHandlerPlayClient sendQueue;

    /**
     * Called when the player wants to send a message
     *
     * @param message The message to send
     * @author Reflxction
     */
    @Overwrite
    public void sendChatMessage(String message) {
        SendMessageEvent event = new SendMessageEvent(message);
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            this.sendQueue.addToSendQueue(new C01PacketChatMessage(message));
        }
    }
}
