/*
 * Copyright (c) 2019, Gluon
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
package com.gluonhq.functionmapper.views;

import com.gluonhq.attach.cache.Cache;
import com.gluonhq.attach.cache.CacheService;
import javafx.scene.image.Image;

import java.time.format.DateTimeFormatter;

public class Util {
    
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
  
    private static final Cache<String, Image> CACHE = CacheService.create()
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
