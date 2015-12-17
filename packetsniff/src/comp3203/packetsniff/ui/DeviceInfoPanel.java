package comp3203.packetsniff.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapIf;
import org.jnetpcap.PcapSockAddr;
import org.jnetpcap.packet.format.FormatUtils;

public class DeviceInfoPanel extends JPanel {
	private JLabel nameLabel = new JLabel("Device: ");
	private JLabel descLabel = new JLabel("Description: ");
	private JTextArea addressArea = new JTextArea("Addresses: ");
	public DeviceInfoPanel() {
		setBorder(BorderFactory.createTitledBorder("Device Details"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		nameLabel.setAlignmentX(LEFT_ALIGNMENT);
		descLabel.setAlignmentX(LEFT_ALIGNMENT);
		addressArea.setAlignmentX(LEFT_ALIGNMENT);
		add(nameLabel);
		add(descLabel);
		addressArea.setEditable(false);
		add(addressArea);
	}
	
	public void setDevice(PcapIf device) {
		nameLabel.setText("Device: " + device.getName());
		descLabel.setText("Description: " + device.getDescription());
		String addresses = "Addresses:\n";
		for(PcapAddr addr : device.getAddresses()) {
			addresses += FormatUtils.ip(addr.getAddr().getData());
			addresses += "\n";
		}
		addressArea.setText(addresses);
	}
}
