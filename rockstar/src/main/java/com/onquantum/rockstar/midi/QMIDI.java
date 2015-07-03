package midi;

/*
 *  Status byte:
 *  1 s s s n n n n 
 *  s s s   - denote the type of message, status byte F0-FF
 *  n n n n - denote the channel number, status byte 8n-En (b 10000000 - )
 */

public class QMIDI {

    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

	//Status byte for MetaMessage (0xFF, or 255), which is used in MIDI files. It has the same value as SYSTEM_RESET.
	public static final int META = 0xFF;

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
	
	
	public enum MIDIMsgType {
		undefined,
		channel,
        system_exclusive,
		system_common,
        system_real_time,
        meta
	}
	
	public enum MIDIMessage {
		
	}

    public static String StatusByte(int statusByte) {
        String returnValue = "none";
        switch (statusByte) {
            // meta message status 0xFF
            case 0x01: returnValue = "Text_Event"; break;                    // Text Event
            case 0x02: returnValue = "Copyright_Notice"; break;              // Copyright Notice
            case 0x03: returnValue = "Sequence_Track_Name"; break;           // Sequence/Track Name
            case 0x04: returnValue = "Instrument_Name"; break;               // Instrument Name
            case 0x05: returnValue = "Lyric"; break;                         // Lyric
            case 0x06: returnValue = "Marker"; break;                        // Marker
            case 0x07: returnValue = "Cue_Point"; break;                     // Cue Point
            case 0x20: returnValue = "MIDI_Channel_Prefix"; break;           // MIDI Channel Prefix
            case 0x2F: returnValue = "End_of_Track"; break;                  // End of Track
            case 0x51: returnValue = "Tempo"; break;                         // Tempo (in microseconds per MIDI quarter-note)
            case 0x54: returnValue = "SMPTE_Offset"; break;                  // SMPTE Offset
            case 0x58: returnValue = "Time_Signature"; break;                // Time Signature
            case 0x59: returnValue = "Key_Signature"; break;                 // Key Signature
            case 0x7F: returnValue = "Sequencer_Specific_Meta_Event"; break; // Sequencer Specific Meta-Event

            case 0xF1: returnValue = "MTC_Quartet_Frame"; break;             // MTC Quartet Frame
            case 0xF2: returnValue = "Song_Position_Pointer"; break;         // Song Position Pointer
            case 0xF3: returnValue = "Song_Select"; break;                   // Song Select
            case 0xF4: returnValue = "Undefined"; break;                     // Undefined
            case 0xF5: returnValue = "Undefined"; break;                     // Undefined
            case 0xF6: returnValue = "Tune_Request"; break;                  // Tune Request
            case 0xF7: returnValue = "End_Of_Exclusive"; break;              // End Of exclusive
            case 0xF8: returnValue = "Timing_Clock"; break;                  // Timing Clock
            case 0xF9: returnValue = "Timing_Tick"; break;                   // Timing Tick
            case 0xFA: returnValue = "Start"; break;                         // Start
            case 0xFB: returnValue = "Continue"; break;                      // Continue
            case 0xFC: returnValue = "Stop"; break;                          // Stop
            case 0xFD: returnValue = "Undefined"; break;                     // Undefined
            case 0xFE: returnValue = "Active_Sensing"; break;                // Active Sensing

            // Chanel voice message
            case 0x80: returnValue = "Note_Off"; break;                      // Note off
            case 0x90: returnValue = "Note_On"; break;                       // Note on
            case 0xA0: returnValue = "Polyphonic_Key_Pressure"; break;       // Polyphonic key pressure
            case 0xB0: returnValue = "Control_Channel"; break;               // Control channel
            case 0xC0: returnValue = "Program_Channel"; break;               // Program channel
            case 0xD0: returnValue = "Channel_Pressure"; break;              // Channel pressure
            case 0xE0: returnValue = "Pitch_Wheel_Change_"; break;           // Pitch wheel change
            default:
                break;
        }
        return returnValue;
    }

    public static String getStringForNote(int note) {
        int index = (note % 12);
        int octave = (note / 12) - 1;
        return NOTE_NAMES[index] + octave;
    }
}
