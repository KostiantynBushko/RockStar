package com.onquantum.rockstar.qmidi;

import java.util.ArrayList;
import java.util.HashSet;

public class QTrack {
    private ArrayList eventsList = new ArrayList();
    private HashSet set = new HashSet();
    private QMidiEvent eotEvent;

    QTrack() {
        QMetaMessage eot = new ImmutableEndOfTrack();
        eotEvent = new QMidiEvent(eot,0);
        eventsList.add(eotEvent);
        set.add(eotEvent);
    }

    public boolean add(QMidiEvent event) {
        if (event == null)
            return false;
        synchronized (eventsList) {
            if (!set.contains(event)) {
                int eventsCount = eventsList.size();
                QMidiEvent lastEvent = null;
                if (eventsCount > 0) {
                    lastEvent = (QMidiEvent)eventsList.get(eventsCount - 1);
                }
                if (lastEvent != eotEvent) {
                    if (lastEvent != null) {
                        eotEvent.setTick(lastEvent.getTick());
                    } else {
                        eotEvent.setTick(0);
                    }
                }
                eventsList.add(eotEvent);
                set.add(eotEvent);
                eventsCount = eventsList.size();
            }
        }
        return true;
    }

    private static class ImmutableEndOfTrack extends QMetaMessage {
        private ImmutableEndOfTrack() {
            super(new byte[3]);
            data[0] = (byte)META;
            data[1] = MidiUtils.META_END_OF_TRACK_TYPE;
            data[2] = 0;
        }
        public void setMessage(int type, byte[] data, int length)
                throws QInvalidMidiDataException {
            throw new QInvalidMidiDataException("cannot modify end of track message");
        }
    }
}
