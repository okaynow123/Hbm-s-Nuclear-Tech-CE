package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.tileentity.machine.TileEntityMachineArcWelder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
@AutoRegister
public class RenderArcWelder extends TileEntitySpecialRenderer<TileEntityMachineArcWelder> implements IItemRendererProvider {
    @Override
    public void render(@NotNull TileEntityMachineArcWelder arc_welder, double x, double y, double z, float partialTicks, int destroyStage,
                       float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y, z + 0.5);
        GlStateManager.enableLighting();
        GlStateManager.enableCull();

        switch (arc_welder.getBlockMetadata() - BlockDummyable.offset) {
            case 2 -> GlStateManager.rotate(90, 0F, 1F, 0F);
            case 4 -> GlStateManager.rotate(180, 0F, 1F, 0F);
            case 3 -> GlStateManager.rotate(270, 0F, 1F, 0F);
            case 5 -> GlStateManager.rotate(0, 0F, 1F, 0F);
        }

        GlStateManager.translate(-0.5, 0, 0);

        bindTexture(ResourceManager.arc_welder_tex);
        ResourceManager.arc_welder.renderAll();

        // Do not change the translation and scale here, it's a 1.12.2 specific workaround
        if (!arc_welder.display.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0D, 1.125D, 0D);
            GlStateManager.enableLighting();
            GlStateManager.rotate(90, 0F, 1F, 0F);
            GlStateManager.rotate(-90, 1F, 0F, 0F);

            if (!arc_welder.display.isEmpty()) {
                ItemStack stack = arc_welder.display.copy();
                stack.setCount(1);
                // 0.5128205 * 1.5 = 0.76923075
                // see 1.7 net.minecraft.client.renderer.entity.RenderItem line 203
                GlStateManager.scale(0.76923075F, 0.76923075F, 0.76923075F);
                Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
            }
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
    }

    @Override
    public Item getItemForRenderer() {
        return Item.getItemFromBlock(ModBlocks.machine_arc_welder);
    }

    @Override
    public ItemRenderBase getRenderer(Item item) {
        return new ItemRenderBase() {
            public void renderInventory() {
                GlStateManager.translate(0, -2, 0);
                GlStateManager.scale(4, 4, 4);
            }

            public void renderCommon() {
                bindTexture(ResourceManager.arc_welder_tex);
                ResourceManager.arc_welder.renderAll();
            }
        };
    }
}
