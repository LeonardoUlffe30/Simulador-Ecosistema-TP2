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
	private Controller _ctrl;
	private AbstractMapViewer _viewer;
	private Frame _parent;

	MapWindow(Frame parent, Controller ctrl) {
		super("[MAP VIEWER]");
		this._ctrl = ctrl;
		this._parent = parent;
		intiGUI();
		// TODO registrar this como observador
		this._ctrl.addObserver(this);
		
	}

	private void intiGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		// TODO poner contentPane como mainPanel
		this.setContentPane(mainPanel);
		
		// TODO crear el viewer y añadirlo a mainPanel (en el centro)
		this._viewer = new MapViewer();
		mainPanel.add(this._viewer, BorderLayout.CENTER);
		
		// TODO en el método windowClosing, eliminar ‘MapWindow.this’ de los
		// observadores
//		this.addWindowListener(
//		});
		
		this.pack();
		if (_parent != null)
			setLocation(
					_parent.getLocation().x + _parent.getWidth()/2 - getWidth()/2,
					_parent.getLocation().y + _parent.getHeight()/2 - getHeight()/2);
		this.setResizable(false);
		this.setVisible(true);
	}

	// TODO otros métodos van aquí….
	
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
			this.pack();
		});
	}
}
