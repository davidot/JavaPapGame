package example.game;

import example.game.util.graphics.Sprite;
import example.game.util.graphics.SpriteSheet;
import example.game.util.sound.SoundData;
import example.game.util.sound.SoundHandler;
import example.game.util.sound.SoundType;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The ResourceLoader holds all the resources for the game and method to load resources <p>This is
 * the advised way to load any resources because when using this file TexturePacks will
 * automatically be checked for overwriting resources</p>
 * @author davidot
 */
public class ResourceLoader {

    /**
     * Default sprite size of sprite sheet
     */
    public static final Dimension SPRITE_SIZE = new Dimension(32, 32);

    /**
     * Constant used in the resource files
     */
    public static final String SPRITE = "sprite";
    /**
     * Constant used in the resource files
     */
    public static final String SPRITESHEET = "spritesheet";
    /**
     * Constant used in the resource files
     */
    public static final String NAME = "name";
    /**
     * Constant used in the resource files
     */
    public static final String DIMENSION = "dimension";
    /**
     * Constant used in the resource files
     */
    public static final String DIMENSIONSPLIT = "x";
    /**
     * Constant used in the resource files
     */
    public static final String SOUND = "sound";
    /**
     * Constant used in the resource files
     */
    public static final String SOUNDTYPE = "type";


    private static final Map<String, Sprite> sprites = new HashMap<String, Sprite>();
    private static final Map<String, SpriteSheet> spriteMaps = new HashMap<String, SpriteSheet>();

    private ResourceLoader() {
        //to make sure no one can initialize one
    }

    /**
     * @param clazz the from which to use the relative paths in the resource files
     * @param input the InputStream to the xml file
     *
     * @throws IOException if the xml file could not be read from the input stream or if the file in
     *                     the resource file could not be found
     */
    @SuppressWarnings("unchecked")
    public static void loadResources(Class<?> clazz, InputStream input) throws IOException {
        List<String> allowedTags = Arrays.asList(SPRITE, SPRITESHEET, SOUND);
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(input);

            while(eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if(event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String name = startElement.getName().getLocalPart();
                    if(allowedTags.contains(name)) {

                        Iterator<? extends Attribute> attributes;
                        try {

                            //noinspection unchecked
                            attributes = (Iterator<Attribute>) startElement.getAttributes();
                        } catch(ClassCastException e) {
                            e.printStackTrace();
                            return;
                        }

                        boolean hasName = false;
                        String currentName = "";
                        Dimension currentDimension = SPRITE_SIZE;
                        SoundType soundType = SoundType.SFX;
                        boolean hasType = false;

                        while(attributes.hasNext()) {
                            Attribute attribute = attributes.next();
                            String attribute_name = attribute.getName().getLocalPart();
                            if(attribute_name.equalsIgnoreCase(NAME)) {
                                hasName = true;
                                currentName = attribute.getValue();
                            } else if(attribute_name.equalsIgnoreCase(DIMENSION)) {
                                try {
                                    String value = attribute.getValue();
                                    String[] parts = value.split(DIMENSIONSPLIT);
                                    if(parts.length < 2) {
                                        continue;
                                    }
                                    int width = Integer.parseInt(parts[0]);
                                    int height = Integer.parseInt(parts[1]);
                                    currentDimension = new Dimension(width, height);
                                } catch(NumberFormatException ignored) {
                                    //just continue we will see what the next input brings
                                }
                            } else if(attribute_name.equalsIgnoreCase(SOUNDTYPE)) {
                                SoundType fromString = SoundType.fromString(attribute.getValue());
                                if(fromString != null) {
                                    soundType = fromString;
                                }
                                hasType = true;
                                /*
                                 * if we find the attribute but can't set it assume default which is
                                 * SFX it is possible we find another valid tag which will then
                                 * overwrite this (because it is inited to that)
                                 */
                            }
                        }
                        if(!hasName) {
                            continue;
                        }

                        if(name.equalsIgnoreCase(SOUND) && !hasType) {
                            System.out.println("Sound " + currentName + " does not have a type");
                            continue;
                        }

                        String location = eventReader.nextEvent().asCharacters().getData();

                        //System.out.println("Loading resource {type =" + name + " ,name =" +
                        // currentName + " ,location =" + location + "}");

                        try {
                            if(name.equalsIgnoreCase(SPRITE)) {
                                loadImg(clazz, currentName, location);
                            } else if(name.equalsIgnoreCase(SPRITESHEET)) {
                                loadSpriteMap(clazz, currentName, location, currentDimension);
                            } else if(name.equalsIgnoreCase(SOUND)) {
                                loadSound(clazz, currentName, location, soundType);
                            }
                        } catch(Exception e) {
                            System.out.println("Error in resource:{type =" + name + ' ' +
                                    ",name =" + currentName + " ,location =" + location + '}');
                            throw new IOException(e.getMessage(), e);
                        }

                    }
                }
            }
        } catch(XMLStreamException e) {
            e.printStackTrace();
            throw new IOException("Xml file could not be read properly");
        }


    }

    /**
     * First checks the external directories and then load the resources from the resources.xml
     * file
     * @throws IOException if any file related operation goes wrong
     */
    protected static void loadDefaultGameData() throws IOException {
        loadResources(ResourceLoader.class,
                ResourceLoader.class.getResourceAsStream("resources.xml"));
    }

    //ADVANCED LOADING
    private static void loadSpriteMap(Class<?> clazz, String name, String location, Dimension dim)
            throws IOException {
        if(spriteMaps.containsKey(name)) {
            System.out.println("WARNING: SpriteSheet being overwritten by {name = " + name +
                    " location = " + location + '}');
        }
        spriteMaps.put(name, new SpriteSheet(ImageIO.read(getInputStream(location, clazz)), dim));
    }

    private static void loadImg(Class<?> clazz, String name, String location) throws IOException {
        if(sprites.containsKey(name)) {
            System.out.println("WARNING: Image being overwritten by {name = " + name +
                    " location = " + location + '}');
        }

        sprites.put(name, Sprite.createAcceleratedSprite(getInputStream(location, clazz)));
    }

    //perhaps move to SoundHandler
    private static void loadSound(Class<?> clazz, String name, String location, SoundType type) {
        SoundHandler.addSound(name,
                new SoundData(new BufferedInputStream(getInputStream(location, clazz)), type));
    }

    private static InputStream getInputStream(String location, Class<?> defaultClass) {
        return defaultClass.getResourceAsStream(location);
    }

    /**
     * Get the {@link SpriteSheet} under the name
     * @param name the name of the SpriteSheet
     *
     * @return the SpriteSheet found
     */
    public static SpriteSheet getSpriteSheet(String name) {
        return spriteMaps.get(name);
    }

    /**
     * Check if a SpriteSheet of that name is already loaded
     * @param name the name of the SpriteSheet
     *
     * @return true if a SpriteSheet under that name is loaded
     */
    public static boolean hasSpriteSheet(String name) {
        return spriteMaps.containsKey(name);
    }

    /**
     * Check if a {@link Sprite}is already loaded
     * @param name the name of the sprite to look for
     *
     * @return true if a sprite under that name is loaded
     */
    public static boolean hasSprite(String name) {
        return sprites.containsKey(name);
    }

    /**
     * Get the image by the given name
     * @param name the name of the image requested
     *
     * @return the image or {@link Sprite#EMPTY_SPRITE} if the image is not loaded (or doesn't
     * exists)
     */
    public static Sprite getSprite(String name) {
        if(!sprites.containsKey(name)) {
            System.out.println("WARNING FILE IS NOT LOADED YET name = " + name);
            return Sprite.EMPTY_SPRITE;
        }
        return sprites.get(name);
    }

}