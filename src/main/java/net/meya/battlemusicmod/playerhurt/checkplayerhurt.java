package net.meya.battlemusicmod.playerhurt;

import net.meya.battlemusicmod.sound.battlesound;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;



public class checkplayerhurt {

    // Constructor to register the event handler
    public checkplayerhurt() {
        // Register this class to listen for LivingHurtEvent
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Event handler for LivingHurtEvent
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player) {
            SoundEvent sound = battlesound.PLAYER_HURT.get();
            if (sound != null) {
                // Play the sound event when a player is hurt
                Player player = (Player) event.getEntity();
                player.playSound(sound, 1.0F, 1.0F);
            }
        }
    }
}
