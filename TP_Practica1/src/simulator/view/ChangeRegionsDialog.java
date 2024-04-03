package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;

import org.json.JSONObject;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class ChangeRegionsDialog extends JDialog implements EcoSysObserver {
	private DefaultComboBoxModel<String> _regionsModel;
	private DefaultComboBoxModel<String> _fromRowModel;
	private DefaultComboBoxModel<String> _toRowModel;
	private DefaultComboBoxModel<String> _fromColModel;
	private DefaultComboBoxModel<String> _toColModel;
	
	private DefaultTableModel _dataTableModel;
	private Controller _ctrl;
	private List<JSONObject> _regionsInfo;
	
	private String[] _headers = { "Key", "Value", "Description" };
	private int _status = 0; //0: cancel, 1:ok

	// TODO en caso de ser necesario, añadir los atributos aquí…
	ChangeRegionsDialog(Controller ctrl) {
		super((Frame) null, true);
		this._ctrl = ctrl;
		initGUI();
		// TODO registrar this como observer;
		this._ctrl.addObserver(this);
	}

	private void initGUI() {
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);
		
		// TODO crea varios paneles para organizar los componentes visuales en el
		// dialogo, y añadelos al mainpanel. P.ej., uno para el texto de ayuda,
		// uno para la tabla, uno para los combobox, y uno para los botones.
		JPanel helpPanel = new JPanel();
		JPanel tablePanel = new JPanel();
		JPanel comboBoxPanel = new JPanel(new GridLayout(4,2));
		JPanel buttonPanel = new JPanel();
		mainPanel.add(helpPanel);
		mainPanel.add(tablePanel);
		mainPanel.add(comboBoxPanel);
		mainPanel.add(buttonPanel);
		
		// TODO crear el texto de ayuda que aparece en la parte superior del diálogo y
		// añadirlo al panel correspondiente diálogo (Ver el apartado Figuras)
		JLabel helpText = new JLabel("Select a region type, the rows/cols interval, and"
				+ "provide values for the parametes in the Value column (default values are "
				+ "usted for parametes with no value).");
		helpPanel.add(helpText);
		
		// _regionsInfo se usará para establecer la información en la tabla
		_regionsInfo = Main._regions_factory.get_info();
		
		// _dataTableModel es un modelo de tabla que incluye todos los parámetros de
		// la region
		_dataTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO hacer editable solo la columna 1
				return column == 1;
			}
		};
		_dataTableModel.setColumnIdentifiers(_headers);
		
		// TODO crear un JTable que use _dataTableModel, y añadirlo al diálogo
		JTable dataTable = new JTable(this._dataTableModel);
		JScrollPane tableScrollPane = new JScrollPane(dataTable);
		tablePanel.add(tableScrollPane);
		
		// _regionsModel es un modelo de combobox que incluye los tipos de regiones
		_regionsModel = new DefaultComboBoxModel<>();
		
		// TODO añadir la descripción de todas las regiones a _regionsModel, para eso
		// usa la clave “desc” o “type” de los JSONObject en _regionsInfo,
		// ya que estos nos dan información sobre lo que puede crear la factoría.
		for(JSONObject regionInfo : this._regionsInfo) {
			String description = regionInfo.optString("desc", regionInfo.optString("type", "Unknown"));
			this._regionsModel.addElement(description);
		}
		
		// TODO crear un combobox que use _regionsModel y añadirlo al diálogo.
		JComboBox<String> regionsComboBox = new JComboBox<>(this._regionsModel);
		regionsComboBox.addActionListener(e -> {
			int selectedIndex = regionsComboBox.getSelectedIndex();
			this.updateDataTableModel(selectedIndex);
		});
		mainPanel.add(regionsComboBox);
		
		// TODO crear 4 modelos de combobox para _fromRowModel, _toRowModel,
		// _fromColModel y _toColModel.
		this._fromRowModel = new DefaultComboBoxModel<>();
		this._toRowModel = new DefaultComboBoxModel<>();
		this._fromColModel = new DefaultComboBoxModel<>();
		this._toColModel = new DefaultComboBoxModel<>();
		
		// TODO crear 4 combobox que usen estos modelos y añadirlos al diálogo.
		comboBoxPanel.add(new JLabel("Row from/to:"));
		comboBoxPanel.add(new JComboBox<>(this._fromRowModel));
		comboBoxPanel.add(new JComboBox<>(this._toRowModel));
		comboBoxPanel.add(new JLabel("Colum from/to:"));
		comboBoxPanel.add(new JComboBox<>(this._fromColModel));
		comboBoxPanel.add(new JComboBox<>(this._toColModel));
		mainPanel.add(comboBoxPanel);
		
		// TODO crear los botones OK y Cancel y añadirlos al diálogo.
		JButton okButton = new JButton("OK");
		okButton.addActionListener(e-> {
			JSONObject regionsJSON = this.createRegionsJSON();
			if(regionsJSON != null) {
				try {
					this._ctrl.set_regions(regionsJSON);
					this._status = 1;
					setVisible(false);
				} catch(Exception ex) {
					ViewUtils.showErrorMsg("Error: " + ex.getMessage());
				}
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> {
			this._status = 0;
			setVisible(false);
		});
		mainPanel.add(okButton);
		mainPanel.add(cancelButton);
		
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

	// TODO el resto de métodos van aquí…
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		this.updateComboBoxModels(map.get_rows(), map.get_cols());
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		this.updateComboBoxModels(map.get_rows(), map.get_cols());
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub
	}
	
	private void updateDataTableModel(int selectedIndex) {
		if(selectedIndex != -1) {
			JSONObject info = this._regionsInfo.get(selectedIndex);
			JSONObject data = info.optJSONObject("data");
			if(data != null) {
				this._dataTableModel.setRowCount(0); // Borrar filas existentes
				for(String key : data.keySet()) {
					String value = data.getString(key);
					this._dataTableModel.addRow(new Object[] {key, " ", value});
				}
			}
		}
	}
	
	private JSONObject createRegionsJSON() {
		int selectedIndex = this._regionsModel.getIndexOf(this._regionsModel.getSelectedItem());
		if(selectedIndex != -1) {
			JSONObject regionInfo = this._regionsInfo.get(selectedIndex);
			JSONObject regionData = new JSONObject();
			for(int i = 0; i < this._dataTableModel.getRowCount(); i++) {
				String key = (String) this._dataTableModel.getValueAt(i, 0);
				String value = (String) this._dataTableModel.getValueAt(i, 1);
				if(!value.isEmpty()) {
					regionData.put(key, value);
				}
			}
			String regionType = regionInfo.optString("type", "Unknown");
			String[] rowBounds = getSelectedRowBounds();
			String[] colBounds = getSelectedColBounds();
			JSONObject region = new JSONObject();
			region.put("row",  new int[] {Integer.parseInt(rowBounds[0]), Integer.parseInt(rowBounds[1])});
			region.put("col",  new int[] {Integer.parseInt(colBounds[0]), Integer.parseInt(colBounds[1])});
			region.put("spec", new JSONObject().put("type", regionType).put("data", regionData));
			return new JSONObject().put("regions", new JSONObject[] {region});
		}
		return null;
	}
	
	private String[] getSelectedRowBounds() {
        String fromRow = (String) _fromRowModel.getSelectedItem();
        String toRow = (String) _toRowModel.getSelectedItem();
        return new String[]{fromRow, toRow};
    }

    private String[] getSelectedColBounds() {
        String fromCol = (String) _fromColModel.getSelectedItem();
        String toCol = (String) _toColModel.getSelectedItem();
        return new String[]{fromCol, toCol};
    }
	
	private void updateComboBoxModels(int rows, int cols) {
		this._fromRowModel.removeAllElements();
		this._toRowModel.removeAllElements();
		this._fromColModel.removeAllElements();
		this._toColModel.removeAllElements();
		for(int i = 0; i < rows; i++) {
			this._fromRowModel.addElement(String.valueOf(i));
			this._toRowModel.addElement(String.valueOf(i));
		}
		
		for(int j = 0; j < rows; j++) {
			this._fromColModel.addElement(String.valueOf(j));
			this._toColModel.addElement(String.valueOf(j));
		}
	}
}
