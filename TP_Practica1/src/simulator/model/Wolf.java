package simulator.model;

import java.util.List;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal {
	private Animal _hunt_target;
	private SelectionStrategy _hunting_strategy;
	
	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy, Vector2D pos) {
		super("Wolf", Diet.CARNIVORE, 50.0, 60.0, mate_strategy, pos);
	}
	
	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		this._hunting_strategy = p1.get_hunting_strategy();
		this._hunt_target = null;
	}
	
	@Override
	public void update(double dt) {
//		1.Si el estado es DEAD no hacer nada (volver inmediatamente).
		if (this.get_state() != State.DEAD) {
//		2. Actualizar el objeto según el estado del animal (ver la descripción abajo)
			this.update_state(dt);
//		3. Si la posición está fuera del mapa, la ajusta y cambia su estado a NORMAL.
			this.adjust();
//		4. Si _energy es 0.0 o _age es mayor de 14.0, cambia su estado a DEAD.
			if (this.get_energy() <= 0.0 || (this.get_age() > 14.0))
				this.set_state(State.DEAD); //Comprobamos que no este muerto de nuevo
//			5. Si su estado no es DEAD, pide comida al gestor de regiones usando get_food(this, dt) y la añade a
//			su _energy (manteniéndolo siempre entre 0.0 y 100.0)
			
			if (this.get_state() != State.DEAD) this.get_region_mngr().get_food(this, dt);
		}
	}
	
	private void update_state(double dt) {
		switch (this.get_state()) {
		case NORMAL:
//			1. Avanzar el animal según los siguiente pasos:
//				1.1. Si la distancia del animal al destino (_dest) es menor que 8.0, elegir otro destino de manera
//				aleatoria (dentro de las dimensiones de mapa).
//				1.2. Avanza (llamando a move) con velocidad _speed*dt*Math.exp((_energy-100.0)*0.007).
			this.move_as_normal(dt);
//				1.3. Añadir dt a la edad.
//				1.4. Quitar 18.0*dt a la energía (manteniéndola siempre entre 0.0 y 100.0).
//				1.5. Añadir 30.0*dt al deseo (manteniéndolo siempre entre 0.0 y 100.0).
//			2. Cambio de estado
//				2.1. Si su energía es menor que 50.0 cambia de estado a HUNGER, y si no lo es y su deseo es mayor 
//				que 65.0 cambia de estado a MATE. En otro caso no hace nada.
			if(this.get_energy() < 50.0)
				this.set_state(State.HUNGER);
			else {
				if(this.get_desire()>65.0)
					this.set_state(State.MATE);
			}
			break;
		case HUNGER:
//			1. Si _hunt_target es null, o no es null pero su estado es DEAD o está fuera del campo visual, buscar
//			otro animal para cazarlo.
//			2. Si _hunt_target es null, avanzar normalmente como el punto 1 del caso NORMAL arriba, y si
//			_hunt_target no es null:
//				2.1. Queremos cambiar el destino para avanzar hacia el animal que quiere cazar. Esto se puede
//				hacer con _hunt_target.get_position() como destino.
//				2.2. Avanza (llamando a move) con velocidad 3.0*_speed*dt*Math.exp((_energy-100.0)*0.007).
//				2.3. Añadir dt a la edad.
//				2.4. Quitar 18.0*1.2*dt a la energía (manteniéndola siempre entre 0.0 y 100.0).
//				2.5. Añadir 30.0*dt al deseo (manteniéndola siempre entre 0.0 y 100.0).
//				2.6. Si la distancia del animal a _hunt_target es menor que 8.0, entonces va a cazar según los
//				siguientes pasos:
//						2.6.1. Poner el estado _hunt_target a DEAD.
//						2.6.2. Poner _hunt_target a null.
//						2.6.3. Sumar 50.0 a la energía (manteniéndola siempre entre 0.0 y 100.0).
			if(this.get_hunt_target()==null) {
				//this.move_as_normal(dt);
				List<Animal> animals_filtered = this.get_region_mngr().get_animals_in_range(this, (Animal a)->a.get_diet()==Diet.HERBIVORE);
				SelectionStrategy aux = this.get_hunting_strategy();
				this.set_hunt_target(aux.select(this, animals_filtered));
				this.move_as_normal(dt);
			} else {
				if(this.get_state() == State.DEAD || this.get_position().distanceTo(this.get_hunt_target().get_position()) > this.get_sight_range()) {
					List<Animal> animals_filtered = this.get_region_mngr().get_animals_in_range(this, (Animal a)->a.get_diet()==Diet.HERBIVORE);
					SelectionStrategy aux = this.get_hunting_strategy();
					this.set_hunt_target(aux.select(this, animals_filtered));
				}
				this.set_destination(this.get_hunt_target().get_position());
				this.move(3.0*this.get_speed()*dt*Math.exp((this.get_energy()-100.0)*0.007));
				this.set_age(this.get_age()+dt);
				if (this.get_energy() - (18.0*1.2*dt) >= 0)
					this.set_energy(this.get_energy() - (18.0*1.2*dt));
				if (this.get_desire() + (30.0*dt) <= 100.0)
					this.set_desire(this.get_desire() + (30.0*dt));
			}
//			3. Cambiar de estado
//				3.1. Si su energía es mayor que 50.0
//					3.1.1. Si el deseo es menor que 65.0 cambia el estado a NORMAL.
//					3.1.2. En otro caso cámbialo a MATE.
//				3.2. Si su energía es menor, no hacer nada.
			if(this.get_energy()>50.0) {
				if(this.get_desire()<65.0)
					this.set_state(State.NORMAL);
				else
					this.set_state(State.MATE);
					
			}
			break;
		case MATE:
//			1. Si _mate_target no es null y su estado es DEAD o está fuera del campo visual, poner _mate_target a
//			null ya que no lo va a seguir para emparejarse.
//			2. Si _mate_target es null, buscar un animal para emparejarse y si no se encuentra uno avanza
//			normalmente como el punto 1 del caso NORMAL arriba, en otro caso (_mate_target ya no era null):
//				2.1. Queremos cambiar el destino para perseguir a _mate_target, esto se puede hacer con
//				_mate_target.get_position() como destino.
//				2.2. Avanza (llamando a move) con velocidad 3.0*_speed*dt*Math.exp((_energy-100.0)*0.007).
//				2.3. Añadir dt a la edad.
//				2.4. Quitar 18.0*1.2*dt a la energía (manteniéndola siempre entre 0.0 y 100.0).
//				2.5. Añadir 30.0*dt al deseo (manteniéndolaosiempre entre 0.0 y 100.0).
//				2.6. Si la distancia del animal a _mate_target es menor que 8.0, entonces van a emparejarse según
//				los siguientes pasos:
//					2.6.1. Resetear el deseo del animal y del _mate_target a 0.0.
//					2.6.2. Si el animal no lleva un bebe ya, con probabilidad de 0.9 va a llevar a un nuevo bebe
//					usando new Wolf(this, _mate_target).
//					2.6.3. Quitar 10.0 de la energía (manteniéndola siempre entre 0.0 y 100.0).
//					2.6.4. Poner _mate_target a null.
			if(this.get_mate_target() != null) {
				if(this.get_state() == State.DEAD || this.get_position().distanceTo(this.get_mate_target().get_position())> this.get_sight_range())
					this.set_mate_target(null);
				this.set_destination(this.get_mate_target().get_position());
				this.move(3.0*this.get_speed()*dt*Math.exp((this.get_energy()-100.0)*0.007));
				this.set_age(this.get_age()+dt);
				if (this.get_energy() - (18.0*1.2*dt) >= 0)
					this.set_energy(this.get_energy() - (18.0*1.2*dt));
				if (this.get_desire() + (30.0*dt) <= 100.0)
					this.set_desire(this.get_desire() + (30.0*dt));
				if(this.get_position().distanceTo(this.get_mate_target().get_position())<8.0) {
					this.set_desire(0.0);
					this.get_mate_target().set_desire(0.0);
//					Si el animal no lleva un bebe ya, con probabilidad de 0.9 va a llevar a un nuevo bebe
////					usando new Wolf(this, _mate_target).
					if (this.get_energy() - (10.0) >= 0)
						this.set_energy(this.get_energy() - (10.0));
					this.set_mate_target(null);
				}
			}
			else {
				List<Animal> animals_filtered = this.get_region_mngr().get_animals_in_range(this, (Animal a)->a.get_diet()==Diet.HERBIVORE);
				SelectionStrategy aux = this.get_hunting_strategy();
				if(aux == null)
					this.move_as_normal(dt);
				else
					this.set_hunt_target(aux.select(this, animals_filtered));
					
			}
//			3. Si su energía es menor que 50.0 cambia de estado a HUNGER, y si no lo es y el deseo es menor que
//			65.0 cambia de estado a NORMAL.
			if(this.get_energy()<50.0)
				this.set_state(State.HUNGER);
			else {
				if(this.get_desire()<65.0)
					this.set_state(State.NORMAL);
			}
			break;
		case DANGER:
//			Un objeto de tipo Wolf nunca puede estar en estado DANGER.
			break;
		case DEAD:
//			Si el estado es DEAD no hacer nada (volver inmediatamente).
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + this.get_state());
		}
	}
	
	public void move_as_normal(double dt) {
		if(this.get_position().distanceTo(this.get_destination()) < 8.0) {
			/*this.set_destination(new Vector2D(Utils.get_randomized_parameter(0, this.get_region_mngr().get_width() - 1),
					Utils.get_randomized_parameter(0, this.get_region_mngr().get_height() - 1)));*/
			this.random_dest();
		}
		this.move(this.get_speed()*dt*Math.exp((this.get_energy()-100.0)*0.007));
		this.set_age(this.get_age() + dt);
		// Quitar 18.0*dt a la energía (manteniéndola siempre entre 0.0 y 100.0).
		/*if (this.get_energy() - (18.0*dt) >= 0)
			this.set_energy(this.get_energy() - (18.0*dt));*/
		this.set_energy(this.get_energy() - (18.0*dt));
		if (this.get_energy() < 0.0) this.set_energy(0.0);
		else if (this.get_energy() > 100.0) this.set_energy(100.0);
		
		// Añadir 30.0*dt al deseo (manteniéndolo siempre entre 0.0 y 100.0).
		/*if (this.get_desire() + (30.0*dt) <= 100.0)
			this.set_desire(this.get_desire() + (30.0*dt));*/
		
		this.set_desire(this.get_desire() + (30.0*dt));
		if (get_desire() < 0) set_desire(0.0);
		else if (get_desire() > 100.0) set_desire(100.0);
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
