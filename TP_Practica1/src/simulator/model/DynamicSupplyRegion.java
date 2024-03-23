package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region {
	private double _food_quantity;
	private double _growth_factor;

	public DynamicSupplyRegion(double food_quantity, double growth_factor) {
		if (food_quantity < 0.0)
			throw new IllegalArgumentException("Food quantity debe ser un numero positivo");
		if (growth_factor < 0.0)
			throw new IllegalArgumentException("Growth factor debe ser un numero positivo");

		this._food_quantity = food_quantity;
		this._growth_factor = growth_factor;
	}

	public void update(double dt) {
		double x = Utils._rand.nextDouble(0, 1);
		if (x <= 0.5)
			this._food_quantity += dt * this._growth_factor;
	}

	public double get_food(Animal a, double dt) {
		if (a.get_diet() == Diet.CARNIVORE)
			return 0.0;
		else {
			int n = 0;
			for (Animal i : this.getAnimals()) {
				if (i.get_diet() == Diet.HERBIVORE)
					++n;
			}
			double food = Math.min(this._food_quantity,
					FOOD_FIRST_FACTOR * Math.exp(-Math.max(0, n - FOOD_SECOND_FACTOR) * FOOD_THIRD_FACTOR) * dt);
			this._food_quantity -= food;
			return food;
		}
	}

	
	public String toString() {
		return "Dynamic Region";
		
	}

}
