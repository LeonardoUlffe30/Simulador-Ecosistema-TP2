package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;

import org.json.JSONObject;
import java.io.BufferedReader;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLEditorKit.Parser;

import simulator.control.*;
import simulator.launcher.Main;

class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Controller _ctrl;
	private ChangeRegionsDialog _changeRegionsDialog;
	private JToolBar _toolBar;
	private JFileChooser _fc;
	private boolean _stopped = true; // utilizado en los botones de run/stop
	private JButton _quitButton; //stop.png
	private JButton _mapButton; //viewer.png
	private JButton _regionsButton; //regions.png
	private JButton _runButton; //run.png
	private JButton _stopButton; //exit.png
	private JButton _openButton; //open.png
	private JLabel _stepsLabel;
	private JSpinner _stepsSpinner;
	private JLabel _dtLabel;
	private JTextField _dtTextField;
	
	ControlPanel(Controller ctrl) {
		this._ctrl = ctrl;
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(new BorderLayout()); //PAGE START, PAGE END, LINE START y LINE END
		this._toolBar = new JToolBar();
		this.add(this._toolBar, BorderLayout.PAGE_START); 		 
		
		this._openButton = new JButton();
		this._mapButton = new JButton();
		this._regionsButton = new JButton();
		this._runButton = new JButton();
		this._stopButton = new JButton();
		
		this._stepsLabel = new JLabel("  Steps:  ");
		this._stepsSpinner = new JSpinner();
		this._dtTextField = new JTextField();
		this._dtLabel = new JLabel("  Delta-Time:  ");
		
		this._toolBar.add(this._openButton);
		this._toolBar.addSeparator();			
		this._toolBar.add(this._mapButton);
		this._toolBar.add(this._regionsButton);
		this._toolBar.addSeparator();
		this._toolBar.add(this._runButton);
		this._toolBar.add(this._stopButton);
		this._toolBar.add(this._stepsLabel);
		this._toolBar.add(this._stepsSpinner);
		this._toolBar.add(this._dtLabel);
		this._toolBar.add(this._dtTextField);
		this._toolBar.add(Box.createGlue());
		this._toolBar.addSeparator();
		
		// Boton para crear MapWindow
		this._mapButton.setToolTipText("Map Viewer");
		this._mapButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
		this._mapButton.addActionListener((e) -> new MapWindow(ViewUtils.getWindow(this), _ctrl));
		
		// Boton para abrir el ChangeRegionsDialog
		this._regionsButton.setToolTipText("Change Regions");
		this._regionsButton.setIcon(new ImageIcon("resources/icons/regions.png"));
		this._regionsButton.addActionListener((e) -> this._changeRegionsDialog.open(ViewUtils.getWindow(this)));
		
		// Boton para iniciar simulacion
		this._runButton.setToolTipText("Run the simulator");
		this._runButton.setIcon(new ImageIcon("resources/icons/run.png"));
		this._runButton.addActionListener((e) -> {
			this._stopped = false;
			this._mapButton.setEnabled(false);
			this._regionsButton.setEnabled(false);
			this._runButton.setEnabled(false);
			this.run_sim((int) this._stepsSpinner.getValue(), Double.parseDouble(this._dtTextField.getText()));
		});
		
		// Boton para detener simulacion
		this._stopButton.setToolTipText("Stop the simulator");
		this._stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
		this._stopButton.addActionListener((e) -> this._stopped = true);
				
		// JSpinner Steps
		this._stepsSpinner.setToolTipText("Simulation steps to run: 1 - 10000");
		this._stepsSpinner.setPreferredSize(new Dimension(80, 40));
		this._stepsSpinner.setMaximumSize(new Dimension(70, 40));
		this._stepsSpinner.setMinimumSize(new Dimension(70, 40));
		
		// JTextFied dt
		this._dtTextField.setToolTipText("Real time (seconds) corresponding to a step");
		this._dtTextField.setText(Main._dt.toString());
		this._dtTextField.setPreferredSize(new Dimension(70,40));
		this._dtTextField.setMaximumSize(new Dimension(70,40));
		this._dtTextField.setMinimumSize(new Dimension(70,40));
		
		// Quit Button
		this._toolBar.add(Box.createGlue()); // this aligns the button to the right
		this._toolBar.addSeparator();
		this._quitButton = new JButton();
		this._quitButton.setToolTipText("Quit");
		this._quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		this._quitButton.addActionListener((e) -> ViewUtils.quit(this));
		this._toolBar.add(this._quitButton);
		
		// Inicializar _fc con una instancia de JFileChooser. Para que siempre
		// abre en la carpeta de ejemplos puedes usar:
		this._openButton.setIcon(new ImageIcon("resources/icons/open.png"));
		this._openButton.setToolTipText("Load an input file into the simulator");
		this._openButton.addActionListener((e)-> {
			int returnVal = _fc.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = _fc.getSelectedFile();
				System.out.println("Opening: " + file.getName());
				JSONObject obj = null;
				try {
					obj = this.readJSONObjectFromFile(file);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				this._ctrl.reset(obj.getInt("cols"), obj.getInt("rows"), obj.getInt("width"), obj.getInt("height"));
				this._ctrl.load_data(obj);
			} else {
				System.out.println("load cancelled by user.");
			}
		});
		
		this._fc = new JFileChooser();
		this._fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
		
		// Inicializar _changeRegionsDialog con instancias del diálogo de cambio de regiones
		this._changeRegionsDialog = new ChangeRegionsDialog(this._ctrl);
	}

	private void run_sim(int n, double dt) {
		if (n > 0 && !this._stopped) {
			try {
				this._ctrl.advance(dt);
				SwingUtilities.invokeLater(() -> run_sim(n - 1, dt));
			} catch (Exception e) {
				// Llamar a ViewUtils.showErrorMsg con el mensaje de error
				// que corresponda
				ViewUtils.showErrorMsg(e.getMessage());
				
				// Activar todos los botones
				this._stopped = true;
				this._mapButton.setEnabled(true);
				this._regionsButton.setEnabled(true);
				this._runButton.setEnabled(true);
			}
		} else {
			// Activar todos los botones
			this._stopped = true;
			this._mapButton.setEnabled(true);
			this._regionsButton.setEnabled(true);
			this._runButton.setEnabled(true);
		}
	}	
	
	private JSONObject readJSONObjectFromFile(File file) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return new JSONObject(sb.toString());
    }
}
