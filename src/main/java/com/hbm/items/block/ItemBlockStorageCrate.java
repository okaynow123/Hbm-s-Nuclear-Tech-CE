package com.hbm.items.block;

public class ItemBlockStorageCrate { //extends ItemBlockBase implements IGUIProvider {

    /*public ItemBlockStorageCrate(Block block) {
        super(block);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        // TODO
        if (!ServerConfig.CRATE_OPEN_HELD.get())
            return new ActionResult<>(EnumActionResult.PASS, stack);

        Block block = Block.getBlockFromItem(stack.getItem());
        if (block == ModBlocks.mass_storage)
            return new ActionResult<>(EnumActionResult.PASS, stack);

        if (!world.isRemote && stack.getCount() == 1) {
            NBTTagCompound nbt = stack.getTagCompound();

            if (nbt != null && nbt.hasKey("lock")) {
                for (ItemStack item : player.inventory.mainInventory) {

                    if (item.isEmpty()) continue;
                    if (!(item.getItem() instanceof ItemKey)) continue;
                    if (item.getTagCompound() == null) continue;

                    if (item.getTagCompound().getInteger("pins") == nbt.getInteger("lock")) {
                        // TODO
                        //TileEntityCrateBase.spawnSpiders(player, world, stack);
                        player.openGui(MainRegistry.instance, 0, world, 0, 0, 0);
                        break;
                    }
                }
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }

            //TileEntityCrateBase.spawnSpiders(player, world, stack);
            player.openGui(MainRegistry.instance, 0, world, 0, 0, 0);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty()) held = player.getHeldItemOffhand();

        Block block = Block.getBlockFromItem(held.getItem());
        if (block == ModBlocks.crate_iron) return new ContainerCrateIron(player.inventory, new InventoryCrate(player, held));
        if (block == ModBlocks.crate_steel) return new ContainerCrateSteel(player.inventory, new InventoryCrate(player, held));
        if (block == ModBlocks.crate_desh) return new ContainerCrateDesh(player.inventory, new InventoryCrate(player, held));
        if (block == ModBlocks.crate_tungsten) return new ContainerCrateTungsten(player.inventory, new InventoryCrate(player, held));
        if (block == ModBlocks.crate_template) return new ContainerCrateTemplate(player.inventory, new InventoryCrate(player, held));
        if (block == ModBlocks.safe) return new ContainerSafe(player.inventory, new InventoryCrate(player, held));
        throw new NullPointerException();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty()) held = player.getHeldItemOffhand();

        Block block = Block.getBlockFromItem(held.getItem());
        if (block == ModBlocks.crate_iron) return new GUICrateIron(player.inventory, new InventoryCrate(player, held));
        if (block == ModBlocks.crate_steel) return new GUICrateSteel(player.inventory, new InventoryCrate(player, held));
        if (block == ModBlocks.crate_desh) return new GUICrateDesh(player.inventory, new InventoryCrate(player, held));
        if (block == ModBlocks.crate_tungsten) return new GUICrateTungsten(player.inventory, new InventoryCrate(player, held));
        if (block == ModBlocks.crate_template) return new GUICrateTemplate(player.inventory, new InventoryCrate(player, held));
        if (block == ModBlocks.safe) return new GUISafe(player.inventory, new InventoryCrate(player, held));
        throw new NullPointerException();
    }

    public static class InventoryCrate extends ItemInventory {

        public InventoryCrate(EntityPlayer player, ItemStack crate) {
            super(player, crate, findCrateType(crate.getItem()).inventory.getSlots());
        }

        @Nonnull
        public static TileEntityCrateBase findCrateType(Item crate) {
            Block block = Block.getBlockFromItem(crate);
            if (block == ModBlocks.crate_iron) return new TileEntityCrateIron();
            if (block == ModBlocks.crate_steel) return new TileEntityCrateSteel();
            if (block == ModBlocks.crate_desh) return new TileEntityCrateDesh();
            if (block == ModBlocks.crate_tungsten) return new TileEntityCrateTungsten();
            if (block == ModBlocks.crate_template) return new TileEntityCrateTemplate();
            if (block == ModBlocks.safe) return new TileEntitySafe();
            throw new NullPointerException();
        }

        public void closeInventory(EntityPlayer player) {
            // Preserve other NBT and enforce size limit on close
            target.setTagCompound(checkNBT(target.getTagCompound()));
            this.player.inventoryContainer.detectAndSendChanges();
            player.world.playSound(null, player.posX, player.posY, player.posZ, HBMSoundHandler.crateClose, SoundCategory.BLOCKS, 1.0F, 0.8F);
        }
    }*/
}
