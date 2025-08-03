package com.hbm.entity.effect;

import com.hbm.interfaces.AutoRegister;
import net.minecraft.world.World;
@AutoRegister(name = "entity_vortex", trackingRange = 1000)
public class EntityVortex extends EntityBlackHole {

	public EntityVortex(World p_i1582_1_) {
		super(p_i1582_1_);
		this.ignoreFrustumCheck = true;
		this.isImmuneToFire = true;
	}

	public EntityVortex(World world, float size) {
		super(world);
		this.getDataManager().set(SIZE, size);
	}
	
	@Override
	public void onUpdate() {
		
		this.getDataManager().set(SIZE, this.getDataManager().get(SIZE) - 0.0025F);
		if(this.getDataManager().get(SIZE) <= 0) {
			this.setDead();
			return;
		}
		
		super.onUpdate();
	}
}
