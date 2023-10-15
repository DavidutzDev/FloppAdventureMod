package fr.davidutz.floppadventuremod.mixins.client;

import com.mojang.blaze3d.platform.Window;
import fr.davidutz.floppadventuremod.FloppAdventureMod;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class MixinWindow {

    @Shadow public abstract long getWindow();

    @Inject(at = @At(value = "HEAD"), method = "setTitle", cancellable = true)
    public void setTitle(CallbackInfo info) {
        info.cancel();

        GLFW.glfwSetWindowTitle(getWindow(), String.format("FloppAdventure %s • %s", FloppAdventureMod.CHAPTER_NAME, Minecraft.getInstance().getUser().getName()));
    }
}
