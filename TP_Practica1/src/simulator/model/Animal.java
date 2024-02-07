package simulator.model;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo {

	private String _genetic_code;
	private Diet _diet;
	private State _state;
	private Vector2D _pos;
	private Vector2D _dest;
	private double _energy;
	private double _speed;
	private double _age;
	private double _desire;
	private double _sight_range;
	private Animal _mate_target;
	private Animal _baby;
	private AnimalMapView _region_mngr;
	private SelectionStrategy _mate_strategy;
	
	protected Animal(String genetic_code, Diet diet, double sight_range,
			double init_speed, SelectionStrategy mate_strategy, Vector2D pos) {
		if (genetic_code == null) throw new IllegalArgumentException("Genetic Code es null"); 
		else if (sight_range <= 0 && init_speed <= 0) throw new IllegalArgumentException("Sight range y/o Init speed no es un valor positivo"); 
		if(mate_strategy == null) throw new IllegalArgumentException("Mate estrategy es null"); 
		
		_genetic_code = genetic_code;
		_diet = diet;
		_sight_range = sight_range;
		_speed = Utils.get_randomized_parameter(init_speed, 0.1);
		_mate_strategy = mate_strategy;
		_pos = pos;
		// si pos = NULL, utiliza init() pero no en constructora
		//init(_reg_mngr); DIAPO 6 
		_state = State.NORMAL;
		_energy = 100.0;
		_desire = 0.0;
		_dest = null;
		_mate_target = null;
		_baby = null;
		_region_mngr = null;
	}
	
	protected Animal(Animal p1, Animal p2) {
		_dest = null;
		_mate_target = null;
		_region_mngr = null;
		_desire = 0.0;
		_state = State.NORMAL;
		_genetic_code = p1.get_genetic_code();
		_diet = p1.get_diet();
		_energy = (p1.get_energy()+p2.get_energy())/2;
		_pos = p1.get_position().plus(Vector2D.get_random_vector(-1,1).scale(60.0*(Utils._rand
				.nextGaussian()+1)));
		_sight_range = Utils.get_randomized_parameter((p1.get_sight_range()+p2.get_sight_range())/2, 0.2);
		_speed = Utils.get_randomized_parameter((p1.get_speed()+p2.get_speed())/2, 0.2);
		}
	
	@Override
	public Vector2D get_position() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_genetic_code() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double get_speed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double get_sight_range() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double get_energy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double get_age() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vector2D get_destination() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean is_pregnant() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(double dt) {
		// TODO Auto-generated method stub

	}
	
	//el gestor de regiones(RegionManager) invocar� a este m�todo al a�adir el
	//animal a la simulaci�n
	public void init(AnimalMapView reg_mngr) {
		_region_mngr = reg_mngr;
		if (_pos == null) {
			_pos = new Vector2D(Utils.get_randomized_parameter(0, reg_mngr.get_width()-1), Utils.get_randomized_parameter(0, reg_mngr.get_height()-1));
		}else {
			ajustar();
		}
	}

	private void ajustar() {
		double x = getPosX();
		double y = getPosY();
		// TODO Auto-generated method stub
		while (x >= get_region_mngr().width) x = (x - get_region_mngr().width);
		while (x < 0) x = (x + get_region_mngr().width);
		while (y >= get_region_mngr().height) y = (y - get_region_mngr().height);
		while (y < 0) y = (y + get_region_mngr().height);
		set_pos(new Vector2D(x, y));
	}

	public Diet get_diet() {
		return _diet;
	}

	public void set_diet(Diet _diet) {
		this._diet = _diet;
	}

	public State get_state() {
		return _state;
	}

	public void set_state(State _state) {
		this._state = _state;
	}

	public Vector2D get_pos() {
		return _pos;
	}

	public void set_pos(Vector2D _pos) {
		this._pos = _pos;
	}

	public Vector2D get_dest() {
		return _dest;
	}

	public void set_dest(Vector2D _dest) {
		this._dest = _dest;
	}

	public double get_desire() {
		return _desire;
	}

	public void set_desire(double _desire) {
		this._desire = _desire;
	}

	public Animal get_mate_target() {
		return _mate_target;
	}

	public void set_mate_target(Animal _mate_target) {
		this._mate_target = _mate_target;
	}

	public Animal get_baby() {
		return _baby;
	}

	public void set_baby(Animal _baby) {
		this._baby = _baby;
	}

	public AnimalMapView get_region_mngr() {
		return _region_mngr;
	}

	public void set_region_mngr(AnimalMapView _region_mngr) {
		this._region_mngr = _region_mngr;
	}

	public SelectionStrategy get_mate_strategy() {
		return _mate_strategy;
	}

	public void set_mate_strategy(SelectionStrategy _mate_strategy) {
		this._mate_strategy = _mate_strategy;
	}

	public void set_genetic_code(String _genetic_code) {
		this._genetic_code = _genetic_code;
	}

	public void set_energy(double _energy) {
		this._energy = _energy;
	}

	public void set_speed(double _speed) {
		this._speed = _speed;
	}

	public void set_age(double _age) {
		this._age = _age;
	}

	public void set_sight_range(double _sight_range) {
		this._sight_range = _sight_range;
	}

	private double getPosX() {
		// TODO Auto-generated method stub
		return _pos.getX();
	}
	
	private double getPosY() {
		// TODO Auto-generated method stub
		return _pos.getY();
	}
	
	Animal deliver_baby() {
		_baby = null;
		return _baby;
	}
	
	protected void move(double speed) {
		_pos = _pos.plus(_dest.minus(_pos).direction().scale(speed));
	}
	
	public JSONObject as_JSON() {
		JSONArray ja = get_pos().asJSONArray();
		JSONObject jo = new JSONObject();
		jo.put("pos", ja);
		jo.put("gcode", get_genetic_code());
		jo.put("diet", get_diet());
		jo.put("state", get_state());
		
	}

}