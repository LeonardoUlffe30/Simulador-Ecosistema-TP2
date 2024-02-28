package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import java.util.List;
import java.util.ArrayList;
import simulator.factories.*;
import simulator.model.Animal;
import simulator.model.Region;
import simulator.model.SelectionStrategy;
import simulator.model.Simulator;
import simulator.control.Controler;

import simulator.misc.Utils;

public class Main {

	private enum ExecMode {
		BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

		private String _tag;
		private String _desc;

		private ExecMode(String modeTag, String modeDesc) {
			_tag = modeTag;
			_desc = modeDesc;
		}

		public String get_tag() {
			return _tag;
		}

		public String get_desc() {
			return _desc;
		}
	}

	// default values for some parameters
	//
	private final static Double _default_time = 10.0; // in seconds
	private final static Double _default_dt = 0.03;
	private final static String _default_output = "output.json";

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double _time = null;
	private static Double _dt = null;
	private static String _in_file = null;
	private static String _out_file = null;
	private static boolean _sv = false;
	private static ExecMode _mode = ExecMode.BATCH;
	
	private static Factory<SelectionStrategy> selection_strategy_factory = null;
	private static Factory<Animal> animal_factory = null; // Cambia esto por la fábrica de animales adecuada
    private static Factory<Region> region_factory = null;
	

	private static void parse_args(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = build_options();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parse_help_option(line, cmdLineOptions);
			parse_in_file_option(line);
			parse_time_option(line);
			parse_dt_option(line);
			parse_output_option(line);
			parse_simple_viewer_option(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options build_options() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Initial configuration file.").build());

		// steps
		cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
				.desc("An real number representing the total simulation time in seconds. Default value: "
						+ _default_time + ".")
				.build());
		
		// dt
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
                .desc("A double representing actual time, in seconds, per simulation step. Default value: " + _default_dt + ".").build());
        
		// output file
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Output file, where output is written.").build());
        
		// simple viewer
		cmdLineOptions.addOption(Option.builder("sv").longOpt("simple-viewer").desc("Show the viewer window in console mode.").build());

		return cmdLineOptions;
	}

	private static void parse_help_option(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parse_in_file_option(CommandLine line) throws ParseException {
		_in_file = line.getOptionValue("i");
		if (_mode == ExecMode.BATCH && _in_file == null) {
			throw new ParseException("In batch mode an input configuration file is required");
		}
	}

	private static void parse_time_option(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", _default_time.toString());
		try {
			_time = Double.parseDouble(t);
			assert (_time >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	}
	
	private static void parse_dt_option(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _default_dt.toString());
		try {
			_dt = Double.parseDouble(dt);
			assert (_dt >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for delta-time: " + dt);
		}
	}
	
	private static void parse_output_option(CommandLine line) {
		_out_file = line.getOptionValue("o", _default_output);
	}
	
	private static void parse_simple_viewer_option(CommandLine line) {
		_sv = line.hasOption("sv");
	}
	
	private static void init_factories() {
		//Utilizar la clase BuilderBasedFactory para crear 3 factorías (para las estrategias, para los animales, y para las regiones).
		//Inicializacion de factoria de estrategias
		List<Builder<SelectionStrategy>> selection_strategy_builders = new ArrayList<>(); 
		selection_strategy_builders.add(new SelectFirstBuilder());
		selection_strategy_builders.add(new SelectClosestBuilder());
//		selection_strategy_builders.add(new SelectYoungestBuilder());
		selection_strategy_factory = new BuilderBasedFactory<SelectionStrategy>(selection_strategy_builders);
		
		//Inicializacion de factorias de animales
		List<Builder<Animal>> animal_builders = new ArrayList<>(); 
		animal_builders.add(new SheepBuilder(selection_strategy_factory));
		animal_builders.add(new WolfBuilder(selection_strategy_factory));
		animal_factory = new BuilderBasedFactory<Animal>(animal_builders);
		
		//Inicializacion de factorias de regiones
		List<Builder<Region>> region_builders = new ArrayList<>(); 
		region_builders.add(new DynamicSupplyRegionBuilder());
		region_builders.add(new DefaultRegionBuilder());
		region_factory = new BuilderBasedFactory<Region>(region_builders);
	}

	private static JSONObject load_JSON_file(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}


	private static void start_batch_mode() throws Exception {
//		(1) cargar el archivo de entrada en un JSONObject
		InputStream is = new FileInputStream(new File(_in_file));
		JSONObject inputJSON = load_JSON_file(is);
		is.close();
		
//		(2) crear el archivo de salida
		OutputStream os = new FileOutputStream(new File(_out_file));
		
//		(3) crear una instancia de Simulator pasando a su constructora la información que necesita
	    int cols = inputJSON.getInt("cols");
	    int rows = inputJSON.getInt("rows");
	    int width = inputJSON.getInt("width");
	    int height = inputJSON.getInt("height");
		Simulator sim = new Simulator(cols, rows, width, height, animal_factory, region_factory);
		
//		(4) crear una instancia de Controller pasandole el simulador
		Controler controler = new Controler(sim);
		
//		(5) llamar a load_data pasandole el JSONObject de la entrada
		controler.load_data(inputJSON);
		
//		(6) llamar al método run con los parámetros correspondents
		controler.run(_time, _dt, _sv, os);
		
//		(7) cerrar el archivo de salida
		os.close();
		
	}

	private static void start_GUI_mode() throws Exception {
		throw new UnsupportedOperationException("GUI mode is not ready yet ...");
	}

	private static void start(String[] args) throws Exception {
		init_factories();
		parse_args(args);
		switch (_mode) {
		case BATCH:
			start_batch_mode();
			break;
		case GUI:
			start_GUI_mode();
			break;
		}
	}

	public static void main(String[] args) {
		Utils._rand.setSeed(2147483647l);
		try {
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}
