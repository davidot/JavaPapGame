package example.game.util.graphics;

import java.awt.Graphics2D;

/**
 * A ProxySprite is a Sprite which calls all the real render and information calls on a underlying
 * Sprite, the Sprite is at {@link #getSprite()}
 * @author davidot
 */
public abstract class ProxySprite extends Sprite {

    @Override
    public int getWidth() {
        return getSprite().getWidth();
    }

    @Override
    public int getHeight() {
        return getSprite().getWidth();
    }

    @Override
    public void render(Graphics2D g, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2,
                       int sy2) {
        getSprite().render(g, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
    }

    @Override
    public void renderRotated(Graphics2D g, int x, int y, int angle, double xScale, double yScale,
                              int xOff, int yOff) {
        getSprite().renderRotated(g, x, y, angle, xScale, yScale, xOff, yOff);
    }

    /**
     * The Sprite on which the actual rendering will be executed should be called here
     * @return the Sprite to execute the render calls on
     */
    public abstract Sprite getSprite();

}
