package comp3203.packetsniff.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
	private PcapIf selectedDevice;
	private ListSelectionListener listListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) return;
			selectedDevice = deviceList.getSelectedValue().getDevice();
			devicePanel.setDevice(selectedDevice);
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					PacketSniffer.listen(selectedDevice);
				}
			}).start();
		}
	};	
	
	
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
	private JPanel searchBeforeDatePanel;
	private JPanel searchAfterDatePanel;
	private JLabel searchIPLabel;
	private JLabel searchProtocolLabel;
	private JLabel searchSourceLabel;
	private JLabel searchDestinationLabel;
	private JLabel searchBeforeDateLabel;
	private JLabel searchAfterDateLabel;
	private JTextField searchIPText;
	private JTextField searchProtocolText;
	private JTextField searchSourceText;
	private JTextField searchDestinationText;
	private JTextField searchBeforeDateText;
	private JTextField searchAfterDateText;
	
	private boolean sortByNew;
	
	private JPanel metricsTab;
	

	
	private DeviceInfoPanel devicePanel = new DeviceInfoPanel();

	public MainWindow(List<PcapIf> devices, List<PacketContainer> packets) {
		super("Packet Sniffer");
		setSize(1280, 800);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		packetData = packets;
		
		buildContentPane(devices);
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
			return device.getName();
		}
	}
	
	
	public void buildContentPane(List<PcapIf> devices){
		buildLiveFeedTab(devices);
		buildPacketDetailsTab();
		buildMetricsTab();
		
		contentPane = new JTabbedPane();
		contentPane.addTab("Live Feed", liveFeedTab);
		contentPane.addTab("Packet Details", packetDetailsTab);
		contentPane.addTab("Metrics", metricsTab);
	}
	
	
	public void buildLiveFeedTab(List<PcapIf> devices){
		liveFeedTab = new JPanel();
		
		liveFeedTab.setLayout(new BorderLayout());
		DeviceListItem[] items = new DeviceListItem[devices.size()];
		for(int i = 0; i < items.length; i++) {
			items[i] = new DeviceListItem(devices.get(i));
		}
		deviceList.setListData(items);
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
		searchBeforeDatePanel = new JPanel();
		searchAfterDatePanel = new JPanel();
		
		searchProtocolPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		searchSourcePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		searchDestinationPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		searchBeforeDatePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		searchAfterDatePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		searchProtocolPanel.setLayout(new GridBagLayout());
		searchSourcePanel.setLayout(new GridBagLayout());
		searchDestinationPanel.setLayout(new GridBagLayout());
		searchBeforeDatePanel.setLayout(new GridBagLayout());
		searchAfterDatePanel.setLayout(new GridBagLayout());
		
		searchIPLabel = new JLabel("IP");
		searchProtocolLabel = new JLabel("Protocol");
		searchSourceLabel = new JLabel("Source");
		searchDestinationLabel = new JLabel("Destination");
		searchBeforeDateLabel = new JLabel("Before Date");
		searchAfterDateLabel = new JLabel("After Date");
		
		searchIPText = new JTextField();
		searchProtocolText = new JTextField();
		searchSourceText = new JTextField();
		searchDestinationText = new JTextField();
		searchBeforeDateText = new JTextField();
		searchAfterDateText = new JTextField();
		
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
		
		c.gridx = 0;
		c.weightx = 0;
		c.insets = new Insets(0,5,0,5);
		c.fill = GridBagConstraints.NONE;
		searchBeforeDatePanel.add(searchBeforeDateLabel,c);
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.BOTH;
		searchBeforeDatePanel.add(searchBeforeDateText,c);
		searchBeforeDateText.setEnabled(false);
		
		c.gridx = 0;
		c.weightx = 0;
		c.insets = new Insets(0,5,0,5);
		c.fill = GridBagConstraints.NONE;
		searchAfterDatePanel.add(searchAfterDateLabel,c);
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(0,0,0,0);
		c.fill = GridBagConstraints.BOTH;
		searchAfterDatePanel.add(searchAfterDateText,c);
		searchAfterDateText.setEnabled(false);
		
		
		
		
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
		searchDetailsPanel.add(searchBeforeDatePanel, c);
		c.gridx = 4;
		searchDetailsPanel.add(searchAfterDatePanel, c);	
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
					protocols, null));
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
				searchBeforeDateText.setText("");
				searchAfterDateText.setText("");
				updateTable();
			}
		});
		
	}
}
