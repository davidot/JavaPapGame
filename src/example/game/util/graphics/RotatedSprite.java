package example.game.util.graphics;

import java.awt.Graphics2D;

/**
 * A RotatedSprite will always render rotated regardless of how it is called
 * @author davidot
 */
public class RotatedSprite extends Sprite {


    private final Sprite sprite;
    private final int angle;

    /**
     * Create a RotatedSprite base on the Sprite given which should always render rotated with the
     * angle given
     * @param sprite the Sprite to draw
     * @param angle  the angle to draw the Sprite on
     */
    public RotatedSprite(Sprite sprite, int angle) {
        this.sprite = sprite;
        this.angle = angle;
    }

    @Override
    public void render(Graphics2D g, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2,
                       int sy2) {
        sprite.renderRotated(g, dx1, dy1, angle,
                ((double) Math.abs(dx2 - dx1)) / ((double) Math.abs(sx2 - sx1)),
                ((double) Math.abs(dy2 - dy1)) / ((double) Math.abs(sy2 - sy1)));
    }

    @Override
    public void renderRotated(Graphics2D g, int x, int y, int angle, double xScale, double yScale,
                              int xOff, int yOff) {
        sprite.renderRotated(g, x, y, angle + this.angle, xScale, yScale, xOff, yOff);
    }


    @Override
    public int getHeight() {
        return sprite.getHeight();
    }

    @Override
    public int getWidth() {
        return sprite.getWidth();
    }
}
