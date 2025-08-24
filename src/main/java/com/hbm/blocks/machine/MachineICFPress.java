package com.hbm.blocks.machine;

import com.hbm.blocks.ITooltipProvider;
import com.hbm.blocks.ModBlocks;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.machine.TileEntityICFPress;
import com.hbm.util.I18nUtil;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;

public class MachineICFPress extends Block implements ITileEntityProvider, ITooltipProvider {

    public MachineICFPress() {
        super(Material.IRON);
        this.setTranslationKey("machine_icf_press");
        this.setRegistryName("machine_icf_press");
        this.setCreativeTab(MainRegistry.machineTab);
        this.setHardness(5.0F);
        this.setResistance(10.0F);
        ModBlocks.ALL_BLOCKS.add(this);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else if (!playerIn.isSneaking()) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileEntityICFPress) {
                playerIn.openGui(MainRegistry.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityICFPress) {
            IItemHandler itemHandler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (itemHandler != null) {
                for (int i = 0; i < itemHandler.getSlots(); ++i) {
                    ItemStack itemstack = itemHandler.getStackInSlot(i);
                    if (!itemstack.isEmpty()) {
                        spawnAsEntity(worldIn, pos, itemstack);
                    }
                }
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityICFPress();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    // note for mlbv: you did splitting by "\n" but the splitting symbols in .lang files are "$". either leave resolveKeyArray or change .lang files
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        for(String s : I18nUtil.resolveKeyArray(this.getTranslationKey() + ".desc")) tooltip.add(TextFormatting.YELLOW + s);
    }
}
