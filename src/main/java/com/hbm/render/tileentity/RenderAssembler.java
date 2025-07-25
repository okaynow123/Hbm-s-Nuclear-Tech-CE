package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.recipes.AssemblerRecipes;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineAssembler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderAssembler extends TileEntitySpecialRenderer<TileEntityMachineAssembler>
    implements IItemRendererProvider {

  @Override
  public boolean isGlobalRenderer(TileEntityMachineAssembler te) {
    return true;
  }

  @Override
  public void render(
      TileEntityMachineAssembler assembler,
      double x,
      double y,
      double z,
      float partialTicks,
      int destroyStage,
      float alpha) {
    // RenderHelper.renderFlashLight(start, start.add(-20, 0, 0), 20, 1, ResourceManager.fl_cookie,
    // partialTicks);
    // FlashlightRenderer.addFlashlight(start, start.add(-20, 0, 0), 20, 20,
    // ResourceManager.fl_cookie, true, true);
    // LightRenderer.addPointLight(start, new Vec3d(1, 0.4, 0.1), 10);
    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5D, y, z + 0.5D);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();
    GlStateManager.rotate(180, 0F, 1F, 0F);
    // GlStateManager.pushMatrix();
    // GlStateManager.translate(0, 5, 0);
    // GlStateManager.bindTexture(RenderHelper.deferredNormalTex);
    // ResourceManager.test.draw();
    // GlStateManager.popMatrix();
    switch (assembler.getBlockMetadata()) {
      case 14:
        GlStateManager.rotate(180, 0F, 1F, 0F);
        GlStateManager.translate(0.5D, 0.0D, -0.5D);
        break;
      case 13:
        GlStateManager.rotate(270, 0F, 1F, 0F);
        GlStateManager.translate(0.5D, 0.0D, -0.5D);
        break;
      case 15:
        GlStateManager.rotate(0, 0F, 1F, 0F);
        GlStateManager.translate(0.5D, 0.0D, -0.5D);
        break;
      case 12:
        GlStateManager.rotate(90, 0F, 1F, 0F);
        GlStateManager.translate(0.5D, 0.0D, -0.5D);
        break;
    }

    bindTexture(ResourceManager.assembler_body_tex);
    ResourceManager.assembler_body.renderAll();

    if (assembler.recipe != -1) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(-1, 0.875, 0);

      try {
        ItemStack stack = AssemblerRecipes.recipeList.get(assembler.recipe).toStack();

        GlStateManager.translate(1, 0, 1);
        if (!(stack.getItem() instanceof ItemBlock)) {
          GlStateManager.rotate(-90, 1F, 0F, 0F);
        } else {
          GL11.glScaled(0.5, 0.5, 0.5);
          GlStateManager.translate(0, -0.875, -2);
        }

        IBakedModel model =
            Minecraft.getMinecraft()
                .getRenderItem()
                .getItemModelWithOverrides(stack, assembler.getWorld(), null);
        model = ForgeHooksClient.handleCameraTransforms(model, TransformType.FIXED, false);
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.translate(0.0F, 1.0F - 0.0625F * 165 / 100, 0.0F);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
      } catch (Exception ex) {
      }

      GlStateManager.popMatrix();
    }
    /*GlStateManager.translate(-0.5, 3.6, -0.5);
    bindTexture(ResourceManager.hatch_tex);
    AnimationWrapper w = new AnimationWrapper(0, ResourceManager.silo_hatch_open);
    ResourceManager.silo_hatch.controller.setAnim(w);
    GlStateManager.shadeModel(GL11.GL_SMOOTH);
    ResourceManager.silo_hatch.renderAnimated(5000);
    GlStateManager.shadeModel(GL11.GL_FLAT);*/

    GlStateManager.popMatrix();

    renderSlider(assembler, x, y, z, partialTicks);
  }

  public void renderSlider(
      TileEntityMachineAssembler tileEntity, double x, double y, double z, float f) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y, z);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();
    GlStateManager.rotate(180, 0F, 1F, 0F);
    switch (tileEntity.getBlockMetadata()) {
      case 14:
        GlStateManager.translate(-1, 0, 0);
        GlStateManager.rotate(180, 0F, 1F, 0F);
        break;
      case 13:
        GlStateManager.rotate(270, 0F, 1F, 0F);
        break;
      case 15:
        GlStateManager.translate(0, 0, -1);
        GlStateManager.rotate(0, 0F, 1F, 0F);
        break;
      case 12:
        GlStateManager.translate(-1, 0, -1);
        GlStateManager.rotate(90, 0F, 1F, 0F);
        break;
    }

    bindTexture(ResourceManager.assembler_slider_tex);

    int offset = (int) (System.currentTimeMillis() % 5000) / 5;

    if (offset > 500) offset = 500 - (offset - 500);

    TileEntityMachineAssembler assembler = (TileEntityMachineAssembler) tileEntity;

    if (assembler.isProgressing) GlStateManager.translate(offset * 0.003 - 0.75, 0, 0);

    ResourceManager.assembler_slider.renderAll();

    bindTexture(ResourceManager.assembler_arm_tex);

    double sway = (System.currentTimeMillis() % 2000) / 2;

    sway = Math.sin(sway / Math.PI / 50);

    if (assembler.isProgressing) GlStateManager.translate(0, 0, sway * 0.3);
    ResourceManager.assembler_arm.renderAll();

    GlStateManager.popMatrix();

    renderCogs(tileEntity, x, y, z, f);
  }

  public void renderCogs(
      TileEntityMachineAssembler tileEntity, double x, double y, double z, float f) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y, z);
    GlStateManager.enableLighting();
    GlStateManager.disableCull();
    GlStateManager.rotate(180, 0F, 1F, 0F);
    switch (tileEntity.getBlockMetadata()) {
      case 14:
        GlStateManager.translate(-1, 0, 0);
        GlStateManager.rotate(180, 0F, 1F, 0F);
        break;
      case 13:
        GlStateManager.rotate(270, 0F, 1F, 0F);
        break;
      case 15:
        GlStateManager.translate(0, 0, -1);
        GlStateManager.rotate(0, 0F, 1F, 0F);
        break;
      case 12:
        GlStateManager.translate(-1, 0, -1);
        GlStateManager.rotate(90, 0F, 1F, 0F);
        break;
    }

    bindTexture(ResourceManager.assembler_cog_tex);

    int rotation = (int) (System.currentTimeMillis() % (360 * 5)) / 5;

    TileEntityMachineAssembler assembler = (TileEntityMachineAssembler) tileEntity;

    if (!assembler.isProgressing) rotation = 0;

    GlStateManager.pushMatrix();
    GlStateManager.translate(-0.6, 0.75, 1.0625);
    GlStateManager.rotate(-rotation, 0F, 0F, 1F);
    ResourceManager.assembler_cog.renderAll();
    GlStateManager.popMatrix();

    GlStateManager.pushMatrix();
    GlStateManager.translate(0.6, 0.75, 1.0625);
    GlStateManager.rotate(rotation, 0F, 0F, 1F);
    ResourceManager.assembler_cog.renderAll();
    GlStateManager.popMatrix();

    GlStateManager.pushMatrix();
    GlStateManager.translate(-0.6, 0.75, -1.0625);
    GlStateManager.rotate(-rotation, 0F, 0F, 1F);
    ResourceManager.assembler_cog.renderAll();
    GlStateManager.popMatrix();

    GlStateManager.pushMatrix();
    GlStateManager.translate(0.6, 0.75, -1.0625);
    GlStateManager.rotate(rotation, 0F, 0F, 1F);
    ResourceManager.assembler_cog.renderAll();
    GlStateManager.popMatrix();

    GlStateManager.popMatrix();
  }

  @Override
  public Item getItemForRenderer() {
    return Item.getItemFromBlock(ModBlocks.machine_assembler);
  }

  @Override
  public ItemRenderBase getRenderer(Item item) {
    return new ItemRenderBase() {
      public void renderInventory() {
        GlStateManager.scale(3.5, 3.5, 3.5);
      }

      public void renderCommon() {
        bindTexture(ResourceManager.assembler_body_tex);
        ResourceManager.assembler_body.renderAll();
        bindTexture(ResourceManager.assembler_slider_tex);
        ResourceManager.assembler_slider.renderAll();
        bindTexture(ResourceManager.assembler_arm_tex);
        ResourceManager.assembler_arm.renderAll();
        bindTexture(ResourceManager.assembler_cog_tex);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.6, 0.75, 1.0625);
        ResourceManager.assembler_cog.renderAll();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.6, 0.75, 1.0625);
        ResourceManager.assembler_cog.renderAll();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.6, 0.75, -1.0625);
        ResourceManager.assembler_cog.renderAll();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.6, 0.75, -1.0625);
        ResourceManager.assembler_cog.renderAll();
        GlStateManager.popMatrix();
      }
    };
  }
}
