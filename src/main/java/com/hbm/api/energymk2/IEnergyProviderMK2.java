package com.hbm.api.energymk2;

import com.hbm.config.GeneralConfig;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.lib.ForgeDirection;
import com.hbm.packet.toclient.AuxParticlePacketNT;
import com.hbm.util.Compat;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * If it sends energy, use this
 */
public interface IEnergyProviderMK2 extends IEnergyHandlerMK2 {

    /**
     * Uses up available power, default implementation has no sanity checking, make sure that the requested power is lequal to the current power
     *
     * @param power The amount of power to use. Ensure this value is less than or equal to the current power.
     */
    default void usePower(long power) {
        // Subtract the specified power from the current power and update the power level
        this.setPower(this.getPower() - power);
    }

    /**
     * Retrieves the maximum speed at which the energy provider can send energy.
     * By default, this method returns the maximum power capacity of the provider.
     *
     * @return The maximum energy transfer speed, represented by the provider's maximum power capacity.
     */
    default long getProviderSpeed() {
        // Return the maximum power capacity as the default provider speed
        return this.getMaxPower();
    }

    /**
     * Attempts to provide energy to a neighboring tile entity.
     * Checks if the neighboring tile entity is an energy conductor or receiver.
     * If it's a conductor, adds this provider to the conductor's network.
     * If it's a receiver or has {@link CapabilityEnergy#ENERGY} and can receive FE, transfers energy to the receiver.
     *
     * @param world The game world in which the operation takes place.
     * @param x The x-coordinate of the current tile entity.
     * @param y The y-coordinate of the current tile entity.
     * @param z The z-coordinate of the current tile entity.
     * @param dir The direction from this provider to the neighboring tile entity.
     */
    default void tryProvide(World world, int x, int y, int z, ForgeDirection dir) {
        boolean red = false;

        if (Compat.getTileStandard(world, x, y, z) instanceof IEnergyConductorMK2 con) {
            if (con.canConnect(dir.getOpposite())) {

                Nodespace.PowerNode node = Nodespace.getNode(world, new BlockPos(x, y, z));

                if (node != null && node.net != null) {
                    node.net.addProvider(this);
                    red = true;
                }
            }
        }

        BlockPos providerPos = new BlockPos(x, y, z);
        ForgeDirection[] directionsToCheck;
        if (dir == ForgeDirection.UNKNOWN) {
            directionsToCheck = ForgeDirection.VALID_DIRECTIONS;
        } else {
            directionsToCheck = new ForgeDirection[]{dir};
        }

        for (ForgeDirection checkDir : directionsToCheck) {
            if (checkDir == null || checkDir == ForgeDirection.UNKNOWN) {
                continue;
            }

            BlockPos neighborPos = providerPos.offset(checkDir.toEnumFacing());
            TileEntity neighborTE = world.getTileEntity(neighborPos);

            if (neighborTE == null || neighborTE == this) {
                continue;
            }

            if (neighborTE instanceof IEnergyReceiverMK2 rec) {
                if (rec.canConnect(checkDir.getOpposite())) {
                    long provides = Math.min(this.getPower(), this.getProviderSpeed());
                    long receives = Math.min(rec.getMaxPower() - rec.getPower(), rec.getReceiverSpeed());
                    long toTransfer = Math.min(provides, receives);
                    toTransfer -= rec.transferPower(toTransfer, false);
                    this.usePower(toTransfer);
                }
            } else {
                EnumFacing neighborFace = checkDir.getOpposite().toEnumFacing();
                if (neighborFace != null && neighborTE.hasCapability(CapabilityEnergy.ENERGY, neighborFace)) {
                    IEnergyStorage cap = neighborTE.getCapability(CapabilityEnergy.ENERGY, neighborFace);
                    boolean ready = cap != null && cap.canReceive() && GeneralConfig.conversionRateHeToRF > 0 && this.getPower() > 0 && this.getProviderSpeed() > 0;
                    if (ready) {
                        long heBudget = Math.min(this.getPower(), this.getProviderSpeed());
                        long feBudget = (long) Math.floor(heBudget * GeneralConfig.conversionRateHeToRF);
                        if (feBudget > 0) {
                            int freeSpaceFE = cap.receiveEnergy(Integer.MAX_VALUE, true);
                            if (freeSpaceFE > 0) {
                                int feToSend = (int) Math.min(Math.min(feBudget, freeSpaceFE), Integer.MAX_VALUE);
                                int feAccepted = cap.receiveEnergy(feToSend, false);
                                if (feAccepted > 0) {
                                    long heAccepted = Math.round(feAccepted / GeneralConfig.conversionRateHeToRF);
                                    this.usePower(heAccepted);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (particleDebug) {
            NBTTagCompound data = new NBTTagCompound();
            data.setString("type", "network");
            data.setString("mode", "power");
            double posX = x + 0.5 - dir.offsetX * 0.5 + world.rand.nextDouble() * 0.5 - 0.25;
            double posY = y + 0.5 - dir.offsetY * 0.5 + world.rand.nextDouble() * 0.5 - 0.25;
            double posZ = z + 0.5 - dir.offsetZ * 0.5 + world.rand.nextDouble() * 0.5 - 0.25;
            data.setDouble("mX", dir.offsetX * (red ? 0.025 : 0.1));
            data.setDouble("mY", dir.offsetY * (red ? 0.025 : 0.1));
            data.setDouble("mZ", dir.offsetZ * (red ? 0.025 : 0.1));
            PacketThreading.createAllAroundThreadedPacket(new AuxParticlePacketNT(data, posX, posY, posZ), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 25));
        }
    }
}
