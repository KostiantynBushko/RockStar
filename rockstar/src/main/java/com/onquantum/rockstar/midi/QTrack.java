package com.onquantum.rockstar.midi;

import java.util.ArrayList;
import java.util.HashSet;

public class QTrack {
	// the list containing the events
	private ArrayList eventsList = new ArrayList();
	// use a hash set to detect duplicate events in add(MidiEvent)
	private HashSet set = new HashSet();
	private QMidiEvent eotEvent; 
	
	public QTrack() {
		QMetaMessage eot = new ImmutableEndOfTrack();
		eotEvent = new QMidiEvent(eot, 0);
		eventsList.add(eotEvent);
		set.add(eotEvent);
	}
	
	/*
	 * Adds a new event to the track. However, if the event is already contained in the track, 
	 * it is not added again. The list of events is kept in time order, meaning that this event inserted at 
	 * the appropriate place in the list, not necessarily at the end.
	 * Parameters: event the event to add
	 * Returns: true if the event did not already exist in the track and was added, otherwise false
	 */
	public boolean add(QMidiEvent event) {
		if(event == null)
			return false;
		synchronized (eventsList){
			if(!set.contains(event)) {
				int eventsCount = eventsList.size();
				
				// get the last event
				QMidiEvent lastEvent = null;
				if(eventsCount > 0) {
					lastEvent = (QMidiEvent)eventsList.get(eventsCount - 1);
				}
				// check that we have a correct and-of-track
				if(lastEvent != eotEvent) {
					// if there is no eot event, add our immutable instance again
					if(lastEvent != null) {
						// set eotEvent's tick to the last tick of the track
						eotEvent.setTick(lastEvent.getTick());
					} else {
						// if the event list is empty, just set the tick to 0
						eotEvent.setTick(0);
					}
					// we needn't to check for duplicate of eotEvent in eventsList
					// since then it would appear in the set.
					eventsList.add(eotEvent);
					set.add(eotEvent);
					eventsCount = eventsList.size();
				}
				
				// first see if we are trying to add 
				// and end of track event
				if(QMidiUtils.isMetaEndOfTrack(event.getMessage())) {
					// since end of track event is useful
					// for delays at the end of a track, we want to keep
					// the tick value requested here if it is greater
					// than the one of the eot we are maintaining.
					// Otherwise, we only want a single eot event, so ignore.
					if(event.getTick() > eotEvent.getTick()) {
						eotEvent.setTick(event.getTick());
					}
					return true;
				}
				// prevent duplicates
				set.add(event);
				
				// insert event such that events is stored in increasing
				int i = eventsCount;
				for( ; i > 0; i--) {
					if(event.getTick() >= ((QMidiEvent)eventsList.get(i-1)).getTick()) {
						break;
					}
				}
				if(i == eventsCount) {
					// wear adding event after that
					// tick value of our eot, so push the eot out.
					// Always add at the and for better performance:
					// this saves all the checks and arraycopy when inserting
					
					// overwrite eot with new event
					eventsList.set(eventsCount - 1, event);
					// set the new time of eot, if necessary
					if(eotEvent.getTick() < event.getTick()) {
						eotEvent.setTick(event.getTick());
					}
					// add eot again at the end
					eventsList.add(eotEvent);
				} else {
					eventsList.add(i,event);
				}
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Removes the specified event from the track.
	 * Parameters:
	 * event the event to remove
	 * Returns:
	 * true if the event existed in the track and was removed, otherwise false
	 */
	public boolean remove(QMidiEvent event) {
		// this implementation allows removing EOT event.
		// pretty bad, but would probably be to risky to
		// change behavior now, in case someone does tricks to:
		//
		// while (track.size() > 0) track.remove(track.get(track.size() - 1));
		
		// also, would it make sense to adjust the EOT's time
		// to the last event, if the last not-EOT event is removed?
		// Or: document that the ticks() length will not be reduced
		// by deleting events (unless the EOT event is removed)
		synchronized(eventsList) {
			if(set.remove(event)) {
				int i = eventsList.indexOf(event);
				if(i >= 0) {
					eventsList.remove(i);
					return true;
				}
			}
		}
		return true;
	}
	
	/*
	 * Obtains the event at the specified index.
	 * Parameters: index the location of the desired event in the event vector
	 */
	public QMidiEvent get(int index) throws ArrayIndexOutOfBoundsException {
		try {
			synchronized(eventsList) {
				return (QMidiEvent)eventsList.get(index);
			}
		}catch(ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException(e.getMessage());
		}
	}
	
	/*
	 * Obtains the number of events in this track.
	 * Returns: the size of the track's event vector
	 */
	public int size() { 
		synchronized(eventsList) { 
			return eventsList.size();
		} 
	}
	
	/*
	 * Obtains the length of the track, expressed in MIDI ticks. 
	 * (The duration of a tick in seconds is determined by the timing resolution of the Sequence containing this track, 
	 * and also by the tempo of the music as set by the sequencer.)
	 * Returns: the duration, in ticks
	 */
	public long ticks() {
		long ret = 0;
		synchronized(eventsList) {
			if(eventsList.size() > 0) {
				ret = ((QMidiEvent)eventsList.get(eventsList.size() - 1)).getTick();
			}
		}
		return ret;
	}
	
	private static class ImmutableEndOfTrack extends QMetaMessage {
		private ImmutableEndOfTrack() {
			super(new byte[3]);
			data[0] = (byte) QMIDI.META;
			data[1] = QMidiUtils.META_END_OF_TRACK_TYPE;
			data[2] = 0;
		}
		public void setMessage(int type, byte[] data, int length) throws QInvalidMidiDataException {
			throw new QInvalidMidiDataException("cannot modify end of track message");
		}
	}

}
