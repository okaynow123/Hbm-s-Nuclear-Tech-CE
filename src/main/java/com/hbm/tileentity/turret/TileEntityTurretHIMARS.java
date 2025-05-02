package com.hbm.tileentity.turret;

import com.hbm.inventory.container.ContainerTurretBase;
import com.hbm.inventory.gui.GUITurretHIMARS;
import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.IGUIProvider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class TileEntityTurretHIMARS extends TileEntityTurretBaseArtillery implements IGUIProvider {

  public enum FiringMode {
    AUTO,
    MANUAL
  }

  public FiringMode mode = FiringMode.AUTO;

  public int typeLoaded = -1;
  public int ammo = 0;
  public float crane;
  public float lastCrane;

  @Override
  @SideOnly(Side.CLIENT)
  public List<ItemStack> getAmmoTypesForDisplay() {

    if (ammoStacks != null) return ammoStacks;

    ammoStacks = new ArrayList<>();

    NonNullList<ItemStack> list = NonNullList.create();
    ModItems.ammo_himars.getSubItems(MainRegistry.weaponTab, list);
    this.ammoStacks.addAll(list);

    return ammoStacks;
  }

  @Override
  protected List<Integer> getAmmoList() {
    return new ArrayList<>();
  }

  @Override
  public String getName() {
    return "container.turretHIMARS";
  }

  @Override
  public long getMaxPower() {
    return 1_000_000;
  }

  @Override
  public boolean doLOSCheck() {
    return false;
  }

  @Override
  public void updateFiringTick() {}

  public boolean hasAmmo() {
    return this.typeLoaded >= 0 && this.ammo > 0;
  }

  @Override
  public void handleButtonPacket(int value, int meta) {
    if (meta == 5) {
      int nextOrdinal = (mode.ordinal() + 1) % FiringMode.values().length;
      this.mode = FiringMode.values()[nextOrdinal];
      this.tPos = null;
      this.targetQueue.clear();

    } else {
      super.handleButtonPacket(value, meta);
    }
  }

  @Override
  public NBTTagCompound writePacket() {
    NBTTagCompound nbt = super.writePacket();
    nbt.setShort("mode", (short) this.mode.ordinal());
    nbt.setInteger("type", this.typeLoaded);
    nbt.setInteger("ammo", this.ammo);
    return nbt;
  }

  @Override
  public void networkUnpack(NBTTagCompound nbt) {
    super.networkUnpack(nbt);
    this.mode = FiringMode.values()[nbt.getShort("mode")];
    this.typeLoaded = nbt.getShort("type");
    this.ammo = nbt.getInteger("ammo");
  }

  @Override
  public void readFromNBT(NBTTagCompound nbt) {
    super.readFromNBT(nbt);
    this.mode = FiringMode.values()[nbt.getShort("mode")];
    this.typeLoaded = nbt.getShort("type");
    this.ammo = nbt.getInteger("ammo");
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
    super.writeToNBT(nbt);
    nbt.setShort("mode", (short) this.mode.ordinal());
    nbt.setInteger("type", this.typeLoaded);
    nbt.setInteger("ammo", this.ammo);
    return nbt;
  }

  @Override
  public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new ContainerTurretBase(player.inventory, this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new GUITurretHIMARS(player.inventory, this);
  }
}
