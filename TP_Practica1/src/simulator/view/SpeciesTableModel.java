package simulator.view;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.State;

class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
	private Controller _ctrl;
	private Map<String, Map<State, Integer>> speciesData; //<oveja/wolf, <normal/mate/hunger/dead/danger, value>
	private List<String> _species; 
	
	SpeciesTableModel(Controller ctrl) {
		this._ctrl = ctrl;
		speciesData = new HashMap<>();
		this._ctrl.addObserver(this);
		_species = new ArrayList<>();
	}

	@Override
	public String getColumnName(int columnIndex) {
		if(columnIndex == 0)
			return "Species";
		else {
			return State.values()[columnIndex-1].name();
		}
    }

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		this.updateData(animals);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		this.updateData(animals);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		this.updateData(a);
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		this.updateData(animals);
		_species.toString();
	}

	@Override
	public int getRowCount() {
		return speciesData.size(); // oveja, lobo
	}

	@Override
	public int getColumnCount() {
		return State.values().length + 1; // +1 para la columna con el nombre de la especie
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == 0) {
			return speciesData.keySet().toArray()[rowIndex];
		} else {
			//String species = (String) speciesData.keySet().toArray()[rowIndex]; // species.get(rowIndex)
			String species = _species.get(rowIndex);
			State state = State.values()[columnIndex - 1];
			return speciesData.get(species).getOrDefault(state, 0);
		}
	}
	
	private void updateData(List<AnimalInfo> animals) {
		speciesData.clear();
		
		for(AnimalInfo animal : animals) {
			String species = animal.get_genetic_code();
			if (!_species.contains(species)){
				_species.add(species);
			}
			speciesData.putIfAbsent(species, new HashMap<>());
			State state = animal.get_state();
			speciesData.get(species).put(state, speciesData.get(species).getOrDefault(state, 0)+1);
		}
		this.fireTableDataChanged();
	}
	private void updateData(AnimalInfo animal) {
		
			String species = animal.get_genetic_code();
			speciesData.putIfAbsent(species, new HashMap<>());
			if (!_species.contains(species)){
				_species.add(species);
			}
			State state = animal.get_state();
			speciesData.get(species).put(state, speciesData.get(species).getOrDefault(state, 0)+1);
		this.fireTableDataChanged();
	}
}
