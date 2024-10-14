package com.hbm.tileentity.machine;

import api.hbm.fluid.IFluidStandardTransceiver;
import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.inventory.fluid.trait.FT_Heatable;
import com.hbm.lib.DirPos;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.Library;
import com.hbm.tileentity.INBTPacketReceiver;

import api.hbm.tile.IHeatSource;
import com.hbm.tileentity.TileEntityLoadedBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityHeatBoiler extends TileEntityLoadedBase implements INBTPacketReceiver, ITickable, IFluidStandardTransceiver {

    public FluidTank[] tanks;
    public int heat;
    public static int maxHeat = 12_800_000; //the heat required to turn 64k of water into steam
    public static final double diffusion = 0.1D;

    public TileEntityHeatBoiler() {
        super();
        tanks = new FluidTank[2];
        this.tanks[0] = new FluidTank(Fluids.WATER, 16_000);
        this.tanks[1] = new FluidTank(Fluids.STEAM, 16_000 * 100);

    }
    @Override
    public void update() {

        if(!world.isRemote) {
            setupTanks();
            updateConnections();
            tryPullHeat();
            tryConvert();

            for(DirPos pos : getConPos()) {
                if(tanks[1].getFill() > 0) this.sendFluid(tanks[1], world, pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ(), pos.getDir());
            }

            NBTTagCompound data = new NBTTagCompound();

            for(int i = 0; i < 2; i++)
                tanks[i].writeToNBT(data, "tank" + i);

            INBTPacketReceiver.networkPack(this, data, 50);
        }
    }

    private void updateConnections() {

        for(DirPos pos : getConPos()) {
            this.trySubscribe(tanks[0].getTankType(), world, pos.getPos().getX(), pos.getPos().getY(), pos.getPos().getZ(), pos.getDir());
        }
    }

    protected DirPos[] getConPos() {

        ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata() - BlockDummyable.offset).getRotation(ForgeDirection.UP);

        return new DirPos[] {
                new DirPos(pos.getX() + dir.offsetX * 2, pos.getY(), pos.getZ() + dir.offsetZ * 2, dir),
                new DirPos(pos.getX() - dir.offsetX * 2, pos.getY(), pos.getZ() - dir.offsetZ * 2, dir.getOpposite()),
                new DirPos(pos.getX(), pos.getY() + 4, pos.getZ(), Library.POS_Y)
        };
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        for(int i = 0; i < tanks.length; i++)
            tanks[i].readFromNBT(nbt, "tank" + i);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        for(int i = 0; i < tanks.length; i++)
            tanks[i].writeToNBT(nbt, "tank" + i);
        return nbt;
    }

    @Override
    public void networkUnpack(NBTTagCompound nbt) {
        for(int i = 0; i < 2; i++)
            tanks[i].readFromNBT(nbt, "tank" + i);
        this.heat = nbt.getInteger("heat");
    }

    protected void setupTanks() {

        if(tanks[0].getTankType().hasTrait(FT_Heatable.class)) {
            FT_Heatable trait = tanks[0].getTankType().getTrait(FT_Heatable.class);
            if(trait.getEfficiency(FT_Heatable.HeatingType.BOILER) > 0) {
                FT_Heatable.HeatingStep entry = trait.getFirstStep();
                tanks[1].setTankType(entry.typeProduced);
                tanks[1].changeTankSize(tanks[0].getMaxFill() * entry.amountProduced / entry.amountReq);
                return;
            }
        }

        tanks[0].setTankType(Fluids.NONE);
        tanks[1].setTankType(Fluids.NONE);
    }

    protected void tryConvert() {

        if(tanks[0].getTankType().hasTrait(FT_Heatable.class)) {
            FT_Heatable trait = tanks[0].getTankType().getTrait(FT_Heatable.class);
            if(trait.getEfficiency(FT_Heatable.HeatingType.BOILER) > 0) {

                FT_Heatable.HeatingStep entry = trait.getFirstStep();
                int inputOps = this.tanks[0].getFill() / entry.amountReq;
                int outputOps = (this.tanks[1].getMaxFill() - this.tanks[1].getFill()) / entry.amountProduced;
                int heatOps = this.heat / entry.heatReq;

                int ops = Math.min(inputOps, Math.min(outputOps, heatOps));

                this.tanks[0].setFill(this.tanks[0].getFill() - entry.amountReq * ops);
                this.tanks[1].setFill(this.tanks[1].getFill() + entry.amountProduced * ops);
                this.heat -= entry.heatReq * ops;
            }
        }
    }
    
    protected void tryPullHeat() {

        if(this.heat >= TileEntityHeatBoiler.maxHeat) return;
        BlockPos blockBelow = pos.down();
        TileEntity con = world.getTileEntity(blockBelow);

        if(con instanceof IHeatSource) {
            IHeatSource source = (IHeatSource) con;
            int diff = source.getHeatStored() - this.heat;

            if(diff == 0) {
                return;
            }

            if(diff > 0) {
                diff = (int) Math.ceil(diff * diffusion);
                source.useUpHeat(diff);
                this.heat += diff;
                if(this.heat > this.maxHeat)
                    this.heat = this.maxHeat;
                return;
            }
        }

        this.heat = Math.max(this.heat - Math.max(this.heat / 1000, 1), 0);
    }

    AxisAlignedBB bb = null;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {

        if(bb == null) {
            bb = new AxisAlignedBB(
                    pos.getX() - 1,
                    pos.getY(),
                    pos.getZ() - 1,
                    pos.getX() + 2,
                    pos.getY() + 4,
                    pos.getZ() + 2
            );
        }

        return bb;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Override
    public FluidTank[] getSendingTanks() {
        return new FluidTank[] {tanks[1]};
    }

    @Override
    public FluidTank[] getReceivingTanks() {
        return new FluidTank[] {tanks[0]};
    }

    @Override
    public FluidTank[] getAllTanks() {
        return tanks;
    }

}