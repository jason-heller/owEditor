package application.swing.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.OverlayLayout;
import javax.swing.SwingConstants;

import application.swing.SwingControl;

public class SplashFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private long openTime;
	
	public SplashFrame() {
		super();
		
		openTime = System.currentTimeMillis();
	
		setAlwaysOnTop(true);
		setVisible(false);
		//setLayout(null);
		setSize(640,320);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		setVisible(true);
		
		JPanel panel = new JPanel();
		panel.setLayout(new OverlayLayout(panel));
		
		BufferedImage img;
		try {
			JLabel text = new JLabel("BranchMap Version 2.0");
			text.setHorizontalAlignment(SwingConstants.CENTER);
			text.setMaximumSize(new Dimension(640, 320));
			text.setForeground(Color.WHITE);
			text.setFont(new Font("Arial", Font.BOLD, 30));
			JLabel under = new JLabel("Branchmap is currently under development");
			under.setMaximumSize(new Dimension(640, 320));
			under.setHorizontalAlignment(SwingConstants.LEFT);
			under.setVerticalAlignment(SwingConstants.BOTTOM);
			under.setForeground(Color.WHITE);
			under.setFont(new Font("Courier New", Font.PLAIN, 15));
			img = ImageIO.read(new File("src/application/swing/frames/splash.png"));
			JLabel label = new JLabel(new ImageIcon(img));
			label.setMaximumSize(new Dimension(640, 320));
			
			panel.add(under);
			panel.add(text);
			panel.add(label);
			
			
		} catch (IOException e) {
			e.printStackTrace();
			close();
		}
		
		add(panel);

		setLocationRelativeTo(SwingControl.glCanvas);
		pack();
		
		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				close();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {
				close();
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				close();
			}
			
		});
		
		new Thread() {
			public void run() {
				while(true) {
					//setVisible(true);
					if (System.currentTimeMillis() - openTime > 4000) {
						close();
						return;
					}
				}
			}
		}.start();
	}

	private void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

}
