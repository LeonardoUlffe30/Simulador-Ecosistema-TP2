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
	private Region[][] _regions;
	private Map<Animal, Region> _animal_region;

	public RegionManager(int cols, int rows, int width, int height) {

		this._cols = cols;
		this._rows = rows;
		this._width = width;
		this._height = height;
		this._width_region = width / cols + (width % cols != 0 ? 1 : 0);
		this._height_region = height / rows + (height % rows != 0 ? 1 : 0);
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

		int row_animal = (int) (y / this.get_region_height()); // fila
		int col_animal = (int) (x / this.get_region_width()); // columna

		this._regions[row_animal][col_animal].add_animal(a);
		this._animal_region.put(a, this._regions[row_animal][col_animal]);
	}

	void unregister_animal(Animal a) {
		this._animal_region.remove(a).remove_animal(a);
	}

	void update_animal_region(Animal a) {
		double x = a.get_position().getX();
		double y = a.get_position().getY();

		int row_animal = (int) (y / this._height_region); // fila
		int col_animal = (int) (x / this._width_region); // columna

		Region r = this._animal_region.get(a); // region actual del animal

		// cambio
		if (r != this._regions[row_animal][col_animal]) { // si region actual es diferente a region correcta
			r.remove_animal(a);
			this._regions[row_animal][col_animal].add_animal(a);
			// actualizamos en _amimal_region la region que realmente debería pertenecer la
			// clave a
			this._animal_region.replace(a, this._regions[row_animal][col_animal]);
		}
	}

	@Override
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter) {
		List<Animal> _animals_in_range = new ArrayList<Animal>();

		// Obtengo posicion en la esquina superior izquierda con respecto a X,Y
		double posComienzoX = e.get_position().getX() - e.get_sight_range();
		double posComienzoY = e.get_position().getY() - e.get_sight_range(); 

		// Obtengo la maxima posicion de X,Y
		double recorridoFila = (posComienzoY + 2 * e.get_sight_range() - 1);
		double recorridoCol = (posComienzoX + 2 * e.get_sight_range() - 1);

		// Obtengo la fila y columna inicial. Antes de obtener la fila y columna,
		// verifico si las posiciones son menores que 0
		int iniFila = (posComienzoY < 0.0) ? 0 : (int) (posComienzoY / this._height_region);
		int iniCol = (posComienzoX < 0.0) ? 0 : (int) (posComienzoX / this._width_region);

		// Obtengo la fila y columna final. Antes de obtener la fila y columna, verifico
		// si las posiciones son mayores que que
		// el numero de filas total y columnas total
		int finFila = (recorridoFila > this._height) ? this._rows - 1
				: (int) (recorridoFila / this._height_region);
		int finCol = (recorridoCol > this._width) ? this._cols - 1
				: (int) (recorridoCol / this._width_region);

		for (int i = iniFila; i <= finFila; i++) {
			for (int j = iniCol; j <= finCol; j++) {
				for (Animal a : this._regions[i][j].getAnimals()) {
					if (e.get_position().distanceTo(a.get_position()) <= e.get_sight_range() && filter.test(a) && a != e) {
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

	// Ancho/Alto del Mapa
	@Override
	public int get_width() {
		return _width;
	}

	@Override
	public int get_height() {
		return _height;
	}

	// Ancho/Alto de la region
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
		return this._animal_region.get(a).get_food(a, dt);
	}

	protected void set_region(int row, int col, Region r) {
		for (Animal a : this._regions[row][col].getAnimals()) {
			r.add_animal(a);
			this._animal_region.replace(a, r);
		}
		this._regions[row][col] = r;
	}

	protected void update_all_regions(double dt) {
		for (int i = 0; i < this._rows; i++) {
			for (int j = 0; j < this._cols; j++) {
				this._regions[i][j].update(dt);
			}
		}
	}

	public JSONObject as_JSON() {
		JSONArray regionesArray = new JSONArray(); // Creamos el arreglo para las regiones

		for (int i = 0; i < this._rows; ++i) {
			for (int j = 0; j < this._cols; ++j) {
				Region r = this._regions[i][j];
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

	protected void set_regions(Region[][] _regions) {
		this._regions = _regions;
	}
	
	@Override
	public Iterator<RegionData> iterator() {
		return iterator();
	}
}
