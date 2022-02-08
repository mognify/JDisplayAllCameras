package main;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamPanel.DrawMode;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JLabel;

public class JDACMainWindow {
	public static int refresh = 60;
	public static boolean motionDetection = false;
	public static String strDisplayLayout = "lateral";

	private JFrame frame;
	private JPanel panel_1;

	private List<WebcamPanel> panels = new ArrayList<WebcamPanel>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JDACMainWindow window = new JDACMainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public JDACMainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 330, 293);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.TRAILING, 5, 5));
		
		initContent();
	}

	private void initContent() {
		
		panel_1 = new JPanel();
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		frame.getContentPane().add(panel_1);

		for (Webcam webcam : Webcam.getWebcams()) {
			int camsize = webcam.getViewSizes().length-1;
			webcam.setViewSize(webcam.getViewSizes()[camsize]);
			System.out.println(webcam.getViewSizes()[camsize]);
			WebcamPanel panel = new WebcamPanel(webcam, false);
			panel.setDrawMode(DrawMode.FILL);

			panels.add(panel);
			panel_1.add(panel);
		}
		for(int i = panels.size()-1; i > 0; i--)
			panel_1.add(panels.get(i));

		frame.setTitle("JDAC (Java, Display All Cameras)");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Settings");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Refresh cameras");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (WebcamPanel panel : panels) {
					panel.start();
				}
				for(WebcamPanel wp : panels) {
					wp.getWebcam().close();
					wp.stop();
				}
		        frame.getContentPane().remove(panel_1);
				panel_1 = new JPanel();
				panels = new ArrayList<WebcamPanel>();
				initContent();
			}
		});
		
		JCheckBox chckbxNewCheckBox_3 = new JCheckBox("Default to these settings");
		mnNewMenu.add(chckbxNewCheckBox_3);
		
		JSeparator separator_2 = new JSeparator();
		mnNewMenu.add(separator_2);
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenuItem mntmRestartInterval = new JMenuItem("Set refresh interval (current: " + refresh + "mins)");
		mntmRestartInterval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		mnNewMenu.add(mntmRestartInterval);
		
		JSeparator separator = new JSeparator();
		mnNewMenu.add(separator);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Record");
		mnNewMenu.add(chckbxNewCheckBox);
		
		JCheckBox chckbxNewCheckBox_2 = new JCheckBox("Only record when motion detected");
		chckbxNewCheckBox_2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				motionDetection = !motionDetection;
			}
		});
		mnNewMenu.add(chckbxNewCheckBox_2);
		
		JCheckBox chckbxNewCheckBox_1 = new JCheckBox("Show timestamps (will appear in recordings)");
		mnNewMenu.add(chckbxNewCheckBox_1);
		
		JSeparator separator_1 = new JSeparator();
		mnNewMenu.add(separator_1);
		
		JLabel lblNewLabel = new JLabel("      Current display layout: " + strDisplayLayout);
		mnNewMenu.add(lblNewLabel);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Horizontally");
		mnNewMenu.add(mntmNewMenuItem_1);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Laterally");
		mnNewMenu.add(mntmNewMenuItem_2);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Grid (horizontal preference)");
		mnNewMenu.add(mntmNewMenuItem_3);
		
		JMenuItem mntmNewMenuItem_4 = new JMenuItem("Grid (lateral preference)");
		mnNewMenu.add(mntmNewMenuItem_4);
		
		frame.setVisible(true);
		new Thread()
		{
		    public void run() {
				for (WebcamPanel panel : panels) {
					panel.start();
				}
				long start = System.currentTimeMillis();
				do {
					if(((System.currentTimeMillis() - start)/60000) > refresh) break;
				}while(true);
				for(WebcamPanel wp : panels) {
					wp.getWebcam().close();
					wp.stop();
				}
		        frame.getContentPane().remove(panel_1);
				panel_1 = new JPanel();
				panels = new ArrayList<WebcamPanel>();
				initContent();
		    }
		}.start();
	}

}
