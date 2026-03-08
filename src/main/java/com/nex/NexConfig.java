package com.nex;

import net.runelite.client.config.*;

@ConfigGroup("nex-swedish-voiceover")
public interface NexConfig extends Config
{
    @Range(
            min = 0,
            max = 100
    )
    @Units(Units.PERCENT)
    @ConfigItem(
            keyName = "volume",
            name = "Volume",
            description = "Volume of Nex sound effects",
            position = 1
    )
    default int volume()
    {
        return 100; // default to 100%
    }
}
