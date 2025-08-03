package com.hbm.entity.effect;

import com.hbm.interfaces.AutoRegister;
import net.minecraft.world.World;
@AutoRegister(name = "entity_digamma_quasar", trackingRange = 1000)
public class EntityQuasar extends EntityBlackHole {

	public EntityQuasar(World world) {
		super(world);
		this.ignoreFrustumCheck = true;
		this.isImmuneToFire = true;
	}

	public EntityQuasar(World world, float size) {
		super(world);
		this.getDataManager().set(SIZE, size);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
	}
}
