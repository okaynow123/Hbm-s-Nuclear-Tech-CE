package com.hbm.blocks.bomb;

import java.util.List;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.BlockAllocatorStandard;
import com.hbm.explosion.vanillant.standard.BlockProcessorStandard;
import com.hbm.particle.helper.ExplosionCreator;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockChargeSemtex extends BlockChargeBase {

    public BlockChargeSemtex(String registryName) {
        super(registryName);
    }

    @Override
    public BombReturnCode explode(World world, BlockPos pos, Entity detonator) {

        if(!world.isRemote) {
            safe = true;
            world.setBlockToAir(pos);
            safe = false;
            int x = pos.getX(), y = pos.getY(), z = pos.getZ();
            ExplosionVNT xnt = new ExplosionVNT(world, x + 0.5, y + 0.5, z + 0.5, 10F);
            xnt.setBlockAllocator(new BlockAllocatorStandard(32));
            xnt.setBlockProcessor(new BlockProcessorStandard()
                    .setAllDrop()
                    .setFortune(3));
            xnt.explode();
            ExplosionCreator.composeEffectSmall(world, x + 0.5, y + 1, z + 0.5);

            return BombReturnCode.DETONATED;
        }

        return BombReturnCode.UNDEFINED;
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.BLUE + "Will drop all blocks.");
        tooltip.add(TextFormatting.BLUE + "Does not do damage.");
        tooltip.add(TextFormatting.BLUE + "");
        tooltip.add(TextFormatting.LIGHT_PURPLE + "Fortune III");
    }
}
