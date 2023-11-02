package net.meya.battlemusicmod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.client.resources.sounds.SoundInstance;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ChatType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;


// The value here should match an entry in the META-INF/mods.toml file
@Mod("battlemusic")
public class battlemusic {
    public static final String MOD_ID = "battlemusic";
    public static final Logger LOGGER = LogUtils.getLogger();
    private SoundInstance lastSound;

    public int decaySeconds = 0;

    public battlemusic() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Add your common setup code here
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            // Add your creative tab contents here
        }
    }

    @SubscribeEvent
    public void onAttack(final LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof LocalPlayer player) {
            Minecraft mc = Minecraft.getInstance();
            SoundManager manager = mc.getSoundManager();
            if (mc.level != null && mc.level.getDifficulty() != Difficulty.PEACEFUL) {
                int hostileMobs = getEntities(player);

                LOGGER.info("Hostile Mobs Nearby: " + hostileMobs);

                if (hostileMobs > 0) {
                    if (!manager.isActive(lastSound)) {
                        LOGGER.info("Attempting to play custom sound");
                        LOGGER.info("lastSound: " + lastSound); // Debugging statement
                        playCustomSound(); // Call the custom method to play the sound
                        decaySeconds = 0;
                    }
                } else {
                    LOGGER.info("No hostile mobs nearby");
                    LOGGER.info("lastSound: " + lastSound); // Debugging statement
                    // If there are no hostile mobs within 15 blocks, stop the sound
                    if (manager.isActive(lastSound)) {
                        LOGGER.info("Stopping the custom sound");
                        manager.stop(lastSound);
                        lastSound = null;
                    }
                }
            }
        }
    }

    private int getEntities(LocalPlayer player) {
        return player.clientLevel.getEntitiesOfClass(Monster.class, new AABB(-12D, -10D, -12D, 12D, 10D, 12D).move(player.getX(), player.getY(), player.getZ()), mob -> mob.canAttack(player)).size();
    }
    private void playCustomSound() {
        Minecraft mc = Minecraft.getInstance();
        SoundManager manager = mc.getSoundManager();

        SoundEvent soundEvent = ModSounds.UNIV_BRAWL.get();

        if (soundEvent == null) {
            LOGGER.error("SoundEvent is null. Make sure it's properly registered.");
            return;
        }

        ResourceLocation soundLocation = soundEvent.getLocation();
        LOGGER.info("Attempting to play sound: " + soundLocation.toString());

        SimpleSoundInstance soundInstance = SimpleSoundInstance.forMusic(soundEvent);
        manager.play(soundInstance);

        lastSound = soundInstance;
    }
}