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
import org.jnetpcap.protocol.lan.*;
import org.jnetpcap.packet.format.FormatUtils.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PacketSniffer {
	
	static StringBuilder errorBuffer;
	static List<PacketContainer> packets = new ArrayList<PacketContainer>();
	static Pcap pcap = null;
	
	public static void main(String[] args) {
		
		List<PcapIf> deviceList = new ArrayList<PcapIf>();
		errorBuffer = new StringBuilder();
		
		int result = Pcap.findAllDevs(deviceList, errorBuffer);
        
		MainWindow window = new MainWindow(deviceList, packets);
		window.setVisible(true);	
		
		System.out.println("Network devices found:");
        for (PcapIf device : deviceList) {
        	String description;
            if(device.getDescription() != null){
            	description = device.getDescription();
            }
            else{
            	description = "No description available";
            }
            System.out.println(device.getName() + " [" + description + "]");
        }
	}

	public static void listen(PcapIf device) {
		int snapLength = 64 * 1024;
        int flags = Pcap.MODE_PROMISCUOUS;
        int timeOut = 10 * 1000;
        
        //stop current capture
		if(pcap != null) {
			pcap.breakloop();
			pcap.close();
		}
        
        pcap = Pcap.openLive(device.getName(), snapLength, flags, timeOut, errorBuffer);
        
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
        		Date timeStamp = new Date();
        		String sourceIpAddress = new String();
        		String destinationIpAddress = new String();
        		PacketContainer pacContain;
        		System.out.println("\n" + checkProtocol(packet));
        		if(packet.hasHeader(ip4)){
        			sourceIp = packet.getHeader(ip4).source();
        			destinationIp = packet.getHeader(ip4).destination();
        			
        			sourceIpAddress = org.jnetpcap.packet.format.FormatUtils.ip(sourceIp);
        			destinationIpAddress = org.jnetpcap.packet.format.FormatUtils.ip(destinationIp);
        			
            		System.out.println("Source IP " + sourceIpAddress);
            		System.out.println("Destination IP " + destinationIpAddress);
            		
            		pacContain = new PacketContainer(sourceIpAddress, destinationIpAddress, checkProtocol(packet), timeStamp);
            		packets.add(pacContain);
        		}
        		else if(packet.hasHeader(ip6)){
        			sourceIp = packet.getHeader(ip6).source();
        			destinationIp = packet.getHeader(ip6).destination();
        			
        			sourceIpAddress = org.jnetpcap.packet.format.FormatUtils.ip(sourceIp);
        			destinationIpAddress = org.jnetpcap.packet.format.FormatUtils.ip(destinationIp);
        			
        			System.out.println("IPv6");
            		System.out.println("Source IP " + sourceIpAddress);
            		System.out.println("Destination IP " + destinationIpAddress);
            		
            		pacContain = new PacketContainer(sourceIpAddress, destinationIpAddress, checkProtocol(packet), timeStamp);
            		packets.add(pacContain);
        		}
        	}
        };
        
        pcap.loop(Pcap.LOOP_INFINITE, packetHandler, "");
	}

	private static List<String> checkProtocol(PcapPacket packet) {
		List<String> protocols = new ArrayList<>();
		//IP
		if(packet.hasHeader(new Ip4())){
			protocols.add("IPv4");
		}
		if(packet.hasHeader(new Ip6())){
			protocols.add("IPv6");
		}
		
		if(packet.hasHeader(new Tcp())){
			protocols.add("TCP");
		}
		else if(packet.hasHeader(new Udp())){
			protocols.add("UDP");
		}
		if(packet.hasHeader(new Http())){
			protocols.add("HTTP");
		}
		//Application
		if(packet.hasHeader(new Html())){
			protocols.add("HTML");
		}
		//WAN
		if(packet.hasHeader(new PPP())){
			protocols.add("PPP");
		}
		//VoIP
		if(packet.hasHeader(new Rtp())){
			protocols.add("RTP");
		}
		if(packet.hasHeader(new Sdp())){
			protocols.add("SDP");
		}
		if(packet.hasHeader(new Sip())){
			protocols.add("SIP");
		}
		//VPN
		if(packet.hasHeader(new L2TP())){
			protocols.add("L2TP");
		}
		//LAN
		if(packet.hasHeader(new Ethernet())){
			protocols.add("Ethernet");
		}
		return protocols;
	}
}
