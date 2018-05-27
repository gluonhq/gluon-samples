package com.gluonhq.dl.mnist.app.views;

import com.gluonhq.charm.glisten.afterburner.AppView;
import static com.gluonhq.charm.glisten.afterburner.AppView.Flag.HOME_VIEW;
import static com.gluonhq.charm.glisten.afterburner.AppView.Flag.SHOW_IN_DRAWER;
import static com.gluonhq.charm.glisten.afterburner.AppView.Flag.SKIP_VIEW_STACK;
import com.gluonhq.charm.glisten.afterburner.AppViewRegistry;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.afterburner.DefaultDrawerManager;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.scene.image.Image;
import java.util.Locale;
import com.gluonhq.dl.mnist.app.GluonMnistDL;

public class AppViewManager {

    private static final AppViewRegistry REGISTRY = new AppViewRegistry();

    public static final AppView START_VIEW     = view("Home", StartPresenter.class, MaterialDesignIcon.HOME, SHOW_IN_DRAWER, HOME_VIEW, SKIP_VIEW_STACK);
    public static final AppView MATCH_VIEW     = view("Match", MatchPresenter.class, MaterialDesignIcon.HOME, SKIP_VIEW_STACK);
    public static final AppView INCORRECT_VIEW = view("Incorrect Match", IncorrectMatchPresenter.class, MaterialDesignIcon.HOME, SKIP_VIEW_STACK);
    
    private static AppView view(String title, Class<? extends GluonPresenter<?>> presenterClass, MaterialDesignIcon menuIcon, AppView.Flag... flags ) {
        return REGISTRY.createView(name(presenterClass), title, presenterClass, menuIcon, flags);
    }

    private static String name(Class<? extends GluonPresenter<?>> presenterClass) {
        return presenterClass.getSimpleName().toUpperCase(Locale.ROOT).replace("PRESENTER", "");
    }
    
    public static void registerViewsAndDrawer(MobileApplication app) {
        for (AppView view : REGISTRY.getViews()) {
            view.registerView(app);
        }

        NavigationDrawer.Header header = new NavigationDrawer.Header("Federated Learning",
                "By Gluon",
                new Avatar(21, new Image(GluonMnistDL.class.getResourceAsStream("/icon.png"))));

        DefaultDrawerManager drawerManager = new DefaultDrawerManager(app, header, REGISTRY.getViews()); 
        drawerManager.installDrawer();
    }
}
