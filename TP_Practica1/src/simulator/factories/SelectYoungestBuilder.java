package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectYoungest;

public class SelectYoungestBuilder extends Builder<SelectYoungest>{
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
}
