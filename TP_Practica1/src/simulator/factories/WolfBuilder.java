package simulator.factories;

import org.json.JSONObject;

import simulator.model.Wolf;

public class WolfBuilder extends Builder<Wolf> {
	public WolfBuilder() {
		super("Hola","Hola");
	}

	@Override
	protected Wolf create_instance(JSONObject data) {
		// TODO Auto-generated method stub
		return null;
	}
}
