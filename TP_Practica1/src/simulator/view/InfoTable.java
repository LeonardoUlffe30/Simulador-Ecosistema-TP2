package simulator.view;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;

public class InfoTable extends JPanel {
	String _title;
	TableModel _tableModel;

	InfoTable(String title, TableModel tableModel) {
		_title = title;
		_tableModel = tableModel;
		initGUI();
	}

	private void initGUI() {
		// cambiar el layout del panel a BorderLayout()
		this.setLayout(new BorderLayout());
		
		// a�adir un borde con t�tulo al JPanel, con el texto _title
		this.setBorder(BorderFactory.createTitledBorder(this._title));
		
		// a�adir un JTable (con barra de desplazamiento vertical) que use
		// _tableModel
		JTable table = new JTable(this._tableModel);
		JScrollPane scrollPanel = new JScrollPane(table);
		this.add(scrollPanel); //, BorderLayout.CENTER;
	}
}
