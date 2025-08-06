package com.hbm.entity.projectile;

import com.hbm.util.Tuple;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public interface IBulletBase {
    public double prevX(); public double prevY(); public double prevZ();
    public void prevX(double d); public void prevY(double d); public void prevZ(double d);
    public List<Tuple.Pair<Vec3d, Double>> nodes();
}
