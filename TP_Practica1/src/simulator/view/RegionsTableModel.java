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
	RegionData[][] matrizRegionData;
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
		return Diet.values().length + 3; // + (row, column, y desc)
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
        	if(this._regions != null) {
        		RegionData aux = this._regions.get(rowIndex);
        		return this.animals_region.get(this._regions.get(rowIndex)).getOrDefault(diet,0);
        	}
        	return null;
        }
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		this.updateTablaRegister(map);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		this.updateTabla(map);
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		Map<Diet, Integer> innerMap = this.animals_region.get(matrizRegionData[row][col]);
		for(AnimalInfo a :  r.getAnimalsInfo()) {
			innerMap.put(a.get_diet(), innerMap.getOrDefault(a.get_diet(),0) + 1);
			this.animals_region.replace(this.matrizRegionData[row][col], innerMap);
		}
		this.fireTableDataChanged();
	}

	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		this.updateTabla(map);
	}
	
	private void updateTabla(MapInfo map) {
		for (Map<Diet, Integer> mapaInterno : this.animals_region.values()) {
            mapaInterno.clear(); // Eliminar el contenido de cada mapa interno
        }
        this.animals_region.clear();
        for (int i = 0; i < this.matrizRegionData.length; i++) {
            for (int j = 0; j < this.matrizRegionData[i].length; j++) {
                this.matrizRegionData[i][j] = null; // Establecer todos los elementos en 0
                // O matriz[i][j] = null; si la matriz es de tipo Integer[][] o cualquier otro tipo de objeto
            }
        }
        this._regions.clear();
		this.matrizRegionData = new RegionData[map.get_rows()][map.get_cols()];
		Iterator<RegionData> aux = map.iterator();
		
		while(aux.hasNext()) {
			RegionData region = aux.next();
			List<AnimalInfo> animals = region.r().getAnimalsInfo();
			this.matrizRegionData[region.row()][region.col()] = region; //Agrego a la matriz de regiones
			this._regions.add(region); //Agrego a la lista de regiones
			this.animals_region.putIfAbsent(region, new HashMap<>()); //Inicializa el mapa interno (valor) de la clave region
			for(AnimalInfo a : animals) {
				Diet aDiet = a.get_diet();
				this.animals_region.get(region).put(aDiet, this.animals_region.get(region).getOrDefault(aDiet, 0)+1);
			}
		}
		this.fireTableDataChanged();
	}
	
	private void updateTablaRegister(MapInfo map) {
		for (Map<Diet, Integer> mapaInterno : this.animals_region.values()) {
            mapaInterno.clear(); // Eliminar el contenido de cada mapa interno
        }
        this.animals_region.clear();
        this._regions.clear();
		this.matrizRegionData = new RegionData[map.get_rows()][map.get_cols()];
		Iterator<RegionData> aux = map.iterator();
		
		while(aux.hasNext()) {
			RegionData region = aux.next();
			List<AnimalInfo> animals = region.r().getAnimalsInfo();
			this.matrizRegionData[region.row()][region.col()] = region; //Agrego a la matriz de regiones
			this._regions.add(region); //Agrego a la lista de regiones
			this.animals_region.putIfAbsent(region, new HashMap<>()); //Inicializa el mapa interno (valor) de la clave region
			for(AnimalInfo a : animals) {
				Diet aDiet = a.get_diet();
				this.animals_region.get(region).put(aDiet, this.animals_region.get(region).getOrDefault(aDiet, 0)+1);
			}
		}
		this.fireTableDataChanged();
		
	}
}
