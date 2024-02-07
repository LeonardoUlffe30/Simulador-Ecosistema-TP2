package simulator.model;

import simulator.misc.Vector2D;

public class Sheep extends Animal {
	private SelectionStrategy _danger_strategy;
	private Animal _danger_source;
	
	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super("Sheep", Diet.HERBIVORE, 40.0, 35.0, mate_strategy, pos);
		this._danger_strategy = danger_strategy;
	}
	
	//Constructora cuando nazca un animal de tipo Sheep
	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		this._danger_strategy = p1.get_danger_strategy();
		this._danger_source = null;
	}

	public SelectionStrategy get_danger_strategy() {
		return _danger_strategy;
	}

	public void set_danger_strategy(SelectionStrategy _danger_strategy) {
		this._danger_strategy = _danger_strategy;
	}

	public Animal get_danger_source() {
		return _danger_source;
	}

	public void set_danger_source(Animal _danger_source) {
		this._danger_source = _danger_source;
	}

}
