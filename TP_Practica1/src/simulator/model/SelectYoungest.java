package simulator.model;

import java.util.Iterator;
import java.util.List;

public class SelectYoungest implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
		if (as.isEmpty())
			return null;
		else {
			Animal youngest = as.get(0);
			Iterator<Animal> it = as.iterator();
			while (it.hasNext()) {
				Animal aux = it.next();
				if (aux.get_age() < youngest.get_age())
					youngest = aux;
			}
			return youngest;
		}
	}
}
