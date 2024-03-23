package simulator.model;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo {
	private final static double ANIMAL_INITIAL_SPEED = 0.1;
	private final static double ANIMAL_INITIAL_ENERGY = 100.0;
	private final static double MUTATION_POS = 60.0;
	private final static double MUTATION_SIGHT_RANGE = 0.2;
	private final static double MUTATION_SPEED = 0.2;

	protected String _genetic_code;
	protected Diet _diet;
	protected State _state;
	protected Vector2D _pos;
	protected Vector2D _dest;
	protected double _energy;
	protected double _speed;
	protected double _age;
	protected double _desire;
	protected double _sight_range;
	protected Animal _mate_target;
	protected Animal _baby;
	protected AnimalMapView _region_mngr;
	protected SelectionStrategy _mate_strategy;

	protected Animal(String genetic_code, Diet diet, double sight_range, double init_speed,
			SelectionStrategy mate_strategy, Vector2D pos) {
		if (genetic_code == null)
			throw new IllegalArgumentException("Genetic Code no acepta una cadena de caracteres vacía");
		if (sight_range <= 0 && init_speed <= 0)
			throw new IllegalArgumentException("Sight Range y/o Init Speed no es un valor positivo");
		if (mate_strategy == null)
			throw new IllegalArgumentException("Mate estrategy no debe ser null");

		this._genetic_code = genetic_code;
		this._diet = diet;
		this._sight_range = sight_range;
		this._pos = pos;
		this._mate_strategy = mate_strategy;
		this._speed = Utils.get_randomized_parameter(init_speed, ANIMAL_INITIAL_SPEED);
		this._state = State.NORMAL;
		this._energy = ANIMAL_INITIAL_ENERGY;
		this._desire = 0.0;
		this._dest = null;
		this._mate_target = null;
		this._baby = null;
		this._region_mngr = null;
	}

	protected Animal(Animal p1, Animal p2) {
		this._dest = null;
		this._baby = null;
		this._mate_target = null;
		this._region_mngr = null;
		this._state = State.NORMAL;
		this._desire = 0.0;
		this._genetic_code = p1.get_genetic_code();
		this._diet = p1.get_diet();
		this._mate_strategy = p2.get_mate_strategy();
		this._energy = (p1.get_energy() + p2.get_energy()) / 2;
		this._pos = p1.get_position()
				.plus(Vector2D.get_random_vector(-1, 1).scale(MUTATION_POS * (Utils._rand.nextGaussian() + 1)));
		this._sight_range = Utils.get_randomized_parameter((p1.get_sight_range() + p2.get_sight_range()) / 2,
				MUTATION_SIGHT_RANGE);
		this._speed = Utils.get_randomized_parameter((p1.get_speed() + p2.get_speed()) / 2, MUTATION_SPEED);
	}

	// El gestor de regiones(RegionManager) invocará a este método al añadir el
	// animal a la simulación
	public void init(AnimalMapView reg_mngr) {
		this.set_region_mngr(reg_mngr);
		if (this.get_position() == null) {
			this.set_position(new Vector2D(Utils._rand.nextDouble(0, this.get_region_mngr().get_width() - 1),
					Utils._rand.nextDouble(0, this.get_region_mngr().get_height() - 1)));
		} else {
			adjust();
		}
		this.set_destination(new Vector2D(Utils._rand.nextDouble(0, this.get_region_mngr().get_width() - 1),
				Utils._rand.nextDouble(0, this.get_region_mngr().get_height() - 1)));
	}

	public void adjust() {
		double x = this.get_position().getX();
		double y = this.get_position().getY();
		boolean change = false;

		while (x >= get_region_mngr().get_width()) {
			x = (x - get_region_mngr().get_width());
			change = true;
		}
		while (x < 0) {
			x = (x + get_region_mngr().get_width());
			change = true;
		}
		while (y >= get_region_mngr().get_height()) {
			y = (y - get_region_mngr().get_height());
			change = true;
		}
		while (y < 0) {
			y = (y + get_region_mngr().get_height());
			change = true;
		}

		if (change) {
			this.set_position(new Vector2D(x, y));
			this.change_to_normal();
		}
	}

	protected abstract void change_to_normal();

	protected abstract void change_to_mate();

	protected void move(double speed) {
		this.set_position(
				this.get_position().plus(this.get_destination().minus(this.get_position()).direction().scale(speed)));
	}

	public JSONObject as_JSON() {
		JSONArray pos_array = this.get_position().asJSONArray();
		JSONObject animal_object = new JSONObject();
		animal_object.put("pos", pos_array);
		animal_object.put("gcode", get_genetic_code());
		animal_object.put("diet", get_diet());
		animal_object.put("state", get_state());
		return animal_object;
	}

	public void move_as_normal(double dt, double DISTANCE_COMPARISON_DEST, double MOVE_SECOND_FACTOR,
			double MOVE_THIRD_FACTOR, double REMOVE_ENERGY_FIRST_FACTOR, double ADD_DESIRE, double MIN_RANGE,
			double MAX_RANGE) {
		if (this.get_position().distanceTo(this.get_destination()) < DISTANCE_COMPARISON_DEST) {
			this.set_destination(new Vector2D(Utils._rand.nextDouble(0, this.get_region_mngr().get_width() - 1),
					Utils._rand.nextDouble(0, this.get_region_mngr().get_height() - 1)));
		}
		this.move(this.get_speed() * dt * Math.exp((this.get_energy() - MOVE_SECOND_FACTOR) * MOVE_THIRD_FACTOR));
		this.set_age(this.get_age() + dt);

		// Quitar 20.0*dt a la energía (manteniéndola siempre entre 0.0 y 100.0).
		this.set_energy(this.get_energy() - (REMOVE_ENERGY_FIRST_FACTOR * dt));
		if (this.get_energy() < MIN_RANGE)
			this.set_energy(MIN_RANGE);

		// Añadir 40.0*dt al deseo (manteniéndolo siempre entre 0.0 y 100.0).
		this.set_desire(this._desire + (ADD_DESIRE * dt));
		if (this._desire > MAX_RANGE)
			this.set_desire(MAX_RANGE);
	}

	@Override
	public Vector2D get_position() {
		return this._pos;
	}

	@Override
	public String get_genetic_code() {
		return this._genetic_code;
	}

	@Override
	public double get_speed() {
		return this._speed;
	}

	@Override
	public double get_sight_range() {
		return this._sight_range;
	}

	@Override
	public double get_energy() {
		return this._energy;
	}

	@Override
	public double get_age() {
		return this._age;
	}

	@Override
	public Vector2D get_destination() {
		return this._dest;
	}

	@Override
	public boolean is_pregnant() {
		if (this._baby == null)
			return false;
		else
			return true;
	}

	@Override
	public Diet get_diet() {
		return _diet;
	}

	@Override
	public State get_state() {
		return _state;
	}

	protected SelectionStrategy get_mate_strategy() {
		return _mate_strategy;
	}

	protected AnimalMapView get_region_mngr() {
		return _region_mngr;
	}

	

	protected Animal get_mate_target() {
		return _mate_target;
	}

	protected Animal get_baby() {
		return _baby;
	}

	protected Animal deliver_baby() {
		Animal aux_baby = this.get_baby();
		this.set_baby(null);
		return aux_baby;
	}

	protected void set_diet(Diet _diet) {
		this._diet = _diet;
	}

	protected void set_state(State _state) {
		this._state = _state;
	}

	protected void set_position(Vector2D _pos) {
		this._pos = _pos;
	}

	protected void set_destination(Vector2D _dest) {
		this._dest = _dest;
	}

	protected void set_desire(double _desire) {
		this._desire = _desire;
	}

	protected void set_mate_target(Animal _mate_target) {
		this._mate_target = _mate_target;
	}

	protected void set_baby(Animal _baby) {
		this._baby = _baby;
	}

	protected void set_region_mngr(AnimalMapView _region_mngr) {
		this._region_mngr = _region_mngr;
	}

	protected void set_mate_strategy(SelectionStrategy _mate_strategy) {
		this._mate_strategy = _mate_strategy;
	}

	protected void set_genetic_code(String _genetic_code) {
		this._genetic_code = _genetic_code;
	}

	protected void set_energy(double _energy) {
		this._energy = _energy;
	}

	protected void set_speed(double _speed) {
		this._speed = _speed;
	}

	protected void set_age(double _age) {
		this._age = _age;
	}

	protected void set_sight_range(double _sight_range) {
		this._sight_range = _sight_range;
	}

	protected void random_dest() {
		double x = Utils._rand.nextDouble(800);
		double y = Utils._rand.nextDouble(600);

		set_destination(new Vector2D(x, y));
	}

}
