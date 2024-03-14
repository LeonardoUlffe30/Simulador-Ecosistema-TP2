package simulator.factories;

import org.json.JSONObject;

import simulator.model.DefaultRegion;
import simulator.model.Region;

public class DefaultRegionBuilder extends Builder<Region> {
	public DefaultRegionBuilder() {
		super("default", "Infinite food supply");
	}

	@Override
	protected DefaultRegion create_instance(JSONObject data) {
		try {
			return new DefaultRegion();
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Datos invalidos para la creacion del objeto DefaultRegion: " + e.getMessage());
		}
	}

	@Override
	protected void fill_in_data(JSONObject o) {
		// Vacio, la clase DefaultRegion tiene constructor por defecto (sin parametros)
	}
}
