package simulator.factories;

import java.util.Collections;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {
	private Map<String, Builder<T>> _builders;
	private List<JSONObject> _builders_info;

	public BuilderBasedFactory() {
// Create a HashMap for _builders, and a LinkedList _builders_info
		this._builders = new HashMap<String, Builder<T>>();
		this._builders_info = new LinkedList<JSONObject>();
	}

	public BuilderBasedFactory(List<Builder<T>> builders) {
		this();
// call add_builder(b) for each builder b in builder
		for (Builder<T> b : builders) {
			this.add_builder(b);
		}
	}

	public void add_builder(Builder<T> b) {
// add an entry “b.getTag() |−> b” to _builders.
		this.get_builders().put(b.getTag(), b);
// add b.get_info() to _buildersInfo
		this.get_builders_info().add(b.get_info());
	}

	@Override
	public T create_instance(JSONObject info) {
		if (info == null) {
			throw new IllegalArgumentException("’info’ cannot be null");
		}
// Look for a builder with a tag equals to info.getString("type"), in the
// map _builder, and call its create_instance method and return the result
// if it is not null. The value you pass to create_instance is the following
// because ‘data’ is optional:
//
// info.has("data") ? info.getJSONObject("data") : new getJSONObject()
		Builder<T> builder = this.get_builders().get(info.getString("type"));
		if (builder != null) {
			return builder.create_instance(info.has("data") ? info.getJSONObject("data") : new JSONObject());
		} else {
// If no builder is found or the result is null ...
			throw new IllegalArgumentException("Unrecognized ‘info’:" + info.toString());
		}
	}

	@Override
	public List<JSONObject> get_info() {
		return Collections.unmodifiableList(_builders_info);
	}

	public Map<String, Builder<T>> get_builders() {
		return _builders;
	}

	public List<JSONObject> get_builders_info() {
		return _builders_info;
	}
}
