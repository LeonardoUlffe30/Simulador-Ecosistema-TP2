package simulator.control;

import java.io.OutputStream;
import java.io.PrintStream;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.Simulator;

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
				for(int r = row.getInt(0); r < row.getInt(1);++r) {
					for(int c = col.getInt(0); c < col.getInt(1);++c) {
						this.get_sim().set_region(r, c, spec);
					}
				}
			}
		}
		//Ya no verificamos si tiene la clave animals puesto que siempre va a existir segun el enunciado
		JSONArray animals = data.getJSONArray("animales");
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
		}
		
		JSONObject final_state = this.get_sim().as_JSON();
		JSONObject result = new JSONObject();
		result.put("init_state", init_state);
		result.put("final_state", final_state);
		
		PrintStream p = new PrintStream(out);
        p.println(result.toString());; //result.toString para convertir el objeto en string
        p.flush();
        
//      Además si el valor de sv es true, hay que mostrar la simulación usando el visor de objetos (ver el
//      apartado “El Visor de Objetos”).
        if(sv) {
        	
        }

	}

	public Simulator get_sim() {
		return _sim;
	}
}
