package com.hbm.tileentity;

import com.google.errorprone.annotations.DoNotCall;
import com.hbm.lib.CapabilityContextProvider;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * Provides conditional inventory access based on the accessor's coordinates.
 *
 * @see CapabilityContextProvider
 * @see TileEntityMachineBase#getCapability
 * @deprecated This interface is obsolete and should not be implemented.
 * Its functionality has been replaced by the Forge Capability system combined with a custom context provider.
 * All logic from these methods should be moved into capability-aware overrides within the TileEntity class itself.
 *
 * <p><b>Porting Guide:</b></p>
 * <ol>
 *   <li>Remove {@code implements IConditionalInvAccess} from your TileEntity.</li>
 *   <li>In your TileEntity, override the new methods from {@link TileEntityMachineBase}:
 *     <ul>
 *       <li>{@code getAccessibleSlotsFromSide(EnumFacing side, BlockPos accessorPos)}</li>
 *       <li>{@code canInsertItem(int slot, ItemStack stack, EnumFacing side, BlockPos accessorPos)}</li>
 *       <li>{@code canExtractItem(int slot, ItemStack stack, int amount, EnumFacing side, BlockPos accessorPos)}</li>
 *     </ul>
 *   </li>
 *   <li>The {@code accessorPos} parameter is provided by {@link CapabilityContextProvider}
 *       and gives you the coordinates of the block that is requesting the access, restoring the original behavior.</li>
 * </ol>
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "1.12.2")
public interface IConditionalInvAccess {

    @Deprecated
    @DoNotCall
    boolean isItemValidForSlot(int x, int y, int z, int slot, ItemStack stack);

    @Deprecated
    @DoNotCall
    default boolean canInsertItem(int x, int y, int z, int slot, ItemStack stack, int side) {
        return isItemValidForSlot(x, y, z, slot, stack);
    }

    @Deprecated
    @DoNotCall
    boolean canExtractItem(int x, int y, int z, int slot, ItemStack stack, int side);

    @Deprecated
    @DoNotCall
    int[] getAccessibleSlotsFromSide(int x, int y, int z, int side);
}