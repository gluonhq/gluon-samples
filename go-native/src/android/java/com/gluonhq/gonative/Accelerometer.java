/**
 * Copyright (c) 2016, Gluon
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
package com.gluonhq.gonative;

import static android.content.Context.SENSOR_SERVICE;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafxports.android.FXActivity;


public class Accelerometer implements SensorEventListener {

    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();
    private final DoubleProperty z = new SimpleDoubleProperty();

    private final SensorManager sensorManager;
    
    private boolean initialized = false;
    private final double alpha = 0.8;
    private final double[] gravity = new double[3];

    public Accelerometer() {
        sensorManager = (SensorManager) FXActivity.getInstance().getSystemService(SENSOR_SERVICE);
    }
    
    public final void start() {
        if (!initialized) {
            sensorManager.registerListener(this, 
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
                    SensorManager.SENSOR_DELAY_NORMAL);
            initialized = true;
        }
    }
    
    public final void stop() {
        if (initialized) {
            sensorManager.unregisterListener(this);
            initialized = false;
        }
    }
    
    @Override
    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            Platform.runLater(() -> {
                
                // filter to remove gravity
                gravity[0] = alpha * gravity[0] + (1 - alpha) * se.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * se.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * se.values[2];

                x.set(se.values[0] - gravity[0]);
                y.set(se.values[1] - gravity[1]);
                z.set(se.values[2] - gravity[2]);
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
    
    public DoubleProperty x() {
        return x;
    }

    public DoubleProperty y() {
        return y;
    }

    public DoubleProperty z() {
        return z;
    }
    
}
