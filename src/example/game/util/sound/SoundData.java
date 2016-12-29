package example.game.util.sound;

import example.game.ResourceLoader;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Sound data holds the data loaded from .wav or .ogg files loaded from the {@link
 * SoundHandler#addSound(InputStream, String, SoundType)} usually from the {@link ResourceLoader}
 * @author davidot
 */
public class SoundData {

    //size of sound loading buffer
    private static final int BUFFERSIZE = 4096;
    private final SoundType type;

    private byte[] data;
    private AudioFormat format;

    /**
     * Sound data holds the data loaded from .wav or .ogg files
     * @param input the BufferedInputStream from which to read the data
     * @param type  the standard type when this SoundData will be played
     */
    public SoundData(BufferedInputStream input, SoundType type) {
        this.type = type;
        try {
            loadData(input);
        } catch(IOException e) {
            e.printStackTrace();
        } catch(UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    private void loadData(BufferedInputStream input)
            throws IOException, UnsupportedAudioFileException {
        AudioInputStream stream = AudioSystem.getAudioInputStream(input);
        format = stream.getFormat();
        data = new byte[(int) (stream.getFrameLength() * format.getFrameSize())];
        byte[] buf = new byte[BUFFERSIZE];
        for(int i = 0; i < data.length; i += BUFFERSIZE) {
            int r = stream.read(buf, 0, BUFFERSIZE);
            if(i + r >= data.length) {
                r = data.length - i;//r = i + r - data.length;
            }
            System.arraycopy(buf, 0, data, i, r);
        }
        stream.close();
    }

    /**
     * Play the sound with the handler, a certain amount of times, 0 = once, 1 = loop once
     * @param handler the handler
     * @param times   the amount of times
     * @param inLevel whether the sound should be stopped when the level is
     */
    protected void play(SoundHandler handler, int times, boolean inLevel) {
        if(type.getVolume() < 0) {
            return; //don't play sound this type is off
        }
        handler.add(new SoundClip(this, type, times, inLevel));
    }

    public SoundType getType() {
        return type;
    }

    protected AudioFormat getFormat() {
        return format;
    }

    protected synchronized byte[] getData() {
        return data;
    }
}
