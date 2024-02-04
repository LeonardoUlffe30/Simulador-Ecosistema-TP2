package simulator.model;

public interface SelectionStrategy {
	Animal select(Animal a, List<Animal> as);
	/*
	 * SelectFirst: devuelvo el primer animal de la lista �as�. SelectClosest:
	 * devuelve el animal m�s cercano al animal �a� de la lista �as�.
	 * SelectYoungest: devuelve el animal m�s joven de la lista �as�.
	 */
}