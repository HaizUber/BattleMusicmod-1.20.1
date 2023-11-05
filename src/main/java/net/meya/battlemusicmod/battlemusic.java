package net.meya.battlemusicmod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;
import net.minecraftforge.event.TickEvent;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;

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
    //inspiration from erussianguy's combat music mod
    private Timer delayTimer = new Timer();
    private TimerTask stopSoundTask; // Add a member variable to keep track of the scheduled task

    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (event.phase == TickEvent.Phase.END && mc.level != null && mc.level.getDifficulty() != Difficulty.PEACEFUL) {
            SoundManager manager = mc.getSoundManager();
            int hostileMobs = getEntities(mc.player);

            if (hostileMobs > 3) {
                if (!manager.isActive(lastSound)) {
                    // Pass the Player object to playCustomSound
                    playCustomSound(mc.player);
                    decaySeconds = 0;

                    // Cancel the scheduled task if there's any
                    if (stopSoundTask != null) {
                        stopSoundTask.cancel();
                        stopSoundTask = null;
                    }
                }
            } else {
                // If there are no hostile mobs nearby, schedule sound stop after 5 seconds
                if (manager.isActive(lastSound) && stopSoundTask == null) {
                    stopSoundTask = new TimerTask() {
                        @Override
                        public void run() {
                            manager.stop(lastSound);
                            lastSound = null;
                            stopSoundTask = null;
                        }
                    };
                    delayTimer.schedule(stopSoundTask, 5000); // 5000 milliseconds = 5 seconds
                }
            }

            // Get the player object from Minecraft
            Player player = mc.player;

            if (player != null) {
                logCurrentBiomeClassName(player);
            }
        }
    }

    private int getEntities(LocalPlayer player) {
        return player.clientLevel.getEntitiesOfClass(Monster.class, new AABB(-12D, -10D, -12D, 12D, 10D, 12D).move(player.getX(), player.getY(), player.getZ()), mob -> mob.canAttack(player)).size();
    }

    private void playCustomSound(Player player) {
        Minecraft mc = Minecraft.getInstance();
        SoundManager manager = mc.getSoundManager();
        SoundEvent soundEvent;
        String playerBiomeClassName = getCurrentBiomeClassName(player);


        // Determine the current biome and select the appropriate sound event
        if ("minecraft:desert".equals(playerBiomeClassName) ||
                "minecraft:badlands".equals(playerBiomeClassName) ||
                "minecraft:wooded_badlands".equals(playerBiomeClassName) ||
                "minecraft:eroded_badlands".equals(playerBiomeClassName) ||
                "minecraft:savanna".equals(playerBiomeClassName) ||
                "minecraft:savanna_plateau".equals(playerBiomeClassName) ||
                "minecraft:windswept_savanna".equals(playerBiomeClassName)) {
            soundEvent = ModSounds.DESERT_BRAWL.get();
        } else if ("minecraft:plains".equals(playerBiomeClassName) ||
                "minecraft:sunflower_plains".equals(playerBiomeClassName)) {
            soundEvent = ModSounds.PLAINS_BRAWL.get();
        } else {
            LOGGER.error("No sound event defined for the current biome: " + playerBiomeClassName);
            return; // Handle cases where no sound event is defined
        }

        if (soundEvent == null) {
            LOGGER.error("SoundEvent is null. Make sure it's properly registered.");
            // Handle the case where the sound event is null (e.g., play a default sound or log an error)
            return;
        }

        ResourceLocation soundLocation = soundEvent.getLocation();

        SimpleSoundInstance soundInstance = SimpleSoundInstance.forAmbientAddition(soundEvent);
        manager.play(soundInstance);

        lastSound = soundInstance;
    }

    //inspiration from yungnickyoung's travelertitles
    private String getCurrentBiomeClassName(Player player) {
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();
        BiomeManager biomeManager = level.getBiomeManager();
        Holder<Biome> currentBiomeHolder = biomeManager.getBiome(playerPos);

        if (currentBiomeHolder != null) {
            Biome currentBiome = currentBiomeHolder.get();
            if (currentBiome != null) {
                ResourceLocation biomeBaseKey = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(currentBiome);
                return biomeBaseKey.toString(); // Return the ResourceLocation as a string
            } else {
                return "Unknown Biome";
            }
        } else {
            return "Unknown Biome";
        }
    }
//left for debugging purpose
    public void logCurrentBiomeClassName(Player player) {
        String playerBiomeClassName = getCurrentBiomeClassName(player);
    }


}
