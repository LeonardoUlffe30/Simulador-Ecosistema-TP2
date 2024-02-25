package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectFirst;

public class SelectFirstBuilder extends Builder<SelectFirst> {
	public SelectFirstBuilder() {
		super("first","Constructor de objetos SelectFirst");
	}

	@Override
	protected SelectFirst create_instance(JSONObject data) {
		try {
			return new SelectFirst();
		} catch(Exception e) {
			throw new IllegalArgumentException("Datos invalidos para la creacion del objeto SelectFirst: " + e.getMessage());
		}
	}

	@Override
	protected void fill_in_data(JSONObject o) {
//		o.put(" ", );
	}
}
