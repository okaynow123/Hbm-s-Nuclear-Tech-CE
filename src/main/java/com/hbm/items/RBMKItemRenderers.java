package com.hbm.items;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.rbmk.RBMKBase;
import com.hbm.main.ResourceManager;
import com.hbm.render.item.ItemRenderBase;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class RBMKItemRenderers {
  public static final Map<Item, TileEntityItemStackRenderer> itemRenderers = new HashMap<>();

  public static final ItemRenderBase RBMK_ROD =
      new ItemRenderBase() {
        public void renderInventory(ItemStack stack) {
          GlStateManager.translate(0, -5.5, 0);
          GlStateManager.scale(3.65, 3.65, 3.65);
        }

        public void renderCommon(ItemStack stack) {
          Block block = Block.getBlockFromItem(stack.getItem());
          if (!(block instanceof RBMKBase)) return;
          Minecraft.getMinecraft()
              .getTextureManager()
              .bindTexture(((RBMKBase) block).columnTexture);

          GlStateManager.pushMatrix();
          for (int i = 0; i < 4; i++) {
            ResourceManager.rbmk_element.renderPart("Column");
            GlStateManager.translate(0, 1, 0);
          }
          GlStateManager.popMatrix();

          // Render lid if needed
          if (block != ModBlocks.rbmk_boiler && block != ModBlocks.rbmk_heater) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 3, 0);
            ResourceManager.rbmk_element.renderPart("Lid");
            GlStateManager.popMatrix();
          }
        }

        public boolean doNullTransform() {
          return true;
        }
      };
  public static final ItemRenderBase RBMK_CONTROL =
      new ItemRenderBase() {
        public void renderInventory(ItemStack stack) {
          GlStateManager.translate(0, -5.5, 0);
          GlStateManager.scale(3.65, 3.65, 3.65);
        }

        public void renderCommon(ItemStack stack) {
          Block block = Block.getBlockFromItem(stack.getItem());
          if (!(block instanceof RBMKBase)) return;
          Minecraft.getMinecraft()
              .getTextureManager()
              .bindTexture(((RBMKBase) block).columnTexture);

          // Rende column base
          GlStateManager.pushMatrix();
          for (int i = 0; i < 4; i++) {
            ResourceManager.rbmk_rods.renderPart("Column");
            GlStateManager.translate(0, 1, 0);
          }
          GlStateManager.popMatrix();

          // Render lid if needed
          if (block != ModBlocks.rbmk_boiler && block != ModBlocks.rbmk_heater) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 3, 0);
            ResourceManager.rbmk_rods.renderPart("Lid");
            GlStateManager.popMatrix();
          }
        }

        public boolean doNullTransform() {
          return true;
        }
      };

  public static final ItemRenderBase RBMK_PASSIVE =
      new ItemRenderBase() {
        public void renderInventory(ItemStack stack) {
          GlStateManager.translate(0, -5.5, 0);
          GlStateManager.scale(3.65, 3.65, 3.65);
        }

        public void renderCommon(ItemStack stack) {
          Block block = Block.getBlockFromItem(stack.getItem());
          if (!(block instanceof RBMKBase)) return;

          // Bind texture properly
          Minecraft.getMinecraft()
              .getTextureManager()
              .bindTexture(((RBMKBase) block).columnTexture);

          GlStateManager.pushMatrix();
          for (int i = 0; i < 4; i++) {
            ResourceManager.rbmk_reflector.renderPart("Column");
            GlStateManager.translate(0, 1, 0);
          }
          GlStateManager.popMatrix();

          if (block != ModBlocks.rbmk_boiler) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 3F, 0);
            ResourceManager.rbmk_reflector.renderPart("Lid");
            GlStateManager.popMatrix();
          }
        }

        public boolean doNullTransform() {
          return true;
        }
      };

  static {
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_rod), RBMK_ROD);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_rod_mod), RBMK_ROD);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_rod_reasim), RBMK_ROD);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_rod_reasim_mod), RBMK_ROD);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_control), RBMK_CONTROL);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_control_mod), RBMK_CONTROL);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_control_auto), RBMK_CONTROL);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_blank), RBMK_PASSIVE);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_boiler), RBMK_CONTROL);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_heater), RBMK_CONTROL);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_reflector), RBMK_PASSIVE);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_absorber), RBMK_PASSIVE);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_moderator), RBMK_PASSIVE);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_outgasser), RBMK_PASSIVE);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_storage), RBMK_PASSIVE);
    itemRenderers.put(Item.getItemFromBlock(ModBlocks.rbmk_cooler), RBMK_PASSIVE);
  }
}
