package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachinePlasmaHeater;
import com.hbm.lib.RefStrings;
import com.hbm.tileentity.machine.TileEntityMachinePlasmaHeater;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GUIMachinePlasmaHeater extends GuiInfoContainer {

	public static ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/gui/reactors/gui_fusion_heater.png");
	private TileEntityMachinePlasmaHeater plasmaHeater;

	public GUIMachinePlasmaHeater(InventoryPlayer invPlayer, TileEntityMachinePlasmaHeater tile) {
		super(new ContainerMachinePlasmaHeater(invPlayer, tile));
		this.plasmaHeater = tile;

		this.xSize = 176;
		this.ySize = 168;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);

		plasmaHeater.tanksNew[0].renderTankInfo(this, mouseX, mouseY, guiLeft + 62, guiTop + 17, 16, 52);
		plasmaHeater.tanksNew[1].renderTankInfo(this, mouseX, mouseY, guiLeft + 134, guiTop + 17, 16, 52);
		plasmaHeater.plasmaNew.renderTankInfo(this, mouseX, mouseY, guiLeft + 98, guiTop + 17, 16, 52);
		this.drawElectricityInfo(this, mouseX, mouseY, guiLeft + 8, guiTop + 17, 16, 34, plasmaHeater.power, TileEntityMachinePlasmaHeater.maxPower);
		super.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		super.drawDefaultBackground();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		int i = (int)plasmaHeater.getPowerScaled(34);
		drawTexturedModalRect(guiLeft + 8, guiTop + 51 - i, 176, 34 - i, 16, i);

		plasmaHeater.tanksNew[0].renderTank(guiLeft + 62, guiTop + 69, this.zLevel, 16, 52);
		plasmaHeater.tanksNew[1].renderTank(guiLeft + 134, guiTop + 69, this.zLevel, 16, 52);
		plasmaHeater.plasmaNew.renderTank(guiLeft + 98, guiTop + 69, this.zLevel, 16, 52);
		GL11.glPopAttrib();
	}
}