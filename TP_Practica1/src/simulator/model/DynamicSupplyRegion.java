package simulator.model;

import java.util.List;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region{

//	La clase DynamicSupplyRegion representa una región que da comida sólo a animales herbívoros, pero la
//	cantidad de comida puede decrecer/crecer. Su constructora recibe la cantidad inicial de la comida (número
//	positivo de tipo double) y un factor de crecimiento (número no negativo de tipo double).
//	Su método get_food(a,dt) devuelve 0.0 si el animal que pide comida es carnívoro, y lo siguiente si es
//	herbívoro donde n es el número de animales herbívoros en la región y _food es la cantidad actual de
//	comida:
//	Math.min(_food,60.0*Math.exp(-Math.max(0,n-5.0)*2.0)*dt)
//	Además quita el valor devuelto a la cantidad de comida _food que tiene la región actualmente. Su método
//	update incrementa, con probabilidad 0.5, la cantidad de comida por dt*_factor donde _factor es el
//	factor de crecimiento.
	
	private double _food_quantity;
	private double _growth_factor;
	
	public DynamicSupplyRegion(double food_quantity, double growth_factor) {
		this._food_quantity = food_quantity;
		this._growth_factor = growth_factor;
	}
	
	public void update(double dt) {
		double x = Utils._rand.nextDouble(0, 1);
		if (x >=0.5) this.set_food_quantity(this.get_food_quantity()+(dt*this.get_growth_factor()));
	}
	
	public double get_food(Animal a, double dt) {
//		Su método get_food(a,dt) devuelve 0.0 si el animal que pide comida es carnívoro, y lo siguiente si es
//		herbívoro donde n es el número de animales herbívoros en la región y _food es la cantidad actual de
//		comida:
		if(a.get_diet()==Diet.CARNIVORE)
			return 0.0;
		else {
			List<Animal> animals_filtered = a.get_region_mngr().get_animals_in_range(a,(Animal b)->b.get_diet() == Diet.HERBIVORE);
			int n = animals_filtered.size();
			double food =  Math.min(this.get_food_quantity(),60.0*Math.exp(-Math.max(0,n-5.0)*2.0)*dt); 
			this.set_food_quantity(get_food_quantity()-food);
			//update(dt);
			return food;
		}
	}

	public double get_food_quantity() {
		return _food_quantity;
	}

	public double get_growth_factor() {
		return _growth_factor;
	}

	public void set_food_quantity(double _food_quantity) {
		this._food_quantity = _food_quantity;
	}

	public void set_growth_factor(double _growth_factor) {
		this._growth_factor = _growth_factor;
	}

}
