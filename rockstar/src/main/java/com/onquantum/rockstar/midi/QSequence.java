package com.onquantum.rockstar.midi;

import java.util.Vector;

public class QSequence {
	
	// The tempo-based timing type, for which the resolution is expressed in pulses (ticks) per quarter note.
	public static final float PPQ = 0.0f;
	// The S M P T E-based timing type with 24 frames per second (resolution is expressed in ticks per frame).
	public static final float SMPTE_24 = 24.0f;
	// The S M P T E-based timing type with 25 frames per second (resolution is expressed in ticks per frame).
	public static final float SMPTE_25 = 25.0f;
	// The S M P T E-based timing type with 29.97 frames per second (resolution is expressed in ticks per frame).
	public static final float SMPTE_30DROP = 29.97f;
	// The S M P T E-based timing type with 30 frames per second (resolution is expressed in ticks per frame).
	public static final float SMPTE_30 = 30.0f;
	
	// The timing division type of the sequence.
	protected float divisionType;
	// The timing resolution of the sequence.
	protected int resolution;
	// The MIDI tracks in this sequence.
	protected Vector<QTrack> tracks = new Vector<QTrack>();
	
	
	/*
	 * Constructs a new MIDI sequence with the specified timing division type and timing resolution. 
	 * The division type must be one of the recognized MIDI timing types. 
	 * For tempo-based timing, divisionType is PPQ (pulses per quarter note) and the resolution is specified in ticks per beat.
	 * For S M T P E timing, divisionType specifies the number of frames per second and the resolution is specified in ticks per frame.
	 * The sequence will contain no initial tracks. 
	 * Tracks may be added to or removed from the sequence using createTrack() and deleteTrack(javax.sound.midi.Track).
	 * Parameters:
	 * divisionType the timing division type (PPQ or one of the S M P T E types)
	 * resolution the timing resolution
	 */
	public QSequence(float divisionType, int resolution) throws QInvalidMidiDataException {
		if (divisionType == PPQ) 
			this.divisionType = PPQ;
		else if(divisionType == SMPTE_24)
			this.divisionType = SMPTE_24;
		else if(divisionType == SMPTE_25)
			this.divisionType = SMPTE_25;
		else if(divisionType == SMPTE_30DROP) 
			this.divisionType = SMPTE_30DROP;
		else if(divisionType == SMPTE_30)
			this.divisionType = SMPTE_30;
		else throw new QInvalidMidiDataException("Unsuported division type " + divisionType);
		this.resolution = resolution;
	}
	/*
	 * Constructs a new MIDI sequence with the specified timing division type, 
	 * timing resolution, and number of tracks. 
	 * The division type must be one of the recognized MIDI timing types. 
	 * For tempo-based timing, divisionType is PPQ (pulses per quarter note) and the resolution is specified in ticks per beat. 
	 * For S M T P E timing, divisionType specifies the number of frames per second and the resolution
	 * is specified in ticks per frame. The sequence will be initialized with the number of tracks specified by numTracks. 
	 * These tracks are initially empty (i.e. they contain only the meta-event End of Track). 
	 * The tracks may be retrieved for editing using the getTracks() method. 
	 * Additional tracks may be added, or existing tracks removed, using createTrack() and deleteTrack(javax.sound.midi.Track).
	 * Parameters:divisionType the timing division type (PPQ or one of the S M P T E types)
	 * resolution the timing resolution
	 * numTracks the initial number of tracks in the sequence.
	 */
	public QSequence(float divisionType, int resolution, int numberTrack) throws QInvalidMidiDataException{
		if (divisionType == PPQ) 
			this.divisionType = PPQ;
		else if(divisionType == SMPTE_24)
			this.divisionType = SMPTE_24;
		else if(divisionType == SMPTE_25)
			this.divisionType = SMPTE_25;
		else if(divisionType == SMPTE_30DROP) 
			this.divisionType = SMPTE_30DROP;
		else if(divisionType == SMPTE_30)
			this.divisionType = SMPTE_30;
		else throw new QInvalidMidiDataException("Unsupported division type " + divisionType);
		this.resolution = resolution;
		for(int i = 0; i < numberTrack; i++) {
			tracks.addElement(new QTrack());
		}
	}

    public QSequence(float divisionType, int resolution, Vector<QTrack>qTracks) throws QInvalidMidiDataException{
        if (divisionType == PPQ)
            this.divisionType = PPQ;
        else if(divisionType == SMPTE_24)
            this.divisionType = SMPTE_24;
        else if(divisionType == SMPTE_25)
            this.divisionType = SMPTE_25;
        else if(divisionType == SMPTE_30DROP)
            this.divisionType = SMPTE_30DROP;
        else if(divisionType == SMPTE_30)
            this.divisionType = SMPTE_30;
        else throw new QInvalidMidiDataException("Unsupported division type " + divisionType);
        this.resolution = resolution;
        tracks = qTracks;
    }

	/*
	 * Obtains the timing division type for this sequence.
	 * Returns:
	 * the division type (PPQ or one of the SMPTE types) 
	 */
	public float getDivisionType() {
		return this.divisionType;
	}
	
	/*
	 * Obtains the timing resolution for this sequence. If the sequence's division type is PPQ, 
	 * the resolution is specified in ticks per beat. For S M T P E timing, the resolution is specified in ticks per frame.
	 * Returns:
	 * the number of ticks per beat (PPQ) or per frame (S M P T E)
	 */
	public int getResolution() { 
		return resolution;
	}
	
	/*
	 * Creates a new, initially empty track as part of this sequence. 
	 * The track initially contains the meta-event End of Track. 
	 * The newly created track is returned. All tracks in the sequence may be retrieved using getTracks(). 
	 * Tracks may be removed from the sequence using deleteTrack(javax.sound.midi.Track).
	 * Returns:the newly created track
	 */
	public QTrack createTrack() {
		QTrack track = new QTrack();
		tracks.addElement(track);
		return track;
	}
	
	/*
	 * Removes the specified track from the sequence.
	 * Parameters:
	 * track the track to remove
	 * Returns: true if the track existed in the track and was removed, otherwise false.
	 */
	public boolean deleteTrack(QTrack track) {
		synchronized(tracks) {
			return tracks.removeElement(track);
		}
	}
	
	/*
	 * Obtains an array containing all the tracks in this sequence. 
	 * If the sequence contains no tracks, an array of length 0 is returned.
	 * Returns: the array of tracks
	 */
	public QTrack[] getTracks() {
		return (QTrack[]) tracks.toArray(new QTrack[tracks.size()]);
	}
	
	/*
	 * Obtains the duration of this sequence, expressed in microseconds.
	 * Returns:this sequence's duration in microseconds.
	 */
	public long getMicrosecondLength() {
		return QMidiUtils.tick2microsecond(this, getTickLength(), null);
	}
	
	/*
	 * Obtains the duration of this sequence, expressed in MIDI ticks.
	 * Returns: this sequence's length in ticks
	 */
	public long getTickLength() {
		long length = 0;
		synchronized(tracks) {
			for(int i = 0; i<tracks.size(); i++) {
				long temp = ((QTrack)tracks.elementAt(i)).ticks();
				if(temp > length) {
					length = temp;
				}
			}
			return length;
		}
	}
}
