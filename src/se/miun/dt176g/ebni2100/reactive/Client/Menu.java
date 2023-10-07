package se.miun.dt176g.ebni2100.reactive.Client;


import io.reactivex.rxjava3.core.Observable;
import se.miun.dt176g.ebni2100.reactive.Client.Shapes.ShapeType;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.*;


/**
 * The menu for selecting shape, color, thickness, and options.
 * @author 	Ebba Nimér
 */
public class Menu extends JMenuBar {

    private static final long serialVersionUID = 1L;

    private Observable<String> optionObservable;
    private Observable<Color> colorObservable;
    private Observable<Integer> thicknessObservable;
    private Observable<ShapeType> shapeObservable;

    /**
     * Sets up the menus.
     */
    public Menu() {
        setOptionsMenu();
        setShapeMenu();
        setColorMenu();
        setThicknessMenu();
    }

    /**
     * Sets up options-menu.
     */
    private void setOptionsMenu(){

        JMenu menuOptions  = new JMenu("Options");
        this.add(menuOptions);

        JMenuItem menuOptionItem = new JMenuItem("Clear");

        // Create observable for clear-option.
        optionObservable = Observable.create(emitter -> {
            menuOptionItem.addActionListener(e -> emitter.onNext("Clear"));
        });
        menuOptions.add(menuOptionItem);
    }

    /**
     * Gets the options-observable.
     * @return Observable with option.
     */
    public Observable<String> getOptionObservable(){
        return optionObservable;
    }

    /**
     * Sets up the shape-menu.
     */
    private void setShapeMenu(){

        JMenu menuShape  = new JMenu("Shapes");
        this.add(menuShape);

        JMenuItem rectangleItem  = new JMenuItem("Rectangle");
        JMenuItem ovalItem  = new JMenuItem("Oval");
        JMenuItem lineItem  = new JMenuItem("Straight Line");
        JMenuItem freehandItem = new JMenuItem("Freehand");

        menuShape.add(rectangleItem);
        menuShape.add(ovalItem);
        menuShape.add(lineItem);
        menuShape.add(freehandItem);

        // Create a custom observable, and pass the ShapeType when the event-listener is triggered.
        shapeObservable = Observable.create(emitter -> {
            rectangleItem.addActionListener(e -> emitter.onNext(ShapeType.RECTANGLE));
            ovalItem.addActionListener(e -> emitter.onNext(ShapeType.OVAL));
            lineItem.addActionListener(e -> emitter.onNext(ShapeType.STRAIGHT_LINE));
            freehandItem.addActionListener(e -> emitter.onNext(ShapeType.FREEHAND));
        });
    }

    /**
     * Gets the shape-observable.
     * @return Observable with ShapeType.
     */
    public Observable<ShapeType> getShapeObservable(){
        return shapeObservable;
    }

    /**
     * Sets up the color-menu.
     */
    private void setColorMenu(){

        JMenu menuColor = new JMenu("Color");
        this.add(menuColor);

        JMenuItem blueItem = new JMenuItem(Constants.STRING_BLUE);
        JMenuItem redItem = new JMenuItem(Constants.STRING_RED);
        JMenuItem greenItem = new JMenuItem(Constants.STRING_GREEN);

        menuColor.add(redItem);
        menuColor.add(blueItem);
        menuColor.add(greenItem);

        // Create a custom observable, and pass the color when the event-listener is triggered.
        colorObservable = Observable.create(emitter -> {
            redItem.addActionListener(e -> emitter.onNext(Constants.COLOR_RED));
            blueItem.addActionListener(e -> emitter.onNext(Constants.COLOR_BLUE));
            greenItem.addActionListener(e -> emitter.onNext(Constants.COLOR_GREEN));
        });
    }

    /**
     * Gets the color-observable.
     * @return Observable with Color.
     */
    public Observable<Color> getColorObservable() {
        return colorObservable;
    }

    /**
     * Sets up the thickness-menu.
     */
    private void setThicknessMenu(){

        JMenu menuThickness = new JMenu("Thickness");
        this.add(menuThickness);

        JMenuItem thicknessSmallItem = new JMenuItem(Constants.STRING_SMALL);
        JMenuItem thicknessMediumItem = new JMenuItem(Constants.STRING_MEDIUM);
        JMenuItem thicknessBigItem = new JMenuItem(Constants.STRING_BIG);


        menuThickness.add(thicknessSmallItem );
        menuThickness.add(thicknessMediumItem);
        menuThickness.add(thicknessBigItem);

        // Create a custom observable, and pass the thickness when the event-listener is triggered.
        thicknessObservable = Observable.create(emitter -> {
            thicknessSmallItem.addActionListener(e -> emitter.onNext(Constants.SMALL));
            thicknessMediumItem.addActionListener(e -> emitter.onNext(Constants.MEDIUM));
            thicknessBigItem.addActionListener(e -> emitter.onNext(Constants.BIG));
        });
    }

    /**
     * Gets the thickness-observable.
     * @return Observable with thickness.
     */
    public Observable<Integer> getThicknessObservable() {
        return thicknessObservable;
    }
}

