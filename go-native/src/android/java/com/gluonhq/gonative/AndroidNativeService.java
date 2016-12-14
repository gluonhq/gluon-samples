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

import static android.app.Activity.RESULT_OK;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafxports.android.FXActivity;

public class AndroidNativeService implements NativeService {

    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;    

    private final ObjectProperty<Image> fxImage = new SimpleObjectProperty<>(
            new Image(getClass().getResourceAsStream("/icon.png")));

    private final Accelerometer accelerometer;

    public AndroidNativeService() {
        this.accelerometer = new Accelerometer();
    }
    
    @Override
    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(FXActivity.getInstance().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("GalleryActivity", "create image file failed " + ex.getMessage());
            }
            if (photoFile != null) {
                final Uri fromFile = Uri.fromFile(photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fromFile);
            
                FXActivity.getInstance().setOnActivityResultHandler((requestCode, resultCode, data) -> {
                    if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK) {
                        fxImage.set(new Image(fromFile.toString()));
                    }
                });
                
                FXActivity.getInstance().startActivityForResult(intent, TAKE_PICTURE);
            }
        } else {
            Log.e("GalleryActivity", "resolveActivity failed");
        }
    }

    @Override
    public void retrievePicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(FXActivity.getInstance().getPackageManager()) != null) {
            FXActivity.getInstance().setOnActivityResultHandler((requestCode, resultCode, data) -> {
                if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
                    InputStream is = null;
                    try {
                        Uri selectedImageUri = data.getData();
                        is = FXActivity.getInstance().getContentResolver().openInputStream(selectedImageUri);
                        fxImage.set(new Image(is));
                    } catch (FileNotFoundException ex) {
                        Log.e("GalleryActivity", "resolveActivity failed " + ex.getMessage());
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                        } catch (IOException ex) {
                            Log.e("GalleryActivity", "resolveActivity failed " + ex.getMessage());
                        }
                    }
                }
            });

            FXActivity.getInstance().startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
        } else {
            Log.e("GalleryActivity", "resolveActivity failed");
        }
    }

    @Override
    public ObjectProperty<Image> imageProperty() {
        return fxImage;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void startAccelerometer() {
        accelerometer.start();
    }

    @Override
    public void stopAccelerometer() {
        accelerometer.stop();
    }

    @Override
    public DoubleProperty x() {
        return accelerometer.x();
    }

    @Override
    public DoubleProperty y() {
        return accelerometer.y();
    }

    @Override
    public DoubleProperty z() {
        return accelerometer.z();
    }

    @Override
    public void vibrate() {
        Vibrator v = (Vibrator) FXActivity.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }
    
}