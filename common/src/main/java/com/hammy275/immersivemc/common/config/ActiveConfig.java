package com.hammy275.immersivemc.common.config;

import com.hammy275.immersivemc.ImmersiveMC;
import com.hammy275.immersivemc.common.network.Network;
import com.hammy275.immersivemc.common.network.packet.ConfigSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public class ActiveConfig {

    // Synced values
    public static boolean useAnvilImmersion = false;
    public static boolean useBrewingImmersion = false;
    public static boolean useChestImmersion = false;
    public static boolean useCraftingImmersion = false;
    public static boolean useFurnaceImmersion = false;
    public static boolean useJukeboxImmersion = false;
    public static boolean useRangedGrab = false;
    public static boolean useButton = false;
    public static boolean useETableImmersion = false;
    public static boolean useCampfireImmersion = false;
    public static boolean useLever = false;
    public static boolean useBackpack = false;
    public static boolean useRepeaterImmersion = false;
    public static boolean useDoorImmersion = false;
    public static boolean useHoeImmersion = false;
    public static boolean canPet = false;
    public static boolean useArmorImmersion = false;
    public static boolean canFeedAnimals = false;
    public static boolean useShulkerImmersion = false;
    public static boolean canPetAnyLiving = false;
    public static boolean immersiveShield = false;
    public static int rangedGrabRange = 0;
    public static boolean useBeaconImmersion = false;
    public static boolean crouchBypassImmersion = false;

    // Non-synced values
    public static int backpackColor = 11901820;
    public static boolean leftHandedBackpack = false;
    public static boolean rightClickChest = false;
    public static boolean autoCenterFurnace = false;
    public static boolean autoCenterBrewing = false;
    public static boolean useLowDetailBackpack = false;
    public static boolean showPlacementGuide = true;
    public static PlacementMode placementMode = PlacementMode.PLACE_ONE;
    public static boolean spinCraftingOutput = true;
    public static boolean rightClickInVR = false;
    public static boolean resourcePack3dCompat = false;

    // For changing config values in-game
    public static FriendlyByteBuf serverCopy = null;

    // On a singleplayer world, the server and client share this value.
    // When set to true when the client-side changes a config value, the server sees
    // this as true, and reloads the config globally, so we can sync it to other
    // LAN players.
    public static boolean clientForceServerReloadForLAN = false;


    public static void loadConfigFromPacket(FriendlyByteBuf buffer) {
        int serverNetworkVersion = buffer.readInt();
        if (serverNetworkVersion != Network.PROTOCOL_VERSION) {
            Network.INSTANCE.sendToServer(ConfigSyncPacket.getKickMePacket());
        }
        int serverConfigVersion = buffer.readInt();
        if (serverConfigVersion != ImmersiveMCConfig.CONFIG_VERSION) { // Kick if we have a different config version
            Network.INSTANCE.sendToServer(ConfigSyncPacket.getKickMePacket());
        }
        serverCopy = new FriendlyByteBuf(buffer.copy()); // Store copy of server configuration
        serverCopy.retain();
        loadFromByteBuffer(buffer);
        buffer.release();
        ImmersiveMC.LOGGER.debug("Loaded config from network: \n" + asString());

    }

    private static void loadFromByteBuffer(FriendlyByteBuf buffer) {
        // We combine client config with server, so if a user doesn't want to use an immersion, they don't
        // even if a server is OK with it.
        loadConfigFromFile(true);
        useAnvilImmersion = buffer.readBoolean() && useAnvilImmersion;
        useBrewingImmersion = buffer.readBoolean() && useBrewingImmersion;
        useChestImmersion = buffer.readBoolean() && useChestImmersion;
        useCraftingImmersion = buffer.readBoolean() && useCraftingImmersion;
        useFurnaceImmersion = buffer.readBoolean() && useFurnaceImmersion;
        useJukeboxImmersion = buffer.readBoolean() && useJukeboxImmersion;
        useRangedGrab = buffer.readBoolean() && useRangedGrab;
        useButton = buffer.readBoolean() && useButton;
        useETableImmersion = buffer.readBoolean() && useETableImmersion;
        useCampfireImmersion = buffer.readBoolean() && useCampfireImmersion;
        useLever = buffer.readBoolean() && useLever;
        useBackpack = buffer.readBoolean() && useBackpack;
        useRepeaterImmersion = buffer.readBoolean() && useRepeaterImmersion;
        useDoorImmersion = buffer.readBoolean() && useDoorImmersion;
        useHoeImmersion = buffer.readBoolean() && useHoeImmersion;
        canPet = buffer.readBoolean() && canPet;
        useArmorImmersion = buffer.readBoolean() && useArmorImmersion;
        canFeedAnimals = buffer.readBoolean() && canFeedAnimals;
        useShulkerImmersion = buffer.readBoolean() && useShulkerImmersion;
        canPetAnyLiving = buffer.readBoolean() && canPetAnyLiving;
        immersiveShield = buffer.readBoolean() && immersiveShield;
        // Always use minimum value between client and server
        rangedGrabRange = Math.min(buffer.readInt(), rangedGrabRange);
        useBeaconImmersion = buffer.readBoolean() && useBeaconImmersion;
        crouchBypassImmersion = buffer.readBoolean() && crouchBypassImmersion;

    }

    public static void loadConfigFromFile() {
        loadConfigFromFile(false);
    }

    public static void loadConfigFromFile(boolean forceLoadServerSettings) {
        // Synced values (only loaded if we're not in a server, or we are the server)
        if (forceLoadServerSettings || Minecraft.getInstance().level == null) {
            useAnvilImmersion = ImmersiveMCConfig.useAnvilImmersion.get();
            useBrewingImmersion = ImmersiveMCConfig.useBrewingImmersion.get();
            useChestImmersion = ImmersiveMCConfig.useChestImmersion.get();
            useCraftingImmersion = ImmersiveMCConfig.useCraftingImmersion.get();
            useFurnaceImmersion = ImmersiveMCConfig.useFurnaceImmersion.get();
            useJukeboxImmersion = ImmersiveMCConfig.useJukeboxImmersion.get();
            useRangedGrab = ImmersiveMCConfig.useRangedGrab.get();
            useButton = ImmersiveMCConfig.useButton.get();
            useETableImmersion = ImmersiveMCConfig.useETableImmersion.get();
            useCampfireImmersion = ImmersiveMCConfig.useCampfireImmersion.get();
            useLever = ImmersiveMCConfig.useLever.get();
            useBackpack = ImmersiveMCConfig.useBackpack.get();
            useRepeaterImmersion = ImmersiveMCConfig.useRepeaterImmersion.get();
            useDoorImmersion = ImmersiveMCConfig.useDoorImmersion.get();
            useHoeImmersion = ImmersiveMCConfig.useHoeImmersion.get();
            canPet = ImmersiveMCConfig.canPet.get();
            useArmorImmersion = ImmersiveMCConfig.useArmorImmersion.get();
            canFeedAnimals = ImmersiveMCConfig.canFeedAnimals.get();
            useShulkerImmersion = ImmersiveMCConfig.useShulkerImmersion.get();
            canPetAnyLiving = ImmersiveMCConfig.canPetAnyLiving.get();
            immersiveShield = ImmersiveMCConfig.immersiveShield.get();
            rangedGrabRange = ImmersiveMCConfig.rangedGrabRange.get();
            useBeaconImmersion = ImmersiveMCConfig.useBeaconImmersion.get();
            crouchBypassImmersion = ImmersiveMCConfig.crouchBypassImmersion.get();
        } else {
            ImmersiveMC.LOGGER.debug("Not re-loading immersive options since we're in a world!");
        }

        // Non-synced values
        backpackColor = ImmersiveMCConfig.backpackColor.get();
        leftHandedBackpack = ImmersiveMCConfig.leftHandedBackpack.get();
        rightClickChest = ImmersiveMCConfig.rightClickChest.get();
        autoCenterFurnace = ImmersiveMCConfig.autoCenterFurnace.get();
        autoCenterBrewing = ImmersiveMCConfig.autoCenterBrewing.get();
        useLowDetailBackpack = ImmersiveMCConfig.useLowDetailBackpack.get();
        showPlacementGuide = ImmersiveMCConfig.showPlacementGuide.get();
        placementMode = PlacementMode.fromInt(ImmersiveMCConfig.itemPlacementMode.get());
        spinCraftingOutput = ImmersiveMCConfig.spinCraftingOutput.get();
        rightClickInVR = ImmersiveMCConfig.rightClickInVR.get();
        resourcePack3dCompat = ImmersiveMCConfig.resourcePack3dCompat.get();
        ImmersiveMC.LOGGER.debug("Loaded config from file: \n" + asString());
    }

    public static void loadOffConfig() {
        useAnvilImmersion = false;
        useBrewingImmersion = false;
        useChestImmersion = false;
        useCraftingImmersion = false;
        useFurnaceImmersion = false;
        useJukeboxImmersion = false;
        useRangedGrab = false;
        useButton = false;
        useETableImmersion = false;
        useCampfireImmersion = false;
        useLever = false;
        useBackpack = false;
        useRepeaterImmersion = false;
        useDoorImmersion = false;
        useHoeImmersion = false;
        canPet = false;
        useArmorImmersion = false;
        canFeedAnimals = false;
        useShulkerImmersion = false;
        canPetAnyLiving = false;
        immersiveShield = false;
        rangedGrabRange = 0;
        useBeaconImmersion = false;
        crouchBypassImmersion = false;
        ImmersiveMC.LOGGER.debug("Loaded 'disabled' config: \n" + asString());
    }

    public static FriendlyByteBuf encodeServerOnlyConfig(FriendlyByteBuf buffer) {
        buffer.writeBoolean(ActiveConfig.useButton).writeBoolean(ActiveConfig.useCampfireImmersion)
                .writeBoolean(ActiveConfig.useLever).writeBoolean(ActiveConfig.useRangedGrab)
                .writeBoolean(ActiveConfig.useDoorImmersion).writeBoolean(ActiveConfig.useHoeImmersion)
                .writeBoolean(ActiveConfig.canPet).writeBoolean(ActiveConfig.useArmorImmersion)
                .writeBoolean(ActiveConfig.canFeedAnimals).writeBoolean(ActiveConfig.canPetAnyLiving)
                .writeInt(ActiveConfig.rangedGrabRange).writeBoolean(ActiveConfig.crouchBypassImmersion);
        return buffer;
    }

    public static String asString() {
        String stringOut = "Use anvil immersion: " + useAnvilImmersion + "\n" +
                "Use brewing immersion: " + useBrewingImmersion + "\n" +
                "Use chest immersion: " + useChestImmersion + "\n" +
                "Use crafting immersion: " + useCraftingImmersion + "\n" +
                "Use furnace immersion: " + useFurnaceImmersion + "\n" +
                "Use jukebox immersion: " + useJukeboxImmersion + "\n" +
                "Use ranged grab: " + useRangedGrab + "\n" +
                "Use button: " + useButton + "\n" +
                "Use enchanting table: " + useETableImmersion + "\n" +
                "Use campfire immersion: " + useCampfireImmersion + "\n" +
                "Use lever: " + useLever + "\n" +
                "Use backpack: " + useBackpack + "\n" +
                "Backpack color: " + backpackColor + "\n" +
                "Left handed backpack: " + leftHandedBackpack + "\n" +
                "Use right click chest: " + rightClickChest + "\n" +
                "Use repeater immersion: " + useRepeaterImmersion + "\n" +
                "Auto-center furnace: " + autoCenterFurnace + "\n" +
                "Auto-center brewing: " + autoCenterBrewing + "\n" +
                "Use low detailed bag: " + useLowDetailBackpack + "\n" +
                "Show placement guide: " + showPlacementGuide + "\n" +
                "Placement mode: " + placementMode + "\n" +
                "Use door immersion: " + useDoorImmersion + "\n" +
                "Use hoe immersion: " + useHoeImmersion + "\n" +
                "Spin crafting output: " + spinCraftingOutput + "\n" +
                "Can pet: " + canPet + "\n" +
                "Use armor immersion: " + useArmorImmersion + "\n" +
                "Can feed animals: " + canFeedAnimals + "\n" +
                "Use Shulker Box Immersion: " + useShulkerImmersion + "\n" +
                "Can pet any living: " + canPetAnyLiving + "\n" +
                "Use immersive shield: " + immersiveShield + "\n" +
                "Ranged grab range: " + rangedGrabRange + "\n" +
                "Right click in VR: " + rightClickInVR + "\n" +
                "3D resource pack compatability: " + resourcePack3dCompat + "\n" +
                "Use beacon immersion: " + useBeaconImmersion;
        return stringOut;
    }

    public static void reloadAfterServer() {
        // Only call on client!
        if (serverCopy != null) {
            int oldIndex = serverCopy.readerIndex();
            loadFromByteBuffer(serverCopy);
            serverCopy.readerIndex(oldIndex);
            ImmersiveMC.LOGGER.debug("Reloaded config while in-game: \n" + asString());
            Network.INSTANCE.sendToServer(ConfigSyncPacket.getToServerConfigPacket()); // Also send our new config to server

            // If we're the host of an SP game, reload config server-side. This re-syncs configs
            // with all clients connected on a LAN world.
            ActiveConfig.clientForceServerReloadForLAN = true;
        }
    }
}
