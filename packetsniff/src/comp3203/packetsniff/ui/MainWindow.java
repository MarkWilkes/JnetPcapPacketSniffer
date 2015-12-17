package comp3203.packetsniff.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import org.jnetpcap.PcapIf;

import comp3203.packetsniff.PacketContainer;
import comp3203.packetsniff.PacketSniffer;

public class MainWindow extends JFrame {
	private JTabbedPane contentPane;
	List<PacketContainer> packetData;
	
	private JPanel liveFeedTab;
	private JList<DeviceListItem> deviceList = new JList<>();
	private JScrollPane listPane = new JScrollPane(deviceList);
	private JTextArea mainTextArea = new JTextArea();
	private JScrollPane mainTextPane = new JScrollPane(mainTextArea);
	private ListSelectionListener listListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting())
				devicePanel.setDevice(deviceList.getSelectedValue().getDevice());
		}
	};	
	
	private List<DeviceListItem> deviceListItems = new ArrayList<>();
	
	private JPanel packetDetailsTab;
	private JTable packetTable;
	private JPanel packetSearchPanel;
	private JPanel searchButtonPanel;
	private JButton searchButton;
	private JButton clearSearchButton;
	private JButton sortByNewButton;
	private JButton sortByOldButton;
	private JScrollPane packetTablePane;
	
	private JPanel searchDetailsPanel;
	private JPanel searchIPPanel;
	private JPanel searchProtocolPanel;
	private JPanel searchSourcePanel;
	private JPanel searchDestinationPanel;
	private JLabel searchIPLabel;
	private JLabel searchProtocolLabel;
	private JLabel searchSourceLabel;
	private JLabel searchDestinationLabel;
	private JTextField searchIPText;
	private JTextField searchProtocolText;
	private JTextField searchSourceText;
	private JTextField searchDestinationText;
	
	private JPanel searchDevicePanel;
	private JComboBox<DeviceListItem> searchDevicePicker;
	
	private boolean sortByNew;
	
	private JPanel metricsTab;
	private JLabel timeRunningMetric;
	private JLabel packetsSniffedMetric;
	private JLabel packetsPerSecondMetric;
	private JLabel tcpPacketsMetric;
	private JLabel udpPacketsMetric;
	private JLabel httpPacketsMetric;
	private JLabel htmlPacketsMetric;
	private JLabel pppPacketsMetric;
	private JLabel rtpPacketsMetric;
	private JLabel sdpPacketsMetric;
	private JLabel sipPacketsMetric;
	private JLabel l2tpPacketsMetric;
	private JLabel lanPacketsMetric;
	private JLabel ip4PacketsMetric;
	private JLabel ip6PacketsMetric;
	private boolean onMetricsTab = false;
	private long seconds = 0;
	
	public static final String SHOW_ALL = "Show all devices";

	
	private DeviceInfoPanel devicePanel = new DeviceInfoPanel();

	public MainWindow(List<PcapIf> devices, List<PacketContainer> packets) {
		super("Packet Sniffer");
		setSize(1280, 800);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		packetData = packets;
		
		for(PcapIf device : devices) {
			deviceListItems.add(new DeviceListItem(device));
		}
		
		buildContentPane();
		add(contentPane);
		
		createButtonListeners();
	}
	
	
	public class DeviceListItem {
		PcapIf device;
		private DeviceListItem(PcapIf device) {
			this.device = device;
		}
		public PcapIf getDevice() {
			return device;
		}
		public String toString() {
			if(device == null) return SHOW_ALL; // massive awful hack
			return device.getName() + " [" + device.getDescription() + "]";
		}
	}
	
	
	public void buildContentPane(){
		buildLiveFeedTab();
		buildPacketDetailsTab();
		buildMetricsTab();
		
		contentPane = new JTabbedPane();
		contentPane.addTab("Live Feed", liveFeedTab);
		contentPane.addTab("Packet Details", packetDetailsTab);
		contentPane.addTab("Metrics", metricsTab);
		
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				if(sourceTabbedPane.getTitleAt(index).toString().equals("Metrics")){
					onMetricsTab = true;
					updateMetrics();
				}
				else {
					onMetricsTab = false;
					if(sourceTabbedPane.getTitleAt(index).toString().equals("Packet Details")) {
						updateTable();
					}
				}
			}
		};
		contentPane.addChangeListener(changeListener);

		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				seconds++;
				if(onMetricsTab) updateMetrics();
			}
			
		}, 1, 1, TimeUnit.SECONDS);
	}
	
	
	public void updateMetrics(){
		timeRunningMetric.setText("Time Running: " + seconds + " Seconds");
		packetsSniffedMetric.setText("Packets Sniffed: " + packetData.size());
		packetsPerSecondMetric.setText("Packets Sniffed Per Second: " + (double)packetData.size()/(double)seconds );
		
		
		int numTCP = 0;
		int numUDP = 0;
		int numHTTP = 0;
		int numHTML = 0;
		int numPPP = 0;
		int numRTP = 0;
		int numSDP = 0;
		int numSIP = 0;
		int numL2TP = 0;
		int numLAN = 0;
		int numIP4 = 0;
		int numIP6 = 0;
		
		
		for(int i = 0; i < packetData.size(); i++){
			if(packetData.get(i).getProtocols().contains("TCP")){
				numTCP++;
			}
			if(packetData.get(i).getProtocols().contains("UDP")){
				numUDP++;
			}
			if(packetData.get(i).getProtocols().contains("HTTP")){
				numHTTP++;
			}
			if(packetData.get(i).getProtocols().contains("HTML")){
				numHTML++;
			}
			if(packetData.get(i).getProtocols().contains("PPP")){
				numPPP++;
			}
			if(packetData.get(i).getProtocols().contains("RTP")){
				numRTP++;
			}
			if(packetData.get(i).getProtocols().contains("SDP")){
				numSDP++;
			}
			if(packetData.get(i).getProtocols().contains("SIP")){
				numSIP++;
			}
			if(packetData.get(i).getProtocols().contains("L2TP")){
				numL2TP++;
			}
			if(packetData.get(i).getProtocols().contains("Ethernet")){
				numLAN++;
			}
			if(packetData.get(i).getProtocols().contains("IPv4")){
				numIP4++;
			}
			if(packetData.get(i).getProtocols().contains("IPv6")){
				numIP6++;
			}
		}
		
		tcpPacketsMetric.setText("TCP Packets Sniffed:   " + numTCP);
		udpPacketsMetric.setText("UDP Packets Sniffed:   " + numUDP);
		httpPacketsMetric.setText("HTTP Packets Sniffed:  " + numHTTP);
		htmlPacketsMetric.setText("HTML Packets Sniffed:  " + numHTML);
		pppPacketsMetric.setText("PPP Packets Sniffed:   " + numPPP);
		rtpPacketsMetric.setText("RTP Packets Sniffed:   " + numRTP);
		sdpPacketsMetric.setText("SDP Packets Sniffed:   " + numSDP);
		sipPacketsMetric.setText("SIP Packets Sniffed:   " + numSIP);
		l2tpPacketsMetric.setText("L2TP Packets Sniffed:  " + numL2TP);
		lanPacketsMetric.setText("Ethernet Packets Sniffed:   " + numLAN);
		ip4PacketsMetric.setText("IPv4 Packets Sniffed:   " + numIP4);
		ip6PacketsMetric.setText("IPv6 Packets Sniffed:   " + numIP6);
	}
	
	
	public void buildLiveFeedTab(){
		liveFeedTab = new JPanel();
		
		liveFeedTab.setLayout(new BorderLayout());
		deviceList.setListData(deviceListItems.toArray(new DeviceListItem[1]));
		deviceList.addListSelectionListener(listListener);
		
		listPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		liveFeedTab.add(listPane, BorderLayout.WEST);
		
		liveFeedTab.add(devicePanel, BorderLayout.SOUTH);
		
		System.setOut(new PrintStreamCapturer(mainTextArea, System.out));
		liveFeedTab.add(mainTextPane, BorderLayout.CENTER);
	}
	
	
	public void buildSearchDetailsPanel(){
		GridBagConstraints c = new GridBagConstraints();
		
		searchDetailsPanel = new JPanel();
		searchDetailsPanel.setLayout(new GridBagLayout());
		searchIPPanel = new JPanel();
		searchProtocolPanel = new JPanel();
		searchSourcePanel = new JPanel();
		searchDestinationPanel = new JPanel();
		searchDevicePanel = new JPanel();
		
		searchProtocolPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		searchSourcePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		searchDestinationPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		searchDevicePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		searchProtocolPanel.setLayout(new GridBagLayout());
		searchSourcePanel.setLayout(new GridBagLayout());
		searchDestinationPanel.setLayout(new GridBagLayout());
		searchDevicePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		searchDevicePanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		searchIPLabel = new JLabel("IP");
		searchProtocolLabel = new JLabel("Protocol");
		searchSourceLabel = new JLabel("Source");
		searchDestinationLabel = new JLabel("Destination");
		
		searchIPText = new JTextField();
		searchProtocolText = new JTextField();
		searchSourceText = new JTextField();
		searchDestinationText = new JTextField();
		searchDevicePicker = new JComboBox<>();
		
		c.gridx = 0;
		c.weightx = 0;
		c.insets = new Insets(0,5,0,5);
		c.fill = GridBagConstraints.NONE;
		searchProtocolPanel.add(searchProtocolLabel,c);
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.BOTH;
		searchProtocolPanel.add(searchProtocolText,c);
		
		c.gridx = 0;
		c.weightx = 0;
		c.insets = new Insets(0,5,0,5);
		c.fill = GridBagConstraints.NONE;
		searchSourcePanel.add(searchSourceLabel,c);
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.BOTH;
		searchSourcePanel.add(searchSourceText,c);
		
		searchDevicePicker.addItem(new DeviceListItem(null));
		for(DeviceListItem item : deviceListItems) {
			searchDevicePicker.addItem(item);
		}
		
		c.gridx = 0;
		c.weightx = 0;
		c.insets = new Insets(0,5,0,5);
		c.fill = GridBagConstraints.NONE;
		searchDestinationPanel.add(searchDestinationLabel,c);
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.BOTH;
		searchDestinationPanel.add(searchDestinationText,c);
		
		searchDevicePanel.add(new JLabel("Device"));
		searchDevicePanel.add(searchDevicePicker);
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = .16;
		c.weighty = .5;
		c.fill = GridBagConstraints.BOTH;
		searchDetailsPanel.add(searchProtocolPanel, c);
		c.gridx = 1;
		searchDetailsPanel.add(searchSourcePanel, c);
		c.gridx = 2;
		searchDetailsPanel.add(searchDestinationPanel, c);
		c.gridx = 3;
		searchDetailsPanel.add(searchDevicePanel, c);
	}

	
	public void buildPacketDetailsTab(){
		packetDetailsTab = new JPanel();
		packetDetailsTab.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		
		//
		//
		//Search
		//
		//
		packetSearchPanel = new JPanel();
		packetSearchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		packetSearchPanel.setLayout(new GridBagLayout());
		
		searchButtonPanel = new JPanel();
		searchButtonPanel.setLayout(new GridBagLayout());
		
		searchButton = new JButton("Search/Update");
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(5,5,5,5);
		c.fill = GridBagConstraints.NONE;
		searchButtonPanel.add(searchButton, c);
		
		clearSearchButton = new JButton("Clear Search");
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(5,5,5,5);
		c.fill = GridBagConstraints.NONE;
		searchButtonPanel.add(clearSearchButton, c);
		
		sortByNewButton = new JButton("Sort By Newest");
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(5,5,5,5);
		c.fill = GridBagConstraints.NONE;
		searchButtonPanel.add(sortByNewButton, c);
		sortByNewButton.setEnabled(false);
		sortByNew = true;
		
		sortByOldButton = new JButton("Sort By Oldest");
		c.gridx = 3;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(5,5,5,5);
		c.fill = GridBagConstraints.NONE;
		searchButtonPanel.add(sortByOldButton, c);
		
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 6;
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.NONE;
		packetSearchPanel.add(searchButtonPanel, c);
		c.gridwidth = 1;
		
		//For organization sake
		buildSearchDetailsPanel();
		
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 4;
		c.weightx = .16;
		c.weighty = .5;
		c.fill = GridBagConstraints.BOTH;
		packetSearchPanel.add(searchDetailsPanel, c);
		c.gridwidth = 1;
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = .1;
		c.fill = GridBagConstraints.BOTH;
		packetDetailsTab.add(packetSearchPanel, c);
		
		//
		//
		//Table
		//
		//
		String[] columnNames = {"Protocol","Source","Destination","Timestamp"};


		packetTable = new JTable(new DefaultTableModel(columnNames,0)){
			private static final long serialVersionUID = 1L;
			
		};


		JTableHeader header = packetTable.getTableHeader();
	      header.setBackground(new Color(150,150,150));
	      header.setForeground(Color.black);
		
		packetTablePane = new JScrollPane(packetTable,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = .9;
		c.fill = GridBagConstraints.BOTH;
		packetDetailsTab.add(packetTablePane,c);

	}
	
	
	public void buildMetricsTab(){
		metricsTab = new JPanel();
		metricsTab.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		timeRunningMetric = new JLabel("Time Running: ");
		packetsSniffedMetric = new JLabel("Packets Sniffed: ");
		packetsPerSecondMetric = new JLabel("Packets Sniffed Per Second: ");
		tcpPacketsMetric = new JLabel("TCP Packets Sniffed:   ");
		udpPacketsMetric = new JLabel("UDP Packets Sniffed:   ");
		httpPacketsMetric = new JLabel("HTTP Packets Sniffed: ");
		htmlPacketsMetric = new JLabel("HTML Packets Sniffed: ");
		pppPacketsMetric = new JLabel("PPP Packets Sniffed:   ");
		rtpPacketsMetric = new JLabel("RTP Packets Sniffed:   ");
		sdpPacketsMetric = new JLabel("SDP Packets Sniffed:   ");
		sipPacketsMetric = new JLabel("SIP Packets Sniffed:   ");
		l2tpPacketsMetric = new JLabel("L2TP Packets Sniffed: ");
		lanPacketsMetric = new JLabel("Ethernet Packets Sniffed:   ");
		ip4PacketsMetric = new JLabel("IP4 Packets Sniffed: ");
		ip6PacketsMetric = new JLabel("IP6 Packets Sniffed:   ");
		
		
		
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,5,5,5);
		c.weightx = 1;
		metricsTab.add(timeRunningMetric, c);
		c.gridy = 1;
		metricsTab.add(packetsSniffedMetric, c);
		c.gridy = 2;
		metricsTab.add(packetsPerSecondMetric, c);
		c.gridy = 3;
		metricsTab.add(tcpPacketsMetric, c);
		c.gridy = 4;
		metricsTab.add(udpPacketsMetric, c);
		c.gridy = 5;
		metricsTab.add(httpPacketsMetric, c);
		c.gridy = 6;
		metricsTab.add(htmlPacketsMetric, c);
		c.gridy = 7;
		metricsTab.add(pppPacketsMetric, c);
		c.gridy = 8;
		metricsTab.add(rtpPacketsMetric, c);
		c.gridy = 9;
		metricsTab.add(sdpPacketsMetric, c);
		c.gridy = 10;
		metricsTab.add(sipPacketsMetric, c);
		c.gridy = 11;
		metricsTab.add(l2tpPacketsMetric, c);
		c.gridy = 12;
		metricsTab.add(lanPacketsMetric, c);
		c.gridy = 13;
		metricsTab.add(ip4PacketsMetric, c);
		c.gridy = 14;
		metricsTab.add(ip6PacketsMetric, c);
		
	}
	
	
	public void updateTable(){
		DefaultTableModel model = (DefaultTableModel) packetTable.getModel();
		applySearchFilter();
		
		if (model.getRowCount() > 0) {
		    for (int i = model.getRowCount() - 1; i > -1; i--) {
		    	model.removeRow(i);
		    }
		}
		
		if(!sortByNew){
			for(int i = 0; i < packetData.size(); i++){
				if(packetData.get(i).isDisplayed()){
					PacketContainer p = packetData.get(i);
					model.addRow(new Object[]{p.getProtocols(), p.getSource(), p.getDestination(), p.getTimeStamp()});
				}
			}
		}
		else{
			for (int i = packetData.size() - 1; i > -1; i--) {
				if(packetData.get(i).isDisplayed()){
					PacketContainer p = packetData.get(i);
					model.addRow(new Object[]{p.getProtocols(), p.getSource(), p.getDestination(), p.getTimeStamp()});
				}
			}
		}
		
	}
	
	
	public void applySearchFilter(){
		List<String> protocols = new ArrayList<>();
		for(String protocol : searchProtocolText.getText().split(",")) {
			String trimmed = protocol.trim();
			if(trimmed.equals("")) continue;
			else protocols.add(trimmed);
		}
		for(int i = 0; i < packetData.size(); i++){
			packetData.get(i).filter(new PacketContainer(searchSourceText.getText(), 
					searchDestinationText.getText(), 
					protocols, null, searchDevicePicker.getSelectedItem().toString()));
		}
	}
	
	
	public void createButtonListeners(){
		
		
		searchButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateTable();
			}
		});
		
		sortByNewButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sortByNew = true;
				updateTable();
				sortByNewButton.setEnabled(false);
				sortByOldButton.setEnabled(true);
			}
		});
		
		sortByOldButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sortByNew = false;
				updateTable();
				sortByNewButton.setEnabled(true);
				sortByOldButton.setEnabled(false);
			}
		});
		
		
		clearSearchButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				searchIPText.setText("");
				searchProtocolText.setText("");
				searchSourceText.setText("");
				searchDestinationText.setText("");
				updateTable();
			}
		});
		
	}
}
