package com.hbm.render.entity.mob;

import com.hbm.entity.mob.EntityTaintCrab;
import com.hbm.interfaces.AutoRegister;
import com.hbm.main.ResourceManager;
import com.hbm.render.misc.BeamPronter;
import com.hbm.render.misc.BeamPronter.EnumBeamType;
import com.hbm.render.misc.BeamPronter.EnumWaveType;
import com.hbm.render.model.ModelTaintCrab;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.registry.IRenderFactory;
@AutoRegister(entity = EntityTaintCrab.class, factory = "FACTORY")
public class RenderTaintCrab extends RenderLiving<EntityTaintCrab> {

	public static final IRenderFactory<EntityTaintCrab> FACTORY = (RenderManager man) -> {
		return new RenderTaintCrab(man);
	};

	public RenderTaintCrab(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelTaintCrab(), 1.0F);
		this.shadowOpaque = 0.0F;
	}

	@Override
	public void doRender(EntityTaintCrab entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1.25, z);

		double sx = entity.posX;
		double sy = entity.posY + 1.25;
		double sz = entity.posZ;

		for(double[] target : ((EntityTaintCrab) entity).targets) {

			double length = Math.sqrt(Math.pow(target[0] - sx, 2) + Math.pow(target[1] - sy, 2) + Math.pow(target[2] - sz, 2));

			BeamPronter.prontBeam(new Vec3d(target[0] - sx, target[1] - sy, target[2] - sz), EnumWaveType.RANDOM, EnumBeamType.SOLID, 0x0051C4, 0x606060, (int) (entity.world.getTotalWorldTime() % 1000 + 1), (int) (length * 5), 0.125F, 2, 0.03125F);
		}

		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityTaintCrab entity) {
		return ResourceManager.taintcrab_tex;
	}

}
