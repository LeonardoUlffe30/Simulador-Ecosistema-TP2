package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class StatsDialog extends JDialog implements EcoSysObserver {
	private static final long serialVersionUID = 1L;
	
	private DefaultTableModel _dataTableModel;
	private JSpinner _lastStepsSpinner;
	private JButton _resetButton;
	private JButton _cancelButton;
	private int _totalSteps;
	private Controller _ctrl;
	private int _counter;
	
	private String[] _headers = { "Time", "Id", "Max Speed" }; // "Region Row", "Region Col"
	
	StatsDialog(Controller ctrl, int totalSteps) {
		super((Frame) null, true);
		this._ctrl = ctrl;
		initGUI();
		this._ctrl.addObserver(this);
		this._totalSteps = totalSteps;
		System.out.println("Total Steps " + totalSteps);
		this._counter = 0;
	}

	private void initGUI() {
		setTitle("Max Speed Statistics");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);
		
		// Crea paneles para organizar los componentes en el dialogo, y añadir al mainpanel.
		JPanel tablePanel = new JPanel( new BorderLayout());
		JPanel buttonPanel = new JPanel();
		mainPanel.add(tablePanel);
		mainPanel.add(buttonPanel);
		
	
		// _dataTableModel es un modelo de tabla que incluye todos los parámetros de
		// la region
		_dataTableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}
		};
		_dataTableModel.setColumnIdentifiers(_headers);
		
		// Crear un JTable que use _dataTableModel, y añadir al diálogo
		JTable dataTable = new JTable(this._dataTableModel);
		TableColumn descColumn = dataTable.getColumnModel().getColumn(2);
		descColumn.setPreferredWidth(300); 

		JScrollPane tableScrollPane = new JScrollPane(dataTable);
		tableScrollPane.setPreferredSize(new Dimension(550,200));
		tablePanel.add(tableScrollPane);
		
		this._lastStepsSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 9999999, 1));
		this._resetButton = new JButton("Reset");
		this._cancelButton = new JButton("Cancel");
		this._resetButton.addActionListener(e -> {
			this._dataTableModel.setRowCount(0);
			this._counter = 0;
		});
		
		
		_cancelButton.addActionListener(e -> {
			setVisible(false);
		});
		
		buttonPanel.add(_lastStepsSpinner);
		buttonPanel.add(_resetButton);
		buttonPanel.add(_cancelButton);
		
		setPreferredSize(new Dimension(700, 400)); // puedes usar otro tamaño
		pack();
		setResizable(false);
		setVisible(false);
	}

	public void open(Frame parent) {
		setLocation(//
				parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2, //
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		System.out.println("El tiempo en on register es " + time);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {

	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		this._counter++;
		System.out.println("Last steps " + (int) this._lastStepsSpinner.getValue());
		System.out.println("Counter " + _counter);
		if(this._counter >= this._totalSteps - (int) this._lastStepsSpinner.getValue()) {
			this.updateDataTableModel(animals, time);
		}
	}
	
	private void updateDataTableModel(List<AnimalInfo> animals, double time) {
		Optional<AnimalInfo> _fastestAnimal = animals.stream().max((a1,a2)-> {
			if(a1.get_age()<a2.get_age()) return 1;
			else if (a1.get_age() == a2.get_age()) return 0;
			else return -1;
		});
		
		this._dataTableModel.addRow(new Object[] { String.format("%.3f", time), _fastestAnimal.get().hashCode(), _fastestAnimal.get().get_speed()});
		this._dataTableModel.fireTableDataChanged();
	}
	
	public String getJSON() {
		StringBuilder s = new StringBuilder();
		s.append('{');
		for (int i = 0; i < _dataTableModel.getRowCount(); i++) {
			String k = _dataTableModel.getValueAt(i, 0).toString();
			String v = _dataTableModel.getValueAt(i, 1).toString();
			if (!v.isEmpty()) {
				s.append('"');
				s.append(k);
				s.append('"');
				s.append(':');
				s.append(v);
				s.append(',');
			}
		}

		if (s.length() > 1)
			s.deleteCharAt(s.length() - 1);
		s.append('}');

		return s.toString();
	}

}
