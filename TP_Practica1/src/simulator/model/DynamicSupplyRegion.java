package simulator.model;

public class DynamicSupplyRegion {

//	La clase DynamicSupplyRegion representa una regi�n que da comida s�lo a animales herb�voros, pero la
//	cantidad de comida puede decrecer/crecer. Su constructora recibe la cantidad inicial de la comida (n�mero
//	positivo de tipo double) y un factor de crecimiento (n�mero no negativo de tipo double).
//	Su m�todo get_food(a,dt) devuelve 0.0 si el animal que pide comida es carn�voro, y lo siguiente si es
//	herb�voro donde n es el n�mero de animales herb�voros en la regi�n y _food es la cantidad actual de
//	comida:
//	Math.min(_food,60.0*Math.exp(-Math.max(0,n-5.0)*2.0)*dt)
//	Adem�s quita el valor devuelto a la cantidad de comida _food que tiene la regi�n actualmente. Su m�todo
//	update incrementa, con probabilidad 0.5, la cantidad de comida por dt*_factor donde _factor es el
//	factor de crecimiento.
	
	private double _food_quantity;
	private double _growth_factor;
	
	public DynamicSupplyRegion(double food_quantity, double growth_factor) {
		this._food_quantity = food_quantity;
		this._growth_factor = growth_factor;
	}
	
	public double get_food(Animal a, double dt) {
		if(a.get_diet()==Diet.CARNIVORE)
			return 0.0;
		else {
			return 0; //En realidad debe retornar lo que dice en el enunciado
		}
	}

}
