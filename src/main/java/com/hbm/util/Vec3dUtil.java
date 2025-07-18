package com.hbm.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class Vec3dUtil {

  public static Vec3d rotateRoll(Vec3d vec, float roll) {
    float f = MathHelper.cos(roll);
    float f1 = MathHelper.sin(roll);
    double d0 = vec.x * (double) f + vec.y * (double) f1;
    double d1 = vec.y * (double) f - vec.x * (double) f1;
    double d2 = vec.z;
    return new Vec3d(d0, d1, d2);
  }

  public static Vec3d lerp(Vec3d vec, Vec3d other, double t) {
    double x = vec.x + (other.x - vec.x) * t;
    double y = vec.y + (other.y - vec.y) * t;
    double z = vec.z + (other.z - vec.z) * t;
    return new Vec3d(x, y, z);
  }

  public static Vec3i convertToVec3i(Vec3d vec) {
    return new Vec3i(vec.x, vec.y, vec.z);
  }
}
