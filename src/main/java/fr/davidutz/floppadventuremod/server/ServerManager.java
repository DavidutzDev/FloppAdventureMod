package fr.davidutz.floppadventuremod.server;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerManager {

    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent event) {

    }
}
