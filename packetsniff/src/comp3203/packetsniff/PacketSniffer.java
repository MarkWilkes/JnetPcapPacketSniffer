package comp3203.packetsniff;

import comp3203.packetsniff.ui.MainWindow;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.*;

import org.jnetpcap.protocol.application.*;
import org.jnetpcap.protocol.network.*;
import org.jnetpcap.protocol.tcpip.*;
import org.jnetpcap.protocol.wan.*;
import org.jnetpcap.protocol.voip.*;
import org.jnetpcap.protocol.vpn.*;

import org.jnetpcap.packet.format.FormatUtils.*;

import java.util.ArrayList;
import java.util.List;

public class PacketSniffer {
	
	public static void main(String[] args) {
		new MainWindow().setVisible(true);
		
		List<PcapIf> deviceList = new ArrayList<PcapIf>();
		StringBuilder errorBuffer = new StringBuilder();
		
		int result = Pcap.findAllDevs(deviceList, errorBuffer);
		
		System.out.println("Network devices found:");
        int i = 0;
        for (PcapIf device : deviceList) {
        	String description;
            if(device.getDescription() != null){
            	description = device.getDescription();
            }
            else{
            	description = "No description available";
            }
            System.out.printf("%s [%s]\n", device.getName(), description);
        }
        
		//TODO check which device the user wants
        
        PcapIf listeningDevice = deviceList.get(0);
        
        int snapLength = 64 * 1024;
        int flags = Pcap.MODE_PROMISCUOUS;
        int timeOut = 10 * 1000;
        
        Pcap pcap = Pcap.openLive(listeningDevice.getName(), snapLength, flags, timeOut, errorBuffer);
        
        if(pcap == null){
        	System.err.printf("Error opening listening device for network capture:" + errorBuffer.toString());
        	
        	return;
        }
        
        PcapPacketHandler<String> packetHandler = new PcapPacketHandler<String>(){
        	
        	public void nextPacket(PcapPacket packet, String user){
        		Ip4 ip4 = new Ip4();
        		Ip6 ip6 = new Ip6();
        		byte[] sourceIp = new byte[4];
        		byte[] destinationIp = new byte[4];
        		
        		String sourceIpAddress = new String();
        		String destinationIpAddress = new String();
        		
        		if(packet.hasHeader(ip4)){	
        			
        			checkProtocolv4(packet);
        			
        			sourceIp = packet.getHeader(ip4).source();
        			destinationIp = packet.getHeader(ip4).destination();
        			
        			sourceIpAddress = org.jnetpcap.packet.format.FormatUtils.ip(sourceIp);
        			destinationIpAddress = org.jnetpcap.packet.format.FormatUtils.ip(destinationIp);
        			
        			System.out.println("Ip4");
            		System.out.println("Source IP " + sourceIpAddress);
            		System.out.println("Destination IP " + destinationIpAddress);
        		}
        		else if(packet.hasHeader(ip6)){
        			
        			checkProtocolv6(packet);
        			
        			sourceIp = packet.getHeader(ip6).source();
        			destinationIp = packet.getHeader(ip6).destination();
        			
        			sourceIpAddress = org.jnetpcap.packet.format.FormatUtils.ip(sourceIp);
        			destinationIpAddress = org.jnetpcap.packet.format.FormatUtils.ip(destinationIp);
        			
        			System.out.println("Ip6");
            		System.out.println("Source IP " + sourceIpAddress);
            		System.out.println("Destination IP " + destinationIpAddress);
        		}
        	}
        };
        
        pcap.loop(Pcap.LOOP_INFINITE, packetHandler, "");
        
        pcap.close();
	}

	protected static void checkProtocolv6(PcapPacket packet) {
		//IP
		if(packet.hasHeader(new Tcp())){
			System.out.print("TCP");
		}
		else if(packet.hasHeader(new Udp())){
			System.out.print("UDP");
		}
		else if(packet.hasHeader(new Http())){
			System.out.print("HTTP");
		}
		//Application
		else if(packet.hasHeader(new Html())){
			System.out.print("HTML");	
		}
		//WAN
		else if(packet.hasHeader(new PPP())){
			System.out.print("PPP");	
		}
		//VoIP
		else if(packet.hasHeader(new Rtp())){
			System.out.print("RTP");	
		}
		else if(packet.hasHeader(new Sdp())){
			System.out.print("SDP");	
		}
		else if(packet.hasHeader(new Sip())){
			System.out.print("SIP");	
		}
		//VPN
		else if(packet.hasHeader(new L2TP())){
			System.out.print("L2TP");	
		}
		else{
			//System.out.println(packet.toString());
		}
	}

	protected static void checkProtocolv4(PcapPacket packet) {
		//IP
		if(packet.hasHeader(new Tcp())){
			System.out.print("TCP");
		}
		else if(packet.hasHeader(new Udp())){
			System.out.print("UDP");
		}
		else if(packet.hasHeader(new Http())){
			System.out.print("HTTP");
		}
		//Application
		else if(packet.hasHeader(new Html())){
			System.out.print("HTML");	
		}
		//WAN
		else if(packet.hasHeader(new PPP())){
			System.out.print("PPP");	
		}
		//VoIP
		else if(packet.hasHeader(new Rtp())){
			System.out.print("RTP");	
		}
		else if(packet.hasHeader(new Sdp())){
			System.out.print("SDP");	
		}
		else if(packet.hasHeader(new Sip())){
			System.out.print("SIP");	
		}
		//VPN
		else if(packet.hasHeader(new L2TP())){
			System.out.print("L2TP");	
		}
		else{
			//System.out.println(packet.toString());
		}
	}
}
