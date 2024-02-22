package simulator.factories;

import org.json.JSONObject;

import simulator.model.DefaultRegion;

public class DefaultRegionBuilder extends Builder<DefaultRegion> {
	public DefaultRegionBuilder() {
		super("Hola","Hola");
	}

	@Override
	protected DefaultRegion create_instance(JSONObject data) {
		// TODO Auto-generated method stub
		return null;
	}
}
