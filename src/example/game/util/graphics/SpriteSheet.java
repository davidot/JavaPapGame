package example.game.util.graphics;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 * A SpriteSheet is a collection of sprites based of one image or sprite This image is split up in
 * smaller sprites
 * @author davidot
 */
public class SpriteSheet {

    Dimension timeSize;
    private Sprite[] sprites;
    private Dimension spriteSize;

    /**
     * Create a SpriteSheet based on a sprite
     * @param sprite     the sprite to base it on
     * @param spriteSize the sizes of the sprites inside the SpriteSheet
     */
    public SpriteSheet(Sprite sprite, Dimension spriteSize) {
        this.spriteSize = spriteSize;
        BufferedImage img = new BufferedImage(sprite.getWidth(), sprite.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        sprite.render(img.createGraphics(), 0, 0);
        timeSize = new Dimension(img.getWidth() / this.spriteSize.width,
                img.getHeight() / this.spriteSize.height);
        sprites = new Sprite[timeSize.width * timeSize.height];
        for(int x = 0; x < img.getWidth(); x += spriteSize.width) {
            for(int y = 0; y < img.getHeight(); y += spriteSize.height) {
                sprites[x / spriteSize.width + y / spriteSize.height * timeSize.width] =
                        Sprite.createAcceleratedSprite(
                                img.getSubimage(x, y, spriteSize.width, spriteSize.height));
            }
        }
    }

    /**
     * Create a spritesheet from the specified image and sprite size
     * @param img        the image on which the sprites are located
     * @param spriteSize the size of each sprite
     */
    public SpriteSheet(BufferedImage img, Dimension spriteSize) {
        this.spriteSize = spriteSize;
        timeSize = new Dimension(img.getWidth() / this.spriteSize.width,
                img.getHeight() / this.spriteSize.height);
        sprites = new Sprite[timeSize.width * timeSize.height];
        for(int x = 0; x < img.getWidth(); x += spriteSize.width) {
            for(int y = 0; y < img.getHeight(); y += spriteSize.height) {
                sprites[x / spriteSize.width + y / spriteSize.height * timeSize.width] =
                        Sprite.createAcceleratedSprite(
                                img.getSubimage(x, y, spriteSize.width, spriteSize.height));
            }
        }
    }

    /**
     * Get the sprite with the specified number
     * @param i the sprite which to get
     *
     * @return the sprite
     */
    public Sprite getSprite(int i) {
        if(i < 0 || i >= sprites.length) {
            return null;
        }
        return sprites[i];
    }

    /**
     * Get all the sprites
     * @return all the sprites
     */
    public Sprite[] getAll() {
        Sprite[] sprite = new Sprite[sprites.length];
        for(int i = 0; i < sprites.length; i++) {
            sprite[i] = getSprite(i);
        }
        return sprite;
    }

    public int getSpriteAmount() {
        return timeSize.width * timeSize.height;
    }

    public Dimension getSpriteSize() {
        return spriteSize;
    }
}