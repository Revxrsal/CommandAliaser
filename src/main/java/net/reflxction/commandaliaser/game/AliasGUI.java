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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.reflxction.commandaliaser.CommandAliaser;
import net.reflxction.commandaliaser.alias.AliasManager;
import net.reflxction.commandaliaser.commons.ChatColor;
import net.reflxction.commandaliaser.utils.SimpleSender;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

public class AliasGUI extends GuiScreen {

    private GuiTextField commandField;
    private GuiTextField aliasField;

    private static final int AQUA = Color.CYAN.getRGB();
    private static final int GREEN = Color.GREEN.getRGB();

    private CommandAliaser aliaser;

    public AliasGUI(CommandAliaser aliaser) {
        this.aliaser = aliaser;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        commandField.drawTextBox();
        aliasField.drawTextBox();
        drawString(Minecraft.getMinecraft().fontRendererObj, "Alias", width / 2 - 25, height / 2 - 36, GREEN);
        drawString(Minecraft.getMinecraft().fontRendererObj, "Command", width / 2 - 25, height / 2 - 80, AQUA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 2:
                if (commandField.getText().isEmpty()) {
                    SimpleSender.send("&cThe command field is required!");
                    break;
                }
                if (aliasField.getText().isEmpty()) {
                    SimpleSender.send("&cThe alias field is required!");
                    break;
                }
                if (!aliasField.getText().isEmpty() && !commandField.getText().isEmpty()) {
                    String command = commandField.getText();
                    String alias = aliasField.getText();
                    if (AliasManager.ALIASES.containsKey(alias)) {
                        SimpleSender.send("&cAn alias with this value already exists! Please choose another value.");
                    } else {
                        aliaser.getAliasManager().create(command, alias);
                        SimpleSender.send("&aCreated new alias for &b" + command);
                    }
                }
                break;
        }
        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        Mouse.setGrabbed(false);
        super.initGui();
        aliasField = new GuiTextField(6, fontRendererObj, width / 2 - 70, height / 2 - 70, 140, 20);
        commandField = new GuiTextField(7, fontRendererObj, width / 2 - 70, height / 2 - 26, 140, 20);
        // Create alias
        createButton(width / 2 - 70, height / 2);

    }

    @Override
    protected void keyTyped(char par1, int par2) throws IOException {
        super.keyTyped(par1, par2);
        this.commandField.textboxKeyTyped(par1, par2);
        this.aliasField.textboxKeyTyped(par1, par2);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.commandField.updateCursorCounter();
        this.aliasField.updateCursorCounter();
    }

    @Override
    protected void mouseClicked(int x, int y, int btn) throws IOException {
        super.mouseClicked(x, y, btn);
        this.commandField.mouseClicked(x, y, btn);
        this.aliasField.mouseClicked(x, y, btn);
    }

    private void createButton(int x, int y) {
        this.buttonList.add(new GuiButton(2, x, y, 140, 20, ChatColor.format("&aCreate alias")));
    }


}
