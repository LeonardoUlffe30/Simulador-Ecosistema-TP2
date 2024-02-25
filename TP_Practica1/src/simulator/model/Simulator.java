package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import simulator.factories.Factory;

public class Simulator implements JSONable {
	
	private Factory<Animal> _animal_factory;
	private Factory<Region> _regions_factory;
	private RegionManager _region_mngr;
	protected List<Animal> _animals_in_list;
	private double dt;

	public Simulator(int cols, int rows, int width, int height,
			Factory<Animal> animals_factory, Factory<Region> regions_factory) {
		this._animal_factory = animals_factory;
		this._regions_factory = regions_factory;
		this._region_mngr = new RegionManager(cols, rows, width, height);
		this._animals_in_list = new ArrayList<Animal>();
		this.dt = 0.0;
	}
	
	private void set_region(int row, int col, Region r) {
		this.get_region_mngr().set_region(row, col, r);
	}
	
	public void set_region(int row, int col, JSONObject r_json) {
		Region R = this.get_regions_factory().create_instance(r_json);
		this.set_region(row, col, R);
	}
	
	private void add_animal(Animal a) {
		this.get_animals_in_list().add(a);
		this.get_region_mngr().register_animal(a);
	}
	
	public void add_animal(JSONObject a_json) {
		Animal a = this.get_animal_factory().create_instance(a_json);
		this.add_animal(a);
	}
	
	public MapInfo get_map_info() {
		return this._region_mngr;
	}
	
	public List<? extends AnimalInfo> get_animals(){
		return Collections.unmodifiableList(this._animals_in_list);
	}
	
	public double get_time() {
		return dt;
	}
	
	public void advance(double dt) {
		//Puede petar al recorrer la lista
		this.dt += dt;
		Iterator<Animal> it = this.get_animals_in_list().iterator();
		while (it.hasNext()) {
			Animal a = it.next();
			if (a.get_state() == State.DEAD) {
				_region_mngr.unregister_animal(a); //Eliminamos el animal del region manager
				it.remove(); //Lo eliminamos de la lista de animales
			}
		}
		
		for (Animal a : this.get_animals_in_list()) {
			a.update(dt);
		}		
		this.get_region_mngr().update_all_regions(dt);
		for (Animal a : this.get_animals_in_list()) {
			if (a.is_pregnant()) {
				this.add_animal(a.deliver_baby());
			}	
		}
		
	}
	
	public JSONObject as_JSON() {
		JSONObject simulatorObject = new JSONObject();
		simulatorObject.put("time", dt);
		simulatorObject.put("state", this.get_region_mngr().as_JSON());
		return simulatorObject;
		
	}

	public Factory<Animal> get_animal_factory() {
		return _animal_factory;
	}

	public Factory<Region> get_regions_factory() {
		return _regions_factory;
	}

	public RegionManager get_region_mngr() {
		return _region_mngr;
	}

	public List<Animal> get_animals_in_list() {
		return _animals_in_list;
	}

	public double getDt() {
		return dt;
	}
}
