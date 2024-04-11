package simulator.view;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.MapInfo.RegionData;
import simulator.model.Diet;
import simulator.model.RegionInfo;
import simulator.model.State;

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
	// TODO definir atributos necesarios
	private Controller _ctrl;
	//private List<Map<String, Integer>> a; //row, col, desc, NORMAL, HUNGER, MATE, DANGER
	private Map<RegionData, Map<Diet, Integer>> animals_region;
	
	private List<RegionData> _regions;
	
	RegionsTableModel(Controller ctrl) {
		this._ctrl = ctrl;
		
		// inicializar estructuras de datos correspondientes
		this._regions = new ArrayList<>();
		
		// registrar this como observador
		this._ctrl.addObserver(this);
	}
	
	// el resto de métodos van aquí…
	
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
		return Diet.values().length + 3; //// 3 columnas extras para row, column, y desc.
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex < 3) {
            // Las tres primeras columnas son row, col y desc.
            switch (columnIndex) {
                case 0:
                    return _regions.get(rowIndex).row();
                case 1:
                    return _regions.get(rowIndex).col();
                case 2:
                    return _regions.get(rowIndex).r().toString();
                default:
                	String as = State.values()[columnIndex].name();
                	RegionInfo data = _regions.get(rowIndex).r();
                	
                    return null;
            }
        } else {
            return null;
        }
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
		
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		
	}

	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		this.updateTabla(map);
	}
	
	private void updateTabla(MapInfo map) {
		this._regions.clear();
//		aux = new RegionData[map.get_rows()][map.get_cols()];
		Iterator<RegionData> aux = map.iterator();
		while(aux.hasNext()) {
			RegionData region = aux.next();
			List<AnimalInfo> animals = region.r().getAnimalsInfo();
			this._regions.add(region);
		}
		this.fireTableDataChanged();
	}
}
