package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectClosest;
import simulator.model.SelectionStrategy;

public class SelectClosestBuilder extends Builder<SelectionStrategy> {
	public SelectClosestBuilder() {
		super("closest","Constructor de objetos SelectClosest");
	}

	@Override
	protected SelectClosest create_instance(JSONObject data) {
		try {
			return new SelectClosest();
		} catch(Exception e) {
			throw new IllegalArgumentException("Datos invalidos para la creacion del objeto SelectClosest: " + e.getMessage());
		}
	}

	@Override
	protected void fill_in_data(JSONObject o) {
		//Vacio, la clase SelectClosest tiene constructor por defecto (sin parametros)
	}

}
