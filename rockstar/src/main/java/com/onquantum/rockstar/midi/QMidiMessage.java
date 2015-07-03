package midi;


public abstract class QMidiMessage {
	
	protected byte[] data;
	protected int length;
    protected QMIDI.MIDIMsgType MessageType = QMIDI.MIDIMsgType.undefined;

    protected QMidiMessage(byte[] data) {
		this.data = data;
		if(this.data != null)
			this.length = this.data.length;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getStatus() {
		if(this.length > 0) {
			return (int)(data[0] & 0xFF);
		}else {
			return 0;
		}
	}
	
	protected void setMessage(byte[] data, int length) throws QInvalidMidiDataException {
		if(length < 0 || (length > 0 && length > data.length)){
			throw new IndexOutOfBoundsException("length out of the bounds : " + length);
		}
		this.length = length;
		if(this.data == null || this.data.length < this.length) {
			this.data = new byte[this.length];
		}
	}
	
	public byte[] getMessage() {
		byte[] returnData = new byte[this.length];
		System.arraycopy(data, 0, returnData, 0, this.length);
		return returnData;
	}


    // Helper methods
    public void setMessageType(QMIDI.MIDIMsgType messageType) {
        this.MessageType = messageType;
    }
    public QMIDI.MIDIMsgType getMidiMessageType() {
        return MessageType;
    }

    @Override
    public String toString() {
        int statusByte;
        if(data[0] == -1) {
            statusByte = (int)data[1] & 0xFF;
        } else {
            statusByte = (int)data[0] & 0xFF;
        }

        return " Object [QMidiMessage] Type [" + MessageType.toString() + "] Status byte(" + Integer.toHexString(statusByte) + "h) " + QMIDI.StatusByte(statusByte);
    }
}
