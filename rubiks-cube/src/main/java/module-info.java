module rubik {
    requires javafx.controls;
    requires org.fxyz3d.importers;

    requires com.gluonhq.charm.glisten;

    requires com.gluonhq.attach.display;
    requires com.gluonhq.attach.lifecycle;
    requires com.gluonhq.attach.statusbar;
    requires com.gluonhq.attach.storage;
    requires com.gluonhq.attach.util;

    exports com.gluonhq.samples.rubik;
}