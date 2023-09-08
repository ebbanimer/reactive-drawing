package se.miun.dt176g.ebni2100.reactive;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import se.miun.dt176g.ebni2100.reactive.Shapes.ShapeType;

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
    private final MainFrame frame;

    private Observable<Color> colorObservable;
    private Observable<Integer> thicknessObservable;
    private Observable<ShapeType> shapeObservable;


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
        JMenuItem rectangleItem;
        JMenuItem ovalItem;
        JMenuItem lineItem;
        JMenuItem freehandItem;

        menuShape = new JMenu("Shapes");
        this.add(menuShape);

        // SHAPES
        rectangleItem = new JMenuItem("Rectangle");
        menuShape.add(rectangleItem);

        ovalItem = new JMenuItem("Oval");
        menuShape.add(ovalItem);

        lineItem = new JMenuItem("Straight Line");
        menuShape.add(lineItem);

        freehandItem = new JMenuItem("Freehand");
        menuShape.add(freehandItem);

        shapeObservable = Observable.create(emitter -> {
            rectangleItem.addActionListener(e -> emitter.onNext(ShapeType.RECTANGLE));
            ovalItem.addActionListener(e -> emitter.onNext(ShapeType.OVAL));
            lineItem.addActionListener(e -> emitter.onNext(ShapeType.STRAIGHT_LINE));
            freehandItem.addActionListener(e -> emitter.onNext(ShapeType.FREEHAND));
        });
    }

    public Observable<ShapeType> getShapeObservable(){
        return shapeObservable;
    }

    private void setColorMenu(){

        JMenu menuColor;
        JMenuItem blueItem;
        JMenuItem redItem;
        JMenuItem greenItem;

        menuColor = new JMenu("Color");
        this.add(menuColor);

        // COLOR
        redItem = new JMenuItem(Constants.STRING_RED);
        menuColor.add(redItem);

        blueItem = new JMenuItem(Constants.STRING_BLUE);
        menuColor.add(blueItem);

        greenItem = new JMenuItem(Constants.STRING_GREEN);
        menuColor.add(greenItem);

        colorObservable = Observable.create(emitter -> {
            redItem.addActionListener(e -> emitter.onNext(Constants.COLOR_RED));
            blueItem.addActionListener(e -> emitter.onNext(Constants.COLOR_BLUE));
            greenItem.addActionListener(e -> emitter.onNext(Constants.COLOR_GREEN));
        });

    }

    public Observable<Color> getColorObservable() {
        return colorObservable;
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
        menuThickness.add(thicknessSmallItem );

        thicknessMediumItem = new JMenuItem(Constants.STRING_MEDIUM);
        menuThickness.add(thicknessMediumItem);

        thicknessBigItem = new JMenuItem(Constants.STRING_BIG);
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

