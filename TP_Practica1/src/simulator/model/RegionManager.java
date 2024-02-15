package simulator.model;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class RegionManager implements AnimalMapView {
	private int _cols;
	private int _rows;
	private int _width;
	private int _height;
	private int _width_region;
	private int _height_region;
	private DefaultRegion [][]_regions;
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
		return 0;
	}

	@Override
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter) {
		List<Animal> animals_filtered_and_sight;
		//if( && filter.test(e))
		return null;
	}

}
