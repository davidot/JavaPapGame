package example.game.util;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Input implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    public final Key test;
    public final Key exit;

    //start at false
    public boolean leftDown;
    public boolean rightDown;
    public boolean wheelDown;

    private List<Key> keys = new ArrayList<Key>();

    //start at false
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean wheelPressed;

    //start at 0,0
    private int x;
    private int y;

    private List<WheelListener> wheelListeners = new ArrayList<WheelListener>();
    private List<KeyToggle> keyToggles = new ArrayList<KeyToggle>();


    public Input(Component comp) {
        if(comp != null) {
            comp.addKeyListener(this);
            comp.addMouseListener(this);
            comp.addMouseMotionListener(this);
            comp.addMouseWheelListener(this);
            setPoint(comp.getMousePosition());
        }

        test = getKey("test", new int[] {KeyEvent.VK_A, KeyEvent.VK_SPACE});
        exit = getKey("exit", new int[] {KeyEvent.VK_ESCAPE});
    }

    private Key getKey(String keyName, int[] defaultKeys) {
        return new Key(keyName, defaultKeys);
    }

    public void tick() {
        for(Key key : keys) {
            key.tick();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public void unPressAll() {
        for(Key key : keys) {
            key.pressed = false;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        call(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        call(e, false);
    }

    public void call(KeyEvent e, boolean pressed) {
        boolean consumed = false;
        int keyNum = e.getKeyCode();
        for(Key key : keys) {
            for(int vk : key.keys) {
                if(vk == keyNum) {
                    for(Iterator<KeyToggle> iter = keyToggles.iterator(); iter.hasNext(); ) {
                        KeyToggle toggle = iter.next();
                        if(toggle.hasKey(key)) {
                            if(!toggle.call(pressed)) {
                                iter.remove();
                            }
                            //if consumed was already true keep it that way
                            consumed = consumed || toggle.willConsume(pressed);
                        }
                    }
                    if(!consumed) {
                        key.toggle(pressed);
                    }
                }
            }
        }
    }

    public boolean isLeftPressed() {
        if(leftPressed) {
            leftPressed = false;
            return true;
        }
        return false;
    }

    public boolean checkLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        if(rightPressed) {
            rightPressed = false;
            return true;
        }
        return false;
    }

    public boolean checkRightPressed() {
        return rightPressed;
    }

    public boolean isWheelPressed() {
        if(wheelPressed) {
            wheelPressed = false;
            return true;
        }
        return false;
    }

    public boolean checkWheelPressed() {
        return wheelPressed;
    }

    public boolean addWheelListener(WheelListener list) {
        return wheelListeners.add(list);
    }

    public boolean removeWheelListener(WheelListener list) {
        return wheelListeners.remove(list);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        setPoint(e.getPoint());
        for(WheelListener list : wheelListeners) {
            list.onScroll(e.getWheelRotation());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        setPoint(e.getPoint());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        setPoint(e.getPoint());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        setPoint(e.getPoint());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setPoint(e.getPoint());
        switch(e.getButton()) {
            case MouseEvent.BUTTON1:
                leftDown = true;
                leftPressed = true;
                break;
            case MouseEvent.BUTTON2:
                wheelDown = true;
                wheelPressed = true;
                break;
            case MouseEvent.BUTTON3:
                rightDown = true;
                rightPressed = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setPoint(e.getPoint());
        switch(e.getButton()) {
            case MouseEvent.BUTTON1:
                leftDown = false;
                break;
            case MouseEvent.BUTTON2:
                wheelDown = false;
                break;
            case MouseEvent.BUTTON3:
                rightDown = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setPoint(e.getPoint());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setPoint(e.getPoint());
        unPressAll();
    }

    public Point getMouseLocation() {
        return new Point(x, y);
    }

    public void setPoint(Point p) {
        if(p == null) {
            return;
        }
        x = p.x;
        y = p.y;
    }

    public Key key(int[] ints, String name) {
        return new Key(name, ints);
    }

    public void addKeyListener(KeyToggleListener keyToggle, Key... keys) {
        keyToggles.add(new KeyToggle(keyToggle, keys));
    }

    public void removeKeyListern(KeyToggleListener keyToggle, Key... keys) {
        for(Iterator<KeyToggle> iterator = keyToggles.iterator(); iterator.hasNext(); ) {
            KeyToggle toggle = iterator.next();
            if(toggle.listener == keyToggle) {
                for(Key key : keys) {
                    toggle.keys.remove(key);
                }
                if(toggle.keys.isEmpty()) {
                    iterator.remove();
                }
            }
        }
    }

    public interface WheelListener {

        /**
         * gives amount of scrolls negative is up positve is down
         * @param change the amount of movement from the mousewheel
         */
        void onScroll(int change);

    }

    private class KeyToggle {

        private final KeyToggleListener listener;
        //we suspect to only need one key
        List<Key> keys = new ArrayList<Key>(1);

        private KeyToggle(KeyToggleListener listener, Key[] listenKeys) {
            this.listener = listener;

            Collections.addAll(this.keys, listenKeys);
        }

        private boolean hasKey(Key key) {
            return keys.contains(key);
        }

        private boolean call(boolean state) {
            return listener.onKeyToggle(state);
        }

        public boolean willConsume(boolean state) {
            return listener.shouldConsume(state);
        }
    }

    public interface KeyToggleListener {


        /**
         * Whether the key press should be consumed so that the normal keys don't get trigged
         * @param state the new state of the key {@code true} when the key is pressed down {@code
         *              false} when the key is released
         *
         * @return whether the key should be consumed
         */
        boolean shouldConsume(boolean state);

        /**
         * Called when the keys to which this Listener listens
         * @param state the new state of the key {@code true} when the key is pressed down {@code
         *              false} when the key is released
         *
         * @return whether the Listener should still be listening
         */
        boolean onKeyToggle(boolean state);

    }


    public class Key {
        private boolean pressed, clicked;
        private int presses, pressdone;
        private final String name;
        private int[] keys;

        protected Key(String name, int[] in) {
            this.name = name;
            keys = in;
            Input.this.keys.add(this);
        }

        public void toggle(boolean in) {
            if(in != pressed) {
                pressed = in;
            }
            if(in) {
                clicked = true;
                presses++;
            }
        }

        public void tick() {
            if(clicked) {
                pressdone++;
                clicked = presses > pressdone;
            }
        }

        public boolean hasCode(int code) {
            for(int key : keys) {
                if(code == key) {
                    return true;
                }
            }
            return false;
        }

        public boolean isPressed() {
            return pressed;
        }

        public boolean isClicked() {
            return clicked;
        }

        public String getName() {
            return name;
        }

        public void removeCode(int code) {
            List<Integer> ints = new ArrayList<Integer>();
            for(int key : keys) {
                if(key != code) {
                    ints.add(key);
                }
            }
            for(int i = 0; i < ints.size(); i++) {
                keys[i] = ints.get(i);
            }
        }

        public void addCode(int code) {
            int[] newKeys = new int[keys.length + 1];
            System.arraycopy(keys, 0, newKeys, 0, keys.length);
            newKeys[keys.length] = code;
            keys = newKeys;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }

            Key k = (Key) o;

            if(pressed != k.pressed || clicked != k.clicked || presses != k.presses ||
                    pressdone != k.pressdone) {
                return false;
            }
            if(!name.equals(k.name)) {
                return false;
            }
            return Arrays.equals(keys, k.keys);

        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

    }

}