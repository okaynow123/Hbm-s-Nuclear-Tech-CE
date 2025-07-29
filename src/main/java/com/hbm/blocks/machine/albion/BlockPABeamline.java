package com.hbm.blocks.machine.albion;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.machine.albion.TileEntityPABeamline;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class BlockPABeamline extends BlockDummyable implements ITooltipProvider {

    public BlockPABeamline(String name) {
        super(Material.IRON, name);
        this.setCreativeTab(MainRegistry.machineTab);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@NotNull World world, int meta) {
        if (meta >= 12) return new TileEntityPABeamline();
        return null;
    }

    @Override
    public int[] getDimensions() {
        return new int[]{0, 0, 0, 0, 1, 1};
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        addStandardInfo(tooltip);
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}