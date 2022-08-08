package net.blf02.immersivemc.client.immersive.info;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractTileEntityImmersiveInfo<T extends TileEntity> extends AbstractImmersiveInfo {

    protected T tileEntity;
    protected Vec3[] positions;
    public ItemStack[] items;
    public final int maxSlotIndex;
    protected final BlockPos pos;

    /**
     * Constructor
     * @param tileEntity Tile entity for immersion
     * @param ticksToExist Ticks this immersion should exist for after being hovered
     * @param maxSlotIndex Maximum slot number if this tile entity represents an inventory.
     *                     Set to the number of positions that need storing if no slots exist.
     */
    public AbstractTileEntityImmersiveInfo(T tileEntity, int ticksToExist, int maxSlotIndex) {
        super(ticksToExist);
        this.tileEntity = tileEntity;
        this.positions = new Vec3[maxSlotIndex+1];
        this.maxSlotIndex = maxSlotIndex;
        this.items = new ItemStack[maxSlotIndex+1];
        this.pos = this.tileEntity.getBlockPos();
    }

    public T getTileEntity() {
        return this.tileEntity;
    }

    @Override
    public Vec3 getPosition(int slot) {
        return positions[slot];
    }

    @Override
    public Vec3[] getAllPositions() {
        return positions;
    }

    @Override
    public void setPosition(int slot, Vec3 position) {
        positions[slot] = position;
    }

    @Override
    public boolean hasPositions() {
        return positions[0] != null;
    }

    @Override
    public boolean readyToRender() {
        return this.hasHitboxes() && this.hasPositions() && this.hasItems();
    }

    @Override
    public BlockPos getBlockPosition() {
        return this.pos;
    }

    public boolean hasItems() {
        return this.items[this.maxSlotIndex] != null;
    }
}
