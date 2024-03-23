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
		this._danger_strategy = p1._danger_strategy;
		this._danger_source = null;
	}

	@Override
	public void update(double dt) {
		if (this._state != State.DEAD) {
			this.update_state(dt);// Actualizar segun estado
			this.adjust();// Ajustar posicion en caso posicion este fuera del mapa
			if (this._energy == 0.0 || (this._age > COMPARISON_AGE))
				this._state = State.DEAD; // Comprobamos que no este muerto de nuevo

			if (this._state != State.DEAD) {
				this._energy += this._region_mngr.get_food(this, dt);
				if (this._energy > MAX_RANGE)
					this._energy = MAX_RANGE;
			}
		}

	}

	private void update_state(double dt) {
		switch (this._state) {
		case NORMAL:
			this.case_normal(dt);
			break;
		case DANGER:
			this.case_danger(dt);
			break;
		case MATE:
			this.case_mate(dt);
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + this._state);
		}
	}
	
	private void case_mate(double dt) {
		if (this._mate_target != null && (this._mate_target._state == State.DEAD || this
				._pos.distanceTo(this._mate_target._pos) > this._sight_range)) {
			this._mate_target = null;
		}

		if (this._mate_target == null) {
			this.search_mate_animal();
			if (this._mate_target == null)
				this.move_as_normal(dt, DISTANCE_COMPARISON_DEST, MOVE_SECOND_FACTOR, MOVE_THIRD_FACTOR,
						REMOVE_ENERGY_FIRST_FACTOR, ADD_DESIRE, MIN_RANGE, MAX_RANGE);
		} else this.chase_mate_target_create_baby(dt);

		if (this._danger_source == null) this.search_dangerous_animal();

		if (this._danger_source != null) this.change_to_danger();
		else { if (this._desire < COMPARISON_DESIRE) this.change_to_normal(); }
		
	}
	
	private void case_normal(double dt) {
		this.move_as_normal(dt, DISTANCE_COMPARISON_DEST, MOVE_SECOND_FACTOR, MOVE_THIRD_FACTOR,
				REMOVE_ENERGY_FIRST_FACTOR, ADD_DESIRE, MIN_RANGE, MAX_RANGE);

		if (this._danger_source == null) this.search_dangerous_animal();

		if (this._danger_source != null) this.change_to_danger();
		else { if (this._desire > COMPARISON_DESIRE) this.change_to_mate();	}
	}
	
	private void case_danger(double dt) {
		if (this._danger_source != null && this._danger_source._state == State.DEAD)
			this._danger_source = null;

		if (this._danger_source == null)
			this.move_as_normal(dt, DISTANCE_COMPARISON_DEST, MOVE_SECOND_FACTOR, MOVE_THIRD_FACTOR,
					REMOVE_ENERGY_FIRST_FACTOR, ADD_DESIRE, MIN_RANGE, MAX_RANGE);
		else this.move_against_danger(dt);
		
		if (this._danger_source == null || this._pos
				.distanceTo(this._danger_source._pos) > this._sight_range) {
			this.search_dangerous_animal();
			if (this._danger_source == null) {
				if (this._desire < COMPARISON_DESIRE) this.change_to_normal();
				else this.change_to_mate();
			}
		}
	}

	private void move_against_danger(double dt) {
		this._dest = this._pos.plus(this._pos.minus(this._danger_source._pos).direction());
		this.move(MOVE_FIRST_FACTOR * this._speed * dt
				* Math.exp((this._energy - MOVE_SECOND_FACTOR) * MOVE_THIRD_FACTOR));
		this._age += dt;
		
		this._energy -= REMOVE_ENERGY_FIRST_FACTOR * REMOVE_ENERGY_SECOND_FACTOR * dt;
		if (this._energy < MIN_RANGE) this._energy = MIN_RANGE;
		
		this._desire += ADD_DESIRE * dt;
		if (this._desire > MAX_RANGE) this._desire = MAX_RANGE;
	}

	private void chase_mate_target_create_baby(double dt) {
		this._dest = this._mate_target._pos;
		this.move(MOVE_FIRST_FACTOR * this._speed * dt
				* Math.exp((this._energy - MOVE_SECOND_FACTOR) * MOVE_THIRD_FACTOR));
		this._age += dt;
		
		this._energy -= REMOVE_ENERGY_FIRST_FACTOR * REMOVE_ENERGY_SECOND_FACTOR * dt;
		if (this._energy < MIN_RANGE) this._energy = MIN_RANGE;

		this._desire += ADD_DESIRE * dt;
		if (this._desire > MAX_RANGE) this._desire = MAX_RANGE;

		if (this._pos.distanceTo(this._mate_target._pos) < DISTANCE_COMPARISON_MATE) {
			this._desire = 0.0;
			this._mate_target._desire = 0.0;

			if (!this.is_pregnant()) {
				if (Utils._rand.nextDouble(0, 1) > PROBABILITY_BABY)
					this._baby = new Sheep(this, this._mate_target);
			}
			this._mate_target = null;
		}
	}

	private void search_mate_animal() {
		List<Animal> animals_filtered = this._region_mngr.get_animals_in_range(this,
				(Animal a) -> a._genetic_code.equalsIgnoreCase("sheep"));
		this._mate_target = this._mate_strategy.select(this, animals_filtered);
	}

	private void search_dangerous_animal() {
		List<Animal> animals_filtered = this._region_mngr.get_animals_in_range(this,
				(Animal a) -> a._diet == Diet.CARNIVORE);
		this._danger_source = this._danger_strategy.select(this, animals_filtered);
	}

	@Override
	protected void change_to_normal() {
		this._state = State.NORMAL;
		this._danger_source = null;
		this._mate_target = null;
	}

	@Override
	protected void change_to_mate() {
		this._state = State.MATE;
		this._danger_source = null;
	}

	public void change_to_danger() {
		this._state = State.DANGER;
		this._mate_target = null;
	}
}
