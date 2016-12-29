package example.game.util.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * A CenteredSprite draws the image not with the left top corner at the X and Y location but with
 * the middle at the given X and Y position
 * @author davidot
 */
public class CenteredSprite extends NormalSprite {

    /**
     * Create a CenteredSprite which will draw the image given
     * @param img the image to render
     */
    public CenteredSprite(BufferedImage img) {
        super(img);
    }

    @Override
    public void render(Graphics2D g, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2,
                       int sy2) {
        int halfWidth = getWidth() / 2;
        int halfHeight = getHeight() / 2;
        super.render(g, dx1 - halfWidth, dy1 - halfHeight, dx2 - halfWidth, dy2 - halfHeight, sx1,
                sy1, sx2, sy2);
    }

    @Override
    public void renderRotated(Graphics2D g, int x, int y, int angle, double xScale, double yScale,
                              int xOff, int yOff) {
        super.renderRotated(g, x - getWidth() / 2, y - getHeight() / 2, angle);
    }

}
