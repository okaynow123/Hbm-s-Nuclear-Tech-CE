package com.hbm.world.dungeon;

import com.hbm.blocks.ModBlocks;
import com.hbm.render.amlfrom1710.Vec3;
import com.hbm.world.phased.AbstractPhasedStructure;
import com.hbm.world.phased.PhasedStructureGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;

public class AncientTombStructure extends AbstractPhasedStructure {
    public static final AncientTombStructure INSTANCE = new AncientTombStructure();
    private AncientTombStructure() {}

    @Override
    protected boolean isCacheable() {
        return false;
    }

    @Override
    protected void buildStructure(@NotNull LegacyBuilder builder, @NotNull Random rand) {
        new AncientTomb().build(builder, rand, 0, 0, 0);
    }

    @Override
    @NotNull
    public Optional<PhasedStructureGenerator.ReadyToGenerateStructure> validate(@NotNull World world, @NotNull PhasedStructureGenerator.PendingValidationStructure pending) {
        BlockPos origin = pending.origin;
        int yOff = Math.max(world.getHeight(origin.getX(), origin.getZ()), 35) - 5;
        BlockPos finalOrigin = new BlockPos(origin.getX(), yOff, origin.getZ());
        return Optional.of(new PhasedStructureGenerator.ReadyToGenerateStructure(pending, finalOrigin));
    }

    @Override
    public void postGenerate(@NotNull World world, @NotNull Random rand, @NotNull BlockPos finalOrigin) {
        int x = finalOrigin.getX();
        int z = finalOrigin.getZ();
        int spikeCount = 36 + rand.nextInt(15);
        Vec3 vec = Vec3.createVectorHelper(20, 0, 0);
        float rot = (float)Math.toRadians(360F / spikeCount);
        for(int i = 0; i < spikeCount; i++) {
            vec.rotateAroundY(rot);
            double variance = 1D + rand.nextDouble() * 0.4D;
            int ix = (int) (x + vec.xCoord * variance);
            int iz = (int) (z + vec.zCoord * variance);
            int iy = world.getHeight(ix, iz) - 3;
            for(int j = iy; j < iy + 7; j++) {
                world.setBlockState(new BlockPos(ix, j, iz), ModBlocks.deco_steel.getDefaultState());
            }
        }
    }
}
