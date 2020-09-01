/*
 * Copyright (c) 2016, 2020, Gluon
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
package com.gluonhq.fiftystates.model;


import com.gluonhq.attach.cache.Cache;
import com.gluonhq.attach.cache.CacheService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

/**
 *
 * List of US USStates
 Source: https://en.wikipedia.org/wiki/List_of_states_and_territories_of_the_United_States
 Flag images available under the Creative Commons CC0 1.0 Universal Public Domain Dedication
 *
 */
public class USStates {

    private static final Cache<String, Image> CACHE;
    
    static {
        CACHE = CacheService.create()
                .map(cache -> cache.<String, Image>getCache("images"))
                .orElseThrow(() -> new RuntimeException("No CacheService available"));
    }

    private static final String URL_PATH = "https://upload.wikimedia.org/wikipedia/commons/thumb/";

    public static ObservableList<USState> statesList = FXCollections.observableArrayList(
            new USState("Alabama", "AL", "Montgomery", 4833722, 135767, URL_PATH + "5/5c/Flag_of_Alabama.svg/23px-Flag_of_Alabama.svg.png"),
            new USState("Alaska", "AK", "Juneau", 735132, 1723337, URL_PATH + "e/e6/Flag_of_Alaska.svg/21px-Flag_of_Alaska.svg.png"),
            new USState("Arizona", "AZ", "Phoenix", 6626624, 295233, URL_PATH + "9/9d/Flag_of_Arizona.svg/23px-Flag_of_Arizona.svg.png"),
            new USState("Arkansas", "AR", "Little Rock", 2959373, 137733, URL_PATH + "9/9d/Flag_of_Arkansas.svg/23px-Flag_of_Arkansas.svg.png"),
            new USState("California", "CA", "Sacramento", 38332521, 423968, URL_PATH + "0/01/Flag_of_California.svg/23px-Flag_of_California.svg.png"),
            new USState("Colorado", "CO", "Denver", 5268367, 269602, URL_PATH + "4/46/Flag_of_Colorado.svg/23px-Flag_of_Colorado.svg.png"),
            new USState("Connecticut", "CT", "Hartford", 3596080, 14356, URL_PATH + "9/96/Flag_of_Connecticut.svg/20px-Flag_of_Connecticut.svg.png"),
            new USState("Delaware", "DE", "Dover", 925749, 6446, URL_PATH + "c/c6/Flag_of_Delaware.svg/23px-Flag_of_Delaware.svg.png"),
            new USState("Florida", "FL", "Tallahassee", 19552860, 170312, URL_PATH + "f/f7/Flag_of_Florida.svg/23px-Flag_of_Florida.svg.png"),
            new USState("Georgia", "GA", "Atlanta", 9992167, 153910, URL_PATH + "5/54/Flag_of_Georgia_%28U.S._state%29.svg/23px-Flag_of_Georgia_%28U.S._state%29.svg.png"),
            new USState("Hawaii", "HI", "Honolulu", 1404054, 28314, URL_PATH + "e/ef/Flag_of_Hawaii.svg/23px-Flag_of_Hawaii.svg.png"),
            new USState("Idaho", "ID", "Boise", 1612136, 216443, URL_PATH + "a/a4/Flag_of_Idaho.svg/19px-Flag_of_Idaho.svg.png"),
            new USState("Illinois", "IL", "Springfield", 12882135, 149997, URL_PATH + "0/01/Flag_of_Illinois.svg/23px-Flag_of_Illinois.svg.png"),
            new USState("Indiana", "IN", "Indianapolis", 6570902, 94327, URL_PATH + "a/ac/Flag_of_Indiana.svg/23px-Flag_of_Indiana.svg.png"),
            new USState("Iowa", "IA", "Des Moines", 3090416, 145746, URL_PATH + "a/aa/Flag_of_Iowa.svg/22px-Flag_of_Iowa.svg.png"),
            new USState("Kansas", "KS", "Topeka", 2893957, 213099, URL_PATH + "d/da/Flag_of_Kansas.svg/23px-Flag_of_Kansas.svg.png"),
            new USState("Kentucky", "KY", "Frankfort", 4395295, 104656, URL_PATH + "8/8d/Flag_of_Kentucky.svg/23px-Flag_of_Kentucky.svg.png"),
            new USState("Louisiana", "LA", "Baton Rouge", 4625470, 135658, URL_PATH + "e/e0/Flag_of_Louisiana.svg/23px-Flag_of_Louisiana.svg.png"),
            new USState("Maine", "ME", "Augusta", 1328302, 91634, URL_PATH + "3/35/Flag_of_Maine.svg/23px-Flag_of_Maine.svg.png"),
            new USState("Maryland", "MD", "Annapolis", 5928814, 32131, URL_PATH + "a/a0/Flag_of_Maryland.svg/23px-Flag_of_Maryland.svg.png"),
            new USState("Massachusetts", "MA", "Boston", 6692824, 27335, URL_PATH + "f/f2/Flag_of_Massachusetts.svg/23px-Flag_of_Massachusetts.svg.png"),
            new USState("Michigan", "MI", "Lansing", 9895622, 250488, URL_PATH + "b/b5/Flag_of_Michigan.svg/23px-Flag_of_Michigan.svg.png"),
            new USState("Minnesota", "MN", "St. Paul", 5420380, 225163, URL_PATH + "b/b9/Flag_of_Minnesota.svg/23px-Flag_of_Minnesota.svg.png"),
            new USState("Mississippi", "MS", "Jackson", 2991207, 125438, URL_PATH + "4/42/Flag_of_Mississippi.svg/23px-Flag_of_Mississippi.svg.png"),
            new USState("Missouri", "MO", "Jefferson City", 6021988, 180540, URL_PATH + "5/5a/Flag_of_Missouri.svg/23px-Flag_of_Missouri.svg.png"),
            new USState("Montana", "MT", "Helena", 1015165, 380832, URL_PATH + "c/cb/Flag_of_Montana.svg/23px-Flag_of_Montana.svg.png"),
            new USState("Nebraska", "NE", "Lincoln", 1868516, 200330, URL_PATH + "4/4d/Flag_of_Nebraska.svg/23px-Flag_of_Nebraska.svg.png"),
            new USState("Nevada", "NV", "Carson City", 2790136, 286380, URL_PATH + "f/f1/Flag_of_Nevada.svg/23px-Flag_of_Nevada.svg.png"),
            new USState("New Hampshire", "NH", "Concord", 1323459, 24214, URL_PATH + "2/28/Flag_of_New_Hampshire.svg/23px-Flag_of_New_Hampshire.svg.png"),
            new USState("New Jersey", "NJ", "Trenton", 8899339, 22592, URL_PATH + "9/92/Flag_of_New_Jersey.svg/23px-Flag_of_New_Jersey.svg.png"),
            new USState("New Mexico", "NM", "Santa Fe", 2085287, 314917, URL_PATH + "c/c3/Flag_of_New_Mexico.svg/23px-Flag_of_New_Mexico.svg.png"),
            new USState("New York", "NY", "Albany", 19651127, 141297, URL_PATH + "1/1a/Flag_of_New_York.svg/23px-Flag_of_New_York.svg.png"),
            new USState("North Carolina", "NC", "Raleigh", 9848060, 139391, URL_PATH + "b/bb/Flag_of_North_Carolina.svg/23px-Flag_of_North_Carolina.svg.png"),
            new USState("North Dakota", "ND", "Bismarck", 723393, 183107, URL_PATH + "e/ee/Flag_of_North_Dakota.svg/21px-Flag_of_North_Dakota.svg.png"),
            new USState("Ohio", "OH", "Columbus", 11570808, 116099, URL_PATH + "4/4c/Flag_of_Ohio.svg/23px-Flag_of_Ohio.svg.png"),
            new USState("Oklahoma", "OK", "Oklahoma City", 3850568, 181038, URL_PATH + "6/6e/Flag_of_Oklahoma.svg/23px-Flag_of_Oklahoma.svg.png"),
            new USState("Oregon", "OR", "Salem", 3930065, 254800, URL_PATH + "b/b9/Flag_of_Oregon.svg/23px-Flag_of_Oregon.svg.png"),
            new USState("Pennsylvania", "PA", "Harrisburg", 12773801, 119279, URL_PATH + "f/f7/Flag_of_Pennsylvania.svg/23px-Flag_of_Pennsylvania.svg.png"),
            new USState("Rhode Island", "RI", "Providence", 1051511, 4002, URL_PATH + "f/f3/Flag_of_Rhode_Island.svg/18px-Flag_of_Rhode_Island.svg.png"),
            new USState("South Carolina", "SC", "Columbia", 4774839, 82931, URL_PATH + "6/69/Flag_of_South_Carolina.svg/23px-Flag_of_South_Carolina.svg.png"),
            new USState("South Dakota", "SD", "Pierre", 844877, 199730, URL_PATH + "1/1a/Flag_of_South_Dakota.svg/23px-Flag_of_South_Dakota.svg.png"),
            new USState("Tennessee", "TN", "Nashville", 6495978, 109152, URL_PATH + "9/9e/Flag_of_Tennessee.svg/23px-Flag_of_Tennessee.svg.png"),
            new USState("Texas", "TX", "Austin", 26448193, 695660, URL_PATH + "f/f7/Flag_of_Texas.svg/23px-Flag_of_Texas.svg.png"),
            new USState("Utah", "UT", "Salt Lake City", 2900872, 219882, URL_PATH + "f/f6/Flag_of_Utah.svg/23px-Flag_of_Utah.svg.png"),
            new USState("Vermont", "VT", "Montpelier", 626630, 24905, URL_PATH + "4/49/Flag_of_Vermont.svg/23px-Flag_of_Vermont.svg.png"),
            new USState("Virginia", "VA", "Richmond", 8260405, 110787, URL_PATH + "4/47/Flag_of_Virginia.svg/22px-Flag_of_Virginia.svg.png"),
            new USState("Washington", "WA", "Olympia", 6971406, 184661, URL_PATH + "5/54/Flag_of_Washington.svg/23px-Flag_of_Washington.svg.png"),
            new USState("West Virginia", "WV", "Charleston", 1854304, 62755, URL_PATH + "2/22/Flag_of_West_Virginia.svg/23px-Flag_of_West_Virginia.svg.png"),
            new USState("Wisconsin", "WI", "Madison", 5742713, 169634, URL_PATH + "2/22/Flag_of_Wisconsin.svg/23px-Flag_of_Wisconsin.svg.png"),
            new USState("Wyoming", "WY", "Cheyenne", 582658, 253335, URL_PATH + "b/bc/Flag_of_Wyoming.svg/22px-Flag_of_Wyoming.svg.png")
    );

    public static Image getUSFlag() {
        return getImage(URL_PATH + "a/a4/Flag_of_the_United_States.svg/320px-Flag_of_the_United_States.svg.png");
    }

    /**
     * This method will always return the required image.
     * It will cache the image and return from cache if still there.
     * @param image: A valid url to retrieve the image
     * @return an Image
     */
    public static Image getImage(String image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        Image cachedImage = CACHE.get(image);
        if (cachedImage == null) {
            cachedImage = new Image(image, true);
            CACHE.put(image, cachedImage);
        }
        return cachedImage;
    }
}
