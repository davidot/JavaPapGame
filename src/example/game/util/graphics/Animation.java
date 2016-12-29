package example.game.util.graphics;

import java.util.Random;

/**
 * A Animation holds a set of Sprites to draw with the settings given
 * @author davidot
 */
public class Animation extends ProxySprite {

    private static final int DEFAULT_FRAME_PER_IMG = 15;
    private boolean running; //false
    private boolean loop; //false
    private int framePerImg = 15;
    private int currentFrame; //0
    private int defaultImg; //0
    private Sprite[] images;
    private int currentImage; //0
    private int startImg; //0
    private int[] customTimes;
    private boolean customTime; //false
    private boolean customLoop; //false
    private int timeBetween; //0
    private boolean rand;

    /**
     * Animation is a series of frames one of which is the default image, the animation when ticked
     * will increase the counter and when the counter reaches a certain time <p> By default the time
     * between frames is {@link Animation#DEFAULT_FRAME_PER_IMG} </p>
     * @param spriteSheet make an animation with all the images of this sprite sheet
     *
     * @see Animation#Animation(Sprite[])
     * @see SpriteSheet#getAll()
     */
    public Animation(SpriteSheet spriteSheet) {
        this(spriteSheet.getAll());
    }

    /**
     * Animation is a series of frames one of which is the default image, the animation when ticked
     * will increase the counter and when the counter reaches a certain time <p> By default the time
     * between frames is {@link Animation#DEFAULT_FRAME_PER_IMG} </p>
     * @param img          the images which are the frames in this animation
     * @param instantStart when true will immediately call start
     */
    public Animation(Sprite[] img, boolean instantStart) {
        this(img);
        if(instantStart) {
            start();
        }
    }

    /**
     * Animation is a series of frames one of which is the default image, the animation when ticked
     * will increase the counter and when the counter reaches a certain time <p> By default the time
     * between frames is {@link Animation#DEFAULT_FRAME_PER_IMG} </p>
     * @param img the images which are the frames in this animation
     */
    public Animation(Sprite[] img) {
        this(img, DEFAULT_FRAME_PER_IMG);
    }

    /**
     * Animation is a series of frames one of which is the default image, the animation when ticked
     * will increase the counter and when the counter reaches a certain time
     * @param img         the images which are the frames in this animation
     * @param framePerImg the amount of ticks within frames
     */
    public Animation(Sprite[] img, int framePerImg) {

        images = img;
        if(images == null) {
            images = new Sprite[0];
        }
        for(int i = 0; i < images.length; i++) {
            //correct any null frames
            if(images[i] == null) {
                images[i] = EMPTY_SPRITE;
            }
        }
        this.framePerImg = framePerImg;
    }

    /**
     * Reset the animation, sets the image to the start image and resets the current frame and
     * counter
     */
    public void reset() {
        currentImage = startImg;
        currentFrame = 0;
    }

    /**
     * Starts or restarts the animation, does not change the current frame
     */
    public void start() {
        running = true;
    }

    /**
     * Pauses the animation, does not change the current frame
     */
    public void pause() {
        running = false;
    }

    /**
     * Stops the animation and resets it
     * @see Animation#reset()
     */
    public void stop() {
        running = false;
        reset();
    }

    @Override
    public Sprite getSprite() {
        if(!running) {
            return images[defaultImg];
        } else {
            return images[currentImage];
        }
    }

    /**
     * Will perform the calculations for this animation, so the current frame can be different after
     * this method is called
     */
    public void tick() {
        if(!running) {
            return;
        }
        currentFrame++;
        if(customTime ? currentFrame > customTimes[currentImage] : currentFrame > framePerImg) {
            currentImage++;
            currentFrame = 0;
            if(currentImage >= images.length) {
                currentImage = startImg;
                if(!loop) {
                    currentImage = defaultImg;
                    running = false;
                }
                if(customLoop) {
                    currentFrame = -(rand ? new Random().nextInt(timeBetween) : timeBetween);
                }
            }
            currentImage = (currentImage >= images.length ? 0 : currentImage);


        }
    }


    /**
     * Set custom times for each of the frames, will warn you if the given array is shorter than the
     * frame array
     * @param time the array with custom times
     *
     * @return returns the object for easy initialization
     */
    public Animation setCustomFrameTime(int[] time) {
        customTime = true;
        if(time.length < images.length) {
            System.out.println(
                    "Custom time is shorter than amount of images animation, this will crash");
        }
        customTimes = time;
        return this;
    }

    /**
     * @return the number of the start image
     */
    public int getStartImg() {
        return startImg;
    }


    /**
     * Set the starting image
     * @param startImg the index of the startimage in the frame array
     *
     * @return returns the object for easy initialization
     */
    public Animation setStartImg(int startImg) {
        this.startImg = startImg;
        return this;
    }


    /**
     * @return the number of the default image
     */
    public int getDefaultImg() {
        return defaultImg;
    }

    /**
     * @param defaultImg the index of the defaultimage in the frame array
     *
     * @return returns the object for easy initialization
     */
    public Animation setDefaultImg(int defaultImg) {
        if(defaultImg < 0 || defaultImg >= images.length) {
            return this;
        }
        this.defaultImg = defaultImg;
        return this;
    }

    /**
     * @return the default frames per image
     */
    public int getFramePerImg() {
        return framePerImg;
    }

    /**
     * Set the time between frames
     * @param framePerImg the amount of frames (render/tick calls) per image
     *
     * @return returns the object for easy initialization
     * @see Animation#setCustomFrameTime(int[])
     */
    public Animation setFramePerImg(int framePerImg) {
        this.framePerImg = framePerImg;
        return this;
    }

    /**
     * @return whether the animation is currently running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Check if the animation has a loop or custom loop
     * @return whether the animation is looping or has a custom loop
     * @see Animation#isCustomLoop()
     */
    public boolean isLoop() {
        return loop;
    }

    /**
     * @param loop set wheter the animation should loop
     *
     * @return returns the object for easy initialization
     */
    public Animation setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    /**
     * Check if the animation has a custom loop
     * @return whether the animation has a custom loop
     */
    public boolean isCustomLoop() {
        return customLoop;
    }

    /**
     * Makes a custom loop for this animation A custom loop waits a certain time between restarting,
     * with random on will wait a maximum time randomly
     * @param timeBetween the time to wait in between runs of the animation, or the max time to wait
     *                    if random is used
     * @param rand        wheter to use random
     *
     * @return returns the object for easy initialization
     */
    public Animation customLoop(int timeBetween, boolean rand) {
        loop = true;
        customLoop = true;
        this.timeBetween = timeBetween;
        this.rand = rand;

        return this;
    }

    @Override
    public int getHeight() {
        return images[0].getHeight();
    }

    @Override
    public int getWidth() {
        return images[0].getWidth();
    }
}
