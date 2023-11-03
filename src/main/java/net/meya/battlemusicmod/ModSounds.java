package net.meya.battlemusicmod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "battlemusic");

    public static final RegistryObject<SoundEvent> DESERT_BRAWL = SOUNDS.register(
            "desert_brawl", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(battlemusic.MOD_ID, "desert_brawl")));
    public static final RegistryObject<SoundEvent> PLAINS_BRAWL = SOUNDS.register(
            "plains_brawl", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(battlemusic.MOD_ID, "plains_brawl")));




    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }

}

