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
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(battlemusic.MOD_ID)
public class battlemusic {
    public static final String MOD_ID = "battlemusic";
    public static final Logger LOGGER = LogUtils.getLogger();

    public int decaySeconds = 0;
    public SimpleSoundInstance lastSound;

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
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.phase == TickEvent.Phase.END && mc.level != null && mc.level.getGameTime() % 20 == 0) {
            SoundManager manager = mc.getSoundManager();
            if (manager.isActive(lastSound)) {
                LocalPlayer player = mc.player;
                if (player != null && getEntities(player) == 0) {
                    decaySeconds++;
                }
                if (decaySeconds > 20) {
                    manager.stop(lastSound);
                }
            }
        }
    }

    @SubscribeEvent
    public void onAttack(final LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof LocalPlayer player) {
            Minecraft mc = Minecraft.getInstance();
            SoundManager manager = mc.getSoundManager();
            if (mc.level != null && mc.level.getDifficulty() != Difficulty.PEACEFUL) {
                if (getEntities(player) > 0) {
                    if (!manager.isActive(lastSound)) {
                        mc.getMusicManager().stopPlaying();
                        lastSound = pickSound(entity.getRandom());
                        manager.play(lastSound);
                    }
                    decaySeconds = 0;
                }
            }
        }
    }

    private int getEntities(LocalPlayer player) {
        return player.clientLevel.getEntitiesOfClass(Monster.class, new AABB(-12D, -10D, -12D, 12D, 10D, 12D).move(player.getX(), player.getY(), player.getZ()), mob -> mob.canAttack(player)).size();
    }

    private SimpleSoundInstance pickSound(RandomSource rand) {
        // Use the registered sound events from ModSounds
        SoundEvent[] sounds = {
                ModSounds.BATTLE_MUSIC_UNIV.get(),
                ModSounds.BATTLE_MUSIC_UNIV.get()
        };

        final int idx = rand.nextInt(sounds.length);
        SoundEvent sound = sounds[idx];

        if (sound == null) {
            throw new NullPointerException("Invalid sound event: The selected sound event is null.");
        }

        return SimpleSoundInstance.forMusic(sound);
    }
}
