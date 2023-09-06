package se.miun.dt176g.ebni2100.reactive;


import io.reactivex.rxjava3.core.Observable;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.*;


/**
 * <h1>Menu</h1>
 *
 * @author 	Ebba Nimér
 * @version 1.0
 * @since 	2022-09-08
 */
public class Menu extends JMenuBar {

    private static final long serialVersionUID = 1L;
    private int thickness;
    private Color color;
    private final MainFrame frame;

    private Observable<Integer> thicknessObservable;
    private Observable<Color> colorObservable;

    public Menu(MainFrame frame) {
        this.frame = frame;
        init();
    }

    private void init() {

        // Set-up menus
        setOptionsMenu();
        setShapeMenu();
        setColorMenu();
        setThicknessMenu();

    }

    private void setOptionsMenu(){

        JMenu menuOptions;
        JMenuItem menuOptionItem;

        menuOptions = new JMenu("Options");
        this.add(menuOptions);

        // OPTIONS
        menuOptionItem = new JMenuItem("Clear");
        menuOptionItem.addActionListener(e -> anEvent(frame));
        menuOptions.add(menuOptionItem);
    }

    private void setShapeMenu(){

        JMenu menuShape;
        JMenuItem menuShapeItem;

        menuShape = new JMenu("Shapes");
        this.add(menuShape);

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
    }

    private void setColorMenu(){

        JMenu menuColor;
        JMenuItem menuColorItem;

        menuColor = new JMenu("Color");
        this.add(menuColor);

        // COLOR
        menuColorItem = new JMenuItem(Constants.STRING_RED);
        menuColorItem.addActionListener(e ->  anotherEvent(frame));
        menuColor.add(menuColorItem);

        menuColorItem = new JMenuItem(Constants.STRING_BLUE);
        menuColorItem.addActionListener(e ->  anotherEvent(frame));
        menuColor.add(menuColorItem);

    }

    private void setThicknessMenu(){

        JMenu menuThickness;
        JMenuItem thicknessSmallItem;
        JMenuItem thicknessMediumItem;
        JMenuItem thicknessBigItem;

        menuThickness = new JMenu("Thickness");
        this.add(menuThickness);

        // THICKNESS
        thicknessSmallItem = new JMenuItem(Constants.STRING_SMALL);
        thicknessSmallItem.addActionListener(e ->  anotherEvent(frame));
        menuThickness.add(thicknessSmallItem );

        thicknessMediumItem = new JMenuItem(Constants.STRING_MEDIUM);
        thicknessSmallItem.addActionListener(e ->  anotherEvent(frame));
        menuThickness.add(thicknessMediumItem);

        thicknessBigItem = new JMenuItem(Constants.STRING_BIG);
        thicknessSmallItem.addActionListener(e ->  anotherEvent(frame));
        menuThickness.add(thicknessBigItem);

        // Initialize observables
        thicknessObservable = Observable.create(emitter -> {
            // Handle thickness selection when a menu item is clicked
            thicknessSmallItem.addActionListener(e -> emitter.onNext(Constants.SMALL));
            thicknessMediumItem.addActionListener(e -> emitter.onNext(Constants.MEDIUM));
            thicknessBigItem.addActionListener(e -> emitter.onNext(Constants.BIG));
        });
    }

    public Observable<Integer> getThicknessObservable() {
        return thicknessObservable;
    }

    public Observable<Color> getColorObservable() {
        return colorObservable;
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

    public Color getColor(){
        return color;
    }

    public int getThickness(){
        return thickness;
    }

}

