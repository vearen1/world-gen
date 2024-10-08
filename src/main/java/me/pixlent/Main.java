package me.pixlent;

import me.pixlent.commands.GamemodeCommand;
import me.pixlent.commands.PoseCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntitySpawnEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;

public class Main {
    public static void main(String[] args) {
        // Initialization
        System.setProperty("minestom.chunk-view-distance", "16");
        MinecraftServer minecraftServer = MinecraftServer.init();

        // Create the instance
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        // Set the ChunkGenerator
        instanceContainer.setChunkSupplier(LightingChunk::new);
        instanceContainer.setGenerator(new TerrainGenerator());
        instanceContainer.setTimeRate(0);

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 70, 0));
        });

        globalEventHandler.addListener(EntitySpawnEvent.class, event -> {
            if (!(event.getEntity() instanceof Player player)) {
                return;
            }

            player.setGameMode(GameMode.SPECTATOR);
            player.setPermissionLevel(4);
        });

        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new GamemodeCommand());
        commandManager.register(new PoseCommand());

        MojangAuth.init();

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);
    }
}