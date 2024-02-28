package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;

public class SheepBuilder extends Builder<Animal> {
	Factory<SelectionStrategy> _factory_selection_strategy;
	
	public SheepBuilder(Factory<SelectionStrategy> factory_selection_strategy) {
		super("sheep","Constructor de objetos Sheep");
		this._factory_selection_strategy = factory_selection_strategy;
	}
	
//	{
//		"type": "sheep"
//		"data": {
//			"mate_strategy" : { … }
//			"danger_strategy" : { … }
//			"pos" : {
//				"x_range" : [ 100.0, 200.0 ],
//				"y_range" : [ 100.0, 200.0 ]
//			}
//	}
	@Override
	protected Sheep create_instance(JSONObject data) {
		try {
			if(data.length() == 0)
				this.fill_in_data(data);
			
			SelectionStrategy _mate_strategy;
			JSONObject mateStrategyJSON = data.optJSONObject("mate_strategy");
			if(mateStrategyJSON != null)
				_mate_strategy = _factory_selection_strategy.create_instance(mateStrategyJSON);
			else
				_mate_strategy = new SelectFirst();
			
			SelectionStrategy _danger_strategy;
			JSONObject dangerStrategyJSON = data.optJSONObject("danger_strategy");
			if(dangerStrategyJSON != null)
				_danger_strategy = _factory_selection_strategy.create_instance(dangerStrategyJSON);
			else
				_danger_strategy = new SelectFirst();
			
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

			return new Sheep(_mate_strategy, _danger_strategy,_pos);
		} catch (Exception e) {
			throw new IllegalArgumentException("Datos invalidos para la creacion del objeto Sheep: " + e.getMessage());
		}
	}

	@Override
	protected void fill_in_data(JSONObject o) {
//		o.put("", );
		o.put("mate_strategy",  new JSONObject());
		o.put("danger_strategy",  new JSONObject());
		JSONArray ja = new JSONArray();
		o.put("pos", ja);
	}



}
