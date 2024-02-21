package simulator.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

public class RegionManager implements AnimalMapView {
	private int _cols;
	private int _rows;
	private int _width;
	private int _height;
	private int _width_region;
	private int _height_region;
	private Region [][]_regions;
	//un mapa (_animal_region) de tipo Map<Animal, Region> que
	//asigna a cada animal su región actual.
	private Map<Animal,Region> _animal_region;
	
	public RegionManager(int cols, int rows, int width, int height) {
		
		this._cols = cols;
		this._rows = rows;
		this._width = width;
		this._height = height;
		this._width_region = width/cols;
		this._height_region = height/rows;	
		this._regions = new DefaultRegion[rows][cols];
		//_animal_region = new Map<Animal, Region>();
		
	}
	
	void register_animal(Animal a) {
		int x = (int) a.get_position().getX();
		int y = (int) a.get_position().getY();
		
		_regions[this.get_width()/x][(this.get_height()/y)].add_animal(a);
		_animal_region.put(a, _regions[this.get_width()/x][(this.get_height()/y)]);
		a.init(this);
	}
	
	void unregister_animal(Animal a) {
		int x = (int) a.get_position().getX();
		int y = (int) a.get_position().getY();
		
		_regions[this.get_width()/x][(this.get_height()/y)].remove_animal(a);
		_animal_region.remove(a, _regions[this.get_width()/x][(this.get_height()/y)]);
	}
	
	void update_animal_region(Animal a) {
		int x = (int) a.get_position().getX();
		int y = (int) a.get_position().getY();
		Region  r = _animal_region.get(a);
		
		if(r != _regions[this.get_width()/x][(this.get_height()/y)]) {
			_regions[this.get_width()/x][(this.get_height()/y)] = r;
			_animal_region.replace(a, r);
		}
	}
	
	@Override
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter) {
		List<Animal> _animals_in_range = new ArrayList<Animal>();
		//Obtengo posicion inicial con respecto a X,Y
		int posComienzoX = (int)Math.abs(e.get_position().getX()-e.get_sight_range()); 
		int posComienzoY = (int)Math.abs(e.get_position().getY()-e.get_sight_range());
		//A partir de la pos(X,Y), obtengo la posicion con respecto a la fila, columna
		int iniFila = posComienzoX / this.get_region_height();
		int iniCol = posComienzoY / this.get_region_width();
		//Obtengo el recorrido (diametro = 2*sr) para saber hasta que columna y fila 
		//como MAXIMO va a recorrer
		int recorrido = 2*(int)e.get_sight_range();
		
		for(int i = iniFila; i < iniFila + recorrido;++i) {
			for(int j = iniCol; j < iniCol + recorrido;++j) {
				List<Animal> aux = this.get_regions()[i][j].getAnimals();
				for(Animal a: aux) {
					if(a.get_position().distanceTo(e.get_position()) <= e.get_sight_range()
							&& filter.test(a)) {
						_animals_in_range.add(a);
					}
				}
			}
		}
		return _animals_in_range;
	}
	
	@Override
	public int get_cols() {
		return _cols;
	}

	@Override
	public int get_rows() {
		return _rows;
	}
	
	//Ancho/Alto del Mapa
	@Override
	public int get_width() {
		return _width;
	}

	@Override
	public int get_height() {
		return _height;
	}
	
	//Ancho/Alto de la region
	@Override
	public int get_region_width() {
		return _width_region;
	}

	@Override
	public int get_region_height() {
		return _height_region;
	}

	@Override
	public double get_food(Animal a, double dt) {
		return a.get_region_mngr().get_food(a, dt);
	}

	void set_region(int row, int col, Region r) {
		
		for(Animal a: _regions[col][row].animals_in_list) {
			r.add_animal(a);
			_animal_region.replace(a, r);
		}
		_regions[col][row] = r;
	}
	
	 void update_all_regions(double dt) {
		 for (int i = 0; i < this.get_cols(); i++) {
			for (int j = 0; j < this.get_height(); j++) {
				_regions[i][j].update(dt);
			}
		}
	 }
	 
	 public JSONObject as_JSON() {
		 JSONArray regionesArray = new JSONArray(); // Creamos el arreglo para las regiones
		 
		 for(int i = 0; i < this.get_rows(); ++i) {
			 for(int j = 0; j < this.get_cols(); ++j) {
				 Region r = this.get_regions()[i][j];
		         JSONObject regionJSON = new JSONObject(); // Creamos un objeto JSON para la región
		         regionJSON.put("row", i); // Agregamos la fila
		         regionJSON.put("col", j); // Agregamos la columna
		         regionJSON.put("data", r.as_JSON()); // Agregamos los datos de la región
		            
		         regionesArray.put(regionJSON); // Agregamos el objeto de la región al arreglo
		        }
		    }
		    
		    JSONObject resultJSON = new JSONObject();
		    resultJSON.put("regiones", regionesArray); // Agregamos el arreglo de regiones al objeto final
		    
		    return resultJSON;
	 }

	public Region[][] get_regions() {
		return _regions;
	}

	public void set_regions(Region[][] _regions) {
		this._regions = _regions;
	}
}
