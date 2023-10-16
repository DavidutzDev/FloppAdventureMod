package fr.davidutz.floppadventuremod.client;

import fr.davidutz.floppadventuremod.FloppAdventureMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientManager {

    private final RPCManager rpcManager = new RPCManager();

    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent e) {
        this.rpcManager.startRPC(Minecraft.getInstance());
    }
}
