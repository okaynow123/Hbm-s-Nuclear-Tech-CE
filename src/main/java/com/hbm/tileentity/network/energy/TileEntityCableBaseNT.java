package com.hbm.tileentity.network.energy;

import com.hbm.api.energymk2.IEnergyConductorMK2;
import com.hbm.api.energymk2.Nodespace;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.interfaces.AutoRegister;
import com.hbm.lib.ForgeDirection;
import com.hbm.packet.toclient.BufPacket;
import com.hbm.tileentity.IBufPacketReceiver;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@AutoRegister
public class TileEntityCableBaseNT extends TileEntity implements IBufPacketReceiver, ITickable, IEnergyConductorMK2 {
	
	protected Nodespace.PowerNode node;
//
//	private final Map<ForgeDirection, VirtualFEReceiver> feReceivers = new HashMap<>();
//	private final Map<ForgeDirection, VirtualFEProvider> feProviders = new HashMap<>();

	@Override
	public void update() {

		if(!world.isRemote) {

			if(this.node == null || this.node.expired) {

				if(this.shouldCreateNode()) {
					this.node = Nodespace.getNode(world, pos);

					if(this.node == null || this.node.expired) {
						this.node = this.createNode();
						Nodespace.createNode(world, this.node);
					}
				}
			}

//			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
//				BlockPos neighborPos = pos.offset(Objects.requireNonNull(dir.toEnumFacing()));
//				TileEntity te = world.getTileEntity(neighborPos);
//				boolean hasCap = false;
//
//				if (te != null && !te.isInvalid()) {
//					EnumFacing face = dir.getOpposite().toEnumFacing();
//					if (te.hasCapability(CapabilityEnergy.ENERGY, face)) {
//						IEnergyStorage cap = te.getCapability(CapabilityEnergy.ENERGY, face);
//						if (cap != null) {
//							hasCap = true;
//
//							if (cap.canReceive()) {
//								VirtualFEReceiver vrec = feReceivers.computeIfAbsent(dir, d -> new VirtualFEReceiver(world, neighborPos, face));
//								vrec.trySubscribe(world, pos.getX(), pos.getY(), pos.getZ(), dir.getOpposite());
//							} else {
//								feReceivers.remove(dir);
//							}
//
//							if (cap.canExtract()) {
//								VirtualFEProvider vprov = feProviders.computeIfAbsent(dir, d -> new VirtualFEProvider(world, neighborPos, face));
//								vprov.tryProvide(world, pos.getX(), pos.getY(), pos.getZ(), dir.getOpposite());
//							} else {
//								feProviders.remove(dir);
//							}
//						}
//					}
//				}
//
//				if (!hasCap) {
//					feReceivers.remove(dir);
//					feProviders.remove(dir);
//				}
//			}
		}
	}

	public boolean canUpdate() {
		return (this.node == null || !this.node.hasValidNet()) && !this.isInvalid();
	}

	public boolean shouldCreateNode() {
		return true;
	}

	public void onNodeDestroyedCallback() {
		this.node = null;
	}

	@Override
	public void invalidate() {
		super.invalidate();

		if(!world.isRemote) {
			if(this.node != null) {
				Nodespace.destroyNode(world, pos);
			}
//
//			feReceivers.clear();
//			feProviders.clear();
		}
	}

	public void networkPackNT(int range) {
		if (!world.isRemote)
			PacketThreading.createAllAroundThreadedPacket(new BufPacket(pos.getX(), pos.getY(), pos.getZ(), this), new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), range));
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir != ForgeDirection.UNKNOWN;
	}

	@Override
	public void serialize(ByteBuf buf) {

	}

	@Override
	public void deserialize(ByteBuf buf) {

	}
//
//	@Override
//	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
//		if (capability == CapabilityEnergy.ENERGY) {
//			return CapabilityEnergy.ENERGY.cast(new InfiniteCableEnergyStorage(this));
//		}
//		return super.getCapability(capability, facing);
//	}
//	public static class InfiniteCableEnergyStorage implements IEnergyStorage {
//		private final TileEntityCableBaseNT cable;
//
//		public InfiniteCableEnergyStorage(TileEntityCableBaseNT cable) {
//			this.cable = cable;
//		}
//
//		@Override
//		public int receiveEnergy(int maxReceive, boolean simulate) {
//			return maxReceive;
//		}
//
//		@Override
//		public int extractEnergy(int maxExtract, boolean simulate) {
//			return maxExtract; // TODO
//		}
//
//		@Override
//		public int getEnergyStored() {
//			return 0;
//		}
//
//		@Override
//		public int getMaxEnergyStored() {
//			return Integer.MAX_VALUE;
//		}
//
//		@Override
//		public boolean canExtract() {
//			return true;
//		}
//
//		@Override
//		public boolean canReceive() {
//			return true;
//		}
//	}
}
