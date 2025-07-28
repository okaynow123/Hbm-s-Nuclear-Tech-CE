package com.hbm.sound;

import com.hbm.packet.toclient.PlayerSoundPacket.SoundType;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SoundLoopPlayer extends MovingSound {

    private static final List<SoundLoopPlayer> playingSounds = new CopyOnWriteArrayList<>();

    private final EntityPlayer player;
    private final SoundType soundType;
    private int ticksUntilTimeout;

    public SoundLoopPlayer(SoundEvent sound, SoundType type, EntityPlayer player) {
        super(sound, SoundCategory.PLAYERS);
        this.player = player;
        this.soundType = type;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.xPosF = (float) player.posX;
        this.yPosF = (float) player.posY;
        this.zPosF = (float) player.posZ;
        this.resetTimeout();

        playingSounds.add(this);
    }

    @Override
    public void update() {
        if (this.player.isDead) {
            this.endSound();
            return;
        }
        this.ticksUntilTimeout--;
        if (this.ticksUntilTimeout <= 0) {
            this.endSound();
            return;
        }
        this.xPosF = (float) this.player.posX;
        this.yPosF = (float) this.player.posY;
        this.zPosF = (float) this.player.posZ;
    }

    public void resetTimeout() {
        this.ticksUntilTimeout = 30;
    }

    public void endSound() {
        this.donePlaying = true;
        this.repeat = false;
        playingSounds.remove(this);
    }

    private SoundType getSoundType() {
        return this.soundType;
    }

    public static SoundLoopPlayer getPlayingSound(SoundType type) {
        for (SoundLoopPlayer sound : playingSounds) {
            if (!sound.isDonePlaying() && sound.getSoundType() == type) {
                return sound;
            }
        }
        return null;
    }
}
