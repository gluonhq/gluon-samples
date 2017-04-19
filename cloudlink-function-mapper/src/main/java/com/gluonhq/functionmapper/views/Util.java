/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gluonhq.functionmapper.views;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.Cache;
import com.gluonhq.charm.down.plugins.CacheService;
import java.time.format.DateTimeFormatter;
import javafx.scene.image.Image;

public class Util {
    
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
  
    private static final Cache<String, Image> CACHE = Services.get(CacheService.class)
            .map(cache -> cache.<String, Image>getCache("images"))
            .orElseThrow(() -> new RuntimeException("No cache service"));
  
    
    public static Image getImage(String imageName) {
        if ((imageName == null) || (imageName.isEmpty())) {
            return null;
        }
        Image image = CACHE.get(imageName);
        if (image == null) {
            image = new Image(imageName, 36.0, 36.0, true, true, true);
            CACHE.put(imageName, image);
        }
        return image;
    }
    
}
