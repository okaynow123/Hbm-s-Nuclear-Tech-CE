package com.hbm.blocks;

import com.hbm.items.IModelRegister;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public interface ICustomBlockItem {
    public default void registerItem() {
        ItemBlock itemBlock = new customBlockItem((Block) this);
        itemBlock.setRegistryName(((Block) this).getRegistryName());
        ForgeRegistries.ITEMS.register(itemBlock);
    }

    public static class customBlockItem extends ItemBlock implements IModelRegister {
        public customBlockItem(Block block) {
            super(block);
        }

        public void registerModels() {}
    }
    public default void registerItemBlockModels(){

    }

}
