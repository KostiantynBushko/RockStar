package com.onquantum.rockstar.midi;

public class QSysexMessage extends QMidiMessage{
	/*
	 * Status byte for System Exclusive message (0xF0, or 240).
	 */
	public static final int SYSTEM_EXCLUSIVE = 0xF0; // 240 
	
	/*
	 * Status byte for Special System Exclusive message (0xF7, or 247), which is used in MIDI files. 
	 * It has the same value as END_OF_EXCLUSIVE, which is used in the real-time "MIDI wire" protocol.
	 */
	public static final int SPECIAL_SYSTEM_EXCLUSIVE = 0xF7; // 247
	
	/*
	 * Constructs a new SysexMessage. The contents of the new message are guaranteed to specify a valid MIDI message. 
	 * Subsequently, you may set the contents of the message using one of the setMessage methods.
	 */
	public QSysexMessage() {
		this(new byte[2]);
		data[0] = (byte) (SYSTEM_EXCLUSIVE & 0xFF);
		data[1] = (byte) (QMIDI.END_OF_EXCLUSIVE & 0xFF);
	}
	
	public QSysexMessage(byte[] data, int length) throws QInvalidMidiDataException {
		super(null);
		setMessage(data, length);
	}
	public QSysexMessage(int status, byte[] data, int length) throws QInvalidMidiDataException {
		super(null);
		setMessage(status, data, length);
	}
	protected QSysexMessage(byte[] data) {
		super(data);
	}
	
    public void setMessage(byte[] data, int length) throws QInvalidMidiDataException {
    	int status = (data[0] & 0xFF);
    	if ((status != 0xF0) && (status != 0xF7)) {
    		throw new QInvalidMidiDataException("Invalid status byte for sysex message: 0x" + Integer.toHexString(status));
    	}
    	super.setMessage(data, length);
    }
    
    public void setMessage(int status, byte[] data, int length) throws QInvalidMidiDataException {
    	if ( (status != 0xF0) && (status != 0xF7) ) {
    		throw new QInvalidMidiDataException("Invalid status byte for sysex message: 0x" + Integer.toHexString(status));
    	}
    	if (length < 0 || length > data.length) {
    		throw new IndexOutOfBoundsException("length out of bounds: "+length);
    	}
    	this.length = length + 1;
    	if (this.data==null || this.data.length < this.length) {
    		this.data = new byte[this.length];
    	}
    	this.data[0] = (byte) (status & 0xFF);
    	if (length > 0) {
    		System.arraycopy(data, 0, this.data, 1, length);
    	}
    }
}
