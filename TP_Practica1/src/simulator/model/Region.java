package simulator.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Region implements Entity, FoodSupplier, RegionInfo {
	protected List<Animal> animals_in_list;
	
	@Override
	public double get_food(Animal a, double dt) {
		return 0;
	}

	@Override
	public void update(double dt) {

	}
	
	final void add_animal(Animal a) {
		this.animals_in_list.add(a);
	} 
	
	final void remove_animal(Animal a) {
		this.animals_in_list.remove(a);
	}
//	devuelve una versión inmodificable de la lista de animales.
	final List<Animal> getAnimals() {
		return this.animals_in_list;
	}
//	devuelve una estructura JSON como la siguiente donde "Ai" es lo que
//	devuelve as_JSON() del animal correspondiente:
//	{
//		"animals":[A1,A2,...],
//	}
	public JSONObject as_JSON() {
		return null;
		
	}

}
