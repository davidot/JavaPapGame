package example.game.util.graphics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

/**
 * This class contains a few utilities for manipulating images
 * @author davidot
 */
public class ImageUtilities {


    private static GraphicsConfiguration gcon =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                    .getDefaultConfiguration();

    /**
     * Creates a sprite with a certain color changed
     * @param sprite the sprite which it should be based upon
     * @param from   color to change from
     * @param to     color to change to
     *
     * @return the new sprite with the changed color
     */
    public static Sprite changeColor(Sprite sprite, Color from, Color to) {
        int colF = from.getRGB();
        int rgb = to.getRGB();
        if(colF == rgb || sprite == null) {
            return sprite;
        }
        BufferedImage img = new BufferedImage(sprite.getWidth(), sprite.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        sprite.render(g, 0, 0);

        for(int x = 0; x < img.getWidth(); x++) {
            for(int y = 0; y < img.getHeight(); y++) {
                if(img.getRGB(x, y) == colF) {
                    img.setRGB(x, y, rgb);
                }
            }
        }
        return Sprite.createAcceleratedSprite(img);
    }

    /**
     * Scales an image times given <p> Just multiplied the size of each pixel, no anti-aliasing
     * </p>
     * @param normal the sprite to base it on
     * @param times  the scale of the new image
     *
     * @return the scaled image
     */
    public static Sprite makeBigger(Sprite normal, int times) {
        if(normal == null || normal == Sprite.EMPTY_SPRITE) {
            return Sprite.EMPTY_SPRITE;
        }
        int widthNew = normal.getWidth() * times;
        int heightNew = normal.getHeight() * times;
        if(widthNew == 0 || heightNew == 0) {
            throw new IllegalArgumentException(
                    "The width or height of the new image is zero (it might be the times)");
        }
        BufferedImage img =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                        .getDefaultConfiguration()
                        .createCompatibleImage(widthNew, heightNew, Transparency.TRANSLUCENT);
        Graphics2D g = img.createGraphics();
        normal.renderScale(g, 0, 0, times);
        g.dispose();
        return Sprite.fromBufferedImage(img);
    }


    /**
     * Makes a copy of the image given but flipped vertically
     * @param base the image to base the image on
     *
     * @return the vertically flipped image
     */
    public static Sprite flipVertical(Sprite base) {
        int width = base.getWidth();
        int height = base.getHeight();
        BufferedImage img =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                        .getDefaultConfiguration()
                        .createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        Graphics2D g = img.createGraphics();
        base.render(g, 0, height, width, 0, 0, 0, width, height);
        g.dispose();
        return Sprite.fromBufferedImage(img);
    }

    /**
     * Makes a copy of the image given but flipped horizontally
     * @param base the image to base the image on
     *
     * @return the horizontally flipped image
     */
    public static Sprite flipHorizontal(Sprite base) {
        int width = base.getWidth();
        int height = base.getHeight();
        BufferedImage img =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                        .getDefaultConfiguration()
                        .createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        Graphics2D g = img.createGraphics();
        base.render(g, width, 0, 0, height, 0, 0, width, height);
        g.dispose();
        return Sprite.fromBufferedImage(img);
    }

    /**
     * Clears image does NOT make copy My "optimization" for not making new buffered image every
     * time render is called
     * @param img the image to clear
     *
     * @return the cleared image
     */
    public static BufferedImage clearImage(BufferedImage img) {
        if(img == null) {
            return null;
        }
        Graphics2D g = img.createGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setComposite(AlphaComposite.SrcOver);
        g.dispose();
        return img;
    }

    /**
     * Convinient method to create accelerated image
     * @param width  the width of the image
     * @param height the height of the image
     *
     * @return the created image
     */
    public static BufferedImage createAcceleratedImage(int width, int height) {
        return gcon.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
    }

}
