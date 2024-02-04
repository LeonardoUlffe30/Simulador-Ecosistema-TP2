package simulator.model;

public interface RegionInfo extends JSONable {

	double get_food(Animal a, double dt);
	// for now it is empty, later we will make it implements the interface
	// Iterable<AnimalInfo>
}
