package simulator.view;

import simulator.control.*;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JFrame;
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
	// TODO crear ControlPanel y añadirlo en PAGE_START de mainPanel
	ControlPanel controlPanel = new ControlPanel(this._ctrl);
	
	// TODO crear StatusBar y añadirlo en PAGE_END de mainPanel
	StatusBar statusBar = new StatusBar();
	mainPanel.add(statusBar, BorderLayout.PAGE_END);
	
	// Definición del panel de tablas (usa un BoxLayout vertical)
	JPanel contentPanel = new JPanel();
	contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
	mainPanel.add(contentPanel, BorderLayout.CENTER);
	// TODO crear la tabla de especies y añadirla a contentPanel.
	// Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño
	// TODO crear la tabla de regiones.
	// Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño
	// TODO llama a ViewUtils.quit(MainWindow.this) en el método windowClosing
	//addWindowListener( … );
	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	pack();
	setVisible(true);
}
}
