package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;

public class DynamicSupplyRegionBuilder extends Builder<DynamicSupplyRegion> {
	public DynamicSupplyRegionBuilder() {
		super("dynamic","Constructor de objetos DynamicSupplyRegion");
	}

//	{
//		"type" : "default",
//		"data" : {
//			"factor" : 2.5,
//			"food" : 1250.0
//		}
//	}

	@Override
	protected DynamicSupplyRegion create_instance(JSONObject data) {
		try {
			double factor, food;
			if(data.has("factor"))
				factor = data.getDouble("factor");
			else
				factor = 2.0;
			
			if(data.has("food"))
				food = data.getDouble("food");
			else
				food = 1000.0;
			return new DynamicSupplyRegion(factor,food);
		} catch(Exception e) {
			throw new IllegalArgumentException("Datos invalidos para la creacion del objeto DynamicSupplyRegion: " + e.getMessage());
		}
	}

	@Override
	protected void fill_in_data(JSONObject o) {
//		o.put("", );
		o.put("factor",  2.5);
		o.put("food", 1250.0);
	}
}
