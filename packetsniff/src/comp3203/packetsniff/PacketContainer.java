package comp3203.packetsniff;

import java.util.Date;
import java.util.List;

public class PacketContainer {
	
	String sourceAddress;
	String destinationAddress;
	List<String> protocols;
	Date timeStamp;
	boolean displayed;
	
	public PacketContainer(String src, String dst, List<String> prt, Date timest){
		displayed = true;
		sourceAddress = src;
		destinationAddress = dst;
		protocols = prt;
		timeStamp = timest;
	}
	
	public String getSource(){ return sourceAddress; }
	public String getDestination(){ return destinationAddress; }
	public List<String> getProtocols(){ return protocols; }
	public Date getTimeStamp(){ return timeStamp; }
	public boolean isDisplayed(){ return displayed; }
	
	public boolean filter(PacketContainer filter){
		if(!filter.getSource().equals(sourceAddress) && filter.getSource() != null && !filter.getSource().equals("")){
			displayed = false;
			return false;
		}
		
		if(!filter.getDestination().equals(destinationAddress) && filter.getDestination() != null && !filter.getDestination().equals("")){
			displayed = false;
			return false;
		}
		
		for(String prot : filter.getProtocols()) {
			boolean contains = false;
			for(String other : protocols) {
				if(prot.trim().equalsIgnoreCase(other.trim())) contains = true;
			}
			if(contains == false) {
				displayed = false;
				return false;
			}
		}
		
		displayed = true;
		return true;
	}
	
	public String toSerializedString(){
		String serialized = new String();
		
		serialized += sourceAddress + "*";
		serialized += destinationAddress + "*";
		serialized += protocols + "*";
		serialized += timeStamp.toString() + "*";
		
		return serialized;
	}
	
}
