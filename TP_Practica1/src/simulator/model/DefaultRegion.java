package simulator.model;

public class DefaultRegion {
	public double get_food(Animal a, double dt) {
		if(a.get_diet()==Diet.CARNIVORE)
			return 0.0;
		else // n es el número de animales herbívoros en la región:
			return 0; //En lugar de 0, debe retornar 60.0*Math.exp(-Math.max(0,n-5.0)*2.0)*dt;

	}

}
