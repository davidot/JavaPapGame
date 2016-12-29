package example.game.util.graphics;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A SpriteBatch can be used to draw multiple Sprites on top of each other with offsets
 * @author davidot
 */
public class SpriteBatch extends Sprite {

    private static final int DEFAULT_CAPACITY = 5;

    private final Sprite base;
    private final List<SpriteDraw> sprites; //don't start at capacity of 10
    private boolean clearAfter; //false

    /**
     * Create a SpriteBatch with the base Sprite on which the other sprites will be drawn <p> The
     * capacity of the list for extra sprites is set to 5, if you know the amount of extra sprites
     * you will draw it is advised to use the other constructor to make sure you don't have to keep
     * remaking the list when adding sprites. </p>
     * @param base the base Sprite
     */
    public SpriteBatch(Sprite base) {
        this(base, DEFAULT_CAPACITY);
    }

    /**
     * Create a SpriteBatch with the base Sprite on which the other sprites will be drawn, and gives
     * the list which stores the other sprites a starting capacity
     * @param base     the base Sprite
     * @param capacity the starting capacity of the List storing the extra Sprite draws
     */
    public SpriteBatch(Sprite base, int capacity) {
        this.base = base;
        sprites = new ArrayList<SpriteDraw>(capacity);
    }

    /**
     * Add a Sprite to draw on top of the SpriteBatch <p> The order of the render calls is the order
     * that the sprites are added </p>
     * @param sprite the Sprite to add
     */
    public void addSprite(Sprite sprite) {
        addSprite(sprite, 0, 0);
    }

    /**
     * Add a Sprite to draw on top of the SpriteBatch <p> The order of the render calls is the order
     * that the sprites are added. The x and y offsets is added to the sprite render call </p>
     * @param sprite the Sprite to add
     * @param xOff   the x offset of the Sprite
     * @param yOff   the y offset of the Sprite
     */
    public void addSprite(Sprite sprite, int xOff, int yOff) {
        addSprite(sprite, xOff, yOff, 0);
    }

    /**
     * Add a Sprite to draw on top of the SpriteBatch <p> The order of the render calls is the order
     * that the sprites are added. The x and y offsets is added to the sprite render call </p>
     * @param sprite   the Sprite to add
     * @param xOff     the x offset of the Sprite
     * @param yOff     the y offset of the Sprite
     * @param angleOff the angle to add when rendering the Sprite rotated
     */
    public void addSprite(Sprite sprite, int xOff, int yOff, int angleOff) {
        sprites.add(new SpriteDraw(sprite, xOff, yOff, angleOff));
    }

    /**
     * Add the Sprite if it is not on this SpriteBatch yet
     * @param sprite the Sprite to add and check for
     * @param xOff   the x offset
     * @param yOff   the y offset
     * @param angle  the angle offset
     */
    public void addUniqueSprite(Sprite sprite, int xOff, int yOff, int angle) {
        for(SpriteDraw draw : sprites) {
            if(draw.sprite == sprite) {
                return;
            }
        }
        sprites.add(new SpriteDraw(sprite, xOff, yOff, angle));
    }


    @Override
    public int getWidth() {
        return base.getWidth();
    }

    @Override
    public int getHeight() {
        return base.getHeight();
    }

    @Override
    public void render(Graphics2D g, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2,
                       int sy2) {
        base.render(g, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
        for(SpriteDraw draw : sprites) {
            draw.render(g, dx1, dy1);
        }
        if(clearAfter) {
            sprites.clear();
        }
    }

    @Override
    public void renderRotated(Graphics2D g, int x, int y, int angle, double xScale, double yScale,
                              int xOff, int yOff) {
        base.renderRotated(g, x, y, angle, xScale, yScale, xOff, yOff);
        for(SpriteDraw draw : sprites) {
            draw.renderRotated(g, x, y, angle);
        }

        if(clearAfter) {
            sprites.clear();
        }
    }

    public boolean isClearAfter() {
        return clearAfter;
    }

    public void setClearAfter(boolean clearAfter) {
        this.clearAfter = clearAfter;
    }

    private class SpriteDraw {

        private Sprite sprite;
        private int xOff;
        private int yOff;
        private int angleOff;

        public SpriteDraw(Sprite sprite, int xOff, int yOff, int angleOff) {
            this.sprite = sprite;
            this.xOff = xOff;
            this.yOff = yOff;
            this.angleOff = angleOff;
        }

        public void render(Graphics2D g, int xBase, int yBase) {
            sprite.render(g, xBase + xOff, yBase + yOff);
        }

        public void renderRotated(Graphics2D g, int x, int y, int angle) {
            sprite.renderRotated(g, x + xOff, y + yOff, angle + angleOff,
                    base.getWidth() / 2 - xOff, base.getHeight() / 2 - yOff);
        }

    }

}
