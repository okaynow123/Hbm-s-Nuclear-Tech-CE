package com.hbm.blocks.bomb;

import java.util.List;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.BlockAllocatorStandard;
import com.hbm.explosion.vanillant.standard.BlockProcessorStandard;
import com.hbm.explosion.vanillant.standard.EntityProcessorStandard;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import com.hbm.particle.helper.ExplosionCreator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockChargeC4 extends BlockChargeBase {

    public BlockChargeC4(String s) {
        super(s);
    }

    @Override
    public BombReturnCode explode(World world, BlockPos pos, Entity detonator) {

        if(!world.isRemote) {
            safe = true;
            world.setBlockToAir(pos);
            safe = false;
            int x = pos.getX(), y = pos.getY(), z = pos.getZ();
            ExplosionVNT xnt = new ExplosionVNT(world, x + 0.5, y + 0.5, z + 0.5, 15F, detonator);
            xnt.setBlockAllocator(new BlockAllocatorStandard(32));
            xnt.setBlockProcessor(new BlockProcessorStandard().setNoDrop());
            xnt.setEntityProcessor(new EntityProcessorStandard());
            xnt.setPlayerProcessor(new PlayerProcessorStandard());
            xnt.explode();
            ExplosionCreator.composeEffectSmall(world, x + 0.5, y + 1, z + 0.5);

            return BombReturnCode.DETONATED;
        }

        return BombReturnCode.UNDEFINED;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(TextFormatting.BLUE + "Does not drop blocks.");
    }
}
