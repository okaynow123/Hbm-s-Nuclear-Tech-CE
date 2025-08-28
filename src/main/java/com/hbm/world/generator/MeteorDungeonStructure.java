package com.hbm.world.generator;

import com.hbm.world.phased.AbstractPhasedStructure;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MeteorDungeonStructure extends AbstractPhasedStructure {
    private final CellularDungeon dungeon;
    private final int y;

    public MeteorDungeonStructure(CellularDungeon dungeon, int y) {
        this.dungeon = dungeon;
        this.y = y;
    }

    @Override
    protected boolean isCacheable() {
        return false;
    }

    @Override
    protected void buildStructure(@NotNull LegacyBuilder builder, @NotNull Random rand) {
        CellularDungeonFactory.meteor.generate(builder, 0, 0, 0, rand);
    }

    @Override
    public List<@NotNull BlockPos> getValidationPoints(@NotNull BlockPos origin) {
        int halfX = (dungeon.dimX * dungeon.width) / 2;
        int halfZ = (dungeon.dimZ * dungeon.width) / 2;
        return Arrays.asList(
                new BlockPos(origin.getX() - halfX, y, origin.getZ() - halfZ),
                new BlockPos(origin.getX() + halfX, y, origin.getZ() - halfZ),
                new BlockPos(origin.getX() - halfX, y, origin.getZ() + halfZ),
                new BlockPos(origin.getX() + halfX, y, origin.getZ() + halfZ)
        );
    }
}
