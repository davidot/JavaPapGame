package example.game.util.graphics;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * The default implementation of {@link Sprite} <p> This Sprite is based on a BufferedImage. </p>
 * @author davidot
 */
public class NormalSprite extends Sprite {


    private BufferedImage img;

    /**
     * Create a NormalSprite based on a Image
     * @param img the image
     */
    protected NormalSprite(BufferedImage img) {
        if(img == null) {
            throw new IllegalArgumentException("Image is null");
        }
        this.img = img;
    }

    @Override
    public void renderRotated(Graphics2D g, int x, int y, int angle, double xScale, double yScale,
                              int xOff, int yOff) {
        AffineTransform trans = new AffineTransform();
        trans.setToTranslation(x, y);
        trans.scale(xScale, yScale);
        trans.rotate(Math.toRadians(angle), xOff, yOff);
        g.drawImage(img, trans, null);
    }


    @Override
    public void render(Graphics2D g, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2,
                       int sy2) {
        g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
    }

    @Override
    public int getHeight() {
        return img.getHeight();
    }

    @Override
    public int getWidth() {
        return img.getWidth();
    }

    protected BufferedImage getImg() {
        return img;
    }
}
