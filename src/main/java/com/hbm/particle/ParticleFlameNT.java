package com.hbm.particle;

import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.world.World;
// Th3_Sl1ze: don't ask me why mojank just made it FUCKING PROTECTED INSTEAD OF PUBLIC
public class ParticleFlameNT extends ParticleFlame {

    public ParticleFlameNT(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
    }
}
