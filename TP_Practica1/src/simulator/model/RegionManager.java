package simulator.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
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
	private 		int cont = 0;
	//un mapa (_animal_region) de tipo Map<Animal, Region> que
	//asigna a cada animal su región actual.
	private Map<Animal,Region> _animal_region;
	
	public RegionManager(int cols, int rows, int width, int height) {
		
		this._cols = cols;
		this._rows = rows;
		this._width = width;
		this._height = height;
		this._width_region = width/cols + (width%cols != 0? 1:0);
		this._height_region = height/rows + (height%rows!= 0?1:0);	
		this._regions = new DefaultRegion[rows][cols];
		for (int i = 0; i < rows; i++) {
		    for (int j = 0; j < cols; j++) {
		        _regions[i][j] = new DefaultRegion();
		    }
		}
		_animal_region = new HashMap<Animal, Region>();
		
	}
	
	void register_animal(Animal a) {
		a.init(this);
		
		double x = a.get_position().getX();
		double y = a.get_position().getY();

		cont++;
		int aux1 = (int) (y/this.get_region_height()); //fila
		int aux2 = (int) (x/this.get_region_width()); //columna
		
		try{this.get_regions()[aux1][aux2].add_animal(a);
		}
		catch (Exception e) {
			System.out.println("Ancho mapa: " + this.get_width());
			System.out.println("Alto mapa: " + this.get_height());
			System.out.println("rows: " + this.get_rows());
			System.out.println("cols: " + this.get_cols());
			System.out.println("Ancho region: " + this.get_region_width());
			System.out.println("Alto region: " + this.get_region_height());
			System.out.println();
			System.out.println("pos x: " + x + " pos y: " + y);
			throw new IllegalArgumentException("Error con el animal:  " +this._cols + " Rows: " + this._rows + " Col animal: " + aux1 + " Row: animal " + aux2  + " x animal " + x + "      " + cont+ " " + e.getMessage());
			}
		
		this.get_animal_region().put(a, this.get_regions()[aux1][aux2]);

	}
	
	void unregister_animal(Animal a) {
		int x = (int) a.get_position().getX();
		int y = (int) a.get_position().getY();
		
		this.get_regions()[y/this.get_region_height()][x/this.get_region_width()].remove_animal(a);
		this.get_animal_region().remove(a, this.get_regions()[y/this.get_region_height()][x/this.get_region_width()]);
	}
	
	/*
	void update_animal_region(Animal a): encuentra la región a la que tiene que pertenecer el
	animal (a partir de su posición actual), y si es distinta de su región actual lo añade a la nueva región, lo
	quita de la anterior, y actualiza _animal_region.
	*/
	void update_animal_region(Animal a) {
		int x = (int) a.get_position().getX();
		int y = (int) a.get_position().getY();
		Region  r = this.get_animal_region().get(a); //region actual del animal
		
		int width_ = this.get_width();
		int height_ = this.get_height();
		int rows_ = this.get_rows();
		int cols_ = this.get_cols();
		int col_matrix = x/this.get_width();
		int row_matrix = y/this.get_height();
		//cambio
		if(r != this.get_regions()[y/this.get_region_height()][x/this.get_region_width()]) { //si region actual es diferente a region correcta
			r.remove_animal(a);
			this.get_regions()[y/this.get_region_height()][x/this.get_region_width()].add_animal(a);
			//this.get_regions()[this.get_width()/x][(this.get_height()/y)].remove_animal(a);
			//actualizamos en _amimal_region la region que realmente debería pertenecer la clave a
			this.get_animal_region().replace(a, this.get_regions()[y/this.get_region_height()][x/this.get_region_width()]);
			//r = this.get_regions()[this.get_width()/x][(this.get_height()/y)]; 
			//this.get_animal_region().replace(a, r);
		}
	}
	
	@Override
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter) {
		List<Animal> _animals_in_range = new ArrayList<Animal>();
		//Obtengo posicion inicial con respecto a X,Y
		int posComienzoX = (int)Math.abs(e.get_position().getX()-e.get_sight_range()); 
		int posComienzoY = (int)Math.abs(e.get_position().getY()-e.get_sight_range());
		//A partir de la pos(X,Y), obtengo la posicion con respecto a la fila, columna
		int iniCol = posComienzoX / this.get_region_width();
		int iniFila = posComienzoY / this.get_region_height();
		//Obtengo el recorrido (diametro = 2*sr) para saber hasta que columna y fila 
		//como MAXIMO va a recorrer
		int recorrido = 2*(int)e.get_sight_range();
		
		
		int range = (int) e.get_sight_range();
		int regionX_range = this.get_region_width()/range;
		int regionY_range = this.get_region_height()/range;
		
		int auxX = iniCol-regionX_range;
		int auxY = iniFila-regionY_range;
		if (auxX < 0)auxX = 0;
		if (auxY < 0)auxY = 0;
		
		//if (regionY_range <= 0)regionY_range = 1;
		for (int i = auxX; i < regionX_range+iniCol && i < this.get_cols(); i++) {
			for (int j = auxY; j < regionY_range+iniFila && i < this.get_rows(); j++) {
				for(Animal a: this.get_regions()[i][j].getAnimals()) {
					/*if(a.get_position().distanceTo(e.get_position()) <= e.get_sight_range()
							&& filter.test(a)) */{
								System.out.println(a.get_position().distanceTo(e.get_position()));
					if(a.get_position().distanceTo(e.get_position()) <= e.get_sight_range()){
						if (filter.test(a)) {
						_animals_in_range.add(a);
						}
					}
				}
			}
		}
	}
		
		/*for(int i = iniCol; i < iniCol + recorrido;++i) {
			for(int j = iniFila; j < iniFila + recorrido;++j) {
				for(Animal a: this.get_regions()[i][j].getAnimals()) {
					if(a.get_position().distanceTo(e.get_position()) <= e.get_sight_range()
							&& filter.test(a)) {
						_animals_in_range.add(a);
					}
				}
			}
		}*/
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
		return _animal_region.get(a).get_food(a, dt);
		//return a.get_region_mngr().get_food(a, dt);
		
	}

	void set_region(int row, int col, Region r) {
		for(Animal a: this.get_regions()[row][col].animals_in_list) {
			r.add_animal(a);
			this.get_animal_region().replace(a, r);
		}
		this.get_regions()[row][col] = r;
	}
	
	 void update_all_regions(double dt) {
		 for (int i = 0; i < this.get_rows(); i++) {
			for (int j = 0; j < this.get_cols(); j++) {
				this.get_regions()[i][j].update(dt);
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

	public Map<Animal, Region> get_animal_region() {
		return _animal_region;
	}
}
