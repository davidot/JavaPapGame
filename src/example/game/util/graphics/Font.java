package example.game.util.graphics;

import example.game.Game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * A Font can render text based on a Sprite sheet containing all character of
 * {@link Font#chars}
 * @author davidot
 */
public class Font {

    /**
     * All the characters currently in the font
     */
    public static final String chars =
            "  ABCDEFGHIJKLMNOPQRSTUVWXYZ    " + "&|:;<>.,()[]\\/\"\'0123456789-+@   ";

    /**
     * Index of the selector in the font
     */
    public static final int SPECIALARROW = 1;
    private static final char SPACE = ' ';

    private SpriteSheet sheet;
    private int width;
    private int height;

    /**
     * Create a Font based on the {@link SpriteSheet}
     * @param img the spritesheet
     */
    public Font(SpriteSheet img) {
        sheet = img;
        width = sheet.getSpriteSize().width;
        height = sheet.getSpriteSize().height;
    }

    /**
     * Draw a message which will soft wrap when wider than blockWidth at
     * the specified coordinates
     * @param g          the Graphics2D used to draw
     * @param x          the x coordinate
     * @param y          the y coordinate
     * @param message    the message to draw
     * @param blockWidth at which width the text should wrap
     */
    public void draw(Graphics2D g, int x, int y, String message, int blockWidth) {
        draw(g, x, y, message, blockWidth, Color.BLACK, 1);
    }

    /**
     * Draw a message with a color which will soft wrap when wider than blockWidth at
     * the specified coordinates
     * @param g          the Graphics2D used to draw
     * @param x          the x coordinate
     * @param y          the y coordinate
     * @param message    the message to draw
     * @param blockWidth at which width the text should wrap
     * @param color      the color to draw with
     */
    public void draw(Graphics2D g, int x, int y, String message, int blockWidth, Color color) {
        draw(g, x, y, message, blockWidth, color, 1);
    }

    /**
     * Draw a message with a scale which will soft wrap when wider than blockWidth at
     * the specified coordinates
     * @param g          the Graphics2D used to draw
     * @param x          the x coordinate
     * @param y          the y coordinate
     * @param message    the message to draw
     * @param blockWidth at which width the text should wrap
     * @param scale      the scale to draw with (1 = 16px)
     */
    public void draw(Graphics2D g, int x, int y, String message, int blockWidth, int scale) {
        draw(g, x, y, message, blockWidth, Color.BLACK, scale);
    }

    /**
     * Draw a message with a color and scale which will soft wrap when wider than blockWidth
     * at the specified coordinates
     * @param g          the Graphics2D used to draw
     * @param x          the x coordinate
     * @param y          the y coordinate
     * @param message    the message to draw
     * @param blockWidth at which width the text should wrap
     * @param color      the color to draw with
     * @param scale      the scale to draw with (1 = 16px)
     */
    public void draw(Graphics2D g, int x, int y, String message, int blockWidth, Color color,
                     int scale) {
        int letterWidth = scale * width;
        int letterHeight = scale * height;

        int currentLine = 0;
        StringBuilder[] lines = {new StringBuilder()};

        for(int i = 0; i < message.length(); i++) {
            char ch = message.charAt(i);
            int currentWidth = lines[currentLine].length() * letterWidth;
            if(ch == Game.LINE_SEPARATOR_CHAR) {
                currentLine++;
                lines = expandArray(lines, 1);
                continue;
            } else if(ch == SPACE) {
                if(currentWidth >= blockWidth) {
                    currentLine++;
                    lines = expandArray(lines, 1);
                } else {
                    lines[currentLine].append(SPACE);
                }
                continue;
            }

            int wordLength = Math.min(message.indexOf(SPACE, i),
                    message.indexOf(Game.LINE_SEPARATOR_CHAR, i));
            if(wordLength < 0) {
                wordLength = message.length();
            }
            wordLength -= i;
            if(currentWidth + wordLength * letterWidth > blockWidth) {
                if(wordLength * letterWidth > blockWidth) {
                    //word doesn't fit in the block so split it
                    if(currentWidth + letterWidth < blockWidth) {
                        //we can fit this letter
                        lines[currentLine].append(ch);
                        continue;
                    }
                    //can't fit the letter expand the lines
                }
                currentLine++;
                lines = expandArray(lines, 1);
            }
            lines[currentLine].append(ch);
        }

        if(color.equals(Color.BLACK)) {
            for(int i = 0; i < lines.length; i++) {
                drawLined(g, x, y + i * letterHeight, lines[i].toString(), scale);
            }
        } else {
            int max = 0;
            for(StringBuilder line : lines) {
                max = Math.max(line.length() * letterWidth, max);
            }
            BufferedImage img = new BufferedImage(max, letterHeight * lines.length,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2D = img.createGraphics();
            for(int i = 0; i < lines.length; i++) {
                drawLined(g2D, 0, i * letterHeight, lines[i].toString(), scale);
            }
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 1.0f));
            g2D.setColor(color);
            g2D.fillRect(0, 0, img.getWidth(), img.getHeight());
            g.drawImage(img, x, y, null);
        }
    }

    private StringBuilder[] expandArray(StringBuilder[] strings, int amount) {
        StringBuilder[] array = new StringBuilder[strings.length + amount];
        Arrays.fill(array, new StringBuilder());
        System.arraycopy(strings, 0, array, 0, strings.length);
        return array;
    }

    /**
     * Draw a Message at the coordinates
     * @param g  the Graphics2D used to draw
     * @param x  the x coordinate
     * @param y  the y coordinate
     * @param ms the message to draw
     */
    public void drawLined(Graphics2D g, int x, int y, String ms) {
        if(ms.isEmpty()) {
            return;
        }
        String msg = ms.toUpperCase();
        for(int i = 0; i < msg.length(); i++) {
            int c = chars.indexOf(msg.charAt(i));
            sheet.getSprite(c < 0 ? sheet.timeSize.width : c).render(g, x + width * i, y);
        }
    }

    /**
     * Draw a Message at the coordinates with a Color
     * @param g     the Graphics2D used to draw
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param ms    the message to draw
     * @param color the color to draw with
     */
    public void drawLined(Graphics2D g, int x, int y, String ms, Color color) {
        BufferedImage img =
                new BufferedImage(getMsWidth(ms), getMsHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = img.createGraphics();
        drawLined(g2D, 0, 0, ms);
        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 1.0f));
        g2D.setColor(color);
        g2D.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.drawImage(img, x, y, null);
    }

    /**
     * Draw a Message at the coordinates at a scale
     * @param g     the Graphics2D used to draw
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param ms    the message to draw
     * @param scale the scale to draw at (1=16px)
     */
    public void drawLined(Graphics2D g, int x, int y, String ms, int scale) {
        if(ms.isEmpty()) {
            return;
        }
        String msg = ms.toUpperCase();
        for(int i = 0; i < msg.length(); i++) {
            int c = chars.indexOf(msg.charAt(i));
            sheet.getSprite(c < 0 ? sheet.timeSize.width : c)
                    .renderScale(g, x + width * i * scale, y, scale);
        }
    }

    /**
     * Draw a Message at the coordinates with a color and scale
     * @param g     the Graphics2D used to draw
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param ms    the message to draw
     * @param color the color to draw with
     * @param scale the scale to draw at (1=16px)
     */
    public void drawLined(Graphics2D g, int x, int y, String ms, Color color, int scale) {
        if(ms.isEmpty()) {
            return;
        }
        //Create image
        BufferedImage img =
                new BufferedImage(getMsWidth(ms), getMsHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = img.createGraphics();

        //since the font is stored in uppercase change to upper case
        String msg = ms.toUpperCase();
        for(int i = 0; i < msg.length(); i++) {
            int c = chars.indexOf(msg.charAt(i));
            //draw at x = place in message because it is on the separate image, and y = 0
            sheet.getSprite(c < 0 ? sheet.timeSize.width : c)
                    .renderScale(g2D, width * i * scale, 0, scale);
        }
        //draw the colored layer on top
        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 1.0f));
        g2D.setColor(color);
        g2D.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.drawImage(img, x, y, null);
    }

    public int getMsHeight() {
        return height;
    }

    /**
     * Get the width this message will be (when not wrapped)
     * @param ms the message
     *
     * @return the width of the message
     */
    public int getMsWidth(String ms) {
        return getMsWidth(ms.length());
    }

    /**
     * Get the width this message will be (when not wrapped)
     * @param length the length of the message
     *
     * @return the width of the message
     */
    public int getMsWidth(int length) {
        return width * length;
    }

    /**
     * Create a {@link Sprite} which stores the message given
     * @param text the message
     *
     * @return the message stored in a Sprite
     */
    public Sprite createMessage(String text) {
        if(text.isEmpty()) {
            return null;
        }
        BufferedImage img = ImageUtilities.createAcceleratedImage(text.length() * width, height);
        Graphics2D g = img.createGraphics();
        String msg = text.toUpperCase();
        for(int i = 0; i < msg.length(); i++) {
            int c = chars.indexOf(msg.charAt(i));
            Sprite s = sheet.getSprite(c < 0 ? sheet.timeSize.width : c);
            s.render(g, width * i, 0);
        }
        return Sprite.fromBufferedImage(img);
    }

    /**
     * Create a {@link Sprite} which stores the message given with a color
     * @param text  the message
     * @param color the color the message will be drawn with
     *
     * @return the message stored in a Sprite
     */
    public Sprite createMessageColor(String text, Color color) {
        Sprite sprite = createMessage(text);
        if(sprite == null) {
            return Sprite.EMPTY_SPRITE;
        }
        return ImageUtilities.changeColor(sprite, Color.BLACK, color);
    }

    /**
     * Create a {@link Sprite} which stores the message given with a scale
     * @param text  the message
     * @param scale the scale the message will be drawn with (1=16px)
     *
     * @return the message stored in a Sprite
     */
    public Sprite createMessageScaled(String text, int scale) {
        return ImageUtilities.makeBigger(createMessage(text), scale);
    }

    /**
     * Create a {@link Sprite} which stores the message given with a color
     * @param text  the message
     * @param color the color the message will be drawn with
     * @param scale the scale the message will be drawn with (1=16px)
     *
     * @return the message stored in a Sprite
     */
    public Sprite createMessageScaledColor(String text, Color color, int scale) {
        return ImageUtilities.makeBigger(createMessageColor(text, color), scale);
    }

    /**
     * Get one character of the font in certain color
     * @param num   the index of character to use
     * @param color the color of the character
     *
     * @return the created sprite of the color given and from the character given
     */
    public Sprite getColor(int num, Color color) {
        return ImageUtilities.changeColor(sheet.getSprite(num), Color.black, color);
    }
}