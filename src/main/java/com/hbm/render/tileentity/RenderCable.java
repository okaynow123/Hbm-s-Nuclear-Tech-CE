package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.Library;
import com.hbm.main.ResourceManager;
import com.hbm.tileentity.network.energy.TileEntityCableBaseNT;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

import java.util.Objects;

public class RenderCable extends TileEntitySpecialRenderer<TileEntityCableBaseNT> {
	
	@Override
	public void render(TileEntityCableBaseNT te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(te.getBlockType() != ModBlocks.red_cable)
			return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		bindTexture(ResourceManager.cable_neo_tex);

		boolean pX = canConnectToNeighbor(te, ForgeDirection.EAST);   // +X
		boolean nX = canConnectToNeighbor(te, ForgeDirection.WEST);   // -X
		boolean pY = canConnectToNeighbor(te, ForgeDirection.UP);     // +Y
		boolean nY = canConnectToNeighbor(te, ForgeDirection.DOWN);   // -Y
		boolean pZ = canConnectToNeighbor(te, ForgeDirection.SOUTH);  // +Z
		boolean nZ = canConnectToNeighbor(te, ForgeDirection.NORTH);  // -Z

		if(pX && nX && !pY && !nY && !pZ && !nZ)
			ResourceManager.cable_neo.renderPart("CX");
		else if(!pX && !nX && pY && nY && !pZ && !nZ)
			ResourceManager.cable_neo.renderPart("CY");
		else if(!pX && !nX && !pY && !nY && pZ && nZ)
			ResourceManager.cable_neo.renderPart("CZ");
		else{
			ResourceManager.cable_neo.renderPart("Core");
			if(pX) ResourceManager.cable_neo.renderPart("posX");
			if(nX) ResourceManager.cable_neo.renderPart("negX");
			if(pY) ResourceManager.cable_neo.renderPart("posY");
			if(nY) ResourceManager.cable_neo.renderPart("negY");
			if(pZ) ResourceManager.cable_neo.renderPart("negZ");
			if(nZ) ResourceManager.cable_neo.renderPart("posZ");
		}

		GlStateManager.translate(-x - 0.5F, -y - 0.5F, -z - 0.5F);
		GlStateManager.popMatrix();
	}

	/**
	 * Checks if it can connect to a HE or FE neighbor.
	 */
	private boolean canConnectToNeighbor(TileEntityCableBaseNT te, ForgeDirection dir) {
		BlockPos neighborPos = te.getPos().offset(Objects.requireNonNull(dir.toEnumFacing()));
		if (Library.canConnect(te.getWorld(), neighborPos, dir)) {
			return true;
		}

		TileEntity neighbor = te.getWorld().getTileEntity(neighborPos);
		if (neighbor != null && !neighbor.isInvalid()) {
			EnumFacing facing = dir.getOpposite().toEnumFacing();
			if (neighbor.hasCapability(CapabilityEnergy.ENERGY, facing)) {
				IEnergyStorage storage = neighbor.getCapability(CapabilityEnergy.ENERGY, facing);
				return storage != null && (storage.canReceive() || storage.canExtract());
			}
		}

		return false;
	}
}
