package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {
	public DynamicSupplyRegionBuilder() {
		super("dynamic", "Dynamic food supply");
	}

	@Override
	protected DynamicSupplyRegion create_instance(JSONObject data) {
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

	}

	@Override
	protected void fill_in_data(JSONObject o) {
		o.put("factor", new JSONObject());
		o.put("food", new JSONObject());
	}
}
