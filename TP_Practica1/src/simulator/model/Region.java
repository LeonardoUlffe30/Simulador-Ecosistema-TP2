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
	
	protected List<Animal> animals_in_list;
	
	protected Region(){
		this.animals_in_list = new ArrayList<Animal>();	
	}
	
	@Override
	public double get_food(Animal a, double dt) {
		return 0;
	}

	@Override
	public void update(double dt) {

	}
	
	final void add_animal(Animal a) {
		this.animals_in_list.add(a);
		System.out.println("Entra en add animal");
	} 
	
	final void remove_animal(Animal a) {
		this.animals_in_list.remove(a);
	}
//	devuelve una versión inmodificable de la lista de animales.
	final List<Animal> getAnimals() {
		return Collections.unmodifiableList(this.animals_in_list);
	}
//	devuelve una estructura JSON como la siguiente donde "Ai" es lo que
//	devuelve as_JSON() del animal correspondiente:
//	{
//		"animals":[A1,A2,...],
//	}
	public JSONObject as_JSON() {
		JSONArray ja = new JSONArray();
	    for(Animal a: this.getAnimals()) {
	    	ja.put(a.as_JSON());
	    }
		JSONObject jo = new JSONObject();
		jo.put("animals", ja);
		return jo;
		
	}

}
