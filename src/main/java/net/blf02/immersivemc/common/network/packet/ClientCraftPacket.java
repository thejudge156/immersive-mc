package net.blf02.immersivemc.common.network.packet;

import net.blf02.immersivemc.common.config.ActiveConfig;
import net.blf02.immersivemc.common.network.Network;
import net.blf02.immersivemc.common.network.NetworkClientHandlers;
import net.blf02.immersivemc.common.network.NetworkUtil;
import net.blf02.immersivemc.server.swap.Swap;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

@Deprecated
public class ClientCraftPacket {

    protected final boolean isAskingForRecipe;
    protected final ItemStack[] inv;
    protected final BlockPos tablePos;

    protected final ItemStack resItem;
    protected final boolean is2x2;

    public ClientCraftPacket(ItemStack[] inv, BlockPos tablePos, boolean retrieveRecipe) {
        this.inv = inv;
        this.tablePos = tablePos;
        this.isAskingForRecipe = retrieveRecipe;
        this.resItem = null;
        this.is2x2 = false;
    }

    protected ClientCraftPacket(ItemStack resItem, boolean is2x2) {
        this.resItem = resItem;
        this.is2x2 = is2x2;
        this.isAskingForRecipe = false;
        this.inv = null;
        this.tablePos = null;
    }

    public static void encode(ClientCraftPacket packet, PacketBuffer buffer) {
        buffer.writeBoolean(packet.resItem == null);
        if (packet.resItem == null) {
            buffer.writeInt(packet.inv.length);
            for (int i = 0; i < packet.inv.length; i++) {
                buffer.writeItem(packet.inv[i]);
            }
            buffer.writeBlockPos(packet.tablePos);
            buffer.writeBoolean(packet.isAskingForRecipe);
        } else {
            buffer.writeItem(packet.resItem);
            buffer.writeBoolean(packet.is2x2);
        }

    }

    public static ClientCraftPacket decode(PacketBuffer buffer) {
        if (buffer.readBoolean()) {
            int numItems = buffer.readInt();
            ItemStack[] inv = new ItemStack[numItems];
            for (int i = 0; i < numItems; i++) {
                inv[i] = buffer.readItem();
            }
            return new ClientCraftPacket(inv, buffer.readBlockPos(), buffer.readBoolean());
        } else {
            return new ClientCraftPacket(buffer.readItem(), buffer.readBoolean());
        }


    }

    public static void handle(final ClientCraftPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (!ActiveConfig.useCraftingImmersion) return;
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null && NetworkUtil.safeToRun(message.tablePos, player)) {
                if (message.isAskingForRecipe) {
                    ICraftingRecipe recipe = Swap.getRecipe(player, message.inv);
                    ItemStack result = recipe == null ? ItemStack.EMPTY : recipe.getResultItem();
                    Network.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                            new ClientCraftPacket(result, message.inv.length == 4));
                } else {
                    Swap.handleDoCraft(player, message.inv, message.tablePos);
                }
            } else if (player == null) {
                if (message.is2x2) {
                    NetworkClientHandlers.setBackpackOutput(message.resItem);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}