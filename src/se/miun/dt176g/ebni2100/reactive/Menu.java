package se.miun.dt176g.ebni2100.reactive;


import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;


/**
 * <h1>Menu</h1>
 *
 * @author 	Ebba Nimér
 * @version 1.0
 * @since 	2022-09-08
 */
public class Menu extends JMenuBar {

    private static final long serialVersionUID = 1L;


    public Menu(MainFrame frame) {
        init(frame);
    }

    private void init(MainFrame frame) {

        // ------------- MENUS

        JMenu menuOptions;
        JMenuItem menuOptionItem;

        JMenu menuShape;
        JMenuItem menuShapeItem;

        JMenu menuColor;
        JMenuItem menuColorItem;

        JMenu menuThickness;
        JMenuItem menuThicknessItem;

        menuOptions = new JMenu("Options");
        this.add(menuOptions);

        menuShape = new JMenu("Shapes");
        this.add(menuShape);

        menuColor = new JMenu("Color");
        this.add(menuColor);

        menuThickness = new JMenu("Thickness");
        this.add(menuThickness);

        // ------------- MENU-ITEMS

        // OPTIONS
        menuOptionItem = new JMenuItem("Clear");
        menuOptionItem.addActionListener(e -> anEvent(frame));
        menuOptions.add(menuOptionItem);


        // SHAPES
        menuShapeItem = new JMenuItem("Rectangle");
        menuShapeItem.addActionListener(e -> anEvent(frame));
        menuShape.add(menuShapeItem);

        menuShapeItem = new JMenuItem("Oval");
        menuShapeItem.addActionListener(e ->  anotherEvent(frame));
        menuShape.add(menuShapeItem);

        menuShapeItem = new JMenuItem("Straight Line");
        menuShapeItem.addActionListener(e ->  anotherEvent(frame));
        menuShape.add(menuShapeItem);

        menuShapeItem = new JMenuItem("Freehand");
        menuShapeItem.addActionListener(e ->  anotherEvent(frame));
        menuShape.add(menuShapeItem);

        // COLOR
        menuColorItem = new JMenuItem("Blue");
        menuColorItem.addActionListener(e ->  anotherEvent(frame));
        menuColor.add(menuColorItem);

        menuColorItem = new JMenuItem("Red");
        menuColorItem.addActionListener(e ->  anotherEvent(frame));
        menuColor.add(menuColorItem);

        // THICKNESS
        menuThicknessItem = new JMenuItem("Thin");
        menuThicknessItem .addActionListener(e ->  anotherEvent(frame));
        menuThickness.add(menuThicknessItem );

        menuThicknessItem  = new JMenuItem("Medium");
        menuThicknessItem .addActionListener(e ->  anotherEvent(frame));
        menuThickness.add(menuThicknessItem );

        menuThicknessItem  = new JMenuItem("Bold");
        menuThicknessItem .addActionListener(e ->  anotherEvent(frame));
        menuThickness.add(menuThicknessItem );


    }

    private void anEvent(MainFrame frame) {

        String message = (String) JOptionPane.showInputDialog(frame,
                "Send message to everyone:");

        if(message != null && !message.isEmpty()) {
            JOptionPane.showMessageDialog(frame, message);
        }
    }

    private void anotherEvent(MainFrame frame) {

    }

}

