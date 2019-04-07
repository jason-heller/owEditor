package utils;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class AppUtils {
	
	private static List<Window> windows = new ArrayList<Window>();
	
	public static void open(Class c) {
		open(c, false);
	}
	
	public static void open(Class c, boolean allowMultiple, Object ... args) {
		if (!allowMultiple) {
			for(Component component : windows) {
				if (component.getClass() == c) {
					return;
				}
			}
		}
		
		try {
			Window w;
			Class[] types = new Class[args.length];
			for(int i = 0; i < args.length; i++) {
				types[i] = args[i].getClass();
			}
			try {
				if (args.length == 0) {
					w = (Window) c.newInstance();
				} else {
					w = (Window) c.getDeclaredConstructor(types).newInstance(args);
				}
			} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				e.printStackTrace();
				return;
			}
			//Window w = (Window) c.newInstance(args);
			windows.add(w);
			w.addWindowListener(new WindowListener() {

				@Override
				public void windowActivated(WindowEvent arg0) {
				}

				@Override
				public void windowClosed(WindowEvent arg0) {
					windows.remove(w);
				}

				@Override
				public void windowClosing(WindowEvent arg0) {
				}

				@Override
				public void windowDeactivated(WindowEvent arg0) {
				}

				@Override
				public void windowDeiconified(WindowEvent arg0) {
				}

				@Override
				public void windowIconified(WindowEvent arg0) {
				}

				@Override
				public void windowOpened(WindowEvent arg0) {
				}
				
			});
			
			
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void errorMessage(String message) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	public static void warningMessage(String message) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
			}
		});
	}
	
	public static void infoMessage(String message) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, message, "", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

	public static void close(Window window) {
		window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
	}
}
