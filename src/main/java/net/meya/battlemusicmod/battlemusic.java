package net.meya.battlemusicmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.eventbus.api.SubscribeEvent;


import static net.meya.battlemusicmod.battlemusic.MOD_ID;

@Mod(MOD_ID)
public class battlemusic {
    public static final String MOD_ID = "battlemusic";

    public int decaySeconds = 0;
    public SimpleSoundInstance lastSound;

    public battlemusic() {
        MinecraftForge.EVENT_BUS.register(this);
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

    private static int getEntities(LocalPlayer player) {
        return player.clientLevel.getEntitiesOfClass(Monster.class, new AABB(-12D, -10D, -12D, 12D, 10D, 12D).move(player.getX(), player.getY(), player.getZ()), mob -> mob.canAttack(player)).size();
    }

    private SimpleSoundInstance pickSound(RandomSource rand) {
        // Replace with your list of sound event names
        final String[] sounds = {
                "minecraft:music_disc.pigstep",
                "minecraft:music_disc.mellohi"
        };

        final int idx = rand.nextInt(sounds.length);
        final SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(sounds[idx]));
        if (sound == null) {
            throw new NullPointerException("Invalid sound event resource location detected: " + sounds[idx]);
        }
        return SimpleSoundInstance.forMusic(sound);
    }
}
