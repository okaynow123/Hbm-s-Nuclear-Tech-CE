package com.hbm.blocks.bomb;

import java.util.Arrays;
import java.util.List;

import com.hbm.explosion.ExplosionNT;
import com.hbm.explosion.ExplosionNT.ExAttrib;
import com.hbm.particle.helper.ExplosionSmallCreator;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockChargeMiner extends BlockChargeBase {

    public BlockChargeMiner(String registryName) {
        super(registryName);
    }

    @Override
    public BombReturnCode explode(World world, BlockPos pos, Entity detonator) {

        if(!world.isRemote) {
            safe = true;
            world.setBlockToAir(pos);
            safe = false;
            int x = pos.getX(), y = pos.getY(), z = pos.getZ();
            ExplosionNT exp = new ExplosionNT(world, detonator, x + 0.5, y + 0.5, z + 0.5, 4F);
            exp.addAllAttrib(Arrays.asList(ExAttrib.NOHURT, ExAttrib.ALLDROP));
            exp.explode();
            ExplosionSmallCreator.composeEffect(world, x + 0.5, y + 0.5, z + 0.5, 15, 3F, 1.25F);

            return BombReturnCode.DETONATED;
        }

        return BombReturnCode.UNDEFINED;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.BLUE + "Will drop all blocks.");
        tooltip.add(TextFormatting.BLUE + "Does not do damage.");
    }
}
