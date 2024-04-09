package simulator.view;

import simulator.control.*;
import javax.swing.JPanel;
import javax.swing.JTable;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.BoxLayout;

public class MainWindow extends JFrame {
	private Controller _ctrl;

	public MainWindow(Controller ctrl) {
		super("[ECOSYSTEM SIMULATOR]");
		_ctrl = ctrl;
		initGUI();
	}

private void initGUI() {
	JPanel mainPanel = new JPanel(new BorderLayout());
	setContentPane(mainPanel);
	 
	ControlPanel controlPanel = new ControlPanel(this._ctrl); // crear ControlPanel y
	mainPanel.add(controlPanel, BorderLayout.PAGE_START); // añadir en PAGE_START de mainPanel
	
	StatusBar statusBar = new StatusBar(this._ctrl); // crear StatusBar y
	mainPanel.add(statusBar, BorderLayout.PAGE_END); // añadir en PAGE_END de mainPanel
	
	// Definición del panel de tablas (usa un BoxLayout vertical)
	JPanel contentPanel = new JPanel();
	contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
	mainPanel.add(contentPanel, BorderLayout.CENTER);
	 
	// TODO crear la tabla de especies y añadirla a contentPanel.
	// Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño
	InfoTable speciesTable = new InfoTable("Species", new SpeciesTableModel(this._ctrl));
	speciesTable.setPreferredSize(new Dimension(500,250));
	contentPanel.add(speciesTable);
	
	// TODO crear la tabla de regiones.
	// Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño
	InfoTable regionsTable = new InfoTable("Regions", new RegionsTableModel(this._ctrl));
	regionsTable.setPreferredSize(new Dimension(500,250));
	contentPanel.add(regionsTable);
	
	// TODO llama a ViewUtils.quit(MainWindow.this) en el método windowClosing
	addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            ViewUtils.quit(MainWindow.this);
        }
    });
	
	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	pack();
	setVisible(true);
}
}
