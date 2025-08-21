package com.hbm.blocks.generic;

import com.hbm.blocks.IStepTickReceiver;
import com.hbm.blocks.ITooltipProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class BlockSpeedy extends BlockBakeBase implements IStepTickReceiver, ITooltipProvider {

    double speed;

    public BlockSpeedy(Material mat, String s, double speed) {
        super(mat, s);
        this.speed = speed;
    }

    @Override
    public void onPlayerStep(World world, int x, int y, int z, EntityPlayer player) {

        if(!world.isRemote)
            return;

        if(player.moveForward != 0 || player.moveStrafing != 0) {
            player.motionX *= speed;
            player.motionZ *= speed;
        }
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(TextFormatting.BLUE + "Increases speed by " + (MathHelper.floor((speed - 1) * 100)) + "%");
    }
}
