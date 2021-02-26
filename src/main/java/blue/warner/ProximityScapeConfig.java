package net.runelite.client.plugins.proximityscape;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("proximityscape")
public interface ProximityScapeConfig extends Config {

    @ConfigItem(
            position = 0,
            keyName = "clientid",
            name = "ClientID",
            description = "Input your ID to link to the Discord bot."
    )
    default String ClientID(){
        return "";
    }

}

