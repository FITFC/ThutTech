package thut.tech.compat.cct;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

@Mod.EventBusSubscriber
public class CCTweaks
{

    @SubscribeEvent
    public static void serverAboutToStart(final FMLServerAboutToStartEvent event)
    {
        if (ModList.get().isLoaded("computercraft")) Peripherals.register();
    }

}
