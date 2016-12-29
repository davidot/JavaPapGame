package example.game.util.files;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A OptionalOutputStream will only put data through another OutputStream
 * if it is active.
 * @author davidot
 */
public class OptionalOutputStream extends OutputStream {

    private OutputStream out;
    private boolean active = true;

    /**
     * Create a OptionalOutputStream with the proxy Stream
     * @param out the proxy OutputStream
     */
    public OptionalOutputStream(OutputStream out) {
        //todo create logger
        this.out = out;
    }

    /**
     * Whether the Output stream is active and data will be send through
     * @return whether the stream is active
     */
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void write(int b) throws IOException {
        if(active) {
            out.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if(active) {
            out.write(b, off, len);
        }
    }

    @Override
    public void close() throws IOException {
        if(active) {
            out.close();
        }
    }

    @Override
    public void flush() throws IOException {
        if(active) {
            out.flush();
        }
    }

    @Override
    public void write(byte[] b) throws IOException {

        if(active) {
            out.write(b);
        }
    }

}
