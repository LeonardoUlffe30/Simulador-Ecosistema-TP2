package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import simulator.factories.Factory;

public class Simulator implements JSONable, Observable<EcoSysObserver> {

	private Factory<Animal> _animal_factory;
	private Factory<Region> _regions_factory;
	private RegionManager _region_mngr;
	protected List<Animal> _animals;
	private List<EcoSysObserver> _observers;
	private double _dt;

	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory,
			Factory<Region> regions_factory) {
		this._animal_factory = animals_factory;
		this._regions_factory = regions_factory;
		this._region_mngr = new RegionManager(cols, rows, width, height);
		this._animals = new ArrayList<Animal>();
		this._dt = 0.0;
		this._observers = new ArrayList<EcoSysObserver>();
	}

	private void set_region(int row, int col, Region r) {
		this._region_mngr.set_region(row, col, r);
		
		for(EcoSysObserver o : this._observers)
			o.onRegionSet(row, col, this._region_mngr, r);
	}

	public void set_region(int row, int col, JSONObject r_json) {
		Region R = this._regions_factory.create_instance(r_json);
		this.set_region(row, col, R);
	}

	private void add_animal(Animal a) {
		this._animals.add(a);
		this._region_mngr.register_animal(a);
		
		List<AnimalInfo> animals = new ArrayList<>(_animals);
		for(EcoSysObserver o : this._observers)
			o.onAnimalAdded(this._dt, this._region_mngr, animals, a);
	}

	public void add_animal(JSONObject a_json) {
		Animal A = this._animal_factory.create_instance(a_json);
		this.add_animal(A);
	}

	public MapInfo get_map_info() {
		return this._region_mngr;
	}

	public List<? extends AnimalInfo> get_animals() {
		return Collections.unmodifiableList(this._animals);
	}

	public double get_time() {
		return _dt;
	}

	public void advance(double _dt) {
		this._dt += _dt;
		List<Animal> dead_animals = new ArrayList<Animal>();
		for (Animal a : this._animals) {
			if (a.get_state() == State.DEAD) {
				this._region_mngr.unregister_animal(a);
				dead_animals.add(a);
			}
		}

		for (Animal a : dead_animals)
			this._animals.remove(a);

		for (Animal a : this._animals) {
			a.update(_dt);
			this._region_mngr.update_animal_region(a);
		}
		this._region_mngr.update_all_regions(_dt);

		List<Animal> babies = new ArrayList<Animal>();
		for (Animal a : this._animals) {
			if (a.is_pregnant())
				babies.add(a.deliver_baby());
		}

		for (Animal a : babies)
			this.add_animal(a);
		
		List<AnimalInfo> animals = new ArrayList<>(_animals);
		for(EcoSysObserver o : this._observers)
			o.onAdvanced(this._dt, this._region_mngr, animals, _dt);
	}

	public JSONObject as_JSON() {
		JSONObject simulatorObject = new JSONObject();
		simulatorObject.put("time", _dt);
		simulatorObject.put("state", this._region_mngr.as_JSON());
		return simulatorObject;

	}

	public void reset(int cols, int rows, int width, int height) {
		_animals.clear();
		this._region_mngr = new RegionManager(cols, rows, width, height);
		this._dt = 0.0;
		
		List<AnimalInfo> animals = new ArrayList<>(_animals);
		for(EcoSysObserver o : this._observers)
			o.onReset(this._dt, this._region_mngr, animals);
	}

	@Override
	public void addObserver(EcoSysObserver o) {
		this._observers.add(o);	
		List<AnimalInfo> animals = new ArrayList<>(_animals);
		o.onRegister(this._dt, this._region_mngr, animals);
	}

	@Override
	public void removeObserver(EcoSysObserver o) {
		this._observers.remove(o);
	}
}
