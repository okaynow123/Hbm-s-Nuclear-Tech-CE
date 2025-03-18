package com.hbm.blocks;

import com.hbm.items.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

public class OreEnumUtil {

    public static final BiFunction<Integer, Random, Integer> BASE2_RAND3_FORTUNE = (fortune, rand) -> 2 + rand.nextInt(3) * fortune;
    public static final BiFunction<Integer, Random, Integer> BASE1_RAND2_FORTUNE = (fortune, rand) -> 1 + rand.nextInt(2) * fortune;
    public static final BiFunction<Integer, Random, Integer> BASE1_RAND_3 = (fortune, rand) -> 1 + rand.nextInt(3);
    public static final BiFunction<Integer, Random, Integer> CONST1 = (fortune, rand) -> 1;
    public static final BiFunction<Integer, Random, Integer> VANILLA_FORTUNE = (fortune, rand) -> 1 + rand.nextInt(fortune + 1);

    public static final Function<Random, ItemStack> METEOR_TREASURE = rand -> switch (rand.nextInt(35)) {
        case 0 -> new ItemStack(ModItems.coil_advanced_alloy);
        case 1 -> new ItemStack(ModItems.plate_advanced_alloy);
        case 2 -> new ItemStack(ModItems.powder_desh_mix);
        case 3 -> new ItemStack(ModItems.ingot_desh);
        case 4 -> new ItemStack(ModItems.battery_advanced);
        case 5 -> new ItemStack(ModItems.battery_lithium_cell);
        case 6 -> new ItemStack(ModItems.battery_advanced_cell);
        case 7 -> new ItemStack(ModItems.nugget_schrabidium);
        case 8 -> new ItemStack(ModItems.ingot_plutonium);
        case 9 -> new ItemStack(ModItems.ingot_thorium_fuel);
        case 10 -> new ItemStack(ModItems.ingot_u233);
        case 11 -> new ItemStack(ModItems.turbine_tungsten);
        case 12 -> new ItemStack(ModItems.ingot_dura_steel);
        case 13 -> new ItemStack(ModItems.ingot_polymer);
        case 14 -> new ItemStack(ModItems.ingot_tungsten);
        case 15 -> new ItemStack(ModItems.ingot_combine_steel);
        case 16 -> new ItemStack(ModItems.ingot_lanthanium);
        case 17 -> new ItemStack(ModItems.ingot_actinium);
        case 18 -> new ItemStack(Item.getItemFromBlock(ModBlocks.block_meteor));
        case 19 -> new ItemStack(Item.getItemFromBlock(ModBlocks.fusion_heater));
        case 20 -> new ItemStack(Item.getItemFromBlock(ModBlocks.fusion_core));
        case 21 -> new ItemStack(Item.getItemFromBlock(ModBlocks.watz_element));
        case 22 -> new ItemStack(Item.getItemFromBlock(ModBlocks.ore_rare));
        case 23 -> new ItemStack(Item.getItemFromBlock(ModBlocks.fusion_conductor));
        case 24 -> new ItemStack(Item.getItemFromBlock(ModBlocks.reactor_computer));
        case 25 -> new ItemStack(Item.getItemFromBlock(ModBlocks.machine_diesel));
        case 26 -> new ItemStack(Item.getItemFromBlock(ModBlocks.machine_rtg_grey));
        case 27 -> new ItemStack(ModItems.pellet_rtg);
        case 28 -> new ItemStack(ModItems.pellet_rtg_weak);
        case 29 -> new ItemStack(ModItems.rtg_unit);
        case 30 -> new ItemStack(ModItems.gun_spark_ammo);
        case 31 -> new ItemStack(ModItems.ammo_nuke);
        case 32 -> new ItemStack(ModItems.ammo_mirv);
        case 33 -> new ItemStack(ModItems.gun_defabricator_ammo);
        case 34 -> new ItemStack(ModItems.gun_osipr_ammo2);
        default -> ItemStack.EMPTY;  // In case something goes wrong
    };


    public static enum OreEnum {

        ASBESTOS(new ItemStack(ModItems.ingot_asbestos), CONST1),
        SULFUR(new ItemStack(ModItems.sulfur), BASE2_RAND3_FORTUNE),
        NITER(new ItemStack(ModItems.niter), BASE1_RAND2_FORTUNE),
        FLUORITE(new ItemStack(ModItems.fluorite), BASE2_RAND3_FORTUNE),
        METEORITE_FRAG(new ItemStack(ModItems.fragment_meteorite), BASE1_RAND_3),
        METEORITE_TREASURE(METEOR_TREASURE, BASE1_RAND_3),
        COBALT(new ItemStack(ModItems.fragment_cobalt), (fortune, rand) -> 4 + rand.nextInt(6)),
        COBALT_NETHER(new ItemStack(ModItems.fragment_cobalt), (fortune, rand) -> 5 + rand.nextInt(8)),
        PHOSPHORUS_NETHER((rand) -> (rand.nextInt(10) == 0 ? new ItemStack(ModItems.ingot_phosphorus) : new ItemStack(ModItems.powder_fire)), VANILLA_FORTUNE ),
        LIGNITE(new ItemStack(ModItems.lignite), VANILLA_FORTUNE)
        ;


        public final Function<Random, ItemStack> dropFunction;
        private final BiFunction<Integer, Random, Integer> quantity;

        OreEnum(Function<Random, ItemStack> dropFunction, BiFunction<Integer, Random, Integer> quantityFunction) {
            this.dropFunction = dropFunction;
            this.quantity = quantityFunction;
        }


        OreEnum(ItemStack drop, @org.jetbrains.annotations.Nullable BiFunction<Integer, Random, Integer> quantity) {
            this((rand) -> drop, quantity);
        }
    }


}
