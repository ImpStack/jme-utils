package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.style.ElementId;
import org.impstack.jme.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * A State that layouts regions over the full size of the window. This way panels can be easily layed-out on the screen.
 * It uses a border layout for the children. Lemur panels can be added to a position and removed from a position using
 * the {@link #add(Panel, BorderLayout.Position)} and {@link #remove(Panel)} methods.
 * <p>
 * Additionally a node can be specified that holds the full layout. When no node is specified, the guiNode will be used.
 */
public class GuiLayoutState extends BaseAppState {

    private static final String ELEMENT_ID = "GUILayout";
    private static final String ELEMENT_ID_NORTH = "north";
    private static final String ELEMENT_ID_EAST = "east";
    private static final String ELEMENT_ID_SOUTH = "south";
    private static final String ELEMENT_ID_WEST = "west";
    private static final String ELEMENT_ID_CENTER = "center";

    private final Node node;
    private final Map<BorderLayout.Position, Container> regions;

    private Container container;

    public GuiLayoutState() {
        this(ApplicationContext.INSTANCE.getGuiNode());
    }

    public GuiLayoutState(Node node) {
        this.node = node;
        this.regions = new HashMap<>();
    }

    @Override
    protected void initialize(Application app) {
        container = new Container(new BorderLayout(), new ElementId(ELEMENT_ID));

        regions.put(BorderLayout.Position.North, container.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.None, FillMode.Even), new ElementId(ELEMENT_ID).child(ELEMENT_ID_NORTH)), BorderLayout.Position.North));
        regions.put(BorderLayout.Position.East, container.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), new ElementId(ELEMENT_ID).child(ELEMENT_ID_EAST)), BorderLayout.Position.East));
        regions.put(BorderLayout.Position.South, container.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.None, FillMode.Even), new ElementId(ELEMENT_ID).child(ELEMENT_ID_SOUTH)), BorderLayout.Position.South));
        regions.put(BorderLayout.Position.West, container.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), new ElementId(ELEMENT_ID).child(ELEMENT_ID_WEST)), BorderLayout.Position.West));
        regions.put(BorderLayout.Position.Center, container.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.None, FillMode.None), new ElementId(ELEMENT_ID).child(ELEMENT_ID_CENTER)), BorderLayout.Position.Center));

        container.setLocalTranslation(0, getApplication().getCamera().getHeight(), 0);
        container.setPreferredSize(container.getPreferredSize()
                .setX(getApplication().getCamera().getWidth())
                .setY(getApplication().getCamera().getHeight()));
    }

    @Override
    protected void onEnable() {
        node.attachChild(container);
    }

    @Override
    protected void onDisable() {
        node.detachChild(container);
    }

    @Override
    protected void cleanup(Application app) {
        container.detachAllChildren();
        regions.clear();
    }

    public void add(Panel panel, BorderLayout.Position position) {
        regions.get(position).addChild(panel);
    }

    public void remove(Panel panel) {
        if (panel != null) {
            panel.removeFromParent();
        }
    }
}
