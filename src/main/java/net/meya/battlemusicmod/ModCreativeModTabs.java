package net.meya.battlemusicmod;

import net.meya.battlemusicmod.battlemusic;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, battlemusic.MOD_ID);

    public static final RegistryObject<CreativeModeTab> BATTLEMUSIC_TAB = CREATIVE_MODE_TABS.register("battlemusic_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.DESERT_BRAWL_MUSIC_DISC.get()))
                    .title(Component.translatable("itemGroup.battlemusic_tab"))
                    .displayItems((pParameters, pOutput) -> {


                        pOutput.accept(ModItems.DESERT_BRAWL_MUSIC_DISC.get());

                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
