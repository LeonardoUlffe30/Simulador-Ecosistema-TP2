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
		double factor, food;
		if (data.has("factor"))
			factor = data.getDouble("factor");
		else 
			factor = 2.0;
		

		if (data.has("food"))
			food = data.getDouble("food");
		else				
			food = 1000.0;
		
		this.fill_in_data(data);
		
		return new DynamicSupplyRegion(factor, food);
	}

	@Override
	protected void fill_in_data(JSONObject o) {
		if(!o.has("factor"))
			o.put("factor", "food increase factor (optional, default 2.0)");
		if(!o.has("food"))
			o.put("food", "initial amount of food (optional, default 1000.0)");
	}
}
