package com.hbm.blocks.generic;

import api.hbm.block.IToolable;
import com.hbm.blocks.BlockMulti;
import com.hbm.blocks.ILookOverlay;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.MetaBlock;
import com.hbm.util.I18nUtil;
import com.hbm.util.InventoryUtil;
import com.hbm.util.Tuple.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public class BlockToolConversion extends BlockMulti implements IToolable, ILookOverlay {

    public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 15);
    public static HashMap<Pair<ToolType, MetaBlock>, Pair<AStack[], MetaBlock>> conversions = new HashMap<>();
    private final List<String> names = new ArrayList<>();

    public BlockToolConversion(Material mat) {
        super(mat);
        this.setDefaultState(this.getBlockState().getBaseState().withProperty(VARIANT, 0));
    }

    public static ToolType quickLookup(ItemStack stack) {
        return ToolType.getType(stack);
    }

    public BlockToolConversion addVariant(String... name) {
        Collections.addAll(names, name);
        return this;
    }

    @Override
    public int getSubCount() {
        return names.size() + 1;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, rectify(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ, int meta,
                                            EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(VARIANT, rectify(meta));
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        int meta = stack.getItemDamage() - 1;
        if (meta == -1 || meta >= names.size()) {
            return super.getTranslationKey();
        }
        return super.getTranslationKey() + "." + names.get(meta);
    }

    @Override
    public boolean onScrew(World world, EntityPlayer player, int x, int y, int z,
                           EnumFacing side, float fX, float fY, float fZ, EnumHand hand, ToolType tool) {
        if (world.isRemote) return false;
        BlockPos pos = new BlockPos(x, y, z);

        IBlockState state = world.getBlockState(pos);
        int meta = getMetaFromState(state);

        Pair<AStack[], MetaBlock> result = conversions.get(new Pair<>(tool, new MetaBlock(this, meta)));

        if (result == null) return false;

        List<AStack> materials = new ArrayList<>(Arrays.asList(result.getKey()));

        if (materials.isEmpty() || InventoryUtil.doesPlayerHaveAStacks(player, materials, true)) {
            world.setBlockState(pos, result.getValue().block.getStateFromMeta(result.getValue().meta), 3);
            return true;
        }

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void printHook(Pre event, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        ItemStack held = Minecraft.getMinecraft().player.getHeldItemMainhand();
        ToolType tool = quickLookup(held);
        if (tool == null) return;

        IBlockState state = world.getBlockState(pos);
        int meta = getMetaFromState(state);

        Pair<AStack[], MetaBlock> result = conversions.get(new Pair<>(tool, new MetaBlock(this, meta)));

        if (result == null) return;

        List<String> text = new ArrayList<>();
        text.add(TextFormatting.GOLD + "Requires:");

        for (AStack stack : result.getKey()) {
            try {
                ItemStack display = stack.extractForCyclingDisplay(20);
                text.add("- " + display.getDisplayName() + " x" + display.getCount());
            } catch (Exception ex) {
                text.add(TextFormatting.RED + "- ERROR");
            }
        }

        ILookOverlay.printGeneric(event, I18nUtil.resolveKey(this.getTranslationKey(new ItemStack(this, 1, meta))), 0xffff00, 0x404000, text);
    }
}

