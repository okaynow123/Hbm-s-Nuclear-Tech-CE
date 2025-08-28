package com.hbm.world.feature;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class OreLayer3D {

    public static int counter = 0;
    public int id;

    private NoiseGeneratorPerlin noiseX;
    private NoiseGeneratorPerlin noiseY;
    private NoiseGeneratorPerlin noiseZ;

    private double scaleH;
    private double scaleV;
    private double threshold;

    private Block block;
    private int meta;
    private int dim = 0;

    public OreLayer3D(Block block, int meta) {
        this.block = block;
        this.meta = meta;
        MinecraftForge.EVENT_BUS.register(this);
        this.id = counter;
        counter++;
    }

    public OreLayer3D setDimension(int dim) {
        this.dim = dim;
        return this;
    }

    public OreLayer3D setScaleH(double scale) {
        this.scaleH = scale;
        return this;
    }

    public OreLayer3D setScaleV(double scale) {
        this.scaleV = scale;
        return this;
    }

    public OreLayer3D setThreshold(double threshold) {
        this.threshold = threshold;
        return this;
    }

    @SubscribeEvent
    public void onDecorate(DecorateBiomeEvent.Pre event) {

        World world = event.getWorld();

        if (world.provider == null || world.provider.getDimension() != this.dim) return;

        if (this.noiseX == null) this.noiseX = new NoiseGeneratorPerlin(new Random(world.getSeed() + 101 + id), 4);
        if (this.noiseY == null) this.noiseY = new NoiseGeneratorPerlin(new Random(world.getSeed() + 102 + id), 4);
        if (this.noiseZ == null) this.noiseZ = new NoiseGeneratorPerlin(new Random(world.getSeed() + 103 + id), 4);

        int cX = event.getChunkPos().x;
        int cZ = event.getChunkPos().x;

        for (int x = cX + 8; x < cX + 24; x++) {
            for (int z = cZ + 8; z < cZ + 24; z++) {
                for (int y = 64; y > 5; y--) {
                    double nX = this.noiseX.getValue(y * scaleV, z * scaleH);
                    double nY = this.noiseY.getValue(x * scaleH, z * scaleH);
                    double nZ = this.noiseZ.getValue(x * scaleH, y * scaleV);

                    if (nX * nY * nZ > threshold) {
                        BlockPos pos = new BlockPos(x, y, z);
                        IBlockState state = world.getBlockState(pos);
                        Block target = state.getBlock();

                        if (target.isNormalCube(state, world, pos)
                                && state.getMaterial() == Material.ROCK
                                && target.isReplaceableOreGen(state, world, pos, BlockMatcher.forBlock(Blocks.STONE))) {
                            world.setBlockState(pos, block.getStateFromMeta(meta), 2);
                        }
                    }
                }
            }
        }
    }
}
