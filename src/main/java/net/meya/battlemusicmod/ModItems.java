package net.meya.battlemusicmod;

import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, "battlemusic");

    public static final RegistryObject<Item> DESERT_BRAWL_MUSIC_DISC = ITEMS.register("desert_brawl_music_disc",
            () -> new RecordItem(6, ModSounds.DESERT_BRAWL, new Item.Properties().stacksTo(1), 2440));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
//4840