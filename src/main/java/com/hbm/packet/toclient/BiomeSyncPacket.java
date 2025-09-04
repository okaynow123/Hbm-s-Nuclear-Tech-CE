package com.hbm.packet.toclient;

import com.hbm.util.Compat;
import com.hbm.world.WorldUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BiomeSyncPacket implements IMessage {

    private int chunkX;
    private int chunkZ;
    private byte blockX;
    private byte blockZ;
    private int biome;
    private int[] biomeArray;

    public BiomeSyncPacket() {
    }

    public BiomeSyncPacket(int chunkX, int chunkZ, byte[] biomeArray) {
        this(chunkX, chunkZ, bytesToInts(biomeArray));
    }

    public BiomeSyncPacket(int chunkX, int chunkZ, int[] biomeArray) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.biomeArray = biomeArray;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkZ);

        if (this.biomeArray == null) {
            buf.writeBoolean(false);
            buf.writeInt(this.biome);
            buf.writeByte(this.blockX);
            buf.writeByte(this.blockZ);
        } else {
            buf.writeBoolean(true);
            for (int i = 0; i < 256; i++) {
                buf.writeInt(this.biomeArray[i]);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.chunkX = buf.readInt();
        this.chunkZ = buf.readInt();

        if (!buf.readBoolean()) {
            this.biome = buf.readInt();
            this.blockX = buf.readByte();
            this.blockZ = buf.readByte();
        } else {
            this.biomeArray = new int[256];
            for (int i = 0; i < 256; i++) {
                this.biomeArray[i] = buf.readInt();
            }
        }
    }

    private static int[] bytesToInts(byte[] byteArray) {
        int size = byteArray.length;
        int[] intArray = new int[size];
        for (int index = 0; index < size; index++) {
            intArray[index] = (byteArray[index] & 0xFF);
        }
        return intArray;
    }

    public static class Handler implements IMessageHandler<BiomeSyncPacket, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(BiomeSyncPacket m, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                WorldClient world = Minecraft.getMinecraft().world;
                if (world == null) return;
                if (!world.getChunkProvider().isChunkGeneratedAt(m.chunkX, m.chunkZ)) return;
                Chunk chunk = world.getChunk(m.chunkX, m.chunkZ);
                chunk.markDirty();

                final int baseX = m.chunkX << 4;
                final int baseZ = m.chunkZ << 4;

                if (Compat.isIDExtensionModLoaded()) {
                    int[] target = WorldUtil.getIntBiomeArray(chunk);
                    if (target == null) return;
                    if (m.biomeArray == null) {
                        int idx = ((m.blockZ & 15) << 4) | (m.blockX & 15);
                        target[idx] = m.biome & 0xFFFF;
                        world.markBlockRangeForRenderUpdate(baseX, 0, baseZ, baseX + 15, 255, baseZ + 15);
                    } else {
                        for (int i = 0; i < 256; i++) {
                            target[i] = m.biomeArray[i] & 0xFFFF;
                        }
                        world.markBlockRangeForRenderUpdate(baseX, 0, baseZ, baseX + 15, 255, baseZ + 15);
                    }
                } else {
                    byte[] target = chunk.getBiomeArray();
                    if (target.length != 256) return;
                    if (m.biomeArray == null) {
                        int idx = ((m.blockZ & 15) << 4) | (m.blockX & 15);
                        target[idx] = (byte) (m.biome & 0xFF);
                        world.markBlockRangeForRenderUpdate(baseX, 0, baseZ, baseX + 15, 255, baseZ + 15);
                    } else {
                        for (int i = 0; i < 256; i++) {
                            target[i] = (byte) (m.biomeArray[i] & 0xFF);
                        }
                        world.markBlockRangeForRenderUpdate(baseX, 0, baseZ, baseX + 15, 255, baseZ + 15);
                    }
                }
            });
            return null;
        }
    }
}
