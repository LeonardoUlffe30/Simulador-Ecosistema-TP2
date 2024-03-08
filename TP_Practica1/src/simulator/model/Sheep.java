package simulator.model;

import java.util.List;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal {
	private final static double SHEEP_INITIAL_SIGHT_RANGE = 40.0;
	private final static double SHEEP_INITIAL_SPEED = 35.0;

	private final static double DISTANCE_COMPARISON_DEST = 8.0;
	private final static double MOVE_FIRST_FACTOR = 2.0;
	private final static double MOVE_SECOND_FACTOR = 100.0;
	private final static double MOVE_THIRD_FACTOR = 0.007;
	private final static double REMOVE_ENERGY_FIRST_FACTOR = 20.0;
	private final static double REMOVE_ENERGY_SECOND_FACTOR = 1.2;
	private final static double ADD_DESIRE = 40.0;
	private final static double MIN_RANGE = 0.0;
	private final static double MAX_RANGE = 100.0;
	private final static double COMPARISON_DESIRE = 65.0;
	private final static double DISTANCE_COMPARISON_MATE = 8.0;
	private final static double COMPARISON_AGE = 8.0;
	private final static double PROBABILITY_BABY = 0.1;

	private SelectionStrategy _danger_strategy;
	private Animal _danger_source;

	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super("Sheep", Diet.HERBIVORE, SHEEP_INITIAL_SIGHT_RANGE, SHEEP_INITIAL_SPEED, mate_strategy, pos);
		this._danger_strategy = danger_strategy;
	}

	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		this._danger_strategy = p1.get_danger_strategy();
		this._danger_source = null;
	}

	@Override
	public void update(double dt) {
		if (this.get_state() != State.DEAD) {
			this.update_state(dt);// Actualizar segun estado
			this.adjust();// Ajustar posicion en caso posicion este fuera del mapa
			if (this.get_energy() == 0.0 || (this.get_age() > COMPARISON_AGE))
				this.set_state(State.DEAD); // Comprobamos que no este muerto de nuevo
			
			if (this.get_state() != State.DEAD) {
				this.set_energy(this.get_energy() + this.get_region_mngr().get_food(this, dt));
				if (this.get_energy() > MAX_RANGE) this.set_energy(MAX_RANGE);
			}
		}

	}

	private void update_state(double dt) {
		switch (this.get_state()) {
		case NORMAL:
			this.move_as_normal(dt, DISTANCE_COMPARISON_DEST, MOVE_SECOND_FACTOR, MOVE_THIRD_FACTOR,
					REMOVE_ENERGY_FIRST_FACTOR, ADD_DESIRE, MIN_RANGE, MAX_RANGE);

			if (this.get_danger_source() == null)
				this.search_dangerous_animal();

			if (this.get_danger_source() != null) {
				this.change_to_danger();
			} else {
				if (this.get_desire() > COMPARISON_DESIRE) {
					this.change_to_mate();
				}
			}
			break;
		case DANGER:
			if (this.get_danger_source() != null && this.get_danger_source().get_state() == State.DEAD) {
				this.set_danger_source(null);
			}
			if (this.get_danger_source() == null) {
				this.move_as_normal(dt, DISTANCE_COMPARISON_DEST, MOVE_SECOND_FACTOR, MOVE_THIRD_FACTOR,
						REMOVE_ENERGY_FIRST_FACTOR, ADD_DESIRE, MIN_RANGE, MAX_RANGE);
			} else {
				this.move_against_danger(dt);
			}
			if (this.get_danger_source() == null || this.get_position()
					.distanceTo(this.get_danger_source().get_position()) > this.get_sight_range()) {
				this.search_dangerous_animal();
				if (this.get_danger_source() == null) {
					if (this.get_desire() < COMPARISON_DESIRE) {
						this.change_to_normal();
					}
					else {
						this.change_to_mate();
					}
				}
			}
			break;
		case MATE:
			//CASO 1 
			if (this.get_mate_target() != null && (this.get_mate_target().get_state() == State.DEAD || this
					.get_position().distanceTo(this.get_mate_target().get_position()) > this.get_sight_range())) {
				this.set_mate_target(null);
			}
			//CASO 2
			if (this.get_mate_target() == null) {
				this.search_mate_animal();
				if (this.get_mate_target() == null)
					this.move_as_normal(dt, DISTANCE_COMPARISON_DEST, MOVE_SECOND_FACTOR, MOVE_THIRD_FACTOR,
							REMOVE_ENERGY_FIRST_FACTOR, ADD_DESIRE, MIN_RANGE, MAX_RANGE);
			}
			else {
				this.chase_mate_target_create_baby(dt);
			}
			if (this.get_danger_source() == null)
				this.search_dangerous_animal();

			if (this.get_danger_source() != null) {
				this.change_to_danger();
			}
			else {
				if (this.get_desire() < COMPARISON_DESIRE) {
					this.change_to_normal();
				}
			}
			break;
		case HUNGER:
//			Un objeto de tipo Sheep nunca puede estar en estado HUNGER.
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + this.get_state());
		}
	}

	public void move_against_danger(double dt) {
		this.set_destination(this.get_position()
				.plus(this.get_position().minus(this.get_danger_source().get_position()).direction()));
		this.move(MOVE_FIRST_FACTOR * this.get_speed() * dt
				* Math.exp((this.get_energy() - MOVE_SECOND_FACTOR) * MOVE_THIRD_FACTOR));
		this.set_age(this.get_age() + dt);
		// Quitar 20.0*1.2*dt a la energía (manteniéndola siempre entre 0.0 y 100.0).
		this.set_energy(this.get_energy() - REMOVE_ENERGY_FIRST_FACTOR * REMOVE_ENERGY_SECOND_FACTOR * dt);
		if (this.get_energy() < MIN_RANGE) this.set_energy(MIN_RANGE);
		// Añadir 40.0*dt al deseo (manteniéndolo siempre entre 0.0 y 100.0).
		this.set_desire(this.get_desire() + ADD_DESIRE * dt);
		if (this.get_desire() > MAX_RANGE) this.set_desire(MAX_RANGE);
	}

	public void chase_mate_target_create_baby(double dt) {
		this.set_destination(this.get_mate_target().get_position());
		this.move(MOVE_FIRST_FACTOR * this.get_speed() * dt
				* Math.exp((this.get_energy() - MOVE_SECOND_FACTOR) * MOVE_THIRD_FACTOR));
		this.set_age(this.get_age() + dt);
		// Quitar 20.0*1.2*dt a la energía (manteniéndola siempre entre 0.0 y 100.0).
		this.set_energy(this.get_energy() - REMOVE_ENERGY_FIRST_FACTOR * REMOVE_ENERGY_SECOND_FACTOR * dt);
		if (this.get_energy() < MIN_RANGE) this.set_energy(MIN_RANGE);

		// Añadir 40.0*dt al deseo (manteniéndolo siempre entre 0.0 y 100.0).
		this.set_desire(this.get_desire() + ADD_DESIRE * dt);
		if (this.get_desire() > MAX_RANGE) this.set_desire(MAX_RANGE);

		if (this.get_position().distanceTo(this.get_mate_target().get_position()) < DISTANCE_COMPARISON_MATE) {
			this.set_desire(0.0);
			this.get_mate_target().set_desire(0.0);

			if (!this.is_pregnant()) {
				double x = Utils._rand.nextDouble(0, 1);
				if (x > PROBABILITY_BABY)
					this.set_baby(new Sheep(this, this.get_mate_target()));
			}
			this.set_mate_target(null);
		}
	}

	public void search_mate_animal() {
		List<Animal> animals_filtered = this.get_region_mngr().get_animals_in_range(this,
				(Animal a) -> a.get_genetic_code().equalsIgnoreCase("sheep"));
		SelectionStrategy aux = this.get_mate_strategy();
		this.set_mate_target(aux.select(this, animals_filtered));
	}

	public void search_dangerous_animal() {
		List<Animal> animals_filtered = this.get_region_mngr().get_animals_in_range(this,
				(Animal a) -> a.get_diet() == Diet.CARNIVORE);
		SelectionStrategy aux = this.get_danger_strategy();
		this.set_danger_source(aux.select(this, animals_filtered));
	}

	public SelectionStrategy get_danger_strategy() {
		return _danger_strategy;
	}

	public Animal get_danger_source() {
		return _danger_source;
	}

	public void set_danger_strategy(SelectionStrategy _danger_strategy) {
		this._danger_strategy = _danger_strategy;
	}

	public void set_danger_source(Animal _danger_source) {
		this._danger_source = _danger_source;
	}

	@Override
	protected void change_to_normal() {
		this.set_state(State.NORMAL);
		this.set_danger_source(null);
		this.set_mate_target(null);
	}
	
	@Override
	protected void change_to_mate() {
		this.set_state(State.MATE);
		this.set_danger_source(null);
	}
	

	public void change_to_danger() {
		this.set_state(State.DANGER);
		this.set_mate_target(null);
	}
}
