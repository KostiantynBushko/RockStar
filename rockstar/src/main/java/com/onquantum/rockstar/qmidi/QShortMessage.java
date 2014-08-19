package com.onquantum.rockstar.qmidi;

public class QShortMessage extends QMidiMessage{
	
	// Status byte for MIDI Time Code Quarter Frame message (0xF1, or 241).
	public static final int MIDI_TIME_CODE = 0xF1;
	
	// Status byte for Song Position Pointer message (0xF2, or 242).
	public static final int SONG_POSITION_POINTER = 0xF2;
	
	// Status byte for MIDI Song Select message (0xF3, or 243).
	public static final int SONG_SELECT = 0xF3;
	
	// Status byte for Tune Request message (0xF6, or 246).
	public static final int TUNE_REQUEST = 0xF6;
	
	// Status byte for End of System Exclusive message (0xF7, or 247).
	public static final int END_OF_EXCLUSIVE = 0xF7;
	
	// Status byte for Timing Clock message (0xF8, or 248).
	public static final int TIMING_CLOCK = 0xF8;
	
	// Status byte for Start message (0xFA, or 250).
	public static final int START = 0xFA;
	
	// Status byte for Continue message (0xFB, or 251).
	public static final int CONTINUE = 0xFB;
	
	// Status byte for Stop message (0xFC, or 252).
	public static final int STOP = 0xFC;
	
	// Status byte for Active Sensing message (0xFE, or 254).
	public static final int ACTIVE_SENSING = 0xFE;
	
	// Status byte for System Reset message (0xFF, or 255).
	public static final int SYSTEM_RESET = 0xFF;
	
	// Command value
	public static final int NOTE_OFF         = 0x80;
	public static final int NOTE_ON          = 0x90;
	public static final int POLY_PRESSURE    = 0xA0;
	public static final int CONTROL_CHANGE   = 0xB0;
	public static final int PROGRAM_CHANGE   = 0xC0;
	public static final int CHANNEL_PRESSURE = 0xD0;
	public static final int PITCH_BEND       = 0xE0;
	
	public QShortMessage() {
		this(new byte[3]);
		this.data[0] = (byte)(NOTE_ON & 0xFF);
		this.data[1] = (byte) 64;
		this.data[2] = (byte) 127;
		this.length = 3;
	}
	
	public QShortMessage(byte[] data) {
		super(data);
	}
	
	protected final int getDataLength(int status) {
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
				return -1;
		}
	}
	
	public int getChannel() {
		return (getStatus() & 0x0F);
	}
	
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
	
}
