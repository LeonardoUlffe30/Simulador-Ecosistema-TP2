package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {
	public DynamicSupplyRegionBuilder() {
		super("dynamic", "Constructor de objetos DynamicSupplyRegion");
	}

	@Override
	protected DynamicSupplyRegion create_instance(JSONObject data) {
		try {
			if (data.length() == 0)
				this.fill_in_data(data);

			double factor, food;
			if (data.has("factor"))
				factor = data.getDouble("factor");
			else
				factor = 2.0;

			if (data.has("food"))
				food = data.getDouble("food");
			else
				food = 1000.0;
			return new DynamicSupplyRegion(factor, food);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Datos invalidos para la creacion del objeto DynamicSupplyRegion: " + e.getMessage());
		}
	}

	@Override
	protected void fill_in_data(JSONObject o) {
		o.put("factor", new JSONObject());
		o.put("food", new JSONObject());
	}
}
