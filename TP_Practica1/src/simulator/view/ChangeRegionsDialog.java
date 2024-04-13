package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
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
import javax.swing.table.TableColumn;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.launcher.Main;
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
		
		// Crea paneles para organizar los componentes en el dialogo, y añadir al mainpanel.
		JPanel helpPanel = new JPanel();
		JPanel tablePanel = new JPanel();
		JPanel comboBoxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10,5));
		JPanel buttonPanel = new JPanel();
		mainPanel.add(helpPanel);
		mainPanel.add(tablePanel);
		mainPanel.add(comboBoxPanel);
		mainPanel.add(buttonPanel);
		
		// Crea texto de ayuda del diálogo y añadir a su panel correspondiente
		JLabel helpText = new JLabel("<html><p>Select a region type, the rows/cols interval, and provide values for the parametes in the <b>Value column</b> (default values are used for parametes with no value).</p></html>");
		helpText.setFont(new Font("Arial", Font.PLAIN, 12));
		helpText.setPreferredSize(new Dimension(650, 30));
		helpPanel.add(helpText);
		
		// _regionsInfo se usa para establecer la información en la tabla
		_regionsInfo = Main._regions_factory.get_info();
	
		// _dataTableModel es un modelo de tabla que incluye todos los parámetros de
		// la region
		_dataTableModel = new DefaultTableModel() {
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
		
		// Combobox de tipos de regiones
		_regionsModel = new DefaultComboBoxModel<>();
		
		// Añadir la descripción de todas las regiones a _regionsModel, para eso
		// usa la clave “desc” o “type” de los JSONObject en _regionsInfo,
		// ya que estos nos dan información sobre lo que puede crear la factoría.
		for(JSONObject regionInfo : this._regionsInfo) {
			String description = regionInfo.getString("type");
			this._regionsModel.addElement(description);
		}
		
		// Crea un combobox que usa _regionsModel y añadir al diálogo.
		JComboBox<String> regionsComboBox = new JComboBox<>(this._regionsModel);
		regionsComboBox.addActionListener(e -> {
			int selectedIndex = regionsComboBox.getSelectedIndex();
			this.updateDataTableModel(selectedIndex);
		});
		comboBoxPanel.add(new JLabel("Region"));
		comboBoxPanel.add(regionsComboBox);
		
		// Combobox para _fromRowModel, _toRowModel, _fromColModel y _toColModel.
		this._fromRowModel = new DefaultComboBoxModel<>();
		this._toRowModel = new DefaultComboBoxModel<>();
		this._fromColModel = new DefaultComboBoxModel<>();
		this._toColModel = new DefaultComboBoxModel<>();
		
		comboBoxPanel.add(new JLabel("Row from/to:"));
		comboBoxPanel.add(new JComboBox<>(this._fromRowModel));
		comboBoxPanel.add(new JComboBox<>(this._toRowModel));
		comboBoxPanel.add(new JLabel("Colum from/to:"));
		comboBoxPanel.add(new JComboBox<>(this._fromColModel));
		comboBoxPanel.add(new JComboBox<>(this._toColModel));
		
		// Crear los botones OK y Cancel y añadir al diálogo.
		JButton okButton = new JButton("OK");
		okButton.addActionListener(e-> {
			JSONObject regionsJSON = this.createRegionsJSON();
			if(regionsJSON != null) {
				try {
										
					//System.out.println(regionsJSON.toString(2));
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
		buttonPanel.add(cancelButton);
		buttonPanel.add(okButton);
		
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
		System.out.println("Rows: " + map.get_rows() + "Cols: " + map.get_cols());
		this.updateComboBoxModels(map.get_rows(), map.get_cols());
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		this.updateComboBoxModels(map.get_rows(), map.get_cols());
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
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
		this._dataTableModel.fireTableDataChanged();
	}
	
	private JSONObject createRegionsJSON() {
		int selectedIndex = this._regionsModel.getIndexOf(this._regionsModel.getSelectedItem());
		if(selectedIndex != -1) {
			JSONArray regionesArray = new JSONArray();
			JSONObject regionInfo = this._regionsInfo.get(selectedIndex);
			JSONObject regionData = new JSONObject();
			
			
			for(int i = 0; i < this._dataTableModel.getRowCount(); i++) {
				String key = (String) this._dataTableModel.getValueAt(i, 0);
				String value = (String) this._dataTableModel.getValueAt(i, 1);
				if(!value.isEmpty()) {
					regionData.put(key, value);
				}
			}
			String regionType = regionInfo.getString("type");
			String[] rowBounds = getSelectedRowBounds();
			String[] colBounds = getSelectedColBounds();
			JSONObject region = new JSONObject();
			region.put("row",  new int[] {Integer.parseInt(rowBounds[0]), Integer.parseInt(rowBounds[1])});
			region.put("col",  new int[] {Integer.parseInt(colBounds[0]), Integer.parseInt(colBounds[1])});
			region.put("spec", new JSONObject().put("type", regionType).put("data", regionData));
			regionesArray.put(region);
			return new JSONObject().put("regions", regionesArray);
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
		
		for(int j = 0; j < cols; j++) {
			this._fromColModel.addElement(String.valueOf(j));
			this._toColModel.addElement(String.valueOf(j));
		}
	}
}
