package simulator.view;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JToolBar;

import simulator.control.*;

public class ControlPanel {
	private Controller _ctrl;
	private ChangeRegionsDialog _changeRegionsDialog;
	private JToolBar _toolaBar;
	private JFileChooser _fc;
	private boolean _stopped = true; // utilizado en los botones de run/stop
	private JButton _quitButton;
	// TODO a�ade m�s atributos aqu� �
	ControlPanel(Controller ctrl) {
	_ctrl = ctrl;
	initGUI();
	}
	private void initGUI() {
	setLayout(new BorderLayout());
	_toolaBar = new JToolBar();
	add(_toolaBar, BorderLayout.PAGE_START);
	// TODO crear los diferentes botones/atributos y a�adirlos a _toolaBar.
	// Todos ellos han de tener su correspondiente tooltip. Puedes utilizar
	// _toolaBar.addSeparator() para a�adir la l�nea de separaci�n vertical
	// entre las componentes que lo necesiten.
	// Quit Button
	_toolaBar.add(Box.createGlue()); // this aligns the button to the right
	_toolaBar.addSeparator();
	_quitButton = new JButton();
	_quitButton.setToolTipText("Quit");
	_quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
	_quitButton.addActionListener((e) -> Utils.quit(this));
	_toolaBar.add(_quitButton);
	// TODO Inicializar _fc con una instancia de JFileChooser. Para que siempre
	// abre en la carpeta de ejemplos puedes usar:
	//
	// _fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
	// TODO Inicializar _changeRegionsDialog con instancias del di�logo de cambio
	// de regiones
	}
	// TODO el resto de m�todos van aqu�
}
