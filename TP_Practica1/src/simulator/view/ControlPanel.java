package simulator.view;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import simulator.control.*;

class ControlPanel extends JPanel {
	private Controller _ctrl;
	private ChangeRegionsDialog _changeRegionsDialog;
	private JToolBar _toolBar;
	private JFileChooser _fc;
	private boolean _stopped = true; // utilizado en los botones de run/stop
	private JButton _quitButton;
	// TODO añade más atributos aquí …
	private JButton _mapButton;
	private JButton _regionsButton;
	private JButton _runButton;
	private JButton _stopButton;
	private JSpinner _pasosSpinner;
	private JTextField _dtTextField;
	
	ControlPanel(Controller ctrl) {
		this._ctrl = ctrl;
		initGUI();
	}
	
	private void initGUI() {
		setLayout(new BorderLayout());
		this._toolBar = new JToolBar();
		add(_toolBar, BorderLayout.PAGE_START);
		
		// TODO crear los diferentes botones/atributos y añadirlos a _toolBar.
		// Todos ellos han de tener su correspondiente tooltip. Puedes utilizar
		// _toolBar.addSeparator() para añadir la línea de separación vertical
		// entre las componentes que lo necesiten.
		this._mapButton = new JButton();
		this._regionsButton = new JButton();
		this._runButton = new JButton();
		this._stopButton = new JButton();
		this._toolBar.add(this._mapButton);
		this._toolBar.add(this._regionsButton);
		this._toolBar.add(this._runButton);
		this._toolBar.add(this._stopButton);
		
		// Boton para crear MapWindow
		this._mapButton.setToolTipText("Map Window");
		this._mapButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
		this._mapButton.addActionListener((e) -> new MapViewer());
		this._toolBar.add(this._mapButton);
		
		// Boton para abrir el ChangeRegionsDialog
		this._regionsButton.setToolTipText("Change regions");
		this._regionsButton.setIcon(new ImageIcon("resources/icons/regions.png"));
		this._regionsButton.addActionListener((e) -> this._changeRegionsDialog.open(ViewUtils.getWindow(this)));
		this._toolBar.add(this._regionsButton);
		
		// Boton para iniciar simulacion
		this._runButton.setToolTipText("Run simulation");
		this._runButton.setIcon(new ImageIcon("resources/icons/run.png"));
		this._runButton.addActionListener((e) -> {
			this._stopped = false;
			this._mapButton.setEnabled(false);
			this._regionsButton.setEnabled(false);
			this._runButton.setEnabled(false);
			//this.run_sim(UNDEFINED_CONDITION, WIDTH);
		});
		this._toolBar.add(this._runButton);
		
		// Boton para detener simulacion
		this._stopButton.setToolTipText("Stop");
		this._stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
		this._stopButton.addActionListener((e) -> this._stopped = true);
		this._toolBar.add(this._stopButton);
		
		// Quit Button
		this._toolBar.add(Box.createGlue()); // this aligns the button to the right
		this._toolBar.addSeparator();
		this._quitButton = new JButton();
		this._quitButton.setToolTipText("Quit");
		this._quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		this._quitButton.addActionListener((e) -> ViewUtils.quit(this));
		this._toolBar.add(this._quitButton);
		
		// TODO Inicializar _fc con una instancia de JFileChooser. Para que siempre
		// abre en la carpeta de ejemplos puedes usar:
		this._fc = new JFileChooser();
		this._fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
		
		// TODO Inicializar _changeRegionsDialog con instancias del diálogo de cambio de regiones
		this._changeRegionsDialog = new ChangeRegionsDialog(this._ctrl);
	}
	// TODO el resto de métodos van aquí…
	private void run_sim(int n, double dt) {
		if (n > 0 && !this._stopped) {
			try {
				this._ctrl.advance(dt);
				SwingUtilities.invokeLater(() -> run_sim(n - 1, dt));
			} catch (Exception e) {
				// TODO llamar a ViewUtils.showErrorMsg con el mensaje de error
				// que corresponda
				ViewUtils.showErrorMsg(e.getMessage());
				
				// TODO activar todos los botones
				this._stopped = true;
				this._mapButton.setEnabled(true);
				this._regionsButton.setEnabled(true);
				this._runButton.setEnabled(true);
			}
		} else {
			// TODO activar todos los botones
			this._stopped = true;
			this._mapButton.setEnabled(true);
			this._regionsButton.setEnabled(true);
			this._runButton.setEnabled(true);
		}
	}
}
