package fr.davidutz.floppadventuremod;

import fr.davidutz.floppadventuremod.client.ClientManager;
import fr.davidutz.floppadventuremod.server.ServerManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FloppAdventureMod.MOD_ID)
public class FloppAdventureMod {

    public static final String MOD_ID = "floppadventuremod";
    public static final String CHAPTER_NAME = "Chapter 2 - L'éveil des Sculk";
    public static final Logger LOGGER = LogManager.getLogger();

    @OnlyIn(Dist.CLIENT)
    private static ClientManager clientManager;

    @OnlyIn(Dist.DEDICATED_SERVER)
    private static ServerManager serverManager;

    public FloppAdventureMod() {
        this.loadPreFML();
        this.loadFML();
        this.loadManagers();
    }

    private void loadPreFML() {
        LOGGER.info("[FloppAdventure] Loading Mod...");
    }

    private void loadFML() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);
    }

    private void loadManagers() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            clientManager = new ClientManager();
            clientManager.init();
        });
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            serverManager = new ServerManager();
            serverManager.init();
        });
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("[FloppAdventure] Mod is now loaded!");
    }

    public static ClientManager getClientManager() {
        return clientManager;
    }

    public static ServerManager getServerManager() {
        return serverManager;
    }
}
