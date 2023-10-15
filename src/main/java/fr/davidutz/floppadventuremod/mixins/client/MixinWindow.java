package fr.davidutz.floppadventuremod.mixins.client;

import com.mojang.blaze3d.platform.Window;
import fr.davidutz.floppadventuremod.FloppAdventureMod;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

@Mixin(Window.class)
public abstract class MixinWindow {

    @Shadow public abstract long getWindow();

    @Inject(at = @At(value = "HEAD"), method = "setTitle", cancellable = true)
    public void setTitle(CallbackInfo info) {
        info.cancel();

        GLFW.glfwSetWindowTitle(getWindow(), String.format("FloppAdventure %s • %s", FloppAdventureMod.CHAPTER_NAME, Minecraft.getInstance().getUser().getName()));
    }

    @Inject(at = @At(value = "HEAD"), method = "setIcon", cancellable = true)
    public void setIcon(CallbackInfo info) {
        info.cancel();

        try (InputStream iconStream = FloppAdventureMod.class.getResourceAsStream("/icon.png")) {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(iconStream));
            int width = image.getWidth();
            int height = image.getHeight();

            int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

            ByteBuffer iconBuffer = ByteBuffer.allocateDirect(width * height * 4);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = pixels[y * width + x];
                    iconBuffer.putInt((pixel << 8) | ((pixel >> 24) & 0xFF));
                }
            }
            iconBuffer.flip();

            GLFWImage.Buffer icons = GLFWImage.malloc(1);
            icons.position(0).width(width).height(height).pixels(iconBuffer);
            GLFW.glfwSetWindowIcon(getWindow(), icons);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
