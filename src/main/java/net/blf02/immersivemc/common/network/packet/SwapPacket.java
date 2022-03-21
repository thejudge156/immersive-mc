package net.blf02.immersivemc.common.network.packet;

import net.blf02.immersivemc.common.swap.FurnaceSwap;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SwapPacket {

    public final BlockPos block;
    public final int slot;
    public final Hand hand;

    public SwapPacket(BlockPos block, int slot, Hand hand) {
        this.block = block;
        this.slot = slot;
        this.hand = hand;
    }

    public static void encode(SwapPacket packet, PacketBuffer buffer) {
        buffer.writeBlockPos(packet.block);
        buffer.writeInt(packet.slot);
        buffer.writeInt(packet.hand == Hand.MAIN_HAND ? 0 : 1);
    }

    public static SwapPacket decode(PacketBuffer buffer) {
        return new SwapPacket(buffer.readBlockPos(), buffer.readInt(),
                buffer.readInt() == 0 ? Hand.MAIN_HAND : Hand.OFF_HAND);
    }

    public static void handle(final SwapPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null && player.level.isLoaded(message.block)) {
                TileEntity tileEnt = player.level.getBlockEntity(message.block);
                if (tileEnt instanceof AbstractFurnaceTileEntity) {
                    AbstractFurnaceTileEntity furnace = (AbstractFurnaceTileEntity) tileEnt;
                    FurnaceSwap.handleSwap(furnace, player, message.hand, message.slot);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }


}