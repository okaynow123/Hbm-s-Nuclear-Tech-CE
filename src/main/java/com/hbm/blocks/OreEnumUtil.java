package com.hbm.blocks;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.hbm.items.ModItems.*;

public class OreEnumUtil {

    public static final BiFunction<Integer, Random, Integer> BASE2_RAND3_FORTUNE = (fortune, rand) -> 2 + rand.nextInt(3) * fortune;
    public static final BiFunction<Integer, Random, Integer> BASE1_RAND2_FORTUNE = (fortune, rand) -> 1 + rand.nextInt(2) * fortune;
    public static final BiFunction<Integer, Random, Integer> BASE1_RAND_3 = (fortune, rand) -> 1 + rand.nextInt(3);
    public static final BiFunction<Integer, Random, Integer> CONST1 = (fortune, rand) -> 1;
    public static final BiFunction<Integer, Random, Integer> VANILLA_FORTUNE = (fortune, rand) -> 1 + rand.nextInt(fortune + 1);

    public static final Function<Random, ItemStack> METEOR_TREASURE = rand -> switch (rand.nextInt(35)) {
        case 0 -> new ItemStack(coil_advanced_alloy);
        case 1 -> new ItemStack(plate_advanced_alloy);
        case 2 -> new ItemStack(powder_desh_mix);
        case 3 -> new ItemStack(ingot_desh);
        case 4 -> new ItemStack(battery_advanced);
        case 5 -> new ItemStack(battery_lithium_cell);
        case 6 -> new ItemStack(battery_advanced_cell);
        case 7 -> new ItemStack(nugget_schrabidium);
        case 8 -> new ItemStack(ingot_plutonium);
        case 9 -> new ItemStack(ingot_thorium_fuel);
        case 10 -> new ItemStack(ingot_u233);
        case 11 -> new ItemStack(turbine_tungsten);
        case 12 -> new ItemStack(ingot_dura_steel);
        case 13 -> new ItemStack(ingot_polymer);
        case 14 -> new ItemStack(ingot_tungsten);
        case 15 -> new ItemStack(ingot_combine_steel);
        case 16 -> new ItemStack(ingot_lanthanium);
        case 17 -> new ItemStack(ingot_actinium);
        case 18 -> new ItemStack(Item.getItemFromBlock(ModBlocks.block_meteor));
        case 19 -> new ItemStack(Item.getItemFromBlock(ModBlocks.fusion_heater));
        case 20 -> new ItemStack(Item.getItemFromBlock(ModBlocks.fusion_core));
        case 21 -> new ItemStack(Item.getItemFromBlock(ModBlocks.watz_element));
        case 22 -> new ItemStack(Item.getItemFromBlock(ModBlocks.ore_rare));
        case 23 -> new ItemStack(Item.getItemFromBlock(ModBlocks.fusion_conductor));
        case 24 -> new ItemStack(Item.getItemFromBlock(ModBlocks.reactor_computer));
        case 25 -> new ItemStack(Item.getItemFromBlock(ModBlocks.machine_diesel));
        case 26 -> new ItemStack(Item.getItemFromBlock(ModBlocks.machine_rtg_grey));
        case 27 -> new ItemStack(pellet_rtg);
        case 28 -> new ItemStack(pellet_rtg_weak);
        case 29 -> new ItemStack(rtg_unit);
        case 30 -> new ItemStack(gun_spark_ammo);
        case 31 -> new ItemStack(ammo_nuke);
        case 32 -> new ItemStack(ammo_mirv);
        case 33 -> new ItemStack(gun_defabricator_ammo);
        case 34 -> new ItemStack(gun_osipr_ammo2);
        default -> ItemStack.EMPTY;  // In case something goes wrong
    };


    public static enum OreEnum {

        COAL(new ItemStack(Items.COAL), VANILLA_FORTUNE),
        DIAMOND(new ItemStack(Items.DIAMOND), VANILLA_FORTUNE),
        EMERALD(new ItemStack(Items.EMERALD), VANILLA_FORTUNE),

        ASBESTOS(new ItemStack(ingot_asbestos), CONST1),
        SULFUR(new ItemStack(sulfur), BASE2_RAND3_FORTUNE),
        NITER(new ItemStack(niter), BASE1_RAND2_FORTUNE),
        FLUORITE(new ItemStack(fluorite), BASE2_RAND3_FORTUNE),
        METEORITE_FRAG(new ItemStack(fragment_meteorite), BASE1_RAND_3),
        METEORITE_TREASURE(METEOR_TREASURE, BASE1_RAND_3),
        COBALT(new ItemStack(fragment_cobalt), (fortune, rand) -> 4 + rand.nextInt(6)),
        COBALT_NETHER(new ItemStack(fragment_cobalt), (fortune, rand) -> 5 + rand.nextInt(8)),
        PHOSPHORUS_NETHER((rand) -> (rand.nextInt(10) == 0 ? new ItemStack(ingot_phosphorus) : new ItemStack(powder_fire)), VANILLA_FORTUNE),
        LIGNITE(new ItemStack(lignite), VANILLA_FORTUNE),
        RARE_EARTHS(new ItemStack(rare_earth_chunk), VANILLA_FORTUNE),
        BLOCK_METEOR((rand) -> (rand.nextInt(10) == 0 ? new ItemStack(plate_dalekanium) : new ItemStack(Item.getItemFromBlock(ModBlocks.block_meteor))), VANILLA_FORTUNE),
        CINNEBAR(new ItemStack(cinnebar), VANILLA_FORTUNE),
        COLTAN(new ItemStack(fragment_coltan), VANILLA_FORTUNE),

        CLUSTER_IRON(new ItemStack(crystal_iron), VANILLA_FORTUNE),
        CLUSTER_TITANIUM(new ItemStack(crystal_titanium), VANILLA_FORTUNE),
        CLUSTER_ALUMINIUM(new ItemStack(crystal_aluminium), VANILLA_FORTUNE),
        CLUSTER_COPPER(new ItemStack(crystal_copper), VANILLA_FORTUNE),

        ;


        public final Function<Random, ItemStack> dropFunction;
        private final BiFunction<Integer, Random, Integer> quantity;

        OreEnum(Function<Random, ItemStack> dropFunction, BiFunction<Integer, Random, Integer> quantityFunction) {
            this.dropFunction = dropFunction;
            this.quantity = quantityFunction;
        }


        OreEnum(ItemStack drop,  BiFunction<Integer, Random, Integer> quantity) {
            this((rand) -> drop, quantity);
        }
    }


}
