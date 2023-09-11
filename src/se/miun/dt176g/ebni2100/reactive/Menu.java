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

    private Observable<String> optionObservable;
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

        JMenu menuOptions  = new JMenu("Options");
        this.add(menuOptions);

        JMenuItem menuOptionItem = new JMenuItem("Clear");

        optionObservable = Observable.create(emitter -> {
            menuOptionItem.addActionListener(e -> emitter.onNext("Clear"));
        });
        menuOptions.add(menuOptionItem);
    }

    public Observable<String> getOptionObservable(){
        return optionObservable;
    }

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

        JMenu menuColor = new JMenu("Color");
        this.add(menuColor);

        JMenuItem blueItem = new JMenuItem(Constants.STRING_BLUE);
        JMenuItem redItem = new JMenuItem(Constants.STRING_RED);
        JMenuItem greenItem = new JMenuItem(Constants.STRING_GREEN);

        menuColor.add(redItem);
        menuColor.add(blueItem);
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

        JMenu menuThickness = new JMenu("Thickness");
        this.add(menuThickness);

        JMenuItem thicknessSmallItem = new JMenuItem(Constants.STRING_SMALL);
        JMenuItem thicknessMediumItem = new JMenuItem(Constants.STRING_MEDIUM);
        JMenuItem thicknessBigItem = new JMenuItem(Constants.STRING_BIG);


        menuThickness.add(thicknessSmallItem );
        menuThickness.add(thicknessMediumItem);
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
}

