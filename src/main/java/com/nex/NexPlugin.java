package com.nex;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.audio.AudioPlayer;

import net.runelite.api.NPC;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
        name = "Nex Swedish Voiceover",
        description = "Plays sounds when Nex says specific overhead lines",
        tags = {"nex", "boss", "audio", "swedish"}
)
public class NexPlugin extends Plugin
{
    // Name as it appears in-game
    private static final String NEX_NAME = "Nex";

    @Inject
    private NexConfig config;

    @Inject
    private AudioPlayer audioPlayer;

    @Provides
    NexConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(NexConfig.class);
    }

    @Override
    protected void startUp()
    {
        log.info("Nex Overhead Audio plugin started");
    }

    @Override
    protected void shutDown()
    {
        log.info("Nex Overhead Audio plugin stopped");
    }

    @Subscribe
    public void onOverheadTextChanged(OverheadTextChanged event)
    {
        if (!(event.getActor() instanceof NPC))
        {
            return;
        }

        NPC npc = (NPC) event.getActor();
        String name = npc.getName();
        if (name == null || !name.equalsIgnoreCase(NEX_NAME))
        {
            return;
        }

        String text = event.getOverheadText();
        if (text == null)
        {
            return;
        }

        log.debug("Nex said: {}", text);

        String soundFile = getSoundForDialogue(text);
        if (soundFile == null)
        {
            // Unknown/unhandled line
            return;
        }

        playSound(soundFile);
    }

    private void playSound(String resourceName)
    {
        float gainDb = getGainDb();

        try
        {
            // Files are in src/main/resources
            audioPlayer.play(NexPlugin.class, "/" + resourceName, gainDb);
        }
        catch (Exception e)
        {
            log.warn("Failed to play Nex sound: {}", resourceName, e);
        }
    }

    private float getGainDb()
    {
        int volPercent = config.volume(); // 0–100
        if (volPercent < 0)
        {
            volPercent = 0;
        }
        if (volPercent > 100)
        {
            volPercent = 100;
        }

        // Map 0–100% to roughly -60 dB .. 0 dB
        if (volPercent == 0)
        {
            // Effectively mute
            return -80.0f;
        }

        double volume = volPercent / 100.0; // 0.0–1.0
        double gain = -60.0 + 60.0 * volume; // -60 dB at 0%, 0 dB at 100%
        return (float) gain;
    }

    /**
     * Map the exact dialogue line to a unique sound file.
     * We normalize case and strip trailing . ! ? so minor punctuation differences don't break it.
     */
    private String getSoundForDialogue(String rawText)
    {
        if (rawText == null)
        {
            return null;
        }

        String t = rawText.trim().toLowerCase();

        // Strip trailing punctuation ., !, ? (only at the end)
        while (t.endsWith(".") || t.endsWith("!") || t.endsWith("?"))
        {
            t = t.substring(0, t.length() - 1).trim();
        }

        switch (t)
        {
            // Pre fight
            case "at last":
                return "nex_at_last.wav";

            case "fumus":
                return "nex_fumus.wav";

            case "umbra":
                return "nex_umbra.wav";

            case "cruor":
                return "nex_cruor.wav";

            case "glacies":
                return "nex_glacies.wav";


            // Smoke Phase
            case "fill my soul with smoke":
                return "nex_fill_my_soul_with_smoke.wav";

            case "let the virus flow through you":
                return "nex_let_the_virus_flow_through_you.wav";

            case "there is":
                return "nex_there_is.wav";

            case "fumus, don't fail me":
                return "nex_fumus_dont_fail_me.wav";


            // Shadow Phase
            case "darken my shadow":
                return "nex_darken_my_shadow.wav";

            case "fear the shadow":
                return "nex_fear_the_shadow.wav";

            case "embrace darkness":
                return "nex_embrace_darkness.wav";

            case "umbra, don't fail me":
                return "nex_umbra_dont_fail_me.wav";

            // Blood Phase
            case "flood my lungs with blood":
                return "nex_flood_my_lungs_with_blood.wav";

            case "a siphon will solve this":
                return "nex_a_siphon_will_solve_this.wav";

            case "i demand a blood sacrifice":
                return "nex_i_demand_a_blood_sacrifice.wav";

            case "cruor, don't fail me":
                return "nex_cruor_dont_fail_me.wav";

            // Ice Phase
            case "infuse me with the power of ice":
                return "nex_infuse_me_with_power_of_ice.wav";

            case "contain this":
                return "nex_contain_this.wav";

            case "die now, in a prison of ice":
                return "nex_die_now_in_a_prison_of_ice.wav";

            case "glacies, don't fail me":
                return "nex_glacies_don_fail_me.wav";

            // Zaros Phase
            case "now, the power of zaros":
                return "nex_now_the_power_of_zaros.wav";


            // Nex dying
            case "taste my wrath":
                return "nex_taste_my_wrath.wav";

            default:
                return null;
        }
    }
}
