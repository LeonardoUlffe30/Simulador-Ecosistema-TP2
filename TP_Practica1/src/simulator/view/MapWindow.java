package simulator.view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class MapWindow extends JFrame implements EcoSysObserver {
	private static final long serialVersionUID = 1L;
	private Controller _ctrl;
	private AbstractMapViewer _viewer;
	private Frame _parent;

	MapWindow(Frame parent, Controller ctrl) {
		super("[MAP VIEWER]");
		this._ctrl = ctrl;
		this._parent = parent;
		intiGUI();
		this._ctrl.addObserver(this);
	}

	private void intiGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		// Poner contentPane como mainPanel
		this.setContentPane(mainPanel);
		// Crear el viewer y añadirlo a mainPanel (en el centro)
		this._viewer = new MapViewer();
		mainPanel.add(this._viewer, BorderLayout.CENTER);

		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				_ctrl.removeObserver(MapWindow.this);
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}
		
		});
		
		this.pack();
		if (_parent != null)
			setLocation(
					_parent.getLocation().x + _parent.getWidth()/2 - getWidth()/2,
					_parent.getLocation().y + _parent.getHeight()/2 - getHeight()/2);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
			this._viewer.reset(time, map, animals);
			this.pack();
		});
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
			this._viewer.reset(time, map, animals);
			this.pack();
		});
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {	
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		SwingUtilities.invokeLater(() -> {
			this._viewer.update(animals, time);
		});
	}
}
