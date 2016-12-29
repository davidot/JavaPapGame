package example.game.util.files;

import example.game.Game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * A Class with methods to easily read and write to files and streams.
 * @author davidot
 */
public class FileUtilities {

    /**
     * The {@link Charset} used for all the files
     */
    public static final Charset charset = Charset.forName(Game.CHARSET_NAME);

    /**
     * Get the contents of a file in {@link String} form
     * @param file the file to read
     *
     * @return the contents of the file
     */
    public static String getStringFromFile(File file) {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            int byteAvailable = in.available();
            byte[] data = new byte[byteAvailable];
            int read = in.read(data);
            if(read != byteAvailable) {
                System.out.println("Bytes read in not equal to bytes available");
            }

            return new String(data, charset);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * Get the contents of this {@link InputStream} as far as {@link InputStream#available()}
     * reaches
     * @param inputStream the input stream
     *
     * @return the contents
     */
    public static String getStringFromInputStream(InputStream inputStream) {
        try {
            int byteAvailable = inputStream.available();
            byte[] data = new byte[byteAvailable];
            int read = inputStream.read(data);
            if(read != byteAvailable) {
                System.out.println("Bytes read in not equal to bytes available");
            }
            String text = new String(data, charset);
            //noinspection HardcodedLineSeparator
            return text.replaceAll("\\r?\\n|\\r", "");//replace all line separators
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * Writes a {@link String} to a {@link File}, this will overwrite any existing data
     * @param file the File to write to
     * @param data the String to write
     *
     * @return whether the String was written
     */
    public static boolean writeStringToFile(File file, String data) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(data.getBytes(charset));
            return true;
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("Error could not write to file");
        } finally {
            if(out != null) {
                try {
                    out.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    /**
     * Write a {@link String} to the end of a {@link File}
     * @param file the file to write to
     * @param data the String to write
     *
     * @return whether the data was written to the file
     */
    public static boolean appendStringToFile(File file, String data) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            out.println(data);
            out.close();
            return true;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
