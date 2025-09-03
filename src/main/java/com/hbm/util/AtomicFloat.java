package com.hbm.util;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static java.lang.Float.floatToRawIntBits;
import static java.lang.Float.intBitsToFloat;

/**
 * Basically copy-pasted Guava's {@link AtomicDouble} implementation.
 */
public class AtomicFloat extends Number implements Serializable {
    private static final long serialVersionUID = 0L;
    private static final AtomicIntegerFieldUpdater<AtomicFloat> updater = AtomicIntegerFieldUpdater.newUpdater(AtomicFloat.class, "value");
    private transient volatile int value;

    public AtomicFloat(float initialValue) {
        value = floatToRawIntBits(initialValue);
    }

    public AtomicFloat() {
        // assert floatToRawIntBits(0.0f) == 0;
    }

    public final float get() {
        return intBitsToFloat(value);
    }

    public final void set(float newValue) {
        value = floatToRawIntBits(newValue);
    }

    public final void lazySet(float newValue) {
        int next = floatToRawIntBits(newValue);
        updater.lazySet(this, next);
    }

    public final float getAndSet(float newValue) {
        int next = floatToRawIntBits(newValue);
        return intBitsToFloat(updater.getAndSet(this, next));
    }

    public final boolean compareAndSet(float expect, float update) {
        return updater.compareAndSet(this, floatToRawIntBits(expect), floatToRawIntBits(update));
    }

    public final boolean weakCompareAndSet(float expect, float update) {
        return updater.weakCompareAndSet(this, floatToRawIntBits(expect), floatToRawIntBits(update));
    }

    @CanIgnoreReturnValue
    public final float getAndAdd(float delta) {
        while (true) {
            int current = value;
            float currentVal = intBitsToFloat(current);
            float nextVal = currentVal + delta;
            int next = floatToRawIntBits(nextVal);
            if (updater.compareAndSet(this, current, next)) {
                return currentVal;
            }
        }
    }

    @CanIgnoreReturnValue
    public final float addAndGet(float delta) {
        while (true) {
            int current = value;
            float currentVal = intBitsToFloat(current);
            float nextVal = currentVal + delta;
            int next = floatToRawIntBits(nextVal);
            if (updater.compareAndSet(this, current, next)) {
                return nextVal;
            }
        }
    }

    @Override
    public String toString() {
        return Float.toString(get());
    }

    @Override
    public int intValue() {
        return (int) get();
    }

    @Override
    public long longValue() {
        return (long) get();
    }

    @Override
    public float floatValue() {
        return get();
    }

    @Override
    public double doubleValue() {
        return get();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        s.writeFloat(get());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();

        set(s.readFloat());
    }

    @CanIgnoreReturnValue
    public final float getAndUpdate(FloatUnaryOperator updateFunction) {
        while (true) {
            int current = value;
            float currentVal = intBitsToFloat(current);
            float nextVal = updateFunction.applyAsFloat(currentVal);
            int next = floatToRawIntBits(nextVal);
            if (updater.compareAndSet(this, current, next)) {
                return currentVal;
            }
        }
    }

    @CanIgnoreReturnValue
    public final float updateAndGet(FloatUnaryOperator updateFunction) {
        while (true) {
            int current = value;
            float currentVal = intBitsToFloat(current);
            float nextVal = updateFunction.applyAsFloat(currentVal);
            int next = floatToRawIntBits(nextVal);
            if (updater.compareAndSet(this, current, next)) {
                return nextVal;
            }
        }
    }

    @FunctionalInterface
    public interface FloatUnaryOperator {
        float applyAsFloat(float value);

        @Contract(pure = true)
        default FloatUnaryOperator compose(@NotNull FloatUnaryOperator before) {
            Objects.requireNonNull(before);
            return v -> applyAsFloat(before.applyAsFloat(v));
        }

        @Contract(pure = true)
        default FloatUnaryOperator andThen(@NotNull FloatUnaryOperator after) {
            Objects.requireNonNull(after);
            return v -> after.applyAsFloat(applyAsFloat(v));
        }

        static FloatUnaryOperator identity() {
            return v -> v;
        }
    }
}
