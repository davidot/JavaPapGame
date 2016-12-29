package example.game;


import example.game.util.Input;
import example.game.util.Input.Key;
import example.game.util.graphics.Font;
import example.game.util.sound.SoundHandler;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * This class contains the main components for the game, the methods for creation and destruction of
 * the game
 * @author davidot
 */
@SuppressWarnings("serial")
public class Game extends Canvas implements Runnable {

    /**
     * Title of the frame of the main {@link Game}
     */
    public static final String TITLE = "Lightbringer Beta";
    /**
     * The charset used to read and write data
     */
    public static final String CHARSET_NAME = "UTF-8";
    /**
     * Time slept per game loop
     */
    public static final int SLEEPTIME = 2;
    /**
     * The target amount of ticks per second
     */
    public static final int TARGET_TICKS = 60;
    /**
     * The amount of nanoseconds one tick can take
     */
    public static final double NS_TICKS = 1000000000.0 / TARGET_TICKS;

    //Strings
    private static final String RESOURCE_ERROR = "Resource folder or resources could not be found";
    private static final String TICK_OVER_PRE = "Skipping ";
    private static final String TICK_OVER_POST = " ticks is the system overloaded?";
    public static final char LINE_SEPARATOR_CHAR = '\n';

    //size of screen in tiles

    /**
     * Default width of the frame
     */
    private static int RENDER_WIDTH = 1024;
    /**
     * Default height of the frame
     */
    private static int RENDER_HEIGHT = 640;

    //private boolean states
    //one time params
    private static Font mainFont;
    private static JFrame frame;
    //needed for threads
    private boolean isRunning;
    //one time objects
    private Input input;

    //handlers
    private SoundHandler soundHandler;

    //thread security
    private boolean running;
    private Thread mainThread;

    private Game() {

    }

    /**
     * This method is called by the game just before initializing, after this method is called and
     * has not stopped the process you can be sure that all default resources have been loaded. If
     * you want to access any resources from the base game see {@link ResourceLoader}
     * @throws IOException          when the files to be loaded could not be found or accessed
     * @throws NullPointerException when the references in the files go wrong
     */
    public static void loadResources() throws IOException {
        ResourceLoader.loadDefaultGameData();
        mainFont = new Font(ResourceLoader.getSpriteSheet("font"));
    }

    /**
     * @return width of the frame in which the game can be rendered
     */
    public static int getRenderWidth() {
        return RENDER_WIDTH;
    }

    /**
     * @return height of the frame in which the game can be rendered
     */
    public static int getRenderHeight() {
        return RENDER_HEIGHT;
    }

    /**
     * @return the font to draw text with
     */
    public static Font getMainFont() {
        return mainFont;
    }



    /**
     * The main method, don't use this to start the game <p>Will convert the program arguments into
     * the game parameters</p>
     * @param args the program arguments
     *
     * @see #startDefault() to start the game with default settings
     */
    public static void main(String[] args) {
        startDefault();
    }

    /**
     * Start the game with no devmode and debug mode
     *
     * @return the Game object which is created
     */
    public static Game startDefault() {

        boolean fullscreen = false;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            e.printStackTrace();
        }

        Game game = new Game();
        game.setMinimumSize(new Dimension(RENDER_WIDTH, RENDER_HEIGHT));
        game.setMaximumSize(new Dimension(RENDER_WIDTH, RENDER_HEIGHT));
        game.setPreferredSize(new Dimension(RENDER_WIDTH, RENDER_HEIGHT));
        game.setIgnoreRepaint(true);

        //Make the frame
        final JFrame frame = new JFrame(Game.TITLE);
        frame.setIgnoreRepaint(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(game, BorderLayout.CENTER);
        //frame.setResizable(false);
        if(fullscreen) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
            setPixelSize(Toolkit.getDefaultToolkit().getScreenSize().width,
                    Toolkit.getDefaultToolkit().getScreenSize().height);
        } else {
            game.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    resizeFrame(e.getComponent().getSize());
                }
            });
        }
        try {
            frame.setIconImage(ImageIO.read(Game.class.getResourceAsStream("res/icon.png")));
        } catch(IOException e) {
            System.out.println("Could not load icon");
            //Dont shut down game if the icon could not be loaded since its a small extra
            e.printStackTrace();
        } catch(NullPointerException e) {
            System.out.println("Could not load icon");
            //Dont shut down game if the icon could not be loaded since its a small extra
            e.printStackTrace();
        }
        //Make sure the frame is packed
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    frame.pack();
                }
            });
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //Hack for a way to display the fps on the frame
        Game.frame = frame;
        game.start();

        return game;
    }

    private static void setPixelSize(int width, int height) {
        RENDER_WIDTH = width;
        RENDER_HEIGHT = height;
    }

    private static void resizeFrame(Dimension d) {
        setPixelSize(d.width, d.height);
    }

    //private init since it should only be called once
    private void init() {
        try {
            loadResources();
        } catch(IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, RESOURCE_ERROR, "Lightbringer loading error",
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(-1);
        } catch(NullPointerException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, RESOURCE_ERROR, "Lightbringer loading error",
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(-1);
        }
//        KitPiece.internalInit();
        soundHandler = new SoundHandler();
        input = new Input(this);

        //sound example
        soundHandler.playLoop("win", 0, false);
    }

    /**
     * The main game-loop of the game
     */
    @Override
    public void run() {
        if(running) {
            return;
        }
        init();
        running = true;
        long lastTime = System.nanoTime();
        double unprocessed = 0;
        int frames = 0;
        int ticks = 0;
        long lastTimer = System.currentTimeMillis();

        while(isRunning) {
            boolean shouldRender = false;
            long now = System.nanoTime();
            unprocessed += (now - lastTime) / NS_TICKS;
            lastTime = now;
            if(unprocessed > TARGET_TICKS * 5) {
                double ticksLeft = TARGET_TICKS * 5;
                System.out
                        .println(TICK_OVER_PRE + (int) (unprocessed - ticksLeft) + TICK_OVER_POST);
                unprocessed = ticksLeft;
            }
            while(unprocessed >= 1) {
                tick();
                ticks++;
                unprocessed--;
                shouldRender = true;
            }


            if(shouldRender) {
                frames++;
                render();
            }

            try {
                Thread.sleep(SLEEPTIME);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            if(System.currentTimeMillis() - lastTimer > 1000) {
                lastTimer += 1000;
                if(frame != null) {
                    frame.setTitle(TITLE + " | " + frames + " fps | " + ticks + " ticks");
                }
                frames = 0;
                ticks = 0;
            }
        }
    }

    int angle = 92;

    //private to make sure the amount of ticks stays on target
    private void tick() {
        soundHandler.tick();

        if(input.isLeftPressed()) {
            Point mouse = input.getMouseLocation();
            if(mouse.x > 100 && mouse.x < 200 && mouse.y > 200 && mouse.y < 400) {
                angle += 15;
                angle = angle % 360;
            }
        }

        //clicked means it was pressed this tick
        if(input.test.isClicked()) {
            angle -= 12;
            angle = angle % 360;
        }

        //pressed means the button is down
        if(input.exit.isPressed()) {
            stop();
        }

        //input is last because it will reset all the inputs for the next tick
        input.tick();
    }

    private void render() {
        BufferStrategy buffer = getBufferStrategy();
        if(buffer == null) {
            this.createBufferStrategy(2);
            requestFocus();
            return;
        }
        Graphics2D g = (Graphics2D) buffer.getDrawGraphics();

        //get the current size of the screen
        int width = getRenderWidth();
        int height = getRenderHeight();

        //clear the last frame
        g.setColor(getBackground());
        g.fillRect(0, 0, width, height);

        //start drawing here

        ResourceLoader.getSprite("test").render(g, 0, 0);

        ResourceLoader.getSprite("test").renderRotated(g, width / 2, height / 2, angle, 1.2, 1.6, 15, 16);

        getMainFont().createMessage("A little example").render(g, 200, height - 100);

        g.setColor(Color.RED);

        g.drawRect(100, 200, 100, 200);

        //stop drawing here
        g.dispose();
        buffer.show();
    }

    /**
     * Create a new key to read input
     * @param ints the keynumbers to trigger this key on {@link java.awt.event.KeyEvent} the VK_???
     *             variables
     * @param name the name of the key in the options menu
     *
     * @return the key created to check if it is pressed
     */
    public Key makeNewKey(int[] ints, String name) {
        return input.key(ints, name);
    }

    /**
     * Start the game
     */
    public void start() {
        if(isRunning) {
            return;
        }
        isRunning = true;
        System.out.println("Starting main thread");
        mainThread = new Thread(this, "Lightbringer");
        mainThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });
        mainThread.start();
    }

    /**
     * Stops the game
     */
    public void stop() {
        if(!isRunning) {
            return;
        }
        isRunning = false;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    mainThread.join();
                    System.exit(0);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
    }


    /**
     * @return the input
     */
    public Input getInput() {
        return input;
    }


    public SoundHandler getSoundHandler() {
        return soundHandler;
    }

}