package comp3203.packetsniff.ui;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jnetpcap.PcapIf;

import comp3203.packetsniff.PacketSniffer;

public class MainWindow extends JFrame {
	private JList<DeviceListItem> deviceList = new JList<>();
	private JScrollPane listPane = new JScrollPane(deviceList);
	private JTextArea mainTextArea = new JTextArea();
	private JScrollPane mainTextPane = new JScrollPane(mainTextArea);
	private PcapIf selectedDevice;
	private ListSelectionListener listListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			selectedDevice = deviceList.getSelectedValue().getDevice();
			devicePanel.setDevice(selectedDevice);
		}
	};
	
	private DeviceInfoPanel devicePanel = new DeviceInfoPanel();

	public MainWindow(List<PcapIf> devices) {
		super("Packet Sniffer");
		setSize(1280, 800);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		
		DeviceListItem[] items = new DeviceListItem[devices.size()];
		for(int i = 0; i < items.length; i++) {
			items[i] = new DeviceListItem(devices.get(i));
		}
		deviceList.setListData(items);
		deviceList.addListSelectionListener(listListener);
		
		listPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(listPane, BorderLayout.WEST);
		
		add(devicePanel, BorderLayout.SOUTH);
		
		System.setOut(new PrintStreamCapturer(mainTextArea, System.out));
		add(mainTextPane, BorderLayout.CENTER);
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
}
