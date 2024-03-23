package simulator.model;

import java.util.List;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal {
	private final static double WOLF_INITIAL_SIGHT_RANGE = 50.0;
	private final static double WOLF_INITIAL_SPEED = 60.0;

	private final static double DISTANCE_COMPARISON_DEST = 8.0;
	private final static double MOVE_FIRST_FACTOR = 3.0;
	private final static double MOVE_SECOND_FACTOR = 100.0;
	private final static double MOVE_THIRD_FACTOR = 0.007;
	private final static double REMOVE_ENERGY_FIRST_FACTOR = 18.0;
	private final static double REMOVE_ENERGY_SECOND_FACTOR = 1.2;
	private final static double ADD_DESIRE = 30.0;
	private final static double ADD_ENERGY = 50.0;
	private final static double REMOVE_ENERGY = 10.0;
	private final static double MIN_RANGE = 0.0;
	private final static double MAX_RANGE = 100.0;
	private final static double COMPARISON_ENERGY = 50.0;
	private final static double COMPARISON_DESIRE = 65.0;
	private final static double DISTANCE_COMPARISON_HUNT = 8.0;
	private final static double DISTANCE_COMPARISON_MATE = 8.0;
	private final static double COMPARISON_AGE = 14.0;
	private final static double PROBABILITY_BABY = 0.1;

	private Animal _hunt_target;
	private SelectionStrategy _hunting_strategy;

	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy, Vector2D pos) {
		super("Wolf", Diet.CARNIVORE, WOLF_INITIAL_SIGHT_RANGE, WOLF_INITIAL_SPEED, mate_strategy, pos);
		this._hunting_strategy = hunting_strategy;
	}

	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		this._hunting_strategy = p1._hunting_strategy;
		this._hunt_target = null;
	}

	@Override
	public void update(double dt) {
		if (this._state != State.DEAD) {
			this.update_state(dt);
			this.adjust();
			if (this._energy == 0.0 || this._age > COMPARISON_AGE)
				this._state = State.DEAD;

			if (this._state != State.DEAD) {
				this._energy += this._region_mngr.get_food(this, dt);
				if (this._energy > MAX_RANGE) this._energy = MAX_RANGE;
			}
		}
	}

	private void update_state(double dt) {
		switch (this._state) {
		case NORMAL:
			this.case_normal(dt);
			break;
		case HUNGER:
			this.case_hunger(dt);
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
			this.search_mate_target();
			if (this._mate_target == null)
				this.move_as_normal(dt, DISTANCE_COMPARISON_DEST, MOVE_SECOND_FACTOR, MOVE_THIRD_FACTOR,
						REMOVE_ENERGY_FIRST_FACTOR, ADD_DESIRE, MIN_RANGE, MAX_RANGE);
		} else this.chase_mate_target_create_baby(dt);

		if (this._energy < COMPARISON_ENERGY) this.change_to_hunger();
		else { if (this._desire < COMPARISON_DESIRE) this.change_to_normal(); }
	}
	
	private void case_hunger(double dt) {
		if (this._hunt_target == null || (this._hunt_target != null)
				&& (this._hunt_target._state == State.DEAD || this._pos
						.distanceTo(this._hunt_target._pos) > this._sight_range)) {
			this.search_hunt_target();
		}

		if (this._hunt_target == null)
			this.move_as_normal(dt, DISTANCE_COMPARISON_DEST, MOVE_SECOND_FACTOR, MOVE_THIRD_FACTOR,
					REMOVE_ENERGY_FIRST_FACTOR, ADD_DESIRE, MIN_RANGE, MAX_RANGE);
		else this.move_to_hunt(dt);

		if (this._energy > COMPARISON_ENERGY) {
			if (this._desire < COMPARISON_DESIRE) this.change_to_normal();
			else this.change_to_mate();
		}
	}
	
	private void case_normal(double dt) {
		this.move_as_normal(dt, DISTANCE_COMPARISON_DEST, MOVE_SECOND_FACTOR, MOVE_THIRD_FACTOR,
				REMOVE_ENERGY_FIRST_FACTOR, ADD_DESIRE, MIN_RANGE, MAX_RANGE);

		if (this._energy < COMPARISON_ENERGY) this.change_to_hunger();
		else { if (this._desire > COMPARISON_DESIRE) this.change_to_mate(); }
	}

	private void move_to_hunt(double dt) {
		this._dest = this._hunt_target._pos;
		this.move(MOVE_FIRST_FACTOR * this._speed * dt
				* Math.exp((this._energy - MOVE_SECOND_FACTOR) * MOVE_THIRD_FACTOR));
		this._age += dt;

		this._energy -= REMOVE_ENERGY_FIRST_FACTOR * REMOVE_ENERGY_SECOND_FACTOR * dt;
		if (this._energy < MIN_RANGE) this._energy = MIN_RANGE;

		this._desire += (ADD_DESIRE * dt);
		if (this._desire > MAX_RANGE) this._desire = MAX_RANGE;

		if (this._pos.distanceTo(this._hunt_target._pos) < DISTANCE_COMPARISON_HUNT) {
			this._hunt_target._state = State.DEAD;
			this._hunt_target = null;

			this._energy += ADD_ENERGY;
			if (this._energy > MAX_RANGE) this._energy = MAX_RANGE;
		}
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
					this._baby = new Wolf(this, this._mate_target);
			}

			this._energy -= REMOVE_ENERGY;
			if (this._energy < MIN_RANGE) this._energy = MIN_RANGE;

			this._mate_target = null;
		}
	}

	private void search_mate_target() {
		List<Animal> animals_filtered = this._region_mngr.get_animals_in_range(this,
				(Animal a) -> a._genetic_code.equalsIgnoreCase("wolf"));
		this._mate_target = this._mate_strategy.select(this, animals_filtered);
	}

	private void search_hunt_target() {
		List<Animal> animals_filtered = this._region_mngr.get_animals_in_range(this,
				(Animal a) -> a._diet == Diet.HERBIVORE);
		this._hunt_target = this._hunting_strategy.select(this, animals_filtered);
	}

	@Override
	protected void change_to_normal() {
		this._state = State.NORMAL;
		this._hunt_target = null;
		this._mate_target = null;
	}

	@Override
	protected void change_to_mate() {
		this._state = State.MATE;
		this._hunt_target = null;
	}

	public void change_to_hunger() {
		this._state = State.HUNGER;
		this._mate_target = null;
	}
}
