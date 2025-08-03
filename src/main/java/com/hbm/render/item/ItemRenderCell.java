package com.hbm.render.item;

import com.hbm.forgefluid.SpecialContainerFillLists.EnumCell;
import com.hbm.interfaces.AutoRegister;
import com.hbm.render.NTMRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.hbm.items.special.ItemCell.getFluidType;
@AutoRegister(item = "cell")
public class ItemRenderCell extends TEISRBase {

    @Override
    public void renderByItem(@NotNull ItemStack stack) {
        EnumCell cellType = EnumCell.getEnumFromFluid(getFluidType(stack));
        IBakedModel model = cellType.getRenderModel();
        if (model == null) {
            model = itemModel;
        }
        NTMRenderHelper.bindBlockTexture();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.5, 0.5);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
        GlStateManager.popMatrix();
        super.renderByItem(stack);
    }
}