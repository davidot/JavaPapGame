package example.game.util.sound;

import example.game.Game;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This is the class where all the methods used for sounds are located. <p> {@link Game} has one
 * instance of the SoundHandler available via {@link Game#getSoundHandler()} </p>
 * @author davidot
 */
public class SoundHandler {

    /**
     * Default value for the hearing range of relative SoundClips
     */
    public static final double PLAYER_HEARING_RANGE = 15.0;
    private static final String COULD_NOT_FIND_SOUND = "Could not find sound{";
    private static final String COULD_NOT_FIND_SOUND_END = "}";

    private static Map<String, SoundData> sounds = new HashMap<String, SoundData>();
    private List<SoundClip> clips = new ArrayList<SoundClip>();

    /**
     * Creates the SoundHandler which reads and writes to the given options
     */
    public SoundHandler() {
        for(SoundType type : SoundType.values()) {
            type.init();
        }
    }

    /**
     * Called every tick to check volumes and remove ended clips
     */
    public void tick() {
        for(Iterator<SoundClip> iterator = clips.iterator(); iterator.hasNext(); ) {
            SoundClip clip = iterator.next();
            if(clip.isDone()) {
                clip.close();
                iterator.remove();
            }
        }

//        for(SoundType type : SoundType.values()) {
//            if(type.changed()) {
//                for(SoundClip clip : clips) {
//                    if(clip.getType().equals(type)) {
//                        clip.setVolume(type.getVolume());
//                    }
//                }
//            }
//        }

//        if(clips.size() > 0) {
//            System.out.println("Current amount of clips:" + clips.size());
//        }
    }

    /**
     * Play the sound with that name and default type once
     * @param name    the name of the sound
     * @param inLevel whether it should react to the level methods
     */
    public void play(String name, boolean inLevel) {
        playLoop(name, 0, inLevel);
    }

    /**
     * Run a sound multiple times 0 = once, 1 = loop once
     * @param name    the name of the sound
     * @param times   the amount of times the sound should loop
     * @param inLevel whether it should react to the level methods
     */
    public void playLoop(String name, int times, boolean inLevel) {
        if(sounds.containsKey(name)) {
            sounds.get(name).play(this, times, inLevel);
        } else {
            System.out.println(COULD_NOT_FIND_SOUND + name + COULD_NOT_FIND_SOUND_END);
        }
    }

    /**
     * Play a sound with a forced SoundType
     * @param name    the name of the sound
     * @param type    the forced type of the sound
     * @param inLevel whether
     */
    public void playForceType(String name, SoundType type, boolean inLevel) {
        playForceTypeLoop(name, type, 0, inLevel);
    }

    /**
     * Play a sound with a forced SoundType a certain amount of times
     * @param name    the name of the sound
     * @param type    the forced type of the sound
     * @param times   the amount of times the sound should loop
     * @param inLevel whether
     */
    public void playForceTypeLoop(String name, SoundType type, int times, boolean inLevel) {
        if(sounds.containsKey(name)) {
            add(new SoundClip(sounds.get(name), type, times, inLevel));
        } else {
            System.out.println(COULD_NOT_FIND_SOUND + name + COULD_NOT_FIND_SOUND_END);
        }
    }

    /**
     * Add a sound to play it later
     * @param name  the name of the sound
     * @param sound the SoundData of the sound
     *
     * @see #addSound(InputStream, String, SoundType) to not have to create the object yourself
     */
    public static void addSound(String name, SoundData sound) {
        if(sounds.containsKey(name)) {
            System.out.println("WARING: Sound being overwritten by {name = " + name +
                    COULD_NOT_FIND_SOUND_END);
        }
        sounds.put(name, sound);
    }

    /**
     * Check if a sound under a certain name has already been loaded
     * @param name the name of the searched sound
     *
     * @return true if a sound under the name given is already loaded
     */
    public static boolean hasSound(String name) {
        return sounds.containsKey(name);
    }

    /**
     * Add a sound with the given parameters and play it later
     * @param input the InputStream to read the sound from
     * @param name  the name of the sound
     * @param type  the type of the sound
     */
    public static void addSound(InputStream input, String name, SoundType type) {
        if(sounds.containsKey(name)) {
            System.out.println("WARING: Sound being overwritten by {name = " + name +
                    COULD_NOT_FIND_SOUND_END);
        }
        sounds.put(name, new SoundData(new BufferedInputStream(input), type));
    }


    void add(SoundClip soundClip) {
        clips.add(soundClip);
    }

    /**
     * Will stop all sounds originating from the level
     */
    public void stopLevelSounds() {
        for(Iterator<SoundClip> iterator = clips.iterator(); iterator.hasNext(); ) {
            SoundClip clip = iterator.next();
            if(clip.isLevelSound()) {
                clip.close();
                iterator.remove();
            }
        }
    }

    //set the state of the LevelSounds
    private void changeStateLevelSounds(boolean playing) {
        for(SoundClip clip : clips) {
            if(clip.isLevelSound()) {
                if(playing) {
                    clip.play();
                } else {
                    clip.stop();
                }
            }
        }
    }

    /**
     * Will pause all sounds originating from the level
     */
    public void pauseLevelSounds() {
        changeStateLevelSounds(false);
    }

    /**
     * Will play all sounds originating from the level
     */
    public void unpauseLevelSounds() {
        changeStateLevelSounds(true);
    }

    /**
     * Set the volume for a certain SoundType
     * @param type     the type to change
     * @param newValue the new volume
     */
    public void changeVolume(SoundType type, int newValue) {
        type.setVolume(newValue);
        for(SoundClip clip : clips) {
            if(clip.getType() == type) {
                clip.setVolume(type.getVolume());
            }
        }
    }

}
