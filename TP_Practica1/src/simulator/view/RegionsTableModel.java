package simulator.view;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model. EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.MapInfo.RegionData;
import simulator.model.Diet;
import simulator.model.RegionInfo;

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
	private Controller _ctrl;
	private Map<RegionData, Map<Diet, Integer>> animals_region;  //<{row, col, RegionInfo}, <{HERBIVORE, CARNIVORE},0}>
	private List<RegionData> _regions;
	
	RegionsTableModel(Controller ctrl) {
		this._ctrl = ctrl;
		this._regions = new ArrayList<>();
		this.animals_region = new HashMap<>();
		this._ctrl.addObserver(this);
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		if(columnIndex == 0)
			return "Row";
		if(columnIndex == 1)
			return "Col";
		if(columnIndex == 2)
			return "Desc.";
		else {
			return Diet.values()[columnIndex-3].name();
		}
    }
	
	@Override
	public int getRowCount() {
		return this._regions.size();
	}

	@Override
	public int getColumnCount() {
		return Diet.values().length + 3; // + (row, column, desc)
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex < 3) {
            switch (columnIndex) {
                case 0:
                    return _regions.get(rowIndex).row();
                case 1:
                    return _regions.get(rowIndex).col();
                case 2:
                    return _regions.get(rowIndex).r().toString();
                default:
                	return null;
            }
        } else {
        	Diet diet = Diet.values()[columnIndex-3];
        	RegionData regionData = this._regions.get(rowIndex);
        	
        	return this.animals_region.get(regionData).getOrDefault(diet,0);
        }
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		this.updateTabla(map);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		this.updateTabla(map);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		this.updateTabla(map);
		animals.indexOf(a);
		
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {		
		int indexToChange = (row * map.get_cols()) + col; // Indice a buscar en lista
		RegionData oldRegionData = _regions.get(indexToChange);
		Map<Diet, Integer> valueCurrentRegionData = this.animals_region.get(oldRegionData); //Obtiene valor <Diet, Integer> asociado a actual regionData
		
		RegionData newRegionData = (new RegionData(row, col, r));
		this._regions.set(indexToChange, newRegionData); //Actualiza lista de regionData
		this.animals_region.remove(oldRegionData); // Elimina clave (actual regionData) del mapa
		this.animals_region.put(newRegionData, valueCurrentRegionData); // Inserta nueva clave  
		
		this.fireTableDataChanged();
	} 
	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		this.updateTabla(map);
	}
	
	private void updateTabla(MapInfo map) {
        this.animals_region.clear(); // Elimina todas las claves del mapa
        this._regions.clear(); //Elimina todos los elementos de la lista
		Iterator<RegionData> aux = map.iterator();
		
		while(aux.hasNext()) {
			RegionData region = aux.next();
			List<AnimalInfo> animals = region.r().getAnimalsInfo();
			
			this._regions.add(region); //Agrego a la lista de regiones
			this.animals_region.putIfAbsent(region, new HashMap<>()); //Inicializa el mapa interno (valor) de la clave region
			
			if(this.animals_region.get(region) == null) {
				for(int i = 0; i < Diet.values().length; i++) {
					this.animals_region.get(region).putIfAbsent(Diet.values()[i], 0);
				}
			}
			
			for(AnimalInfo a : animals) {
				Diet aDiet = a.get_diet();
				this.animals_region.get(region).put(aDiet, this.animals_region.get(region).getOrDefault(aDiet, 0)+1);
			}
		}
		this.fireTableDataChanged();
	}
}
