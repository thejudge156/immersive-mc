package net.blf02.immersivemc.client.subscribe;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blf02.immersivemc.client.immersive.AbstractImmersive;
import net.blf02.immersivemc.client.immersive.Immersives;
import net.blf02.immersivemc.client.immersive.info.AbstractImmersiveInfo;
import net.blf02.immersivemc.common.vr.VRPluginVerify;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ConcurrentModificationException;

public class ClientRenderSubscriber {

    @SubscribeEvent
    public void onWorldRender(RenderLevelLastEvent event) {
        try {
            for (AbstractImmersive<? extends AbstractImmersiveInfo> singleton : Immersives.IMMERSIVES) {
                renderInfos(singleton, event.getPoseStack());
            }
        } catch (ConcurrentModificationException ignored) {
            // Skip rendering if the list is modified mid-render
            // It's fine, since we were only going to read it anyway!!
        }


    }

    protected <I extends AbstractImmersiveInfo> void renderInfos(AbstractImmersive<I> singleton,
                                                                 PoseStack stack) {
        try {
            for (I info : singleton.getTrackedObjects()) {
                singleton.doRender(info, stack, VRPluginVerify.clientInVR);
            }
        } catch (ConcurrentModificationException ignored) {
            // Skip rendering if the list is modified mid-render
            // It's fine, since we were only going to read it anyway!!
        }
        Minecraft.getInstance().renderBuffers().bufferSource().endBatch(); // Write all our buffers!

    }

}
