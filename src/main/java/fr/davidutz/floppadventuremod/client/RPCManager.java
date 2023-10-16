package fr.davidutz.floppadventuremod.client;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import fr.davidutz.floppadventuremod.FloppAdventureMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RPCManager {

    private final DiscordRPC rpcLib = DiscordRPC.INSTANCE;
    private final DiscordEventHandlers handlers = new DiscordEventHandlers();
    private final DiscordRichPresence RPC = new DiscordRichPresence();
    private boolean isStarted;

    private void makeChanges(RPCCallback callback) {
        if (this.isStarted) callback.updateRPC(this.RPC);
    }

    public void startRPC(Minecraft minecraft) {
        if (this.isStarted) return;
        FloppAdventureMod.LOGGER.info("[FloppAdventure] Starting RPC");

        final String applicationId = "1163474701327290448";
        this.handlers.ready = (user) -> FloppAdventureMod.LOGGER.info(String.format("[FloppAdventure] Discord RPC loaded for %s", user.username));
        this.rpcLib.Discord_Initialize(applicationId, this.handlers, true, "");

        this.RPC.startTimestamp = System.currentTimeMillis() / 1000;
        this.RPC.largeImageText = "FloppAdventure";
        this.RPC.largeImageKey = "icon";
        this.RPC.details = "Chargement du portail en cours...";
        this.RPC.smallImageText = minecraft.getUser().getName();
        this.rpcLib.Discord_UpdatePresence(this.RPC);
        this.isStarted = true;

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && minecraft.isRunning() && this.isStarted) {
                try {
                    if (minecraft.screen instanceof TitleScreen)
                        this.makeChanges(rpc -> rpc.details = "En attente de téléportation...");
                    else if (minecraft.getConnection() != null)
                        this.makeChanges(rpc -> rpc.details = "Explore le royaume de Flopenia...");
                    this.rpcLib.Discord_UpdatePresence(RPC);
                    this.rpcLib.Discord_RunCallbacks();
                    Thread.sleep(2000);
                } catch (InterruptedException e) { }
            }
        }, "DiscordRPC-Manager").start();
    }

    @FunctionalInterface
    public interface RPCCallback {
        void updateRPC(DiscordRichPresence rpc);
    }
}
