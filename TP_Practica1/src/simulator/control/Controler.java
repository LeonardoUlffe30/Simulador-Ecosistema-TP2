package simulator.control;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class Controler {
//	Tiene que tener un atributo (_sim) para la instancia de Simulator.
	Simulator _sim;
	
//	La única constructora recibe como parámetro un objeto del tipo Simulator y lo almacena en el atributo
//	correspondiente.
	public Controler(Simulator sim) {
		this._sim = sim;
	}
	
//	public void load_data(JSONObject data): asumimos que data tiene las dos claves
//	“animals” y “regions”, siendo este último opcional. Los valores de estas claves son de tipo
//	JSONArray (lista) y cada elemento de la lista es un JSONObject que corresponde a una especificación
//	de animales o regiones. Para cada elemento hay que hacer lo siguiente (es muy importante añadir las
//	regiones antes de añadir los animales):
	public void load_data(JSONObject data) {
		
		if(data.has("regiones")) { //si tiene la clave regiones porque puede ser opcional
			JSONArray regions = data.getJSONArray("regiones");
//			Iterar sobre cada especificación de la region
			for(int i = 0; i < regions.length();++i) {
//				{“row”: [rf,rt], “col”: [cf,ct], “spec”: O}				
				JSONObject region = regions.getJSONObject(i);
				JSONArray row = region.getJSONArray("row");
				JSONArray col = region.getJSONArray("col");
				JSONObject spec = region.getJSONObject("spec");
//				Hay que llamar a _sim.set_region(R,C,O) para cada rf≤R≤rt y
//				cf≤C≤ct (es decir usando un bucle anidado para modificar varias regiones).
				for(int r = row.getInt(0); r <= row.getInt(1);++r) {
					for(int c = col.getInt(0); c <= col.getInt(1);++c) {
						this.get_sim().set_region(r, c, spec);
					}
				}
			}
		}
		//Ya no verificamos si tiene la clave animals puesto que siempre va a existir segun el enunciado
		JSONArray animals = data.getJSONArray("animals");
//		Iterar sobre cada especificacion del animal
		for(int i = 0; i < animals.length();++i) { 
//			{“amount: N, “spec”: O}
			JSONObject animal = animals.getJSONObject(i); 
			int amount = animal.getInt("amount");
			JSONObject spec = animal.getJSONObject("spec");
			for(int j = 0; j < amount; ++j) {
				this.get_sim().add_animal(spec);
			}
		}
	}
	
//	Método para ejecutar el simulador por un tiempo determinado y escribir los estados inicial 
//	y final en un OutputStream
	public void run(double t, double dt, boolean sv, OutputStream out) {
		SimpleObjectViewer view = null;  
		if (sv) {
			MapInfo m = this.get_sim().get_map_info();
			view = new SimpleObjectViewer("[ECOSYSTEM]", m.get_width(), m.get_height(),	m.get_cols(), m.get_rows());
			view.update(to_animals_info(this.get_sim().get_animals()), this.get_sim().get_time(), dt);
		}
//		Además, tiene que escribir en out una estructura JSON de la siguiente forma:
//		{
//		"in": init_state,
//		"out": final_state
//		}
//		Donde init_state es el resultado que devuelve _sim.as_JSON() antes de entrar en el bucle, y
//		final_state es el resultado que devuelve _sim.as_JSON() al salir del bucle.
		JSONObject init_state = this.get_sim().as_JSON();
		
		while(this.get_sim().get_time()<=t) {
			this.get_sim().advance(dt);
			if (sv) 
				view.update(to_animals_info(this.get_sim().get_animals()), this.get_sim().get_time(), dt);
		}
		
		JSONObject final_state = this.get_sim().as_JSON();
		JSONObject result = new JSONObject();
		result.put("in", init_state);
		result.put("out", final_state);
		
		PrintStream p = new PrintStream(out);
		try {
			p.write(result.toString(2).getBytes());
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
        p.println(); //result.toString para convertir el objeto en string
        p.flush();
        
        if(sv)
        	view.close();
	}
	
	private List<ObjInfo> to_animals_info(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
			ol.add(new ObjInfo(a.get_genetic_code(), 
					(int) a.get_position().getX(),	
					(int) a.get_position().getY(),
					(int)Math.round(a.get_age())+2));
		return ol;
	}

	public Simulator get_sim() {
		return _sim;
	}
}
