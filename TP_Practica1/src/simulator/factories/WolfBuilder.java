package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;

public class WolfBuilder extends Builder<Animal> {
	Factory<SelectionStrategy> _factory_selection_strategy;

	public WolfBuilder(Factory<SelectionStrategy> factory_selection_strategy) {
		super("wolf", "Constructor de objetos Wolf");
		this._factory_selection_strategy = factory_selection_strategy;
	}

	@Override
	protected Wolf create_instance(JSONObject data) {
			this.fill_in_data(data);

			SelectionStrategy _mate_strategy;
			JSONObject mateStrategyJSON = data.optJSONObject("mate_strategy");
			if (mateStrategyJSON.length() > 0)
				_mate_strategy = _factory_selection_strategy.create_instance(mateStrategyJSON);
			else
				_mate_strategy = new SelectFirst();

			SelectionStrategy _hunt_strategy;
			JSONObject dangerStrategyJSON = data.optJSONObject("hunt_strategy");
			if (dangerStrategyJSON.length() > 0)
				_hunt_strategy = _factory_selection_strategy.create_instance(dangerStrategyJSON);
			else
				_hunt_strategy = new SelectFirst();

			Vector2D _pos;
			JSONObject posJSON = data.optJSONObject("pos");
			if (posJSON != null) {
				JSONArray xRange = posJSON.getJSONArray("x_range");
				JSONArray yRange = posJSON.getJSONArray("y_range");
				double minX = xRange.getDouble(0);
				double maxX = xRange.getDouble(1);
				double minY = yRange.getDouble(0);
				double maxY = yRange.getDouble(1);
				double x = Utils._rand.nextDouble(minX, maxX);
				double y = Utils._rand.nextDouble(minY, maxY);
				_pos = new Vector2D(x, y);
			} else {
				_pos = null;
			}

			return new Wolf(_mate_strategy, _hunt_strategy, _pos);
	}

	@Override
	protected void fill_in_data(JSONObject o) {
		if (!o.has("mate_strategy"))
			o.put("mate_strategy", new JSONObject());

		if (!o.has("hunt_strategy"))
			o.put("hunt_strategy", new JSONObject());

		if (!o.has("pos"))
			o.put("pos", new JSONArray());
	}
}
