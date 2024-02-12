package simulator.model;

import java.util.List;
import java.util.function.Predicate;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.RegionManager;

public class Sheep extends Animal {
	private SelectionStrategy _danger_strategy;
	private Animal _danger_source;

	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super("Sheep", Diet.HERBIVORE, 40.0, 35.0, mate_strategy, pos);
		this._danger_strategy = danger_strategy;
	}

	// Constructora cuando nazca un animal de tipo Sheep
	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		this._danger_strategy = p1.get_danger_strategy();
		this._danger_source = null;
	}

	Predicate<Animal> predicate_sheep = new Predicate<Animal>() {
		@Override
		public boolean test(Animal a) {
			return a.get_diet().equals(Diet.CARNIVORE);
		}
	};

	@Override
	public void update(double dt) {
		// DEAD -> NO HACE NADA
		// ACTUALIZAR OBJETO SEGUN ESTADO
		// SI POSICION FUERA DE MAPA, AJUSTAR
		if (this.get_energy() <= 0.0 || (this.get_age() > 8.0))
			this.set_state(State.DEAD);
		this.get_region_mngr().get_food(this, dt);

	}

	Predicate<Animal> predicat;

	private void update_state(double dt) {
		switch (this.get_state()) {
		case NORMAL:
			this.move_as_normal(dt);
			// Si _danger_source es null, buscar un nuevo animal que se considere peligroso.
			if (this.get_danger_source() == null) {
				List<Animal> animals_filtered = this.get_region_mngr().get_animals_in_range(this, (Animal a)->a.get_diet()==Diet.CARNIVORE);
				SelectionStrategy aux = this.get_danger_strategy();
				this.set_danger_source(aux.select(this, animals_filtered));
				if(this.get_desire() > 65.0) //Si _danger_source es null y el deseo mayor de 65.0 cambiar estado a MATE
					this.set_state(State.MATE);
			} else { // Si _danger_source null, cambiar el estado a DANGER
				this.set_state(State.DANGER);
			}
			break;
		case HUNGER:
			//Un objeto de tipo Sheep nunca puede estar en estado HUNGER.
			break;
		case MATE:
			if(this.get_mate_target() != null) {
				if(this.get_mate_target().get_state() == State.DEAD || 
						this.get_position().distanceTo(this.get_mate_target().get_position())>this.get_sight_range())
					this.set_mate_target(null);
				this.set_destination(this.get_mate_target().get_position());
				this.move(2.0*this.get_speed()*dt*Math.exp((this.get_energy()-100.0)*0.007));
				this.set_age(this.get_age()+dt);
				// Quitar 20.0*1.2*dt a la energía (manteniéndola siempre entre 0.0 y 100.0).
				if (this.get_energy() >= 0)
					this.set_energy(this.get_energy() - 20.0 * 1.2 * dt);
				// Añadir 40.0*dt al deseo (manteniéndolo siempre entre 0.0 y 100.0).
				if (this.get_desire() <= 100)
					this.set_desire(this.get_desire() + 40.0 * dt);
				if(this.get_position().distanceTo(this.get_mate_target().get_position()) < 8.0) {
					this.set_desire(0.0);
					this.get_mate_target().set_desire(0.0);
//					Si el animal no lleva un bebé ya, con probabilidad de 0.9 va a llevar a un nuevo bebé
//					usando new Sheep(this, _mate_target).
					this.set_mate_target(null);
				}
				
			} else { 
				List<Animal> animals_filtered = this.get_region_mngr().get_animals_in_range(this, (Animal a)->a.get_diet()==Diet.HERBIVORE);
				SelectionStrategy aux = this.get_danger_strategy();
				if(aux.select(this, animals_filtered) != null) {
					this.set_danger_source(aux.select(this, animals_filtered));
				} else {
					this.move_as_normal(dt);
				}
			}
			if(this.get_danger_source() == null) {
				if(this.get_desire() < 65.0) {
					this.set_state(State.NORMAL);
				} else {
					List<Animal> animals_filtered = this.get_region_mngr().get_animals_in_range(this, (Animal a)->a.get_diet()==Diet.HERBIVORE);
					SelectionStrategy aux = this.get_danger_strategy();
					this.set_danger_source(aux.select(this, animals_filtered));					
				}
			} else {
				this.set_state(State.DANGER);
			}
			break;
		case DANGER:
			if (this._danger_source != null) {
				if (this._danger_source.get_state() == State.DEAD) {
					this._danger_source = null;
				} else {
					this.set_destination(this.get_position().plus(this.get_position().minus(this._danger_source.get_position()).direction()));
					this.move(2.0 * this.get_speed() * dt * Math.exp((this.get_energy() - 100.0) * 0.007));
					this.set_age(this.get_age() + dt);
					// Quitar 20.0*1.2*dt a la energía (manteniéndola siempre entre 0.0 y 100.0).
					if (this.get_energy() >= 0)
						this.set_energy(this.get_energy() - 20.0 * 1.2 * dt);
					// Añadir 40.0*dt al deseo (manteniéndolo siempre entre 0.0 y 100.0).
					if (this.get_desire() <= 100)
						this.set_desire(this.get_desire() + 40.0 * dt);
				}
				//Si _danger_source no es nulo y _danger_source no está en el campo visual del animal
				if(this.get_position().distanceTo(this.get_danger_source().get_position()) > this.get_sight_range()) {
					List<Animal> animals_filtered = this.get_region_mngr().get_animals_in_range(this, (Animal a)->a.get_diet()==Diet.CARNIVORE);
					SelectionStrategy aux = this.get_danger_strategy();
					this.set_danger_source(aux.select(this, animals_filtered));
				}
					
			} else { // si danger source es nulo, repite los mismos pasos como si estuviera en estado NORMAL
				this.move_as_normal(dt);
				//Si _danger_source es null
				List<Animal> animals_filtered = this.get_region_mngr().get_animals_in_range(this, (Animal a)->a.get_diet()==Diet.CARNIVORE);
				SelectionStrategy aux = this.get_danger_strategy();
				this.set_danger_source(aux.select(this, animals_filtered));
				if(this.get_desire() < 65.0) //Si _danger_source es null y el deseo mayor de 65.0 cambiar estado a MATE
					this.set_state(State.NORMAL);
				else {
					this.set_state(State.MATE);
				}
			}
			break;
		case DEAD:
//			Si el estado es DEAD no hacer nada (volver inmediatamente).
			break;

		default:
			throw new IllegalArgumentException("Unexpected value: " + this.get_state());
		}
	}
	
	public void move_as_normal(double dt) {
		if (this.get_position().distanceTo(this.get_destination()) < 8.0) {
			this.set_destination(new Vector2D(Utils.get_randomized_parameter(0, this.get_region_mngr().get_width() - 1),
					Utils.get_randomized_parameter(0, this.get_region_mngr().get_height() - 1)));
		}
		this.move(this.get_speed() * dt * Math.exp((this.get_energy() - 100.0) * 0.007));
		this.set_age(this.get_age() + dt);
		// Quitar 20.0*dt a la energía (manteniéndola siempre entre 0.0 y 100.0).
		if (this.get_energy() - (20.0 * dt) >= 0)
			this.set_energy(this.get_energy() - (20.0 * dt));
		// Añadir 40.0*dt al deseo (manteniéndolo siempre entre 0.0 y 100.0).
		if (this.get_desire() + (40.0 * dt) <= 100.0)
			this.set_desire(this.get_desire() + (40.0 * dt));
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
}
