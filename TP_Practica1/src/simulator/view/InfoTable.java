package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;

public class InfoTable extends JPanel {
	private static final long serialVersionUID = 1L;
	String _title;
	TableModel _tableModel;

	InfoTable(String title, TableModel tableModel) {
		_title = title;
		_tableModel = tableModel;
		initGUI();
	}

	private void initGUI() {
		// Cambiar el layout del panel a BorderLayout()
		this.setLayout(new BorderLayout());
		
		// Añadir un borde con título al JPanel, con el texto _title
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black,2), this._title));
		
		// Añadir un JTable (con barra de desplazamiento vertical) que use
		// _tableModel
		JTable table = new JTable(this._tableModel);
		JScrollPane scrollPanel = new JScrollPane(table);
		this.add(scrollPanel);
	}
}
