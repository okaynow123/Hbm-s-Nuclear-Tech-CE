package com.hbm.blocks.machine;

import com.hbm.tileentity.machine.TileEntityWatzStruct;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;

import static com.hbm.blocks.ModBlocks.ALL_BLOCKS;

public class BlockWatzStruct extends BlockContainer {

    public BlockWatzStruct(Material mat, String s) {
        super(mat);
        this.setTranslationKey(s);
        this.setRegistryName(s);
        ALL_BLOCKS.add(this);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityWatzStruct();
    }


    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
