package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Region implements Entity, FoodSupplier, RegionInfo {
	protected final static double FOOD_FIRST_FACTOR = 60.0;
	protected final static double FOOD_SECOND_FACTOR = 5.0;
	protected final static double FOOD_THIRD_FACTOR = 2.0;

	protected List<Animal> _animals;

	protected Region() {
		this._animals = new ArrayList<Animal>();
	}

	@Override
	public double get_food(Animal a, double dt) {
		return 0;
	}

	@Override
	public void update(double dt) {

	}

	final void add_animal(Animal a) {
		this._animals.add(a);
	}

	final void remove_animal(Animal a) {
		this._animals.remove(a);
	}

//	devuelve una versión inmodificable de la lista de animales.
	final List<Animal> getAnimals() {
		return Collections.unmodifiableList(this._animals);
	}

//	devuelve una estructura JSON como la siguiente donde "Ai" es lo que
//	devuelve as_JSON() del animal correspondiente:
//	{
//		"animals":[A1,A2,...],
//	}
	public JSONObject as_JSON() {
		JSONArray ja = new JSONArray();
		for (Animal a : this.getAnimals()) {
			ja.put(a.as_JSON());
		}
		JSONObject jo = new JSONObject();
		jo.put("animals", ja);
		return jo;

	}

	@Override
	public List<AnimalInfo> getAnimalsInfo() {
		//Collections.unmodifiableList(_animals);
		return new ArrayList<>(_animals); // se puede usar Collections.unmodifiableList(_animals);
	}

}
