package com.hbm.items.weapon;

import com.hbm.items.IDynamicModels;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

import java.util.ArrayList;
import java.util.List;

import static com.hbm.items.ModItems.ammo_himars;

public interface IMetaItemTesr {
    List<IMetaItemTesr> INSTANCES = new ArrayList<>();
    int getSubitemCount();
   static void redirectModels(){ INSTANCES.forEach(IMetaItemTesr::redirectModel); }
    String getName();
    default void redirectModel(){
        for(int i = 1; i < getSubitemCount(); i++){
            ModelLoader.setCustomModelResourceLocation((Item) this, i, new ModelResourceLocation(getName(), "inventory"));
        }
    }


}
