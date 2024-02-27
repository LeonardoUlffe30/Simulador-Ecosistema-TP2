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
	private final static double PROBABILITY_BABY = 0.9;
	
	private Animal _hunt_target;
	private SelectionStrategy _hunting_strategy;
	
	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy, Vector2D pos) {
		super("Wolf", Diet.CARNIVORE,WOLF_INITIAL_SIGHT_RANGE , WOLF_INITIAL_SPEED, mate_strategy, pos);
	}
	
	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		this._hunting_strategy = p1.get_hunting_strategy();
		this._hunt_target = null;
	}
	
	@Override
	public void update(double dt) {
		if (this.get_state() != State.DEAD) {
			this.update_state(dt);
			this.adjust();
			if (this.get_energy() <= 0.0 || (this.get_age() > COMPARISON_AGE))
				this.set_state(State.DEAD);
			
			if (this.get_state() != State.DEAD) {
				if(this.get_energy() + this.get_region_mngr().get_food(this, dt) <= MAX_RANGE)
					this.set_energy(this.get_energy() + this.get_region_mngr().get_food(this, dt));
			}
		}
	}	
	private void update_state(double dt) {
		switch (this.get_state()) {
		case NORMAL:
			this.move_as_normal(dt, DISTANCE_COMPARISON_DEST, MOVE_SECOND_FACTOR, MOVE_THIRD_FACTOR, 
					REMOVE_ENERGY_FIRST_FACTOR, ADD_DESIRE, MIN_RANGE, MAX_RANGE);
			
			if(this.get_energy() < COMPARISON_ENERGY)
				this.set_state(State.HUNGER);
			else {
				if(this.get_desire() > COMPARISON_DESIRE)
					this.set_state(State.MATE);
			}
			break;
		case HUNGER:
			if(this.get_hunt_target() == null || (this.get_hunt_target() != null && (this.get_hunt_target().get_state() == State.DEAD || 
			this.get_position().distanceTo(this.get_hunt_target().get_position()) > this.get_sight_range()))) {
				this.search_hunt_target();
			}
			
			if(this.get_hunt_target() == null)
				this.move_as_normal(dt, DISTANCE_COMPARISON_DEST, MOVE_SECOND_FACTOR, MOVE_THIRD_FACTOR, 
						REMOVE_ENERGY_FIRST_FACTOR, ADD_DESIRE, MIN_RANGE, MAX_RANGE);
			else {
				this.move_to_hunt(dt);
			}
			
			if(this.get_energy() > COMPARISON_ENERGY) {
				if(this.get_desire() < COMPARISON_DESIRE)
					this.set_state(State.NORMAL);
				else
					this.set_state(State.MATE);
			}
			break;
		case MATE:
			if(this.get_mate_target() != null && (this.get_mate_target().get_state() == State.DEAD || 
			this.get_position().distanceTo(this.get_mate_target().get_position()) > this.get_sight_range())) {
				this.set_mate_target(null);
			}
			
			if(this.get_mate_target() == null) {
				this.search_mate_target();
				if(this.get_mate_target() == null)
					this.move_as_normal(dt, DISTANCE_COMPARISON_DEST, MOVE_SECOND_FACTOR, MOVE_THIRD_FACTOR, 
							REMOVE_ENERGY_FIRST_FACTOR, ADD_DESIRE, MIN_RANGE, MAX_RANGE);
				else {
					this.chase_mate_target_create_baby(dt);
				}
			}
			
			if(this.get_energy() < COMPARISON_ENERGY)
				this.set_state(State.HUNGER);
			else {
				if(this.get_desire() < COMPARISON_DESIRE)
					this.set_state(State.NORMAL);
			}
			break;
		case DANGER:
//			Un objeto de tipo Wolf nunca puede estar en estado DANGER.
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + this.get_state());
		}
	}
	
	
	
	public void move_to_hunt(double dt) {
		this.set_destination(this.get_hunt_target().get_position());
		this.move(MOVE_FIRST_FACTOR *this.get_speed()*dt*Math.exp((this.get_energy()-MOVE_SECOND_FACTOR)*MOVE_THIRD_FACTOR));
		this.set_age(this.get_age()+dt);
		if (this.get_energy() - (REMOVE_ENERGY_FIRST_FACTOR*REMOVE_ENERGY_SECOND_FACTOR*dt) >= MIN_RANGE)
			this.set_energy(this.get_energy() - (REMOVE_ENERGY_FIRST_FACTOR*REMOVE_ENERGY_SECOND_FACTOR*dt));
		if (this.get_desire() + (ADD_DESIRE*dt) <= MAX_RANGE)
			this.set_desire(this.get_desire() + (ADD_DESIRE*dt));
		
		if(this.get_position().distanceTo(this.get_hunt_target().get_position()) < DISTANCE_COMPARISON_HUNT) {
			this.get_hunt_target().set_state(State.DEAD);
			this.set_hunt_target(null);
			if(this.get_energy() + ADD_ENERGY <= MAX_RANGE)
				this.set_energy(this.get_energy()+ADD_ENERGY);
		}
	}
	
	public void chase_mate_target_create_baby(double dt) {
		this.set_destination(this.get_mate_target().get_position());
		this.move(MOVE_FIRST_FACTOR*this.get_speed()*dt*Math.exp((this.get_energy()-MOVE_SECOND_FACTOR)*MOVE_THIRD_FACTOR));
		this.set_age(this.get_age()+dt);
		if (this.get_energy() - (REMOVE_ENERGY_FIRST_FACTOR*REMOVE_ENERGY_SECOND_FACTOR*dt) >= MIN_RANGE)
			this.set_energy(this.get_energy() - (REMOVE_ENERGY_FIRST_FACTOR*REMOVE_ENERGY_SECOND_FACTOR*dt));
		if (this.get_desire() + (ADD_DESIRE*dt) <= MAX_RANGE)
			this.set_desire(this.get_desire() + (ADD_DESIRE*dt));
		
		if(this.get_position().distanceTo(this.get_mate_target().get_position())< DISTANCE_COMPARISON_MATE) {
			this.set_desire(0.0);
			this.get_mate_target().set_desire(0.0);
//			
			if(!this.is_pregnant()) {
				double x = Utils._rand.nextDouble(0, 1);
				System.out.println(x);
				if(x < PROBABILITY_BABY)
					this.set_baby(new Wolf(this, this.get_mate_target()));
				if (this.get_energy() - (REMOVE_ENERGY) >= MIN_RANGE)
					this.set_energy(this.get_energy() - (REMOVE_ENERGY));
			}
			this.set_mate_target(null);
		}
	}
	
	public void search_mate_target() {
		List<Animal> animals_filtered = this.get_region_mngr().get_animals_in_range(this, (Animal a)->a.get_diet()==Diet.CARNIVORE);
		SelectionStrategy aux = this.get_hunting_strategy();
		this.set_hunt_target(aux.select(this, animals_filtered));
	}
	
	public void search_hunt_target() {
		List<Animal> animals_filtered = this.get_region_mngr().get_animals_in_range(this, (Animal a)->a.get_diet()==Diet.HERBIVORE);
		SelectionStrategy aux = this.get_hunting_strategy();
		this.set_hunt_target(aux.select(this, animals_filtered));
	}
	
	public Animal get_hunt_target() {
		return _hunt_target;
	}

	public SelectionStrategy get_hunting_strategy() {
		return _hunting_strategy;
	}
	
	public void set_hunt_target(Animal _hunt_target) {
		this._hunt_target = _hunt_target;
	}

	public void set_hunting_strategy(SelectionStrategy _hunting_strategy) {
		this._hunting_strategy = _hunting_strategy;
	}

}
