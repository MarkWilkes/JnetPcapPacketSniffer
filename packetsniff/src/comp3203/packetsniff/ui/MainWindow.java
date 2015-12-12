package comp3203.packetsniff.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jnetpcap.PcapIf;

public class MainWindow extends JFrame {
	private JList<DeviceListItem> deviceList = new JList<>();
	private JScrollPane listPane = new JScrollPane(deviceList);
	private static UIDefaults defaults = UIManager.getDefaults();
	private ListSelectionListener listListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			// TODO Auto-generated method stub
			devicePanel.setDevice(deviceList.getSelectedValue().getDevice());
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
