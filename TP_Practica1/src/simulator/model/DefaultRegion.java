package simulator.model;

public class DefaultRegion extends Region {

	public double get_food(Animal a, double dt) {
		if (a.get_diet() == Diet.CARNIVORE)
			return 0.0;
		else {
			int n = 0;
			for (Animal i : this.getAnimals()) {
				if (i.get_diet() == Diet.HERBIVORE)
					++n;
			}
			return FOOD_FIRST_FACTOR * Math.exp(-Math.max(0, n - FOOD_SECOND_FACTOR) * FOOD_THIRD_FACTOR) * dt;
		}
	}

}
