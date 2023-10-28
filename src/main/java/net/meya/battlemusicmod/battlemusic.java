package net.meya.battlemusicmod;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(battlemusic.MOD_ID)
public class battlemusic {
    public static final String MOD_ID = "battlemusic";
    public static final Logger LOGGER = LogUtils.getLogger();

    public class battlesound {
        public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
                DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, battlemusic.MOD_ID);

        public static final RegistryObject<SoundEvent> COMBAT_MUSIC = registerSoundEvents("battle_music");

        private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
            return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(battlemusic.MOD_ID, name)));
        }

        public static void register(IEventBus eventBus) {
            SOUND_EVENTS.register(eventBus);
            // Register your event handler method for the LivingHurtEvent
            eventBus.register(net.meya.battlemusicmod.battlemusic.class);
        }

        // Event handler for LivingHurtEvent
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            Entity target = event.getEntity();
            Entity source = event.getSource().getEntity();

            if (target instanceof Player && source instanceof Mob) {
                SoundEvent sound = net.meya.battlemusicmod.battlemusic.battlesound.COMBAT_MUSIC.get(); // Fixed this line
                if (sound != null) {
                    // Play the sound event when a player is hurt by a hostile mob
                    ServerPlayer player = (ServerPlayer) target;
                    player.playSound(sound, 1.0F, 1.0F);
                }
            }
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
