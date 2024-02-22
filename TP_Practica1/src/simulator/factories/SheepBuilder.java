package simulator.factories;

import org.json.JSONObject;

import simulator.model.Sheep;

public class SheepBuilder extends Builder<Sheep> {
	public SheepBuilder() {
		super("Hola","Hola");
	}

	@Override
	protected Sheep create_instance(JSONObject data) {
		// TODO Auto-generated method stub
		return null;
	}
}
