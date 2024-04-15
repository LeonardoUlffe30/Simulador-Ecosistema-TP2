package simulator.view;

import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.State;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class MapViewer extends AbstractMapViewer {

    // Anchura/altura/ de la simulaciÃ³n -- se supone que siempre van a ser iguales
    // al tamaÃ±o del componente
    private int _width;
    private int _height;

    // NÃºmero de filas/columnas de la simulaciÃ³n
    private int _rows;
    private int _cols;

    // Anchura/altura de una regiÃ³n
    int _rwidth;
    int _rheight;

    // Mostramos sÃ³lo animales con este estado. Los posibles valores de _currState
    // son null, y los valores deAnimal.State.values(). Si es null mostramos todo.
    // Animal.State _currState;
    
    // TODO Comprobar si es lo que piden
    State _currState;

    // En estos atributos guardamos la lista de animales y el tiempo que hemos
    // recibido la última vez para dibujarlos.
    volatile private Collection<AnimalInfo> _objs;
    volatile private Double _time;

    // Una clase auxilar para almacenar información sobre una especie
    private static class SpeciesInfo {
        private Integer _count;
        private Color _color;

        SpeciesInfo(Color color) {
            _count = 0;
            _color = color;
        }
    }

    // Un mapa para la información sobre las especies
    Map<String, SpeciesInfo> _kindsInfo = new HashMap<>();

    // El font que usamos para dibujar texto
    private Font _font = new Font("Arial", Font.BOLD, 12);

    // Indica si mostramos el texto la ayuda o no
    private boolean _showHelp;

    public MapViewer() {
        initGUI();
    }

    private void initGUI() {

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'h':
                        _showHelp = !_showHelp;
                        repaint();
                        break;
                    case 's':
                        // TODO Cambiar _currState al siguiente (de manera circular). Después de null
                        // viene el primero de Animal.State.values() y después del último viene null.
                        // Repintar después de cambiar el estado
                        int index = (_currState == null) ? 0 : _currState.ordinal() + 1;
                        _currState = (index >= State.values().length) ? null : State.values()[index];
                        repaint();
                        break;
                    default:
                }
            }

        });

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                requestFocus(); // Esto es necesario para capturar las teclas cuando el ratón está sobre este
                // componente.
            }
        });

        // Por defecto mostramos todos los animales
        _currState = null;

        // Por defecto mostramos el texto de ayuda
        _showHelp = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D gr = (Graphics2D) g;
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Cambiar el font para dibujar texto
        g.setFont(_font);

        // Dibujar fondo blanco
        gr.setBackground(Color.WHITE);
        gr.clearRect(0, 0, _width, _height);

        // Dibujar los animales, el tiempo, etc.
        if (_objs != null)
            drawObjects(gr, _objs, _time);

        // TODO Mostrar el texto de ayuda si _showHelp es true. El texto a mostrar es el
        // siguiente (en 2 líneas):
        //
        // h: toggle help
        // s: show animals of a specific state
        if (_showHelp) {
            g.setColor(Color.BLACK);
            g.drawString("h: toggle help", 10, 20);
            g.drawString("s: show animals of a specific state", 10, 35);
        }
    }

    private boolean visible(AnimalInfo a) {
        // TODO Devolver true si el animal es visible, es decir si _currState es null o
        // su estado es igual a _currState.
        return ((a.get_state() == this._currState) || (this._currState == null));
    }

    private void drawObjects(Graphics2D g, Collection<AnimalInfo> animals, Double time) {

        // TODO Dibujar el grid de regiones
        for (int i = 0; i < _rows; i++) {
            for (int j = 0; j < _cols; j++) {
                int x = j * _rwidth;
                int y = i * _rheight;
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(x, y, _rwidth, _rheight);
            }
        }

        // Dibujar los animales
        for (AnimalInfo a : animals) {

            // Si no es visible saltamos la iteración
            if (!visible(a))
                continue;

            // La información sobre la especie de 'a'
            SpeciesInfo esp_info = _kindsInfo.get(a.get_genetic_code());

            // TODO Si esp_info es null, añade una entrada correspondiente al mapa. Para el
            // color usa ViewUtils.get_color(a.get_genetic_code())
            // Map<String, SpeciesInfo> _kindsInfo = new HashMap<>();
            /*
             * SpeciesInfo(Color color) { _count = 0; _color = color; }
             */
            if (esp_info == null) {
                esp_info = new SpeciesInfo(ViewUtils.get_color(a.get_genetic_code()));
                _kindsInfo.put(a.get_genetic_code(), esp_info);
            }

            // TODO Incrementar el contador de la especie (es decir el contador dentro de
            // tag_info)
            esp_info._count++;

            // TODO Dibujar el animal en la posicion correspondiente, usando el color
            // tag_info._color. Su tamaño tiene que ser relativo a su edad, por ejemplo
            // edad/2+2. Se puede dibujar usando fillRoundRect, fillRect o fillOval.

            // Dibujar el animal en la posición correspondiente
            int size = (int) (a.get_age() / 2 + 2); // Tamaño relativo al animal
            g.setColor(esp_info._color);
            int x = (int) (a.get_position().getX());
            int y = (int) (a.get_position().getY());
            g.fillOval(x, y, size, size); // Dibuja un círculo, puedes cambiarlo a lo que prefieras
            //g.fillRoundRect();
            g.fillRect(x, y, size, size);
        }

        // TODO Dibujar la etiqueta del estado visible, sin no es null.
        if (_currState != null) {
            g.setColor(Color.BLACK);
            g.drawString("Current State: " + _currState.toString(), 10, _height - 20);
        }

        // TODO Dibujar la etiqueta del tiempo. Para escribir solo 3 decimales puede
        // usar String.format("%.3f", time)
        if (_time != null) {
            g.setColor(Color.BLACK);
            g.drawString("Time: " + String.format("%.3f", _time), 10, _height - 5);
        }

        // TODO Dibujar la información de todas la especies. Al final de cada iteración
        // poner el contador de la especie correspondiente a 0 (para resetear el cuento)
        int yOffset = 50; // Ajusta este valor según la posición que desees para la información de las especies
        for (Entry<String, SpeciesInfo> e : _kindsInfo.entrySet()) {
            String species = e.getKey();
            SpeciesInfo info = e.getValue();
            g.setColor(info._color);
            g.drawString(species + ": " + info._count, _width - 100, yOffset);
            yOffset += 15; // Incrementa el desplazamiento vertical para la siguiente especie
            // Resetea el contador
            info._count = 0;
        }
    }

    // Un método que dibujar un texto con un rectángulo
    void drawStringWithRect(Graphics2D g, int x, int y, String s) {
        Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
        g.drawString(s, x, y);
        g.drawRect(x - 1, y - (int) rect.getHeight(), (int) rect.getWidth() + 1, (int) rect.getHeight() + 5);
    }

    @Override
    public void update(List<AnimalInfo> objs, Double time) {
        // TODO Almacenar objs y time en los atributos correspondientes y llamar a
        // repaint() para redibujar el componente.
        _objs = objs;
        _time = time;
        repaint();
    }

    @Override
    public void reset(double time, MapInfo map, List<AnimalInfo> animals) {
        // TODO Actualizar los atributos _width, _height, _cols, _rows, etc.
        _width = map.get_width();
        _height = map.get_height();
        _cols = map.get_cols();
        _rows = map.get_rows();
        _rwidth = _width / _cols;
        _rheight = _height / _rows;

        // Esto cambia el tamaño del componente, y así cambia el tamaño de la ventana
        // porque en MapWindow llamamos a pack() después de llamar a reset
        setPreferredSize(new Dimension(map.get_width(), map.get_height()));

        // Dibuja el estado
        update(animals, time);
    }
}