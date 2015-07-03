package midi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class QMidiFileReader {
	/*
	 * Obtains the MIDI file format of the input stream provided. The stream must point to valid MIDI file data. In general, 
	 * MIDI file readers may need to read some data from the stream before determining whether they support it. 
	 * These parsers must be able to mark the stream, read enough data to determine whether they support the stream, 
	 * and, if not, reset the stream's read pointer to its original position. If the input stream does not support this, 
	 * this method may fail with an IOException.
	 * Parameters: stream the input stream from which file format information should be extracted
	 * Returns: a MidiFileFormat object describing the MIDI file format
	 */
	public abstract QMidiFileFormat getMidiFileFormat(InputStream stream) throws QInvalidMidiDataException, IOException;
	
	/*
	 * Obtains the MIDI file format of the File provided. The File must point to valid MIDI file data.
	 * Parameters:
	 * file the File from which file format information should be extracted
	 * Returns:
	 * a MidiFileFormat object describing the MIDI file format
	 */
	public abstract QMidiFileFormat getMidiFileFormat(File file) throws QInvalidMidiDataException, IOException;
}
