package com.onquantum.rockstar.midi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QMidiFileFormat {
	public static final int UNKNOWN_LENGTH = -1;
	protected int type;
	protected float divisionType;
	protected int resolution;
	protected int byteLength;
	protected long microsecondLength;
	private HashMap<String, Object> properties;
	
	/*
	 * Constructs a MidiFileFormat.
	 * Parameters:
	 * type the MIDI file type (0, 1, or 2)
	 * divisionType the timing division type (PPQ or one of the SMPTE types)
	 * resolution the timing resolution
	 * bytes the length of the MIDI file in bytes, or UNKNOWN_LENGTH if not known
	 * microseconds the duration of the file in microseconds, or UNKNOWN_LENGTH if not known
	 */
	public QMidiFileFormat(int type, float divisionType, int resolution, int bytes, long microseconds) {
		this.type = type;
		this.divisionType = divisionType;
		this.resolution = resolution;
		this.byteLength = bytes;
		this.microsecondLength = microseconds;
		this.properties = null;
	}
	
	/*
	 * Construct a MidiFileFormat with a set of properties.
	 * Parameters:
	 * type the MIDI file type (0, 1, or 2)
	 * divisionType the timing division type (PPQ or one of the SMPTE types)
	 * resolution the timing resolution
	 * bytes the length of the MIDI file in bytes, or UNKNOWN_LENGTH if not known
	 * microseconds the duration of the file in microseconds, or UNKNOWN_LENGTH if not known
	 * properties a Map<String,Object> object with properties
	 */
	public QMidiFileFormat(int type, float divisionType, int resolution, int bytes, long microseconds, Map<String, Object> properties) {
		this(type, divisionType, resolution, bytes, microseconds);
		this.properties = new HashMap<String, Object>(properties);
	}
	
	/*
	 * Obtains the MIDI file type.
	 * Returns:
	 * the file's type (0, 1, or 2)
	 */
	public int getType() {
		return type;
	}
	
	/*
	 * Obtains the timing division type for the MIDI file.
	 * Returns:
	 * the division type (PPQ or one of the SMPTE types)
	 */
	public float getDivisionType() {
		return divisionType;
	}
	/*
	 * Obtains the length of the MIDI file, expressed in 8-bit bytes.
	 * Returns:
	 * the number of bytes in the file, or UNKNOWN_LENGTH if not known
	 */
	public int getByteLength() {
		return byteLength;
	}
	
	/*
	 * Obtains the length of the MIDI file, expressed in microseconds.
	 * Returns:
	 * the file's duration in microseconds, or UNKNOWN_LENGTH if not known
	 */
	public long getMicrosecondLength() {
		return microsecondLength;
	}
	
	/*
	 * Obtain an unmodifiable map of properties. The concept of properties is further explained in the class description.
	 * Returns: a Map<String,Object> object containing all properties. If no properties are recognized, an empty map is returned.
	 */
	public Map<String,Object> properties() {
		Map<String,Object> ret;
		if (properties == null) {
			ret = new HashMap<String,Object>(0);
		} else {
			ret = (Map<String,Object>) (properties.clone());
		}
		return (Map<String,Object>) Collections.unmodifiableMap(ret);
	}
	
	/*
	 * Obtain the property value specified by the key. The concept of properties is further explained in the class description.
	 * If the specified property is not defined for a particular file format, this method returns null.
	 * Parameters:
	 * key the key of the desired property
	 * Returns:
	 * the value of the property with the specified key, or null if the property does not exist.
	 */
	public Object getProperty(String key) {
		if (properties == null) {
			return null;
		}
		return properties.get(key); 
	}
}
