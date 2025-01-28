package com.hbm.hazard.type;

import com.hbm.config.GeneralConfig;
import com.hbm.hazard.helper.HazardHelper;
import com.hbm.hazard.modifier.HazardModifier;
import com.hbm.util.BobMathUtil;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.hbm.util.I18nUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class HazardTypeRadiation extends HazardTypeBase {

	@Override
	public void onUpdate(final EntityLivingBase target, float level, final ItemStack stack) {


		final boolean reacher = HazardHelper.isHoldingReacher(target);

		level *= stack.getCount();
		
		if(level > 0) {
			float rad = (level / 20F)/2;
			
			if(GeneralConfig.enable528 && reacher) {
				rad = rad / 49F;	//More realistic function for 528: x / distance^2
			} else if(reacher) {
				rad = (float) BobMathUtil.sqrt(rad); //Reworked radiation function: sqrt(x+1/(x+2)^2)-1/(x+2)
			}											
			
			ContaminationUtil.contaminate(target, HazardType.RADIATION, ContaminationType.CREATIVE, rad*hazardRate);
		}
	}

	@Override
	public void updateEntity(final EntityItem item, final float level) { }

	@Override
	@SideOnly(Side.CLIENT)
	public void addHazardInformation(final EntityPlayer player, final List list, float level, final ItemStack stack, final List<HazardModifier> modifiers) {
		
		level = HazardModifier.evalAllModifiers(stack, player, level, modifiers);
		
		if(level < 1e-5)
			return;
		
		list.add(TextFormatting.GREEN + "[" + I18nUtil.resolveKey("trait.radioactive") + "]");
		final String rad = "" + (Math.floor(level* 1000) / 1000);
		list.add(TextFormatting.YELLOW + (rad + "RAD/s"));
		
		if(stack.getCount() > 1) {
			list.add(TextFormatting.YELLOW + "Stack: " + ((Math.floor(level * 1000 * stack.getCount()) / 1000) + "RAD/s"));
		}
	}

}
