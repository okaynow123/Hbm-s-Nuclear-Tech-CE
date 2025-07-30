package com.hbm.handler.guncfg;

import com.hbm.handler.BulletConfigSyncingUtil;
import com.hbm.handler.BulletConfiguration;
import com.hbm.handler.GunConfiguration;
import com.hbm.items.ModItems;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.render.misc.RenderScreenOverlay.Crosshair;

import java.util.ArrayList;

public class GunGrenadeFactory {

	public static BulletConfiguration getGrenadeConfig() {
		
		BulletConfiguration bullet = BulletConfigFactory.standardGrenadeConfig();
		
		bullet.ammo = ModItems.ammo_grenade;
		bullet.velocity = 2.0F;
		bullet.dmgMin = 10;
		bullet.dmgMax = 15;
		bullet.wear = 10;
		bullet.trail = 0;
		
		return bullet;
	}
	
	public static BulletConfiguration getGrenadeHEConfig() {
		
		BulletConfiguration bullet = BulletConfigFactory.standardGrenadeConfig();
		
		bullet.ammo = ModItems.ammo_grenade_he;
		bullet.velocity = 2.0F;
		bullet.dmgMin = 20;
		bullet.dmgMax = 15;
		bullet.wear = 15;
		bullet.explosive = 5.0F;
		bullet.trail = 1;
		
		return bullet;
	}
	
	public static BulletConfiguration getGrenadeIncendirayConfig() {
		
		BulletConfiguration bullet = BulletConfigFactory.standardGrenadeConfig();
		
		bullet.ammo = ModItems.ammo_grenade_incendiary;
		bullet.velocity = 2.0F;
		bullet.dmgMin = 15;
		bullet.dmgMax = 15;
		bullet.wear = 15;
		bullet.trail = 0;
		bullet.incendiary = 2;
		
		return bullet;
	}
	
	public static BulletConfiguration getGrenadeChlorineConfig() {
		
		BulletConfiguration bullet = BulletConfigFactory.standardGrenadeConfig();
		
		bullet.ammo = ModItems.ammo_grenade_toxic;
		bullet.velocity = 2.0F;
		bullet.dmgMin = 10;
		bullet.dmgMax = 15;
		bullet.wear = 10;
		bullet.trail = 3;
		bullet.explosive = 0;
		bullet.chlorine = 50;
		
		return bullet;
	}
	
	public static BulletConfiguration getGrenadeSleekConfig() {
		
		BulletConfiguration bullet = BulletConfigFactory.standardGrenadeConfig();
		
		bullet.ammo = ModItems.ammo_grenade_sleek;
		bullet.velocity = 2.0F;
		bullet.dmgMin = 10;
		bullet.dmgMax = 15;
		bullet.wear = 10;
		bullet.trail = 4;
		bullet.explosive = 7.5F;
		bullet.jolt = 6.5D;
		
		return bullet;
	}
	
	public static BulletConfiguration getGrenadeConcussionConfig() {
		
		BulletConfiguration bullet = BulletConfigFactory.standardGrenadeConfig();
		
		bullet.ammo = ModItems.ammo_grenade_concussion;
		bullet.velocity = 2.0F;
		bullet.dmgMin = 15;
		bullet.dmgMax = 20;
		bullet.blockDamage = false;
		bullet.explosive = 10.0F;
		bullet.trail = 3;
		
		return bullet;
	}
	
	public static BulletConfiguration getGrenadeFinnedConfig() {
		
		BulletConfiguration bullet = getGrenadeConfig();
		
		bullet.ammo = ModItems.ammo_grenade_finned;
		bullet.gravity = 0.02;
		bullet.explosive = 1.5F;
		bullet.trail = 5;
		
		return bullet;
	}
	
	public static BulletConfiguration getGrenadeNuclearConfig() {
		
		BulletConfiguration bullet = getGrenadeConfig();
		
		bullet.ammo = ModItems.ammo_grenade_nuclear;
		bullet.velocity = 4;
		bullet.explosive = 0.0F;
		bullet.nuke = 15;
		
		return bullet;
	}
	
	public static BulletConfiguration getGrenadePhosphorusConfig() {
		
		BulletConfiguration bullet = BulletConfigFactory.standardGrenadeConfig();
		
		bullet.ammo = ModItems.ammo_grenade_phosphorus;
		bullet.velocity = 2.0F;
		bullet.dmgMin = 15;
		bullet.dmgMax = 15;
		bullet.wear = 15;
		bullet.trail = 0;
		bullet.incendiary = 2;
		
		bullet.bImpact = BulletConfigFactory.getPhosphorousEffect(10, 60 * 20, 100, 0.5D, 1F);
		
		return bullet;
	}
	
	public static BulletConfiguration getGrenadeTracerConfig() {

		BulletConfiguration bullet = BulletConfigFactory.standardGrenadeConfig();

		bullet.ammo = ModItems.ammo_grenade_tracer;
		bullet.velocity = 2.0F;
		bullet.wear = 10;
		bullet.explosive = 0F;
		bullet.trail = 5;
		bullet.vPFX = "bluedust";

		return bullet;
	}

	public static BulletConfiguration getGrenadeKampfConfig() {

		BulletConfiguration bullet = BulletConfigFactory.standardRocketConfig();

		bullet.ammo = ModItems.ammo_grenade_kampf;
		bullet.spread = 0.0F;
		bullet.gravity = 0.0D;
		bullet.wear = 15;
		bullet.explosive = 3.5F;
		bullet.style = BulletConfiguration.STYLE_GRENADE;
		bullet.trail = 4;
		bullet.vPFX = "smoke";

		return bullet;
	}
}
