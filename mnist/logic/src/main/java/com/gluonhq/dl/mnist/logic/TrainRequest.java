
package com.gluonhq.dl.mnist.logic;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TrainRequest {
    
    boolean invert;
    byte[] b;
    int label;

    public TrainRequest(InputStream is, int label, boolean invert) {
        try {
            this.invert = invert;
            this.b = new byte[is.available()];
            this.label = label;
        } catch (IOException ex) {
            Logger.getLogger(TrainRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
