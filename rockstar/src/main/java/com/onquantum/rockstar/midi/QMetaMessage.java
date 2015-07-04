package com.onquantum.rockstar.midi;

   /*
    * A MetaMessage is a MidiMessage that is not meaningful to synthesizers,
    * but that can be stored in a MIDI file and interpreted by a sequencer program.
    * (See the discussion in the MidiMessage class description.)
    * The Standard MIDI Files specification defines various types of meta-events,
    * such as sequence number, lyric, cue point, and set tempo. There are also meta-events for such information as lyrics,
    * copyrights, tempo indications, time and key signatures, markers, etc.
    * For more information, see the Standard MIDI Files 1.0 specification, which is part of the Complete MIDI 1.0
    * Detailed Specification published by the MIDI Manufacturer's Association (http://www.midi.org).
    * When data is being transported using MIDI wire protocol, a ShortMessage with the status value 0xFF represents a system reset message.
    * In MIDI files, this same status value denotes a MetaMessage.
    * The types of meta-message are distinguished from each other by the first byte that follows the status byte 0xFF.
    * The subsequent bytes are data bytes. As with system exclusive messages, there are an arbitrary number of data bytes,
    * depending on the type of MetaMessage.
    */

public class QMetaMessage extends QMidiMessage {

     private static byte[] defaultMessage = { (byte)QMIDI.META,0 };

     private int dataLength = 0;

     /*
      * Constructs a new MetaMessage. The contents of the message are not set here; use setMessage to set them subsequently.
      */
     public QMetaMessage() {
         this(defaultMessage);
     }

     /*
      * Constructs a new MetaMessage.
      * Parameters:
      * data an array of bytes containing the complete message. The message data may be changed using the setMessage method.
      */
     protected QMetaMessage(byte[] data) {
         super(data);
         if(data.length >= 3) {
             dataLength = data.length - 3;
             int pos = 2;
             while(pos < dataLength && (data[pos] & 0x80) != 0) {
                 dataLength--;
                 pos++;
             }
         }
     }

    /*
     * Sets the message parameters for a MetaMessage. Since only one status byte value, 0xFF, is allowed for meta-messages,
     * it does not need to be specified here. Calls to getStatus return 0xFF for all meta-messages.
     * The type argument should be a valid value for the byte that follows the status byte in the MetaMessage.
     * The data argument should contain all the subsequent bytes of the MetaMessage. In other words,
     * the byte that specifies the type of MetaMessage is not considered a data byte.
     * Parameters:
     * type meta-message type (must be less than 128)
     * data the data bytes in the MIDI message
     * length the number of bytes in the data byte array
     * Throws:
     * InvalidMidiDataException if the parameter values do not specify a valid MIDI meta message
     */
    public void setMessage(int type, byte[] data, int length) throws QInvalidMidiDataException {
        if(type >= 128 || type < 0) {
            throw new QInvalidMidiDataException("Invalid meta event with type " + type);
        }
        if((length > 0 && length > data.length) || length < 0) {
            throw new QInvalidMidiDataException("length out of the bounds: " + length);
        }
        this.length = 2 + getVarIntLength(length) + length;
        this.dataLength = length;
        this.data = new byte[this.length];
        this.data[0] = (byte) QMIDI.META;        // status value for MetaMessages (meta events)
        this.data[1] = (byte) type;              // MetaMessage type
        writeVarInt(this.data, 2, length);       // write the length as a variable int
        if (length > 0) {
            System.arraycopy(data, 0, this.data, this.length - this.dataLength, this.dataLength);
        }
    }

    /*
     * Obtains the type of the MetaMessage.
     * Returns:
     * an integer representing the MetaMessage type
     */
    public int getType() {
        if(length >= 2) {
            return data[1] & 0xFF;
        }
        return 0;
    }

    /*
     * Obtains a copy of the data for the meta message.
     * The returned array of bytes does not include the status byte or the message length data.
     * The length of the data for the meta message is the length of the array.
     * Note that the length of the entire message includes the status byte and the meta message type byte,
     * and therefore may be longer than the returned array.
     * Returns:
     * array containing the meta message data.
     */
    public byte[] getData() {
        byte[] returnArray = new byte[dataLength];
        System.arraycopy(data,(length - dataLength), returnArray, 0, dataLength);
        return returnArray;
    }

    /*
     * Creates a new object of the same class and with the same contents as this object.
     * Returns:
     * a clone of this instance
     */
    public Object clone() {
        byte[] newData = new byte[length];
        System.arraycopy(data, 0, newData, 0, newData.length);
        QMetaMessage metaMessage = new QMetaMessage(newData);
        return metaMessage;
    }

    private int getVarIntLength(long value) {
        int length = 0;
        do {
            value = value >> 7;
            length++;
        } while (value > 0);
        return length;
    }

    private final static long mask = 0x7F;

    private void writeVarInt(byte[] data, int off, long value) {
        int shift=63; // number of bitwise left-shifts of mask
        // first screen out leading zeros
        while ((shift > 0) && ((value & (mask << shift)) == 0)) shift-=7;
        // then write actual values
        while (shift > 0) {
            data[off++]=(byte) (((value & (mask << shift)) >> shift) | 0x80);
            shift-=7;
        }
        data[off] = (byte) (value & mask);
    }
}