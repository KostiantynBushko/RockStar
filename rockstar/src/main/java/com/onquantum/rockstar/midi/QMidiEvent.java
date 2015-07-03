package midi;

public class QMidiEvent {
	// The MIDI message for this event.
	private final QMidiMessage message;
	
	// The tick value for this event.
	private long tick;
	
	/*
	 * Constructs a new MidiEvent.
	 * Parameters:
	 * message the MIDI message contained in the event
	 * tick the time-stamp for the event, in MIDI ticks
	 */
	public QMidiEvent(QMidiMessage message, long tick) {
		this.message = message;
		this.tick = tick;
	}
	
	/*
	 * Obtains the MIDI message contained in the event.
	 * Returns:
	 * the MIDI message
	 */
	public QMidiMessage getMessage() {
		return message;
	}
	
	/*
	 * Sets the time-stamp for the event, in MIDI ticks
	 * Parameters:
	 * tick the new time-stamp, in MIDI ticks
	 */
	public void setTick(long tick) {
		this.tick = tick;
	}
	
	/*
	 * Obtains the time-stamp for the event, in MIDI ticks
	 * Returns:
	 * the time-stamp for the event, in MIDI ticks
	 */
	public long getTick() {
		return this.tick;
	}
}
