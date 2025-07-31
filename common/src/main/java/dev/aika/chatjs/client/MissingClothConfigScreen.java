package dev.aika.chatjs.client;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class MissingClothConfigScreen extends Screen {
    private final static String CLOTH_CONFIG_MODRINTH = "https://modrinth.com/mod/9s6osm5g";
    private final static String CLOTH_CONFIG_CURSEFORGE = "https://www.curseforge.com/minecraft/mc-mods/cloth-config";

    private final Screen parent;
    private final Component message = Component.translatable("gui.chatjs.missing_cloth_config.message");

    public MissingClothConfigScreen(Screen parent) {
        super(Component.translatable("gui.chatjs.missing_cloth_config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        int posY = this.height / 2 - 20;
        int posX = (this.width - 200) / 2;
        posY += this.font.wordWrapHeight(message, 300);
        this.addRenderableWidget(Button.builder(Component.translatable("gui.chatjs.missing_cloth_config.curseforge_download"),
                b -> Util.getPlatform().openUri(CLOTH_CONFIG_CURSEFORGE)).bounds(posX, posY, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.chatjs.missing_cloth_config.modrinth_download"),
                b -> Util.getPlatform().openUri(CLOTH_CONFIG_MODRINTH)).bounds(posX + 100, posY, 100, 20).build());
        posY += 25;
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK,
                (pressed) -> Minecraft.getInstance().setScreen(this.parent)).bounds(posX, posY, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(font, title, width / 2, this.height / 2 - 60, CommonColors.WHITE);
        MultiLineLabel.create(this.font, message, 300).renderCentered(graphics, this.width / 2, this.height / 2 - 30);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
