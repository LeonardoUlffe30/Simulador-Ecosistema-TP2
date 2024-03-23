package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectYoungest;
import simulator.model.SelectionStrategy;

public class SelectYoungestBuilder extends Builder<SelectionStrategy> {
	public SelectYoungestBuilder() {
		super("youngest", "Constructor de objetos SelectYoungest");
	}

	@Override
	protected SelectYoungest create_instance(JSONObject data) {
			return new SelectYoungest();

	}

	@Override
	protected void fill_in_data(JSONObject o) {
		// Vacio, la clase SelectYoungest tiene constructor por defecto (sin parametros)
	}
}
