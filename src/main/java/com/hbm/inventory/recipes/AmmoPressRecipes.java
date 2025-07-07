package com.hbm.inventory.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.inventory.RecipesCommon.*;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ItemEnums.EnumCasingType;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.sedna.factory.GunFactory.EnumAmmo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hbm.inventory.OreDictManager.*;

public class AmmoPressRecipes extends SerializableRecipe {
  public static List<AmmoPressRecipe> recipes = new ArrayList<>();

  @Override
  public void registerDefaults() {

    OreDictStack lead = new OreDictStack(PB.ingot());
    OreDictStack nugget = new OreDictStack(PB.nugget());
    OreDictStack flechette = new OreDictStack(PB.bolt());
    OreDictStack steel = new OreDictStack(STEEL.ingot());
    OreDictStack wSteel = new OreDictStack(WEAPONSTEEL.ingot());
    OreDictStack copper = new OreDictStack(CU.ingot());
    OreDictStack plastic = new OreDictStack(ANY_PLASTIC.ingot());
    OreDictStack uranium = new OreDictStack(U238.ingot());
    OreDictStack ferro = new OreDictStack(FERRO.ingot());
    OreDictStack nb = new OreDictStack(NB.ingot());
    OreDictStack smokeless = new OreDictStack(ANY_SMOKELESS.dust());
    OreDictStack he = new OreDictStack(ANY_HIGHEXPLOSIVE.ingot());
    OreDictStack wp = new OreDictStack(P_WHITE.ingot());
    OreDictStack rp = new OreDictStack(P_RED.dust());
    OreDictStack pipe = new OreDictStack(STEEL.pipe());
    ComparableStack smokeful = new ComparableStack(Items.GUNPOWDER);
    ComparableStack rocket = new ComparableStack(ModItems.rocket_fuel);
    ComparableStack cSmall =
        new ComparableStack(ModItems.casing, 1, EnumCasingType.SMALL.ordinal());
    ComparableStack cBig = new ComparableStack(ModItems.casing, 1, EnumCasingType.LARGE.ordinal());
    ComparableStack sSmall =
        new ComparableStack(ModItems.casing, 1, EnumCasingType.SMALL_STEEL.ordinal());
    ComparableStack sBig =
        new ComparableStack(ModItems.casing, 1, EnumCasingType.LARGE_STEEL.ordinal());
    ComparableStack bpShell =
        new ComparableStack(ModItems.casing, 1, EnumCasingType.SHOTSHELL.ordinal());
    ComparableStack pShell =
        new ComparableStack(ModItems.casing, 1, EnumCasingType.BUCKSHOT.ordinal());
    ComparableStack sShell =
        new ComparableStack(ModItems.casing, 1, EnumCasingType.BUCKSHOT_ADVANCED.ordinal());

    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.M357_BP.ordinal(), 16),
            null,
            lead.copy(),
            null,
            null,
            smokeful,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.M357_SP.ordinal(), 8),
            null,
            lead,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.M357_FMJ.ordinal(), 8),
            null,
            steel,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.M357_JHP.ordinal(), 8),
            plastic,
            copper,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.M357_AP.ordinal(), 8),
            null,
            wSteel,
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.M357_EXPRESS.ordinal(), 8),
            null,
            steel,
            null,
            null,
            smokeless.copy(),
            null,
            null,
            cSmall,
            null));

    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.M44_BP.ordinal(), 12),
            null,
            lead.copy(),
            null,
            null,
            smokeful,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.M44_SP.ordinal(), 6),
            null,
            lead,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.M44_FMJ.ordinal(), 6),
            null,
            steel,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.M44_JHP.ordinal(), 6),
            plastic,
            copper,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.M44_AP.ordinal(), 6),
            null,
            wSteel,
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.M44_EXPRESS.ordinal(), 6),
            null,
            steel,
            null,
            null,
            smokeless.copy(),
            null,
            null,
            cSmall,
            null));

    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.P22_SP.ordinal(), 24),
            null,
            lead,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.P22_FMJ.ordinal(), 24),
            null,
            steel,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.P22_JHP.ordinal(), 24),
            plastic,
            copper,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.P22_AP.ordinal(), 24),
            null,
            wSteel,
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sSmall,
            null));

    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.P9_SP.ordinal(), 12),
            null,
            lead,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.P9_FMJ.ordinal(), 12),
            null,
            steel,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.P9_JHP.ordinal(), 12),
            plastic,
            copper,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.P9_AP.ordinal(), 12),
            null,
            wSteel,
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sSmall,
            null));

    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.P45_SP.ordinal(), 8),
            null,
            lead,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.P45_FMJ.ordinal(), 8),
            null,
            steel,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.P45_JHP.ordinal(), 8),
            plastic,
            copper,
            null,
            null,
            smokeless,
            null,
            null,
            cSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.P45_AP.ordinal(), 8),
            null,
            wSteel,
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sSmall,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.P45_DU.ordinal(), 8),
            null,
            uranium,
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sSmall,
            null));

    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.R556_SP.ordinal(), 16),
            null,
            lead.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            cSmall.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.R556_FMJ.ordinal(), 16),
            null,
            steel.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            cSmall.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.R556_JHP.ordinal(), 16),
            plastic,
            copper.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            cSmall.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.R556_AP.ordinal(), 16),
            null,
            wSteel.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sSmall.copy(),
            null));

    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.R762_SP.ordinal(), 12),
            null,
            lead.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            cSmall.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.R762_FMJ.ordinal(), 12),
            null,
            steel.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            cSmall.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.R762_JHP.ordinal(), 12),
            plastic,
            copper.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            cSmall.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.R762_AP.ordinal(), 12),
            null,
            wSteel.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sSmall.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.R762_DU.ordinal(), 12),
            null,
            uranium.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sSmall.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.R762_HE.ordinal(), 12),
            he,
            ferro,
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sSmall.copy(),
            null));

    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.BMG50_SP.ordinal(), 12),
            null,
            lead.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            cBig,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.BMG50_FMJ.ordinal(), 12),
            null,
            steel.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            cBig,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.BMG50_JHP.ordinal(), 12),
            plastic,
            copper.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            cBig,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.BMG50_AP.ordinal(), 12),
            null,
            wSteel.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sBig,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.BMG50_DU.ordinal(), 12),
            null,
            uranium.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sBig,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.BMG50_HE.ordinal(), 12),
            he,
            ferro,
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sBig,
            null));

    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G12_BP.ordinal(), 6),
            null,
            nugget.copy(),
            null,
            null,
            smokeful,
            null,
            null,
            bpShell,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G12_BP_MAGNUM.ordinal(), 6),
            null,
            nugget.copy(),
            null,
            null,
            smokeful,
            null,
            null,
            bpShell,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G12_BP_SLUG.ordinal(), 6),
            null,
            lead,
            null,
            null,
            smokeful,
            null,
            null,
            bpShell,
            null));

    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G12.ordinal(), 6),
            null,
            nugget.copy(),
            null,
            null,
            smokeless,
            null,
            null,
            pShell,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G12_SLUG.ordinal(), 6),
            null,
            lead,
            null,
            null,
            smokeless,
            null,
            null,
            pShell,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G12_FLECHETTE.ordinal(), 6),
            null,
            flechette.copy(),
            null,
            null,
            smokeless,
            null,
            null,
            pShell,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G12_MAGNUM.ordinal(), 6),
            null,
            nugget.copy(),
            null,
            null,
            smokeless,
            null,
            null,
            sShell,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G12_EXPLOSIVE.ordinal(), 6),
            null,
            he,
            null,
            null,
            smokeless,
            null,
            null,
            sShell,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G12_PHOSPHORUS.ordinal(), 6),
            null,
            wp,
            null,
            null,
            smokeless,
            null,
            null,
            sShell,
            null));

    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G10.ordinal(), 4),
            null,
            nugget.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sShell,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G10_SHRAPNEL.ordinal(), 4),
            plastic,
            nugget.copy(),
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sShell,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G10_DU.ordinal(), 4),
            null,
            uranium,
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sShell,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G10_SLUG.ordinal(), 4),
            null,
            lead,
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sShell,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G10_EXPLOSIVE.ordinal(), 4),
            he,
            ferro,
            null,
            null,
            smokeless.copy(),
            null,
            null,
            sShell,
            null));

    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G26_FLARE.ordinal(), 4),
            null,
            rp,
            null,
            null,
            smokeless,
            null,
            null,
            cBig,
            null));

    ComparableStack dyn = new ComparableStack(ModItems.ball_dynamite);
    OreDictStack coplate = new OreDictStack(CU.plate());
    OreDictStack diesel = new OreDictStack(Fluids.DIESEL.getDict(1_000));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G40_HE.ordinal(), 4),
            null,
            dyn,
            null,
            null,
            smokeless,
            null,
            null,
            cBig,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G40_HEAT.ordinal(), 4),
            coplate,
            he,
            null,
            null,
            smokeless,
            null,
            null,
            cBig,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G40_DEMO.ordinal(), 4),
            null,
            he.copy(),
            null,
            null,
            smokeless,
            null,
            null,
            cBig,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G40_INC.ordinal(), 4),
            diesel,
            dyn,
            null,
            null,
            smokeless,
            null,
            null,
            cBig,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.G40_PHOSPHORUS.ordinal(), 4),
            wp,
            he,
            null,
            null,
            smokeless,
            null,
            null,
            cBig,
            null));

    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.ROCKET_HE.ordinal(), 2),
            null,
            dyn,
            null,
            null,
            cBig,
            null,
            null,
            smokeless.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.ROCKET_HE.ordinal(), 2),
            null,
            dyn,
            null,
            null,
            cBig,
            null,
            null,
            rocket,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.ROCKET_HEAT.ordinal(), 2),
            coplate,
            he,
            null,
            null,
            cBig,
            null,
            null,
            smokeless.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.ROCKET_HEAT.ordinal(), 2),
            coplate,
            he,
            null,
            null,
            cBig,
            null,
            null,
            rocket,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.ROCKET_DEMO.ordinal(), 2),
            null,
            he.copy(),
            null,
            null,
            cBig,
            null,
            null,
            smokeless.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.ROCKET_DEMO.ordinal(), 2),
            null,
            he.copy(),
            null,
            null,
            cBig,
            null,
            null,
            rocket,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.ROCKET_INC.ordinal(), 2),
            diesel,
            dyn,
            null,
            null,
            cBig,
            null,
            null,
            smokeless.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.ROCKET_INC.ordinal(), 2),
            diesel,
            dyn,
            null,
            null,
            cBig,
            null,
            null,
            rocket,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.ROCKET_PHOSPHORUS.ordinal(), 2),
            wp,
            he,
            null,
            null,
            cBig,
            null,
            null,
            smokeless.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.ROCKET_PHOSPHORUS.ordinal(), 2),
            wp,
            he,
            null,
            null,
            cBig,
            null,
            null,
            rocket,
            null));

    OreDictStack sPlate = new OreDictStack(STEEL.plate());
    ComparableStack napalm = new ComparableStack(ModItems.canister_napalm);
    OreDictStack gas = new OreDictStack(Fluids.GAS.getDict(1000));
    OreDictStack bf = new OreDictStack(Fluids.BALEFIRE.getDict(1000));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.FLAME_DIESEL.ordinal(), 1),
            null,
            sPlate,
            null,
            null,
            diesel,
            null,
            null,
            sPlate,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.FLAME_NAPALM.ordinal(), 1),
            null,
            sPlate,
            null,
            null,
            napalm,
            null,
            null,
            sPlate,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.FLAME_GAS.ordinal(), 1),
            null,
            sPlate,
            null,
            null,
            gas,
            null,
            null,
            sPlate,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.FLAME_BALEFIRE.ordinal(), 1),
            null,
            sPlate,
            null,
            null,
            bf,
            null,
            null,
            sPlate,
            null));

    OreDictStack silicon = new OreDictStack(SI.billet());
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.CAPACITOR.ordinal(), 4),
            null,
            plastic,
            null,
            null,
            silicon.copy(),
            null,
            null,
            plastic,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.CAPACITOR_OVERCHARGE.ordinal(), 4),
            null,
            plastic,
            null,
            null,
            silicon.copy(),
            null,
            null,
            plastic,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.CAPACITOR_IR.ordinal(), 4),
            null,
            plastic,
            null,
            null,
            nb,
            null,
            null,
            plastic,
            null));

    OreDictStack lPlate = new OreDictStack(PB.plate());
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.TAU_URANIUM.ordinal(), 16),
            null,
            lPlate,
            null,
            null,
            uranium,
            null,
            null,
            lPlate,
            null));

    OreDictStack tungsten = new OreDictStack(W.ingot());
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.COIL_TUNGSTEN.ordinal(), 4),
            null,
            null,
            null,
            null,
            tungsten,
            null,
            null,
            null,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.COIL_FERROURANIUM.ordinal(), 4),
            null,
            null,
            null,
            null,
            ferro,
            null,
            null,
            null,
            null));

    ComparableStack shell = new ComparableStack(ModItems.assembly_nuke);
    ComparableStack tatb = new ComparableStack(ModItems.ball_tatb);
    OreDictStack plutonium = new OreDictStack(PU239.nugget());
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.NUKE_STANDARD.ordinal(), 1),
            null,
            plutonium,
            null,
            null,
            shell,
            null,
            null,
            null,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.NUKE_DEMO.ordinal(), 1),
            null,
            plutonium.copy(),
            null,
            null,
            shell,
            null,
            null,
            null,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.NUKE_HIGH.ordinal(), 1),
            null,
            plutonium.copy(),
            null,
            null,
            shell,
            null,
            null,
            null,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.NUKE_TOTS.ordinal(), 1),
            null,
            plutonium.copy(),
            null,
            null,
            tatb.copy(),
            null,
            null,
            sPlate.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.NUKE_HIVE.ordinal(), 1),
            null,
            he.copy(),
            null,
            null,
            sBig.copy(),
            null,
            null,
            sPlate.copy(),
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.NUKE_BALEFIRE.ordinal(), 1),
            null,
            new ComparableStack(ModItems.egg_balefire_shard),
            null,
            null,
            shell,
            null,
            null,
            null,
            null));

    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.CT_HOOK.ordinal(), 16),
            null,
            steel,
            null,
            null,
            pipe,
            null,
            null,
            smokeless,
            null));
    recipes.add(
        new AmmoPressRecipe(
            new ItemStack(ModItems.ammo_standard, EnumAmmo.CT_MORTAR.ordinal(), 4),
            null,
            he.copy(),
            null,
            null,
            pipe,
            null,
            null,
            smokeless,
            null));
  }

  public static HashMap<Object, Object> getRecipes() {
    HashMap<Object, Object> recipes = new HashMap<>();

    for (AmmoPressRecipe recipe : AmmoPressRecipes.recipes) {
      List<AStack> inputs = new ArrayList<>();
      for (AStack stack : recipe.input) if (stack != null) inputs.add(stack);
      recipes.put(inputs.toArray(new AStack[0]), recipe.output.copy());
    }

    return recipes;
  }

  @Override
  public String getFileName() {
    return "hbmAmmoPress.json";
  }

  @Override
  public String getComment() {
    return "Input array describes slots from left to right, top to bottom. Make sure the input array is exactly 9 elements long, empty slots are represented by null.";
  }

  @Override
  public Object getRecipeObject() {
    return recipes;
  }

  @Override
  public void deleteRecipes() {
    recipes.clear();
  }

  @Override
  public void readRecipe(JsonElement recipe) {
    JsonObject obj = (JsonObject) recipe;

    ItemStack output = readItemStack(obj.get("output").getAsJsonArray());
    JsonArray inputArray = obj.get("input").getAsJsonArray();
    AStack[] input = new AStack[9];

    for (int i = 0; i < 9; i++) {
      JsonElement element = inputArray.get(i);
      if (element.isJsonNull()) {
        input[i] = null;
      } else {
        input[i] = readAStack(element.getAsJsonArray());
      }
    }

    recipes.add(new AmmoPressRecipe(output, input));
  }

  @Override
  public void writeRecipe(Object recipe, JsonWriter writer) throws IOException {
    AmmoPressRecipe rec = (AmmoPressRecipe) recipe;

    writer.name("output");
    writeItemStack(rec.output, writer);

    writer.name("input").beginArray();
    for (int i = 0; i < rec.input.length; i++) {
      if (rec.input[i] == null) {
        writer.nullValue();
      } else {
        writeAStack(rec.input[i], writer);
      }
    }
    writer.endArray();
  }

  public static class AmmoPressRecipe {
    public ItemStack output;
    public AStack[] input;

    public AmmoPressRecipe(ItemStack output, AStack... input) {
      this.output = output;
      this.input = input;
    }
  }
}
