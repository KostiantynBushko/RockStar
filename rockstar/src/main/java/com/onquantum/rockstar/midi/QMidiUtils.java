package midi;

import java.util.ArrayList;

public class QMidiUtils {
	public final static int DEFAULT_TEMPO_MPQ = 500000; // 120bpm
	public final static int META_END_OF_TRACK_TYPE = 0x2F;
	public final static int META_TEMPO_TYPE = 0x51;
	
	private QMidiUtils(){}
	
	/*
	 *  return true if the passed message is Meta End Of Track
	 */
	public static boolean isMetaEndOfTrack(QMidiMessage midiMsg) {
		// check if it is a META message at all
		if(midiMsg.getLength()!= 3 || midiMsg.getStatus() != QMIDI.META) {
			return false;
		}
		// get message and check for end of track
		byte[] msg = midiMsg.getMessage();	
		return ((msg[1] & 0xFF) == META_END_OF_TRACK_TYPE) && (msg[2] == 0);
	} 
	
	/*
	 * return if the given message is a meta tempo message
	 */
	public static boolean isMetaTempo(QMidiMessage midiMsg) {
		// first check if it is a META message at all
		if (midiMsg.getLength() != 6 || midiMsg.getStatus() != QMIDI.META) {
			return false;
		}
		// now get message and check for tempo
		byte[] msg = midiMsg.getMessage();
		// meta type must be 0x51, and data length must be 3
		return ((msg[1] & 0xFF) == META_TEMPO_TYPE) && (msg[2] == 3);
	}
	
	public static int getTempoMPQ(QMidiMessage midiMsg) {
		// first check if it is a META message at all
		if (midiMsg.getLength() != 6 || midiMsg.getStatus() != QMIDI.META) {
			return -1;
		}
		byte[] msg = midiMsg.getMessage();
		if (((msg[1] & 0xFF) != META_TEMPO_TYPE) || (msg[2] != 3)) {
			return -1;
		}
		int tempo = (msg[5] & 0xFF) | ((msg[4] & 0xFF) << 8) | ((msg[3] & 0xFF) << 16);
		return tempo;
    }
	/*
	 * convert tick to microsecond with given tempo. Does not take tempo changes into account. Does not work for SMPTE timing!
	 */
	public static long ticks2microsec(long tick, double tempoMPQ, int resolution) {
		 return (long) (((double) tick) * tempoMPQ / resolution);
	}
	
	/*
	 * Given a tick, convert to microsecond
	 */
	public static long tick2microsecond(QSequence seq, long tick, TempoCache cache) {
		if(seq.getDivisionType() != QSequence.PPQ) {
			double seconds = ((double)tick / (double)(seq.getDivisionType() * seq.getResolution()));
			return (long)(1000000 * seconds);
		}
		if(cache == null) {
			cache = new TempoCache(seq);
		}
		
		int resolution = seq.getResolution();
		long[] ticks = cache.ticks;
		int[] tempos = cache.tempos;
		int cacheCount = tempos.length;
		
		int snapshotIndex = cache.snapshotIndex;
		int snapshotMicro = cache.snapshotMicro;
		
		long us = 0;
		
		if(snapshotIndex <= 0 || snapshotIndex >= cacheCount || ticks[snapshotIndex] > tick) {
			snapshotMicro = 0;
			snapshotIndex = 0;
		}
		if (cacheCount > 0) {
			// this implementation needs a tempo event at tick 0!
			int i = snapshotIndex + 1;
			while (i < cacheCount && ticks[i] <= tick) {
				snapshotMicro += ticks2microsec(ticks[i] - ticks[i - 1], tempos[i - 1], resolution);
				snapshotIndex = i;
				i++;
			}
			us = snapshotMicro + ticks2microsec(tick - ticks[snapshotIndex],tempos[snapshotIndex],resolution);
	    }
		cache.snapshotIndex = snapshotIndex;
	    cache.snapshotMicro = snapshotMicro;
	    return us;
	}
	
	public static class TempoCache {
		long[] ticks;
		int[] tempos; // in MPQ
		int snapshotIndex = 0;
		int snapshotMicro = 0;
		int currTempo; //MPQ, used as return value for microseconds2tick
		
		private boolean firstTempoIsFake = false;
		
		public TempoCache() {
			ticks = new long[1];
			tempos = new int[1];
			tempos[0] = DEFAULT_TEMPO_MPQ;
			snapshotIndex = 0;
			snapshotMicro = 0;
		}
		public TempoCache(QSequence sequence) {
			this();
			
		}
		public synchronized void refresh(QSequence sequence) {
			ArrayList list  = new ArrayList();
			QTrack[] tracks = sequence.getTracks();
			if(tracks.length > 0) {
				QTrack track = tracks[0];
				int count = track.size();
				for(int i = 0; i < count; i++){
					QMidiEvent midiEvent = track.get(i);
					QMidiMessage msg = midiEvent.getMessage();
					if(isMetaTempo(msg)) {
						list.add(midiEvent);
					}
				}
			}
			int size = list.size() + 1;
			firstTempoIsFake = true;
			if(size > 1 && (((QMidiEvent)list.get(0)).getTick() == 0)) {
				size--;
				firstTempoIsFake = false;
			}
			ticks = new long[size];
			tempos = new int[size];
			int e = 0;
			if(firstTempoIsFake) {
				ticks[0] = 0;
				tempos[0] = DEFAULT_TEMPO_MPQ;
				e++;
			}
			for (int i = 0; i < list.size(); i++, e++) {
				QMidiEvent evt = (QMidiEvent) list.get(i);
				ticks[e] = evt.getTick();
				tempos[e] = getTempoMPQ(evt.getMessage());
			}
			snapshotIndex = 0;
		    snapshotMicro = 0;
		}
	    public int getCurrTempoMPQ() {
	    	return currTempo;
	    }
	    
	    float getTempoMPQAt(long tick) {
	    	return getTempoMPQAt(tick, -1.0f);
	    }
	    
	    synchronized float getTempoMPQAt(long tick, float startTempoMPQ) {
	    	for (int i = 0; i < ticks.length; i++) {
	    		if (ticks[i] > tick) {
	    			if (i > 0) i--;
	    			if (startTempoMPQ > 0 && i == 0 && firstTempoIsFake) {
	    				return startTempoMPQ;
	    			}
	    			return (float) tempos[i];
	    		}
	    	}
	    	return tempos[tempos.length - 1];
	   }
	}	
}
