package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectYoungest;
import simulator.model.SelectionStrategy;

public class SelectYoungestBuilder extends Builder<SelectionStrategy>{
	public SelectYoungestBuilder() {
		super("youngest","Constructor de objetos SelectYoungest");
	}

	@Override
	protected SelectYoungest create_instance(JSONObject data) {
		try {
			return new SelectYoungest();
		} catch(Exception e) {
			throw new IllegalArgumentException("Datos invalidos para la creacion del objeto SelectYoungest: " + e.getMessage());
		}
	}

	@Override
	protected void fill_in_data(JSONObject o) {
//		o.put("", );
		//VACIO
	}
}
