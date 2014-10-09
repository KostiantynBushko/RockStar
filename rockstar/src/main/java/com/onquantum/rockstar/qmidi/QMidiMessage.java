package com.onquantum.rockstar.qmidi;

public abstract class QMidiMessage implements Cloneable{
	
	protected byte[] data;
	protected int length;
	
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
	
	protected void setMessage(byte[] data, int length) {
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
}
