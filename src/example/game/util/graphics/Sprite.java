package example.game.util.graphics;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * A Sprite is the basic element used for drawing things to the screen there are various subtypes
 * and you can create your own
 * @author davidot
 */
public abstract class Sprite {

    /**
     * A sprite which does not render anything <p> Useful when you need to return a sprite but don't
     * need anything rendered </p>
     */
    public static Sprite EMPTY_SPRITE = new EmptySprite();


    /**
     * Creates a accelerated sprite from the inputstream
     * @param in the inputstream from which to read an image (done with {@link
     *           ImageIO#read(InputStream)})
     *
     * @return the sprite created
     * @throws IOException if the image could not be loaded properly
     */
    public static Sprite createAcceleratedSprite(InputStream in) throws IOException {
        return createAcceleratedSprite(ImageIO.read(in));
    }

    /**
     * Creates a accelerated sprite from the image
     * @param img the image from which to read an image (done with {@link
     *            ImageIO#read(InputStream)})
     *
     * @return the sprite created
     */
    public static Sprite createAcceleratedSprite(Image img) {
        BufferedImage image =
                ImageUtilities.createAcceleratedImage(img.getWidth(null), img.getHeight(null));
        image.getGraphics().drawImage(img, 0, 0, null);
        return Sprite.fromBufferedImage(image);
    }

    public abstract int getWidth();

    public abstract int getHeight();

    /**
     * Render this sprite at a certain location
     * @param g the graphics to use
     * @param x the x coordinate to draw on
     * @param y the y coordinate to draw on
     */
    public void render(Graphics2D g, int x, int y) {
        this.render(g, x, y, getWidth(), getHeight());
    }

    /**
     * Render this sprite in a rectangle
     * @param g      the graphics to use
     * @param x      the x coordinate to draw on
     * @param y      the y coordinate to draw on
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     */
    public void render(Graphics2D g, int x, int y, int width, int height) {
        this.render(g, x, y, x + width, y + height, 0, 0, getWidth(), getHeight());
    }

    /**
     * Draws as much of the specified area of the specified image as is currently available, scaling
     * it on the fly to fit inside the specified area of the destination drawable surface.
     * @param g   the graphics to use
     * @param dx1 the x coordinate of the first corner of the destination rectangle.
     * @param dy1 the y coordinate of the first corner of the destination rectangle.
     * @param dx2 the x coordinate of the second corner of the destination rectangle.
     * @param dy2 the y coordinate of the second corner of the destination rectangle.
     * @param sx1 the x coordinate of the first corner of the source rectangle.
     * @param sy1 the y coordinate of the first corner of the source rectangle.
     * @param sx2 the x coordinate of the second corner of the source rectangle.
     * @param sy2 the y coordinate of the second corner of the source rectangle.
     */
    public abstract void render(Graphics2D g, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
                                int sx2, int sy2);

    /**
     * Render this sprite at a certain location with a certain scale
     * @param g     the graphics to use
     * @param x     the x coordinate to draw on
     * @param y     the y coordinate to draw on
     * @param scale the scale at which to draw this sprite
     */
    public void renderScale(Graphics2D g, int x, int y, int scale) {
        this.render(g, x, y, scale * getWidth(), scale * getHeight());
    }

    /**
     * Renders a sprite rotated at a certain location <p> It will render the sprite at the position
     * it would normally draw the sprite (x,y right top) just rotated </p>
     * @param g     the graphics to draw with
     * @param x     the x location to draw at
     * @param y     the y location to draw at
     * @param angle the angle to draw the sprite at
     */
    public void renderRotated(Graphics2D g, int x, int y, int angle) {
        renderRotated(g, x, y, angle, 1.0, 1.0);
    }

    /**
     * Renders a sprite rotated at a certain location with a certain scale
     * @param g      the graphics to draw with
     * @param x      the x location to draw at
     * @param y      the y location to draw at
     * @param angle  the angle to draw the sprite at
     * @param xScale the scale in the x (width) direction
     * @param yScale the scale in the y (height) direction
     *
     * @see #renderRotated(Graphics2D, int, int, int)
     */
    public void renderRotated(Graphics2D g, int x, int y, int angle, double xScale, double yScale) {
        renderRotated(g, x, y, angle, xScale, yScale, getWidth() / 2, getHeight() / 2);
    }

    /**
     * Renders a sprite rotated at a certain location with a certain offset
     * @param g     the graphics to draw with
     * @param x     the x location to draw at
     * @param y     the y location to draw at
     * @param angle the angle to draw the sprite at
     * @param xOff  the offset in the x (width) direction
     * @param yOff  the offset in the y (height) direction
     */
    public void renderRotated(Graphics2D g, int x, int y, int angle, int xOff, int yOff) {
        renderRotated(g, x, y, angle, 1.0, 1.0, xOff, yOff);
    }

    /**
     * Renders a sprite rotated at a certain location with a certain scale and offset
     * @param g      the graphics to draw with
     * @param x      the x location to draw at
     * @param y      the y location to draw at
     * @param angle  the angle to draw the sprite at
     * @param xScale the scale in the x (width) direction
     * @param yScale the scale in the y (height) direction
     * @param xOff   the offset in the x (width) direction
     * @param yOff   the offset in the y (height) direction
     */
    public abstract void renderRotated(Graphics2D g, int x, int y, int angle, double xScale,
                                       double yScale, int xOff, int yOff);

    /**
     * Creates a Sprite from the {@link BufferedImage} <p> This will not copy the BufferedImage so
     * if the BufferedImage changes so does the sprite </p>
     * @param bimg the image to base it on
     *
     * @return the Sprite created
     */
    public static Sprite fromBufferedImage(BufferedImage bimg) {
        return new NormalSprite(bimg);
    }


    private static class EmptySprite extends Sprite {

        private EmptySprite() {
        }

        @Override
        public void render(Graphics2D g, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1,
                           int sx2, int sy2) {
            //don't
        }

        @Override
        public void renderRotated(Graphics2D g, int x, int y, int angle, double xScale,
                                  double yScale, int xOff, int yOff) {
            //don't
        }

        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public int getHeight() {
            return 0;
        }
    }

}
