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
	private int cont = 0;
	//un mapa (_animal_region) de tipo Map<Animal, Region> que
	//asigna a cada animal su regi�n actual.
	private Map<Animal,Region> _animal_region;
	
	public RegionManager(int cols, int rows, int width, int height) {
		
		this._cols = cols;
		this._rows = rows;
		this._width = width;
		this._height = height;
		this._width_region = width/cols + (width%cols != 0? 1:0);
		this._height_region = height/rows + (height%rows!= 0?1:0);	
		this._regions = new Region[rows][cols];
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

		int row_animal = (int) (y/this.get_region_height()); //fila
		int col_animal = (int) (x/this.get_region_width()); //columna
		
		try{
			this.get_regions()[row_animal][col_animal].add_animal(a);
			this.get_animal_region().put(a, this.get_regions()[row_animal][col_animal]);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Error con el animal:  " +this._cols + " Rows: " + this._rows + " row animal: " + row_animal + " col: animal " + col_animal  + " x animal " + x + "      " + cont+ " " + e.getMessage());
		}
	}
	
	void unregister_animal(Animal a) {
		double x = a.get_position().getX();
		double y = a.get_position().getY();
		
		int row_animal = (int) y/this.get_region_height(); //fila
		int col_animal = (int) x/this.get_region_width(); //columna
		
		this.get_regions()[row_animal][col_animal].remove_animal(a);
		this.get_animal_region().remove(a, this.get_regions()[row_animal][col_animal]);
	}
	
	/*
	void update_animal_region(Animal a): encuentra la regi�n a la que tiene que pertenecer el
	animal (a partir de su posici�n actual), y si es distinta de su regi�n actual lo a�ade a la nueva regi�n, lo
	quita de la anterior, y actualiza _animal_region.
	*/
	void update_animal_region(Animal a) {
		double x = a.get_position().getX();
		double y = a.get_position().getY();
		
		int row_animal = (int) y/this.get_region_height(); //fila
		int col_animal = (int) x/this.get_region_width(); //columna
		
		Region  r = this.get_animal_region().get(a); //region actual del animal

		//cambio
		if(r != this.get_regions()[row_animal][col_animal]) { //si region actual es diferente a region correcta
			r.remove_animal(a);
			this.get_regions()[row_animal][col_animal].add_animal(a);
			//this.get_regions()[this.get_width()/x][(this.get_height()/y)].remove_animal(a);
			//actualizamos en _amimal_region la region que realmente deber�a pertenecer la clave a
			this.get_animal_region().replace(a, this.get_regions()[row_animal][col_animal]);
			//r = this.get_regions()[this.get_width()/x][(this.get_height()/y)]; 
			//this.get_animal_region().replace(a, r);
		}
	}
	
	@Override
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter) {
		List<Animal> _animals_in_range = new ArrayList<Animal>();
		
		//Obtengo posicion en la esquina superior izquierda con respecto a X,Y
		double posComienzoX = e.get_position().getX() - e.get_sight_range();
		double posComienzoY = e.get_position().getY() - e.get_sight_range();
		
		//Obtengo la maxima posicion de X,Y
		double recorridoFila = (posComienzoY + 2*e.get_sight_range()-1);
		double recorridoCol = (posComienzoX + 2*e.get_sight_range()-1);
		
		//Obtengo la fila y columna inicial. Antes de obtener la fila y columna, verifico si las posiciones son menores que 0
		int iniFila = (posComienzoY < 0.0)? 0: (int)posComienzoY/this.get_region_height(); //verifico si 
		int iniCol = (posComienzoX < 0.0)? 0: (int)posComienzoX/this.get_region_width();
		
		//Obtengo la fila y columna final. Antes de obtener la fila y columna, verifico si las posiciones son mayores que que
		//el numero de filas total y columnas total
		int finFila = (recorridoFila > this.get_height())?this.get_rows()-1:(int)recorridoFila/this.get_region_height();
		int finCol = (recorridoCol > this.get_width())?this.get_cols()-1:(int)recorridoCol/this.get_region_width();
		
		for (int i = iniFila; i <=finFila; i++) {
			for (int j = iniCol; j <= finCol; j++) {
				for(Animal a: this.get_regions()[i][j].getAnimals()) {
					if(e.get_position().distanceTo(a.get_position()) <= e.get_sight_range()
							&& filter.test(a)) {
						_animals_in_range.add(a);
					}
				}
			}
		}
		
		/*
		//Obtengo posicion inicial con respecto a X,Y
		int posComienzoX = (int)Math.abs(e.get_position().getX()-e.get_sight_range()); 
		int posComienzoY = (int)Math.abs(e.get_position().getY()-e.get_sight_range());
		
		//A partir de la poho(X,Y), obtengo la posicion con respecto a la fila, columna
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
					if(a.get_position().distanceTo(e.get_position()) <= e.get_sight_range()
							&& filter.test(a)) {
						_animals_in_range.add(a);
					}
				}
			}
		}
		*/
		return _animals_in_range;
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
		return this.get_animal_region().get(a).get_food(a, dt);		
	}

	void set_region(int row, int col, Region r) {
		for(Animal a: this.get_regions()[row][col].getAnimals()) {
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
		         JSONObject regionJSON = new JSONObject(); // Creamos un objeto JSON para la regi�n
		         regionJSON.put("row", i); // Agregamos la fila
		         regionJSON.put("col", j); // Agregamos la columna
		         regionJSON.put("data", r.as_JSON()); // Agregamos los datos de la regi�n
		            
		         regionesArray.put(regionJSON); // Agregamos el objeto de la regi�n al arreglo
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
