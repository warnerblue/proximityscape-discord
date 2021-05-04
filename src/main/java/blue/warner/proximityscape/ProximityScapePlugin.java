package blue.warner.proximityscape;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@PluginDescriptor(
        name = "ProximityScape - Discord",
        description = "Proximity chat for RuneScape",
        tags = {"discord", "chat", "proximity", "chunk", "vc"}
)

public class ProximityScapePlugin extends Plugin {

    public Socket socket;

    @Inject
    private Client client;

    @Inject
    private ProximityScapeConfig config;

    private ScheduledExecutorService ses;

    @Override
    protected void startUp() {
         if(config.ClientID() == ""){
                client.addChatMessage(ChatMessageType.MODCHAT,"[ProximityScape]", "No ClientID specified.","");
                 return;
         } else {
            
        socket = null;
        ses = Executors.newSingleThreadScheduledExecutor();

        ses.scheduleAtFixedRate(() -> {
            if (socket == null && !config.ClientID().equals("")) {
                try {
                    socket = new Socket("143.198.107.197", 4999);
                    if(client.getGameState().equals(GameState.LOGGED_IN))
                    client.addChatMessage(ChatMessageType.MODCHAT,"[ProximityScape]", "Connected to Socket.","");
                } catch (IOException e) {
                    if(client.getGameState().equals(GameState.LOGGED_IN))
                    client.addChatMessage(ChatMessageType.MODCHAT,"[ProximityScape]", "Failed to connect to the Socket, report this to warnerBlue.","");;
                }
            }
            if (!config.ClientID().equals("") && client.getGameState().equals(GameState.LOGGED_IN)) {
                try {
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                    printWriter.println(config.ClientID() + ":" + client.getWorld() + ":" + client.getLocalPlayer().getWorldLocation().getRegionID());
                    printWriter.flush();
                } catch (Exception e) {
                }
            } else {
                try{
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                    printWriter.println(config.ClientID() + ":" + "LOGGED_OUT");
                    printWriter.flush();
                }catch (Exception e){

                }
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
         }
    }

    @Override
    protected void shutDown() {
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            printWriter.println("QUIT");
            printWriter.flush();
        } catch (Exception e) {
        }
        socket = null;
        if(ses != null){
                ses.shutdown();
        }
    }


    @Provides
    public ProximityScapeConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ProximityScapeConfig.class);
    }

}
