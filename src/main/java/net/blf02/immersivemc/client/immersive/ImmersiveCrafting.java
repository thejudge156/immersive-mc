package net.blf02.immersivemc.client.immersive;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.blf02.immersivemc.client.config.ClientConfig;
import net.blf02.immersivemc.client.immersive.info.CraftingInfo;
import net.blf02.immersivemc.client.storage.ClientStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ImmersiveCrafting extends AbstractImmersive<CraftingInfo> {

    public static final ImmersiveCrafting singleton = new ImmersiveCrafting();
    private final double spacing = 3d/16d;

    @Override
    protected void handleImmersion(CraftingInfo info, MatrixStack stack) {
        super.handleImmersion(info, stack);
        Direction forward = getForwardFromPlayer(Minecraft.getInstance().player);
        Vector3d pos = getTopCenterOfBlock(info.tablePos);
        Direction left = getLeftOfDirection(forward);

        Vector3d leftOffset = new Vector3d(
                left.getNormal().getX() * spacing, 0, left.getNormal().getZ() * spacing);
        Vector3d rightOffset = new Vector3d(
                left.getNormal().getX() * -spacing, 0, left.getNormal().getZ() * -spacing);

        Vector3d topOffset = new Vector3d(
                forward.getNormal().getX() * -spacing, 0, forward.getNormal().getZ() * -spacing);
        Vector3d botOffset = new Vector3d(
                forward.getNormal().getX() * spacing, 0, forward.getNormal().getZ() * spacing);


        List<ItemStack> slots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            slots.add(ClientStorage.craftingStorage.getItem(i));
        }

        Vector3d[] positions = new Vector3d[]{
                pos.add(leftOffset).add(topOffset), pos.add(topOffset), pos.add(rightOffset).add(topOffset),
                pos.add(leftOffset), pos, pos.add(rightOffset),
                pos.add(leftOffset).add(botOffset), pos.add(botOffset), pos.add(rightOffset).add(botOffset)
        };

        float itemSize = ClientConfig.itemScaleSizeCrafting / info.getCountdown();
        float hitboxSize = ClientConfig.itemScaleSizeCrafting / 3f;

        for (int i = 0; i < 9; i++) {
            info.setHitbox(i, createHitbox(positions[i], hitboxSize));
            renderItem(ClientStorage.craftingStorage.getItem(i), stack, positions[i],
                    itemSize, forward, Direction.UP, info.getHibtox(i));
        }

    }

    @Override
    public boolean shouldHandleImmersion(CraftingInfo info) {
        if (Minecraft.getInstance().player == null) return false;
        Direction forward = getForwardFromPlayer(Minecraft.getInstance().player);
        World level = Minecraft.getInstance().level;
        return level != null && level.getBlockState(info.tablePos.relative(forward)).isAir();
    }

    public void trackObject(BlockPos tablePos) {
        for (CraftingInfo info : getTrackedObjects()) {
            if (info.tablePos.equals(tablePos)) {
                info.setTicksLeft(ClientConfig.ticksToRenderCrafting);
                return;
            }
        }
        infos.add(new CraftingInfo(tablePos, ClientConfig.ticksToRenderCrafting));
    }
}