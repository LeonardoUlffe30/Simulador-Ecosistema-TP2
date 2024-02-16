package simulator.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Simulator implements JSONable {
	
	Factory<Animal> _animal_factory;
	Factory<Region> _regions_factory;
	RegionManager _region_mngr;
	protected List<Animal> _animals_in_list;
	double dt;

	public Simulator(int cols, int rows, int width, int height,
			Factory<Animal> animals_factory, Factory<Region> regions_factory) {
		this._animal_factory = animals_factory;
		this._regions_factory = regions_factory;
		this.dt = 0.0;
		_region_mngr = new RegionManager(cols, rows, width, height);
		_animals_in_list = new ArrayList<Animal>();
	}
	
	private void set_region(int row, int col, Region r) {
		_region_mngr.set_region(row, col, r);
	}
	
	private void set_region(int row, int col, JSONObject r_json) {
		JSONArray animales = r_json.getJSONArray("animals");
		Region r = new Region();
		for(Animal a : animales.keySet()) {
			
		}
		for (int i = 0; i < animales.length(); i++) {
			r.register_animal(animales.get(i));
		}
		//_region_mngr.set_region(row, col, r_json);
		
		Region r = (Region) r_json.getJSONArray("animals");
		set_region(row, col, r);
		Region r2 = new Region();
		}
	
	private void add_animal(Animal a) {
		
	}
	
	public void add_animal(JSONObject a_json) {
		
	}
}
