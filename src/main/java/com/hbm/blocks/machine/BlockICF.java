package com.hbm.blocks.machine;

import com.hbm.api.energymk2.IEnergyReceiverMK2;
import com.hbm.blocks.BlockBase;
import com.hbm.interfaces.AutoRegister;
import com.hbm.lib.ForgeDirection;
import com.hbm.tileentity.machine.TileEntityICFController;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlockICF extends BlockBase implements ITileEntityProvider {
    public static final PropertyBool IO_ENABLED = PropertyBool.create("io");

    public BlockICF() {
        super(Material.IRON, "icf_block");
        this.setDefaultState(this.blockState.getBaseState().withProperty(IO_ENABLED, false));
    }

    @NotNull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IO_ENABLED);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(IO_ENABLED) ? 1 : 0;
    }

    @NotNull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(IO_ENABLED, meta == 1);
    }

    @NotNull
    @Override
    public EnumBlockRenderType getRenderType(@NotNull IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @NotNull
    @Override
    public Item getItemDropped(@NotNull IBlockState state, @NotNull Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public boolean hasTileEntity(@NotNull IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@NotNull World worldIn, int meta) {
        return new TileEntityBlockICF();
    }

    @Override
    public void breakBlock(World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);

        if (tile instanceof TileEntityBlockICF icf) {
            if (icf.originalBlockState != null) {
                worldIn.setBlockState(pos, icf.originalBlockState, 3);
            }
            if (icf.corePos != null) {
                TileEntity controller = worldIn.getTileEntity(icf.corePos);
                if (controller instanceof TileEntityICFController) {
                    ((TileEntityICFController) controller).assembled = false;
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @AutoRegister
    public static class TileEntityBlockICF extends TileEntity implements ITickable, IEnergyReceiverMK2 {

        public IBlockState originalBlockState;
        public BlockPos corePos;
        private TileEntityICFController cachedCore;

        @Override
        public void update() {
            if (!world.isRemote && corePos != null && world.getTotalWorldTime() % 20 == 0) {
                TileEntityICFController controller = getCore();
                if (controller == null || !controller.assembled) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                }
            }
        }

        @Nullable
        private TileEntityICFController getCore() {
            if (corePos == null) return null;
            if (cachedCore != null && !cachedCore.isInvalid() && cachedCore.getPos().equals(corePos)) {
                return cachedCore;
            }
            if (world.isBlockLoaded(corePos)) {
                TileEntity tile = world.getTileEntity(corePos);
                if (tile instanceof TileEntityICFController) {
                    cachedCore = (TileEntityICFController) tile;
                    return cachedCore;
                }
            }
            cachedCore = null;
            return null;
        }

        public void setCore(BlockPos core) {
            this.corePos = core;
            this.markDirty();
        }

        @Override
        public void readFromNBT(@NotNull NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            if (nbt.hasKey("originalBlockState", 10)) {
                originalBlockState = NBTUtil.readBlockState(nbt.getCompoundTag("originalBlockState"));
            }
            if (nbt.hasKey("corePos", 10)) {
                corePos = NBTUtil.getPosFromTag(nbt.getCompoundTag("corePos"));
            }
        }

        @NotNull
        @Override
        public NBTTagCompound writeToNBT(@NotNull NBTTagCompound nbt) {
            super.writeToNBT(nbt);
            if (originalBlockState != null) {
                nbt.setTag("originalBlockState", NBTUtil.writeBlockState(new NBTTagCompound(), originalBlockState));
            }
            if (corePos != null) {
                nbt.setTag("corePos", NBTUtil.createPosTag(corePos));
            }
            return nbt;
        }

        @Override
        public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
            if (world.getBlockState(pos).getValue(IO_ENABLED) && capability == CapabilityEnergy.ENERGY) {
                TileEntityICFController core = getCore();
                return core != null && core.hasCapability(capability, facing);
            }
            return super.hasCapability(capability, facing);
        }

        @Nullable
        @Override
        public <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
            if (world.getBlockState(pos).getValue(IO_ENABLED) && capability == CapabilityEnergy.ENERGY) {
                TileEntityICFController core = getCore();
                if (core != null) {
                    return core.getCapability(capability, facing);
                }
            }
            return super.getCapability(capability, facing);
        }

        @Override
        public long getPower() {
            if(!world.getBlockState(pos).getValue(IO_ENABLED)) return 0;
            if(originalBlockState == null) return 0;
            TileEntityICFController controller = this.getCore();
            if(controller != null) return controller.getPower();
            return 0;
        }

        @Override
        public void setPower(long power) {
            if(!world.getBlockState(pos).getValue(IO_ENABLED)) return;
            if(originalBlockState == null) return;
            TileEntityICFController controller = this.getCore();
            if(controller != null) controller.setPower(power);
        }

        @Override
        public long getMaxPower() {
            if(!world.getBlockState(pos).getValue(IO_ENABLED)) return 0;
            if(originalBlockState == null) return 0;
            TileEntityICFController controller = this.getCore();
            if(controller != null) return controller.getMaxPower();
            return 0;
        }

        private boolean isLoaded = true;

        @Override
        public boolean isLoaded() {
            return isLoaded;
        }

        @Override
        public void onChunkUnload() {
            super.onChunkUnload();
            this.isLoaded = false;
        }

        @Override
        public boolean canConnect(ForgeDirection dir) {
            if(!world.getBlockState(pos).getValue(IO_ENABLED)) return false;
            return dir != ForgeDirection.UNKNOWN;
        }
    }
}
