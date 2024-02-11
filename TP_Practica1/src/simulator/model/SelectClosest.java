package simulator.model;

import java.util.Iterator;
import java.util.List;

public class SelectClosest implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if(as.isEmpty()) return null;
		else {
			Animal closest = as.get(0);
			double distance = a.get_position().distanceTo(closest.get_position()) ;
			Iterator<Animal> it = as.iterator();
			while(it.hasNext()) {
				Animal aux = it.next();
				if(a.get_position().distanceTo(aux.get_position()) < distance) {
					distance = a.get_position().distanceTo(aux.get_position());
					closest = aux;					
				}
			}
			return closest;
		}
	}
}
