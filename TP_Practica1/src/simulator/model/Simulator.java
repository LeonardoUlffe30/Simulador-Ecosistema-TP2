package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
		//llamar a las factorias
		//TODO
		/*JSONArray animales = r_json.getJSONArray("animals");
		Region r = new Region();
		for(Animal a : animales.keySet()) {
			
		}
		for (int i = 0; i < animales.length(); i++) {
			r.register_animal(animales.get(i));
		}
		//_region_mngr.set_region(row, col, r_json);
		
		Region r = (Region) r_json.getJSONArray("animals");
		set_region(row, col, r);
		Region r2 = new Region();*/
		}
	
	private void add_animal(Animal a) {
		_animals_in_list.add(a);
		_region_mngr.register_animal(a);
	}
	
	public void add_animal(JSONObject a_json) {
		//llamar a las factorias
		//TODO
	}
	
	public MapInfo get_map_info() {
		return this._region_mngr;
	}
	
	public List<? extends AnimalInfo> get_animals(){
		//List<String> unModifiableStringList = Collections.unmodifiableList(myActualModifiableList);
		return Collections.unmodifiableList(this._animals_in_list);
	}
	
	public double get_time() {
		return dt;
	}
	
	public void advance(double dt) {
		//Puede petar al recorrer la lista
		this.dt += dt;
		Iterator<Animal> it = this._animals_in_list.iterator();
		while (it.hasNext()) {
			Animal a = it.next();
			if (a.get_state() == State.DEAD) {
				_region_mngr.unregister_animal(a); //Eliminamos el animal del region manager
				it.remove(); //Lo eliminamos de la lista de animales
			}
		}
		
		for (Animal a : _animals_in_list) {
			a.update(dt);
		}		
		this._region_mngr.update_all_regions(dt);
		for (Animal a : _animals_in_list) {
			if (a.is_pregnant()) {
				add_animal(a.deliver_baby());
			}	
		}
		
	}
	
	public JSONObject as_JSON() {
		JSONObject jo = new JSONObject();
		jo.put("time", dt);
		jo.put("state", this._region_mngr.as_JSON());
		return jo;
		
	}
}
