package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class StatusBar extends JPanel implements EcoSysObserver {
	private static final long serialVersionUID = 1L;
	private JLabel _timeLabel;
	private JLabel _totalAnimalsLabel;
	private JLabel _dimensionLabel;
	private Controller _ctrl;
	
	StatusBar(Controller ctrl) {
		initGUI();
		this._ctrl = ctrl;
		this._ctrl.addObserver(this);
	}

	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));
		
		// Crear varios JLabel para el tiempo, el n�mero de animales, y la
		// dimensi�n y a�adirlos al panel. Puedes utilizar el siguiente c�digo
		// para a�adir un separador vertical:
		this._timeLabel = new JLabel("Time: ");
		this._totalAnimalsLabel = new JLabel("Total Animals: ");
		this._dimensionLabel = new JLabel("Dimension: ");
		
		this.add(this._timeLabel);
		this.addSeparator();
		this.add(this._totalAnimalsLabel);
		this.addSeparator();
		this.add(this._dimensionLabel);
		this.addSeparator();
	}

	private void addSeparator() {
		JSeparator s = new JSeparator(JSeparator.VERTICAL);
		s.setPreferredSize(new Dimension(15, 20));
		this.add(s);
	}
	
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		this.updateStatusBar(time, map, animals);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		this.updateStatusBar(time, map, animals);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		this.updateStatusBar(time, map, animals);
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		this.updateStatusBar(time, map, animals);
	}
	
	private void updateStatusBar(double time, MapInfo map, List<AnimalInfo> animals) {
		this._timeLabel.setText("Time: " + String.format("%.3f", time));
		this._totalAnimalsLabel.setText("Total Animals: " + animals.size());
		this._dimensionLabel.setText("Dimension: " + map.get_width() + "x" + map.get_height() 
		+ " " + map.get_cols() + "x" + map.get_rows());
		
	}
}
