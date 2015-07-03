package midi;

/*
 * 	A ShortMessage contains a MIDI message that has at most two data bytes following its status byte. 
 * 	The types of MIDI message that satisfy this criterion are channel voice, channel mode, system common, 
 * 	and system real-time--in other words, everything except system exclusive and meta-events. 
 * 	The ShortMessage class provides methods for getting and setting the contents of the MIDI message.
 * 	A number of ShortMessage methods have integer parameters by which you specify a MIDI status or data byte.
 * 	If you know the numeric value, you can express it directly. 
 *	For system common and system real-time messages, you can often use the corresponding fields of ShortMessage, such as SYSTEM_RESET. 
 * 	For channel messages, the upper four bits of the status byte are specified by a command 
 * 	value and the lower four bits are specified by a MIDI channel number. 
 * 	To convert incoming MIDI data bytes that are in the form of Java's signed bytes, 
 * 	you can use the conversion code given in the MidiMessage
 * 
 */

public class QShortMessage extends QMidiMessage{
	
	/*
	 * Constructs a new ShortMessage. The contents of the new message are guaranteed to specify a valid MIDI message. 
	 * Subsequently, you may set the contents of the message using one of the setMessage methods.
	 */
	public QShortMessage() {
		this(new byte[3]);
		this.data[0] = (byte)(QMIDI.NOTE_ON & 0xFF);
		this.data[1] = (byte) 64;
		this.data[2] = (byte) 127;
		this.length = 3;
	}
	
	/*
	 * Constructs a new ShortMessage which represents a MIDI message that takes no data bytes. 
	 * The contents of the message can be changed by using one of the setMessage methods.
	 * Parameters: status the MIDI status byte
	 */
	public QShortMessage(int status) throws QInvalidMidiDataException {
		super(null);
		this.setMessage(status);
	}
	/*
	 * Constructs a new ShortMessage which represents a MIDI message that takes up to two data bytes. 
	 * If the message only takes one data byte, the second data byte is ignored. If the message does not take any data bytes, both data bytes are ignored. 
	 * The contents of the message can be changed by using one of the setMessage methods.
	 * Parameters:
	 * status the MIDI status byte
	 * data1 the first data byte
	 * data2 the second data byte
	 */
	public QShortMessage(int status, int data1, int data2) throws QInvalidMidiDataException {
		super(null);
		setMessage(status, data1, data2);
	}
	
	/*
	 * Constructs a new ShortMessage.
	 * Parameters:
	 * data an array of bytes containing the complete message. The message data may be changed using the setMessage method.
	 */
	public QShortMessage(byte[] data) {
		super(data);
	}
	
	/*
	 * Sets the parameters for a MIDI message that takes no data bytes.
	 * Parameters:
	 * status the MIDI status byte
	 */
	public void setMessage(int status) throws QInvalidMidiDataException{
		int dataLength = getDataLength(status);
		if(dataLength != 0) {
			throw new QInvalidMidiDataException("Status byte: " + status + " requires " + dataLength + " data bytes");
		}
		setMessage(status, 0, 0);
	}
	
	/*
	 * Sets the parameters for a MIDI message that takes one or two data bytes. 
	 * If the message takes only one data byte, the second data byte is ignored; 
	 * if the message does not take any data bytes, both data bytes are ignored.
	 * Parameters:
	 * status the MIDI status byte
	 * data1 the first data byte
	 * data2 the second data byte
	 */
	public void setMessage(int status, int data1, int data2) throws QInvalidMidiDataException {
		int dataLength = getDataLength(status);
		if(dataLength > 0) {
			if(data1 < 0 || data1 > 127) {
				throw new QInvalidMidiDataException("data1 out of the range: " + data1);
			}
			if(dataLength > 1) {
				if(data2 < 0 || data2 > 127) {
					throw new QInvalidMidiDataException("data2 out of the range " + data2);
				}
			}
		}
		length = dataLength + 1;
		if(data == null || data.length < length) {
			data = new byte[3];
		}
		data[0] = (byte)(status & 0xFF);
		if(length > 1) {
			data[1] = (byte)(data1 & 0xFF);
			if(length > 2) {
				data[2] = (byte)(data2 & 0xFF);
			}
		}
	}
	
	/*
	 * Sets the short message parameters for a channel message which takes up to two data bytes. 
	 * If the message only takes one data byte, the second data byte is ignored; 
	 * if the message does not take any data bytes, both data bytes are ignored.
	 * Parameters:
	 * command the MIDI command represented by this message
	 * channel the channel associated with the message
	 * data1 the first data byte
	 * data2 the second data byte
	 */
	public void setMessage(int command, int channel, int data1, int data2) throws QInvalidMidiDataException {
		if(command >= 0xF0 || command < 0x80) {
			throw new QInvalidMidiDataException("command out of the range: 0x" + Integer.toHexString(command));
		}
		if((channel & 0xFFFFFFF0) != 0) {
			throw new QInvalidMidiDataException("channel out of the range: " + channel);
		}
		setMessage((command & 0xF0) | (channel & 0x0F), data1,data2);
	}
	
	/*
	 * Obtains the MIDI channel associated with this event. This method assumes that the event is a MIDI channel message; 
	 * if not, the return value will not be meaningful.
	 * Returns:
	 * MIDI channel associated with the message.
	 */
	public int getChannel() {
		return (getStatus() & 0x0F);
	}
	
	/*
	 * Obtains the MIDI command associated with this event. This method assumes that the event is a MIDI channel message; 
	 * if not, the return value will not be meaningful.
	 */
	public int getCommand() {
		return (getStatus() & 0xF0);
	}
	
	public int getData1() {
		if(length > 1) {
			return data[1] & 0xFF;
		}
		return 0;
	}
	
	public int getData2() {
		if(length > 2) {
			return data[2] & 0xFF;
		}
		return 0;
	}
	
	public Object clone() {
		byte[] newData = new byte[length];
		System.arraycopy(data, 0, newData, 0, newData.length);
		QShortMessage msg = new QShortMessage(newData);
		return msg;
	}
	
	/*
	 * Retrieves the number of data bytes associated with a particular status byte value.
	 * Parameters:
	 * status status byte value, which must represent a short MIDI message
	 * Returns:
	 * data length in bytes (0, 1, or 2)
	 */
	protected final int getDataLength(int status) throws QInvalidMidiDataException {
		// system common and system real-time messages
		switch(status) {
			case 0xF6:    // Tune request
			case 0xF7:    // EOX
				
	    // System real-time messages
			case 0xF8:  
			case 0xF9:
			case 0xFA:
			case 0xFB:
			case 0xFC:
			case 0xFD:
			case 0xFE:
			case 0xFF:
				return 0;
			case 0xF1:
			case 0xF3:
				return 1;
			case 0xF2:
				return 2;
			default:
		}
		// channel voice and mode messages
		switch(status & 0xF0) {
			case 0x80:
			case 0x90:
			case 0xA0:
			case 0xB0:
			case 0xE0:
				return 2;
			case 0xC0:
			case 0xD0:
				return 1;
			default:
				throw new QInvalidMidiDataException("Invalid status byte: " + status);
		}
	}

    @Override
    public String toString() {
        String string = "";
        int statusByte = (int)data[0] & 0xFF;
        int dataOne = (int)data[1] & 0xFF;
        if(data.length > 2) {
            int dataTwo = (int) data[2] & 0xFF;
            string += " Object [QShortMessage] Type [" + MessageType.toString() + "] Status byte(" + Integer.toHexString(statusByte) + "h) " + QMIDI.StatusByte(statusByte)
                    + " data[1] = " + Integer.toHexString(dataOne) + " data[2] = " + Integer.toHexString(dataTwo);
        } else {
            string += " Object [QShortMessage] Type [" + MessageType.toString() + "] Status byte(" + Integer.toHexString(statusByte) + "h) " + QMIDI.StatusByte(statusByte)
                    + " data[1] = " + Integer.toHexString(dataOne);
        }
        if((data[0] & 0xF0) == 0x90 || (data[0] & 0xF0) == 0x80) {
            string += " NOTE : " + QMIDI.getStringForNote((int)data[1]);
        }
        return string;
    }
}
