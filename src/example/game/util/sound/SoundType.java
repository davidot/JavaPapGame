package example.game.util.sound;

/**
 * Sound type to allow users to change different types of sounds
 * @author davidot
 */
public enum SoundType {

    /**
     * The music sound type, use this for music
     */
    MUSIC("musicVolume"),
    /**
     * The envoirement sound type, use this for envoirement
     */
    ENVIRONMENT("envVolume"),
    /**
     * The sound-effect sound type, use this for sound-effect
     */
    SFX("sfxVolume"),
    /**
     * The voice sound type, use this for any voicing
     */
    VOICE("voiceVolume"),
    /**
     * The mob sound type, use this for mobs
     */
    MOB("mobVolume"),;

    /**
     * Default volume value for the options
     */
    public static final int DEFAULTVOLUME = 100;

    /**
     * Maximum volume
     */
    public static final int MAX_VOLUME = 100;

    /**
     * Minimum volume
     */
    public static final int MIN_VOLUME = 0;

    private final String optionName;

    @SuppressWarnings("NonFinalFieldInEnum")
    private int volume = 100;

    @SuppressWarnings("NonFinalFieldInEnum")
    private boolean changed; //false

    SoundType(String optionName) {
        this.optionName = optionName;
    }

    /**
     * Initialize the volume of the sound
     */
    protected void init() {
        //set volume here
        volume = DEFAULTVOLUME;
    }

    public int getVolume() {
        return volume;
    }

    /**
     * Set the volume of this sound type
     * @param volume  the new volume
     */
    protected void setVolume(int volume) {
        this.volume = Math.max(MIN_VOLUME, Math.min(volume, MAX_VOLUME));
        changed = true;
    }

    /**
     * Get a SoundType from a String
     * @param string the string to try to parse
     *
     * @return the type found or null
     */
    public static SoundType fromString(String string) {
        for(SoundType type : SoundType.values()) {
            if(string.equalsIgnoreCase(type.name())) {
                return type;
            }
        }
        return null;
    }

}
