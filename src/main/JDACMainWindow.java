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
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuBar;

public class JDACMainWindow {

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
			webcam.setViewSize(webcam.getViewSizes()[camsize]);//WebcamResolution.QVGA.getSize());
			System.out.println(webcam.getViewSizes()[camsize]);
			WebcamPanel panel = new WebcamPanel(webcam, false);
			panel.setDrawMode(DrawMode.FILL);

			panels.add(panel);
			panel_1.add(panel);
		}
		for(int i = panels.size()-1; i > 0; i--)
			panel_1.add(panels.get(i));

		frame.setTitle("Few Cameras At Once");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Record");
		menuBar.add(chckbxNewCheckBox);
		
		JButton btnNewButton = new JButton("Restart");
		menuBar.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {

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
		
		frame.setVisible(true);
		new Thread()
		{
		    public void run() {
				for (WebcamPanel panel : panels) {
					panel.start();
				}
				long start = System.currentTimeMillis();
				do {
					if(System.currentTimeMillis() - start > 3600000) break;
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
