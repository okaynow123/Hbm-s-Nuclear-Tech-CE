package com.hbm.render.tileentity;

import com.hbm.render.item.ItemRenderBase;
import net.minecraft.item.Item;

public interface IItemRendererProvider {
    Item getItemForRenderer();

    default Item[] getItemsForRenderer() {
        return new Item[] { this.getItemForRenderer() };
    }

    ItemRenderBase getRenderer(Item item);
}
