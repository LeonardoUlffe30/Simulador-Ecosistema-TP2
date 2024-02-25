package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;

public class WolfBuilder extends Builder<Wolf> {
	Factory<SelectionStrategy> _factory_selection_strategy;
	
	public WolfBuilder(Factory<SelectionStrategy> factory_selection_strategy) {
		super("wolf","Constructor de objetos Wolf");
		this._factory_selection_strategy = factory_selection_strategy;
	}

//	{
//		"type": "wolf"
//		"data": {
//			"mate_strategy" : { … }
//			"hunt_strategy" : { … }
//			"pos" : {
//				"x_range" : [ 100.0, 200.0 ],
//				"y_range" : [ 100.0, 200.0 ]
//			}
//		}
//	}

	@Override
	protected Wolf create_instance(JSONObject data) {
		try {
			SelectionStrategy _mate_strategy;
			JSONObject mateStrategyJSON = data.optJSONObject("mate_strategy");
			if(mateStrategyJSON != null)
				_mate_strategy = _factory_selection_strategy.create_instance(mateStrategyJSON);
			else
				_mate_strategy = new SelectFirst();
			
			SelectionStrategy _hunt_strategy;
			JSONObject dangerStrategyJSON = data.optJSONObject("hunt_strategy");
			if(mateStrategyJSON != null)
				_hunt_strategy = _factory_selection_strategy.create_instance(dangerStrategyJSON);
			else
				_hunt_strategy = new SelectFirst();
			
			Vector2D _pos;
			JSONObject posJSON = data.optJSONObject("pos");
			if(posJSON != null) {
				JSONArray xRange = posJSON.getJSONArray("x_range");
				JSONArray yRange = posJSON.getJSONArray("y_range");
				double minX = xRange.getDouble(0);
				double maxX = xRange.getDouble(1);
				double minY = xRange.getDouble(0);
				double maxY = xRange.getDouble(1);
				double x = Utils.get_randomized_parameter(minX, minX);
				double y = Utils.get_randomized_parameter(minY, minY);
				_pos = new Vector2D(x,y);
			} else {
				_pos = null;
			}
			
			return new Wolf(_mate_strategy, _hunt_strategy,_pos);
		} catch (Exception e) {
			throw new IllegalArgumentException("Datos invalidos para la creacion del objeto Wolf: " + e.getMessage());
		}
	}

	@Override
	protected void fill_in_data(JSONObject o) {
//		o.put("", );
		o.put("mate_strategy",  new JSONObject());
		o.put("hunt_strategy",  new JSONObject());
		JSONArray ja = new JSONArray();
		o.put("pos", ja);
	}
}
