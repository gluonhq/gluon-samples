/*
 * Copyright (c) 2018, Gluon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of Gluon, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.gluonhq.dl.mnist.app;

import com.gluonhq.dl.mnist.app.views.AppViewManager;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import static org.nd4j.versioncheck.VersionCheck.VERSION_CHECK_PROPERTY;

public class GluonMnistDL extends MobileApplication {

    @Override
    public void init() {
        AppViewManager.registerViewsAndDrawer(this);
    }

    @Override
    public void postInit(Scene scene) {
        Swatch.BLUE_GREY.assignTo(scene);
        scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());
        
        ((Stage) scene.getWindow()).getIcons().add(new Image(GluonMnistDL.class.getResourceAsStream("/icon.png")));
    }
    
    public static void main(String[] args) {
        try {
// System.setProperty("javafx.platform", "ios");
            System.out.println("[GDL] Main");
            System.setProperty(VERSION_CHECK_PROPERTY, "false");
            System.out.println("java.vm.name = "+System.getProperty("java.vm.name"));
            System.out.println(".arch "+System.getProperty("os.arch"));
            System.out.println("os.name = "+System.getProperty("os.name"));
            System.out.println("java.vendor = "+System.getProperty("java.vendor"));
            System.out.println("arch = "+System.getProperty("sun.arch.data.model"));
            System.out.println("javafxplatform = "+System.getProperty("javafx.platform"));
            System.out.println("glassxplatform = "+System.getProperty("glass.platform"));
            System.out.println("set arch!");
            System.setProperty("java.vm.name", "gluonvm");
            System.out.println("PROP1 = "+System.getProperty("java.vm.name"));
            launch();
        } catch (Throwable e) {
            System.out.println("[GDL] main method has exception: "+e);
            e.printStackTrace();
        }
    }
}
