package fr.davidutz.floppadventuremod.client.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class TextButton extends AbstractButton {

    private final IPressable iPressable;
    private final Color color;

    public TextButton(int pX, int pY, Component pMessage, Color color, IPressable iPressable) {
        super(pX, pY, Minecraft.getInstance().font.width(pMessage), Minecraft.getInstance().font.lineHeight, pMessage);
        this.iPressable = iPressable;
        this.color = color;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        drawString(pPoseStack, Minecraft.getInstance().font, this.getMessage(), this.x, this.y, this.color.getRGB());
    }

    @Override
    public void onPress() {
        this.iPressable.onPress(this);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    public interface IPressable {
        void onPress(TextButton button);
    }
}
