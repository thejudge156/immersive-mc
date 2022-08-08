package net.blf02.immersivemc.client.tracker;

import net.blf02.immersivemc.common.config.ActiveConfig;
import net.blf02.immersivemc.common.network.Network;
import net.blf02.immersivemc.common.network.packet.GrabItemPacket;
import net.blf02.immersivemc.common.tracker.AbstractTracker;
import net.blf02.immersivemc.common.util.Util;
import net.blf02.immersivemc.common.vr.VRPlugin;
import net.blf02.immersivemc.common.vr.VRPluginVerify;
import net.blf02.vrapi.api.data.IVRData;
import net.blf02.vrapi.api.data.IVRPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.Player;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class RangedGrabTrackerClient extends AbstractTracker {

    public static final double threshold = 0.1;

    protected IVRData last = null;
    protected ItemEntity selected = null;

    public RangedGrabTrackerClient() {
        ClientTrackerInit.trackers.add(this);
    }

    @Override
    protected void tick(Player player) {
        IVRPlayer vrPlayer = VRPlugin.API.getVRPlayer(player);
        IVRData controller = vrPlayer.getController0();

        double dist = Minecraft.getInstance().gameMode.getPickRange();

        if (last != null) {
            if (Minecraft.getInstance().options.keyAttack.isDown()) {
                if (controller.position().y - last.position().y > threshold
                        && selected != null) {
                    Network.INSTANCE.sendToServer(new GrabItemPacket(selected));
                    selected = null;
                }
            } else {
                selected = null;

                Vec3 start = controller.position();
                Vec3 viewVec = controller.getLookAngle();
                Vec3 end = start.add(viewVec.x * dist, viewVec.y * dist,
                        viewVec.z * dist);

                List<Entity> ents = player.level.getEntities(player, player.getBoundingBox().inflate(10),
                        (entity -> entity instanceof ItemEntity && Util.canPickUpItem((ItemEntity) entity, player)));
                List<AABB> hitboxes = new LinkedList<>();
                for (Entity ent : ents) {
                    hitboxes.add(ent.getBoundingBox().inflate(1d/3d));
                }
                Optional<Integer> result = Util.rayTraceClosest(start, end, hitboxes.toArray(new AABB[0]));

                if (result.isPresent()) {
                    selected = (ItemEntity) ents.get(result.get());
                }
            }
        }

        if (selected != null) {
            Vec3 pos = selected.position().add(0, 0.2, 0);
            selected.level.addParticle(new RedstoneParticleData(0, 1, 1, 1),
                    pos.x, pos.y, pos.z, 0.01, 0.01, 0.01);
        }

        last = controller;
    }

    @Override
    protected boolean shouldTick(Player player) {
        return VRPluginVerify.clientInVR && Minecraft.getInstance().gameMode != null
                && ActiveConfig.useRangedGrab && VRPlugin.API.apiActive(player);
    }
}
