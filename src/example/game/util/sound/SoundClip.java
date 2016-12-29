package example.game.util.sound;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A SoundClip is a currently playing audio, you can specify the volume and pause and resume it, it
 * is loaded from SoundData
 * @author davidot
 */
public class SoundClip implements Runnable {

    private final AtomicBoolean inited = new AtomicBoolean(false);

    private static ExecutorService executors = Executors.newFixedThreadPool(3);

    private final SoundType type;
    private final SoundData soundData;
    private final int times;
    private final boolean inLevel;
    private boolean useVolume; //false
    private FloatControl volumeControl;
    private Clip clip = null;

    static {
        //mabye do something else i'm not sure if this is the right way
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                onClose();
            }
        }));
    }


    /**
     * Create a SoundClip from the SoundData given, with the type given which will play (times + 1)
     * times
     * @param soundData the SoundData for this clip
     * @param type      the type to check the volume of
     * @param times     the amount of times it will loop
     * @param inLevel   whether it should react to the in level methods
     */
    public SoundClip(SoundData soundData, SoundType type, int times, boolean inLevel) {
        this.type = type;
        this.soundData = soundData;
        if(times < 0 && times != Clip.LOOP_CONTINUOUSLY) {
            throw new IllegalArgumentException("Time has to be at least zero");
        }
        this.times = times;
        this.inLevel = inLevel;
        executors.submit(this);
    }

    /**
     * Don't call this method, because it might load the sound twice which can lock up the game
     */
    @Override
    public void run() {
        if(!inited.get()) {
            if(clip != null) {
                return; //this means it has already been called
            }
            try {
                clip = AudioSystem.getClip();
            } catch(LineUnavailableException e) {
                e.printStackTrace();
            }
            if(clip == null) {
                throw new IllegalAccessError("Could not find AudioSystem.getClip() Shutting down");
            }
            try {
                byte[] data = soundData.getData();
                clip.open(soundData.getFormat(), data, 0, data.length);
                clip.setFramePosition(0);
                clip.loop(times);
                if(clip.isControlSupported(FloatControl.Type.VOLUME)) {
                    volumeControl = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
                    //System.out.println("GOT VOLUME");
                } else if(clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    //System.out.println("GOT MASTER_GAIN");
                }
                useVolume = volumeControl != null;
                if(useVolume) {
                    setVolume(type.getVolume());
                } else {
                    System.out.println("Error system doesn't support sound");
                }

                clip.start();
            } catch(LineUnavailableException e) {
                e.printStackTrace();
            }
            inited.set(true);
        }
    }

    private boolean inited() {
        return inited.get();
    }

    /**
     * Start the sound, or start playing again if it was paused
     * @see #stop()
     */
    protected void play() {
        if(!inited()) {
            return;
        }
        if(!clip.isRunning()) {
            clip.start();
        }
    }

    /**
     * Stop the SoundClip, it can still be finished if {@link #play()} is called
     * @see #play()
     */
    protected void stop() {
        if(!inited()) {
            return;
        }
        if(clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * Close the SoundClip, after this it can no longer be started and will be removed from the
     * SoundHandler
     */
    protected void close() {
        if(!inited()) {
            return;
        }
        stop();
        executors.submit(new Runnable() {
            @Override
            public void run() {
                clip.close();
            }
        });
    }

    protected void setVolume(int volume) {
        if(!inited()) {
            return;
        }
        if(!useVolume) {
            return;
        }

//        System.out.println("Setting volume to " + volume);
        if(volume == 0) {
            volumeControl.setValue(volumeControl.getMinimum());
        }
        //volumeControl.getMinLabel();
        float range = volumeControl.getMaximum() - volumeControl.getMinimum();
        double control = Math.max(0, Math.log10(volume) - 1);
        float value = (float) (range * control + volumeControl.getMinimum());
        volumeControl.setValue(value);
    }

    public boolean isDone() {
        if(!inited()) {
            return false;
        }
        //return clip.getFrameLength() <= clip.getFramePosition()
        // || !(clip.isRunning() && clip.isActive());
        return !(clip.isRunning() || clip.isActive());
    }

    public SoundType getType() {
        return type;
    }

    public boolean isLevelSound() {
        return inLevel;
    }

    private static void onClose() {
        executors.shutdown();
    }
}
