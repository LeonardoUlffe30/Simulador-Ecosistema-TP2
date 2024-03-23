package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectClosest;
import simulator.model.SelectionStrategy;

public class SelectClosestBuilder extends Builder<SelectionStrategy> {
	public SelectClosestBuilder() {
		super("closest", "Constructor de objetos SelectClosest");
	}

	@Override
	protected SelectClosest create_instance(JSONObject data) {
			return new SelectClosest();

	}

	@Override
	protected void fill_in_data(JSONObject o) {
		// Vacio, la clase SelectClosest tiene constructor por defecto (sin parametros)
	}

}
