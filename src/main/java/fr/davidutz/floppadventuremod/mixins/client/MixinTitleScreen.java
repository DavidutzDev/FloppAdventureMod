package fr.davidutz.floppadventuremod.mixins.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import fr.davidutz.floppadventuremod.FloppAdventureMod;
import fr.davidutz.floppadventuremod.client.ui.TextButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {

    private Minecraft minecraft;
    private int scaledWidth;
    private int scaledHeight;

    protected MixinTitleScreen(Component pTitle) {
        super(pTitle);
    }

    @Inject(at = @At("HEAD"), method = "init()V", cancellable = true)
    public void init(CallbackInfo info) {
        info.cancel();

        this.minecraft = Minecraft.getInstance();

        this.scaledWidth = this.minecraft.getWindow().getGuiScaledWidth();
        this.scaledHeight = this.minecraft.getWindow().getGuiScaledHeight();

        this.addRenderableWidget(new TextButton((this.scaledWidth / 2) - 50, (this.scaledHeight / 2) + 30, Component.literal("Entrer dans le portail"), Color.WHITE, (p) -> {
            this.minecraft.setScreen(new JoinMultiplayerScreen(null));
        }));

        this.addRenderableWidget(new TextButton((this.scaledWidth / 2) - 45, (this.scaledHeight / 2) + 75, Component.literal("Creer un monde"), Color.WHITE, (p) -> {
            this.minecraft.setScreen(new SelectWorldScreen(null));
        }));
    }

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo info) {
        info.cancel();

        RenderSystem.setShaderTexture(0, new ResourceLocation(FloppAdventureMod.MOD_ID, "textures/portal.png"));
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(0, 0, 0).uv(0, 0).endVertex();
        builder.vertex(0, scaledHeight, 0).uv(0, 1).endVertex();
        builder.vertex(scaledWidth, scaledHeight, 0).uv(1, 1).endVertex();
        builder.vertex(scaledWidth, 0, 0).uv(1, 0).endVertex();

        BufferUploader.drawWithShader(builder.end());

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();

        drawString(pPoseStack, minecraft.font, "Devloped by Davidutz, Illustration by Retrix_Off", 0, this.scaledHeight - 10, Color.WHITE.getRGB());

        this.renderables.forEach(r -> r.render(pPoseStack, pMouseX, pMouseY, pPartialTick));
    }
}
