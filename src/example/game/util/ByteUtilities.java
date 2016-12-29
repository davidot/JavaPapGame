package example.game.util;

/**
 * This class contains some useful utilities method for bit shifting and other binary operations
 * @author davidot
 */
public class ByteUtilities {

    private ByteUtilities() {
    }


    /**
     * Method to check if bit in byte is set<br> example: isBitSet((byte)0b00100000,6) return true
     * @param num        number to check bit from
     * @param bitToCheck bit to check
     *
     * @return wheter the bit specified in the number is set
     */
    public static boolean isBitSet(byte num, int bitToCheck) {
        return (num & 1 << bitToCheck) != 0;
    }

    /**
     * Sets a bit to one in a byte then returns new byte
     * @param original byte
     * @param bitToSet bit to set
     *
     * @return byte with changed bit
     */
    public static byte setBit(byte original, int bitToSet) {
        return (byte) (original | (1 << bitToSet));
    }

    /**
     * Sets a bit to zero in a byte then returns new byte
     * @param original   byte
     * @param bitToUnSet bit to set
     *
     * @return byte with changed bit
     */
    public static byte unsetBit(byte original, int bitToUnSet) {
        return (byte) (original & ~(1 << bitToUnSet));
    }

    /**
     * Splits a short into four parts<br> The array returned goes from the front to the back<br> So
     * the lowest 4 bits (1&2&4&8) are in the array[0] and so on
     * @param num the number to split
     *
     * @return the array of split bytes
     */
    public static byte[] splitShortFourParts(short num) {
        byte[] data = new byte[4];
        for(int i = 0; i < data.length; i++) {
            data[i] = (byte) ((num >> i * 4) & 15);
            //15 = 0b00001111
        }
        return data;
    }

    /**
     * Convert a byte array into a short takes the 4 smallest bits of every byte, you can give an
     * array with less or more than 4 bytes put you have a risk of overflow or an unexpected result
     * @param bytes the bytes which to convert
     *
     * @return the short created
     */
    public static short bytesToShort(byte[] bytes) {
        short num = 0;
        for(int i = 0; i < bytes.length; i++) {
            num += (bytes[i] & 0xF) << (i * 4);
        }
        return num;
    }

}
