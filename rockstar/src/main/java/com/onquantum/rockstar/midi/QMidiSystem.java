package com.onquantum.rockstar.midi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class QMidiSystem {
    private static int[] MTRK = new int[]{'M','T','r','k'};
    private QMidiSystem() {}

    public static QSequence getSequence(InputStream inputStream) throws QInvalidMidiDataException, IOException {
        Vector<SimpleTrack> simpleTracks = null;
        int[] intArray;
        int midiFileFormat = -1;
        int numberOfTrack = 0;
        int timingResolution = 0;
        float timingDivisionType = 0;

        int fileSize = inputStream.available();

        intArray = new int[fileSize];
        int count = 0;
        int b;
        int c = 0;
        while((b = inputStream.read()) != -1) {
            intArray[count] = b;
            //System.out.println( c + " : " + Integer.toHexString(b) + "h " + /*"ASCII : " + (char)b */ (byte)b); c++;
            count++;
        }
        midiFileFormat = getFileFormat(inputStream, intArray);
        numberOfTrack = getNumberOfTrack(inputStream, intArray);
        timingDivisionType = getTimingDivisionType(intArray);
        timingResolution = getTimingResolution(intArray);


        simpleTracks = findAllAddressOfTracks(fileSize,intArray);
        Vector<QTrack> trackList = parseTracks(simpleTracks, intArray);
        return new QSequence(timingDivisionType,timingResolution,trackList);
    }

    public static QSequence getSequence(File file) throws QInvalidMidiDataException, IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStream inputStream = (InputStream)fileInputStream;
        return getSequence(inputStream);
    }


    private static void getHeader() {
        try {
            QSequence s = new QSequence(0,0);
            s.tracks.add(new QTrack());
        } catch (QInvalidMidiDataException e) {
            e.printStackTrace();
        }
    }
    // Get MIDI file format ( 0, 1, 2 )
    private static int getFileFormat(InputStream inputStream, int[] intArray) {
        if(inputStream == null)
            return -1;
        return ((intArray[8] << 8 & 0xFF00) | (intArray[9] & 0x00FF));
    }

    private static int getNumberOfTrack(InputStream inputStream, int[] intArray) {
        if(inputStream == null)
            return 0;
        return ((intArray[10] << 8 & 0xFF00) | (intArray[11] & 0x00FF));
    }

    private static float getTimingDivisionType(int[] intArray) {
        int timing = ((intArray[12] << 8 & 0xFF00) | (intArray[13] & 0x00FF));
        if((timing & 0x8000) == 0) {
            return 0.0f;
        }else {
            int t = timing & 0x7f00;
            if(t == 24) {
                return 24.0f;
            }else if(t == 25) {
                return 25.0f;
            }else if(t == 29) {
                return 29.97f;
            }else if(t == 30) {
                return 30.0f;
            }else {
                return 0.0f;
            }
        }
    }

    private static int getTimingResolution(int[] intArray) {
        int timing = ((intArray[12] << 8 & 0xFF00) | (intArray[13] & 0x00FF));
        return timing & 0x7fff;
    }

    // Check if it is begin of track
    private static boolean checkBeginOfTrack(int beginFromPosition, int[] intArray) {
        int[] s = Arrays.copyOfRange(intArray, beginFromPosition, beginFromPosition + 4);
        return Arrays.equals(s, MTRK);
    }

    // Find all tracks in the file and save address of tracks to array
    private static Vector<SimpleTrack> findAllAddressOfTracks(int fileSize, int[] intArray) {
        int index = 0;
        Vector<SimpleTrack>simpleTrackVector = new Vector<SimpleTrack>();
        while(index < fileSize) {
            if(checkBeginOfTrack(index,intArray)) {
                SimpleTrack simpleTrack = new SimpleTrack();
                simpleTrack.address = index;
                simpleTrack.size = getTrackSize(index,intArray);
                simpleTrackVector.add(simpleTrack);
            }
            index++;
        }
        return simpleTrackVector;
    }

    // Get track size
    private static int getTrackSize(int trackAddress, int[] intArray) {
        int size = 0;
        size |= ((byte)intArray[trackAddress + 4] & 0xFF) << 8;
        size |= ((byte)intArray[trackAddress + 5] & 0xFF) << 8;
        size |= ((byte)intArray[trackAddress + 6] & 0xFF) << 8;
        size |= ((byte)intArray[trackAddress + 7] & 0xFF);
        return size;
    }


    // Parse tracks from midi file
    private static Vector<QTrack> parseTracks(Vector<SimpleTrack>simpleTracks, int[] intArray) {

        Vector<QTrack>midiTracks = new Vector<QTrack>();

        Iterator<SimpleTrack> iterator = simpleTracks.iterator();

        int currentTrack = 0;

        while(iterator.hasNext()) {
            midiTracks.add(new QTrack());

            SimpleTrack track = iterator.next();

            int trackSize = track.size;                           // Track size
            int trackStartAddress = track.address;                // Track start address contain MTrk + address and messages
            int trackEventsStartAddress = trackStartAddress + 8;  // 4 byte "MTrk" (0x4D 0x54 0x72 0x6B) + 4 chunk size
            int dataByteMask = 0x7F;                              // 01111111b check if byte is data byte
            int statusByteMask = 0x80;                            // 10000000b check id byte is status byte

            int count = 0;
            int[] marker = new int[1];
            marker[0] = trackEventsStartAddress;

            // Last status byte
            int lastStatusByte = 0;

            while(marker[0] < (trackSize + trackEventsStartAddress)) {

                //  Read delta time of message variable length value
                int deltaTime = ReadVariableLengthQuantity(marker, intArray);

                // Read first byte of the message is a status byte of message
                // Check if byte is status or data: if 0x80 status else data
                if((intArray[marker[0]] & 0x80) != 0 || lastStatusByte != 0) {

                    if((intArray[marker[0]] <= 0xEF || lastStatusByte != 0) && intArray[marker[0]] < 0xF0) {
                        // Obtain Chanel message
                        if((intArray[marker[0]] & 0x80) != 0) {
                            lastStatusByte = 0;
                        }
                        if(lastStatusByte == 0) {
                            lastStatusByte = intArray[marker[0]];
                            marker[0]++;
                        }

                        // System.out.println(" Chanel message status byte : = " + Integer.toHexString(lastStatusByte) + " ASCII : " + (char)lastStatusByte + " position = " + marker[0]);
                        switch ((lastStatusByte & 0xF0)) {
                            case 0x80:
                            case 0x90:
                            case 0xA0:
                            case 0xB0:
                            case 0xE0:
                            {
                                int statusByte = lastStatusByte; //intArray[marker[0]];
                                int dataByteOne = intArray[marker[0]];
                                int dataByteTwo = intArray[++marker[0]];
                                try {
                                    QShortMessage shortMessage = new QShortMessage(statusByte,dataByteOne, dataByteTwo);
                                    shortMessage.setMessageType(QMIDI.MIDIMsgType.channel);
                                    //System.out.println(" - " + shortMessage.toString());
                                    QMidiEvent event = new QMidiEvent(shortMessage, deltaTime);
                                    midiTracks.get(currentTrack).add(event);
                                } catch (QInvalidMidiDataException e) {
                                    e.printStackTrace();
                                }
                                marker[0]++;
                                break;
                            }
                            case 0xC0:
                            case 0xD0:
                            {
                                byte[] data = new byte[2];
                                data[0] = (byte)lastStatusByte;        // status byte of message
                                data[1] = (byte)intArray[marker[0]];   // data byte of message
                                QShortMessage shortMessage = new QShortMessage(data);
                                shortMessage.setMessageType(QMIDI.MIDIMsgType.channel);
                                //System.out.println(" - " + shortMessage.toString());
                                QMidiEvent event = new QMidiEvent(shortMessage, deltaTime);
                                midiTracks.get(currentTrack).add(event);
                                marker[0]++;
                                break;
                            }
                            default:break;
                        }

                    } else if(intArray[marker[0]] >= 0xF0) {
                        // Obtain System message
                        // System.out.println(" System message status byte : = " + Integer.toHexString(intArray[marker[0]])  + " ASCII : " + (char)intArray[marker[0]]);
                        lastStatusByte = 0;

                        switch (intArray[marker[0]]) {
                            // System exclusive
                            case 0xF0:
                            case 0xF7: {
                                System.out.println(" ERROR UNDEFINED PARSER : " + Integer.toHexString(intArray[marker[0]]));
                                break;
                            }
                            //System common message
                            case 0xF1:
                            case 0xF3:{
                                int statusByte = intArray[marker[0]];
                                int dataByteOne = intArray[++marker[0]];
                                byte[] messageArray = new byte[]{(byte)statusByte, (byte)dataByteOne};
                                QShortMessage shortMessage = new QShortMessage(messageArray);
                                shortMessage.setMessageType(QMIDI.MIDIMsgType.system_common);
                                QMidiEvent event = new QMidiEvent(shortMessage, deltaTime);
                                midiTracks.get(currentTrack).add(event);
                                marker[0] += 1;
                                break;
                            }
                            case 0xF2: {
                                int statusByte = intArray[marker[0]];
                                int dataByteOne = intArray[++marker[0]];
                                int dataByteTwo = intArray[++marker[0]];
                                try {
                                    QShortMessage shortMessage = new QShortMessage(statusByte, dataByteOne, dataByteTwo);
                                    shortMessage.setMessageType(QMIDI.MIDIMsgType.system_common);
                                    QMidiEvent event = new QMidiEvent(shortMessage, deltaTime);
                                    midiTracks.get(currentTrack).add(event);
                                } catch (QInvalidMidiDataException e) {
                                    e.printStackTrace();
                                }
                                marker[0] += 1;
                                break;
                            }

                            case 0xF4:
                            case 0xF5:
                            case 0xF6:
                                // System real time message
                            case 0xF8:
                            case 0xF9:
                            case 0xFA:
                            case 0xFB:
                            case 0xFC:
                            case 0xFD:
                            case 0xFE:{
                                try {
                                    QShortMessage shortMessage = new QShortMessage(intArray[marker[0]]);
                                    shortMessage.setMessageType(QMIDI.MIDIMsgType.system_real_time);
                                    QMidiEvent event = new QMidiEvent(shortMessage, deltaTime);
                                    midiTracks.get(currentTrack).add(event);
                                } catch (QInvalidMidiDataException e) {
                                    e.printStackTrace();
                                }
                                marker[0]++;
                                break;
                            }
                            // Meta message
                            case 0xFF:{
                                // If next byte after 0xFF status byte parse meta event else create system reset event
                                if((intArray[marker[0] + 1] & 0x80) == 1) {
                                    try {
                                        QShortMessage shortMessage = new QShortMessage(intArray[marker[0]]);
                                        shortMessage.setMessageType(QMIDI.MIDIMsgType.meta);
                                        QMidiEvent event = new QMidiEvent(shortMessage, deltaTime);
                                        midiTracks.get(currentTrack).add(event);
                                    } catch (QInvalidMidiDataException e) {
                                        e.printStackTrace();
                                    }
                                    marker[0]++;
                                } else {
                                    // The message is meta event
                                    int metaMessageType = intArray[++marker[0]];
                                    switch (metaMessageType) {

                                        case 0x00:{
                                            marker[0]++;
                                            break;
                                        }
                                        case 0x01:   // Text Event
                                        case 0x02:   // Copyright Notice
                                        case 0x03:   // Sequence/Track Name
                                        case 0x04:   // Instrument Name
                                        case 0x05:   // Lyric
                                        case 0x06:   // Marker
                                        case 0x07:   // Cue Point
                                        case 0x20:   // MIDI Channel Prefix
                                        case 0x2F:   // End of Track
                                        case 0x51:   // Tempo (in microseconds per MIDI quarter-note)
                                        case 0x54:   // S M P T E Offset
                                        case 0x58:   // Time Signature
                                        case 0x59:   // Key Signature
                                        {
                                            marker[0]++;
                                            // parse meta event length "variable length"
                                            int messageLength = ReadVariableLengthQuantity(marker, intArray);
                                            byte[] data = new byte[messageLength];
                                            for (int i = 0; i < messageLength; i++) {
                                                data[i] = (byte)intArray[marker[0]++];
                                            }
                                            QMetaMessage metaMessage = new QMetaMessage();
                                            try {
                                                metaMessage.setMessage(metaMessageType,data,messageLength);
                                                metaMessage.setMessageType(QMIDI.MIDIMsgType.meta);
                                                QMidiEvent event = new QMidiEvent(metaMessage,deltaTime);
                                                midiTracks.get(currentTrack).add(event);
                                            } catch (QInvalidMidiDataException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        }
                                        case 0x7F:{

                                        }
                                        default:break;
                                    }
                                }
                                break;
                            }
                            default:break;
                        }
                    }
                }
            }
            currentTrack += 1;
        }
        return midiTracks;
    }

    public static byte[] CreateMidiFromSequence(QSequence sequence) {
        List<Byte>out = new ArrayList<Byte>();
        // midi file header MThd
        out.add((byte) 0x4D);
        out.add((byte) 0x54);
        out.add((byte) 0x68);
        out.add((byte) 0x64);

        // four bytes contain size of file header, size always 6
        out.add((byte)0x00);
        out.add((byte)0x00);
        out.add((byte)0x00);
        out.add((byte)0x06);

        // file header data byte 1.

        //First two data bytes of header contain midi file format (0,1,2)
        int tracksCount = sequence.getTracks().length;
        int fileFormat = 1;
        if(tracksCount > 1) {
            fileFormat = 1;
            out.add((byte)((fileFormat >> 8) & 0xFF));
            out.add((byte)(fileFormat & 0xFF));
        } else {
            out.add((byte)((fileFormat >> 8) & 0xFF));
            out.add((byte)(fileFormat & 0xFF));
        }

        //Two bytes of headers contain size of tracks contained in file
        out.add((byte)((tracksCount >> 8) & 0xFF));
        out.add((byte)(tracksCount & 0xFF));

        // Last two bytes represent time resolution "p p q n"
        int timeResolution = sequence.getResolution();

        out.add((byte)((timeResolution >> 8) & 0xFF));
        out.add((byte)(timeResolution & 0xFF));

        QTrack[] tracksArray = sequence.getTracks();
        for (int i = 0; i < tracksArray.length; i++) {
            // MTrk first four bytes of track
            out.add((byte)0x4D);
            out.add((byte)0x54);
            out.add((byte)0x72);
            out.add((byte)0x6B);

            int trackSize = 0;
            int trackSizeIndex = out.size();
            out.add((byte)0x00);
            out.add((byte)0x00);
            out.add((byte)0x00);
            out.add((byte)0x00);

            byte statusByte = 0;

            for (int j = 0; j < tracksArray[i].size(); j++) {
                QMidiEvent midiEvent = tracksArray[i].get(j);

                //System.out.println(j + " Delta time = " + midiEvent.getTick() + " " + midiEvent.getMessage().toString());
                byte[] deltaTime = WriteVariableLengthQuantity(midiEvent.getTick());
                for (int n = 0; n < deltaTime.length; n++) {
                    out.add(deltaTime[n]);
                    trackSize ++;
                }

                byte[] data = new byte[midiEvent.getMessage().getLength()];
                if(statusByte == midiEvent.getMessage().getStatus()) {
                    System.arraycopy(midiEvent.getMessage().getMessage(),1,data,0,midiEvent.getMessage().getLength());
                } else {
                    data = midiEvent.getMessage().getMessage();
                }

                for (int m = 0; m < data.length; m++) {
                    out.add(data[m]);
                    trackSize++;
                }

                switch (midiEvent.getMessage().getMidiMessageType()) {
                    case undefined:
                        //statusByte = 0;
                        break;
                    case channel:
                        statusByte = (byte)midiEvent.getMessage().getStatus();
                        break;
                    case system_exclusive:
                        //statusByte = 0;
                        break;
                    case system_common:
                        //statusByte = 0;
                        break;
                    case system_real_time:
                        //statusByte = 0;
                        break;
                    case meta:
                        //statusByte = 0;
                        break;
                }
            }
            out.set(trackSizeIndex, (byte)((trackSize >> 24) & 0xFF));
            out.set(trackSizeIndex + 1, (byte)((trackSize >> 16) & 0xFF));
            out.set(trackSizeIndex + 2, (byte)((trackSize >> 8) & 0xFF));
            out.set(trackSizeIndex + 3, (byte)(trackSize & 0xFF));
        }



        byte[] result = new byte[out.size()];
        for (int i = 0; i < out.size(); i++) {
            result[i] = out.get(i);
        }

        return result;
    }

    public static int ReadVariableLengthQuantity(int[] markerInArray, int[] data) {
        //  Parse variable length value
        //  if byte is greater or equal 80h the next byte is also part of the lvm
        //  else byte is the last byte of the lvm.
        int value = 0;
        int count = markerInArray[0];
        while(count < data.length) {
            value |= (data[markerInArray[0]] & ~(1 << 7)) & 0x7F;
            if(data[markerInArray[0]] < 0x80) {
                markerInArray[0]++;
                break;
            }
            markerInArray[0]++;
            value = value << 7;
        }
        return value;
    }

    public static byte[] WriteVariableLengthQuantity(long value) {
        LinkedList<Byte> result = new LinkedList<Byte>();
        result.push((byte)(value & 0x7F));

        while ((value >>= 7) != 0){
            result.push((byte)((value & 0x7F) | 0x80));
            //System.out.println("value = " + value);
        }
        if((result.getLast() & 0x80) != 0)
            result.addLast((byte)0x00);

        byte[] byteArray = new byte[result.size()];
        int count = 0;
        for (Byte b : result) {
            byteArray[count] = b;
            count ++;
            //System.out.print("0x" + Integer.toHexString(0x000000FF & b) + " ");
        }
        System.out.println("");
        return byteArray;
    }


    private static class SimpleTrack {
        public SimpleTrack(){}
        int address;
        int size;
    }
}