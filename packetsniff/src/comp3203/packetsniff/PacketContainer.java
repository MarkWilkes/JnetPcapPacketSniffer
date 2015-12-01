package comp3203.packetsniff;

import java.util.Date;

public class PacketContainer {
	
	String sourceAddress;
	String destinationAddress;
	String protocol;
	String ipType;
	Date timeStamp;
	boolean displayed;
	
	PacketContainer(String src, String dst, String prt, String ip, Date timest){
		displayed = true;
		sourceAddress = src;
		destinationAddress = dst;
		protocol = prt;
		ipType = ip;
		timeStamp = timest;
	}
	
	public String getSource(){ return sourceAddress; }
	public String getDestination(){ return destinationAddress; }
	public String getProtocol(){ return protocol; }
	public String getIPType(){ return ipType; }
	public Date getTimeStamp(){ return timeStamp; }
	public boolean isDisplayed(){ return displayed; }
	
	public boolean filter(PacketContainer filter){
		displayed = true;
		if(!filter.getSource().equals(sourceAddress) || filter.getSource()!= null){
			displayed = false;
		}
		
		if(!filter.getDestination().equals(destinationAddress) || filter.getDestination() != null){
			displayed = false;
		}
		
		if(!filter.getProtocol().equals(protocol) || filter.getProtocol() != null){
			displayed = false;
		}
		
		if(!filter.getIPType().equals(ipType) || filter.getIPType() != null){
			displayed = false;
		}
		
		return displayed;
	}
	
	public String toSerializedString(){
		String serialized = new String();
		
		serialized += sourceAddress + "*";
		serialized += destinationAddress + "*";
		serialized += protocol + "*";
		serialized += ipType + "*";
		serialized += timeStamp.toString() + "*";
		
		return serialized;
	}
	
}
