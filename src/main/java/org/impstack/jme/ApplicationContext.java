package org.impstack.jme;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;

public enum ApplicationContext {
    INSTANCE;

    private Node guiNode;
    private Node rootNode;
    private Application application;

    void init(SimpleApplication application) {
        this.guiNode = application.getGuiNode();
        this.rootNode = application.getRootNode();
        this.application = application;
    }

    public Node getGuiNode() {
        return guiNode;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public Application getApplication() {
        return application;
    }
}
