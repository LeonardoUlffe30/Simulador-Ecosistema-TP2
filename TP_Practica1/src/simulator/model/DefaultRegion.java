package simulator.model;

import java.util.List;

public class DefaultRegion extends Region{
	public double get_food(Animal a, double dt) {
		if(a.get_diet()==Diet.CARNIVORE)
			return 0.0;
		else { // n es el número de animales herbívoros en la región:
			List<Animal> animals_filtered = a.get_region_mngr().get_animals_in_range(a,(Animal b)->b.get_diet() == Diet.HERBIVORE);
			int n = animals_filtered.size();
			return 60.0*Math.exp(-Math.max(0,n-5.0)*2.0)*dt;
		}
	}

}
