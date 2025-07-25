package com.hbm.render.entity.rocket.part;

import com.hbm.dim.CelestialBody;
import com.hbm.entity.missile.EntityRideableRocket;
import com.hbm.entity.missile.EntityRideableRocket.RocketState;
import com.hbm.main.ResourceManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11; import net.minecraft.client.renderer.GlStateManager;

public class RenderDropPod extends RenderRocketPart {

	private boolean brakes;
	private long brakeStart;

	@Override
	public void render(TextureManager tex, EntityRideableRocket rocket, float interp) {
		tex.bindTexture(ResourceManager.drop_pod_tex);

		if(CelestialBody.inOrbit(rocket.world)) {
			ResourceManager.drop_pod.renderAll();
			return;
		}

		RocketState state = rocket.getState();
		int timer = rocket.getStateTimer();
		float lerpTimer = (float)timer + interp;
		
		ResourceManager.drop_pod.renderPart("DropPod");

		// Render door
		GlStateManager.pushMatrix();
		{

			float doorRotation = 0;
			if(state == RocketState.LANDED) {
				doorRotation = MathHelper.clamp(lerpTimer * 2, 0, 90);
			} else if(state == RocketState.AWAITING) {
				doorRotation = MathHelper.clamp(90 - lerpTimer * 2, 0, 90);
			}

			GlStateManager.translate(0.69291F, 2.8333F, 0);
			GlStateManager.rotate(doorRotation, 0, 0, 1);
			GlStateManager.translate(-0.69291F, -2.8333F, 0);
			ResourceManager.drop_pod.renderPart("Door");

		}
		GlStateManager.popMatrix();

		// Render airbrakes
		GlStateManager.pushMatrix();
		{

			float brakeRotation = 0;
			if(state == RocketState.LANDING) {
				if(rocket.motionY > -0.4F) {
					if(brakes) {
						brakes = false;
						brakeStart = System.currentTimeMillis();
					}
				} else {
					brakes = true;
				}

				if(!brakes) {
					float t = MathHelper.clamp((float)(System.currentTimeMillis() - brakeStart) / 1000, 0, 1);
					brakeRotation = (1 - t) * 65;
				} else {
					brakeRotation = 65;
				}
			}

			for(int i = 0; i < 4; i++) {
				GlStateManager.pushMatrix();
				{

					GlStateManager.rotate(i * 90 - 45, 0, 1, 0);
					GlStateManager.translate(0.46194, 3.5, 0);
					GlStateManager.rotate(brakeRotation, 0, 0, 1);
					GlStateManager.translate(-0.46194, -3.5, 0);
					GlStateManager.rotate(45, 0, 1, 0);
					ResourceManager.drop_pod.renderPart("Airbrake0");

				}
				GlStateManager.popMatrix();
			}

		}
		GlStateManager.popMatrix();

		// Render legs
		GlStateManager.pushMatrix();
		{

			double legExtension = 1;
			if(state == RocketState.LAUNCHING) legExtension = 0;

			GlStateManager.translate(0, -legExtension * 0.5, 0);
			ResourceManager.drop_pod.renderPart("Legs");

		}
		GlStateManager.popMatrix();
	}

}
