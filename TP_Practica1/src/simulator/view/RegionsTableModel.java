package simulator.view;

import java.util.List;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.Diet;
import simulator.model.RegionInfo;

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
	// TODO definir atributos necesarios
	private Controller _ctrl;
	private List<RegionInfo> _regions;
	
	RegionsTableModel(Controller ctrl) {
		this._ctrl = ctrl;
		
		// inicializar estructuras de datos correspondientes
		this._regions = new ArrayList<>();
		
		// registrar this como observador
		this._ctrl.addObserver(this);
	}
	
	// el resto de métodos van aquí…

	@Override
	public int getRowCount() {
		return this._regions.size();
	}

	@Override
	public int getColumnCount() {
		return Diet.values().length + 3; //// 3 columnas extras para row, column, y desc.
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return null;
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub
		
	}
}
