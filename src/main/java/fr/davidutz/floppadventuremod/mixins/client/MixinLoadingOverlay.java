package fr.davidutz.floppadventuremod.mixins.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import fr.davidutz.floppadventuremod.FloppAdventureMod;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.Mth;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(LoadingOverlay.class)
public abstract class MixinLoadingOverlay extends Overlay {

    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private boolean fadeIn;
    @Shadow @Final private ReloadInstance reload;
    @Shadow private long fadeInStart;
    @Shadow private long fadeOutStart;
    @Shadow @Final private Consumer<Optional<Throwable>> onFinish;

    @Shadow protected abstract void drawProgressBar(PoseStack pPoseStack, int pMinX, int pMinY, int pMaxX, int pMaxY, float pPartialTick);

    @Shadow private float currentProgress;
    private int scaledWidth;
    private int scaledHeight;

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
        this.scaledWidth = this.minecraft.getWindow().getGuiScaledWidth();
        this.scaledHeight = this.minecraft.getWindow().getGuiScaledHeight();
        long millis = Util.getMillis();
        if (this.fadeIn && this.fadeInStart == -1L) {
            this.fadeInStart = millis;
        }

        float f = this.fadeOutStart > -1L ? (float)(millis - this.fadeOutStart) / 1000.0F : -1.0F;
        float f1 = this.fadeInStart > -1L ? (float)(millis - this.fadeInStart) / 500.0F : -1.0F;

        float f3 = this.reload.getActualProgress();
        this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + f3 * 0.050000012F, 0.0F, 1.0F);

        /* Custom Render */
        RenderSystem.setShaderTexture(0, new ResourceLocation(FloppAdventureMod.MOD_ID, "textures/banner2.png"));
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(0, 0, 0).uv(0, 0).endVertex();
        builder.vertex(0, scaledHeight, 0).uv(0, 1).endVertex();
        builder.vertex(scaledWidth, scaledHeight, 0).uv(1, 1).endVertex();
        builder.vertex(scaledWidth, 0, 0).uv(1, 0).endVertex();

        BufferUploader.drawWithShader(builder.end());

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        this.floppAdventureMod$drawProgressionBar(this.currentProgress);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();

        /* Finish Minecraft Initialisation */
        if (f >= 2.0F) {
            this.minecraft.setOverlay((Overlay)null);
        }

        if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || f1 >= 2.0F)) {
            this.fadeOutStart = Util.getMillis(); // Moved up to guard against inf loops caused by callback
            try {
                this.reload.checkExceptions();
                this.onFinish.accept(Optional.empty());
            } catch (Throwable throwable) {
                this.onFinish.accept(Optional.of(throwable));
            }

            if (this.minecraft.screen != null) {
                this.minecraft.screen.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
            }
        }
        ci.cancel();
    }

    @Unique
    private void floppAdventureMod$drawProgressionBar(float progress) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(0, scaledHeight - 5.5, 0).color(33, 86, 94, 255).endVertex();
        builder.vertex(0, scaledHeight, 0).color(33, 86, 94, 255).endVertex();
        builder.vertex(scaledWidth * progress, scaledHeight, 0).color(12, 25, 26, 255).endVertex();
        builder.vertex(scaledWidth * progress, scaledHeight - 5.5, 0).color(12, 25, 26, 255).endVertex();

        BufferUploader.drawWithShader(builder.end());
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

    }
}
