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
package com.jpl.games.math;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jpereda, April 2014 - @JPeredaDnr
 */
public class Rotations {
    
    /*
    Each of the 27 cubies are stored as a volume in a three dimensional array of indexes
    These indexes are related to number in the name of the block of the 3D model, as
    they are marked as "Block46", "Block46 (2)",...,"Block72 (6)" (see Model3D)
    
    The initial position refers to:    
    (U)up White, (F)front Blue, (R)right Green, (L)left Red, (D)down Yellow, (B)back Orange
    - first 9 indexes are the 9 cubies in (F)Front face, from top left (R/W/B) to down right (Y/O/B)
    - second 9 indexes are from (S)Standing, from top left (R/W) to down right (Y/O)
    - last 9 indexes are from (B)Back, from top left (G/R/W) to down right (G/Y/O)
    */
    
    private final int[][][] cube = {{{50, 51, 52}, {49, 54, 53}, {59, 48, 46}}, 
                                    {{58, 55, 60}, {57, 62, 61}, {47, 56, 63}}, 
                                    {{67, 64, 69}, {66, 71, 70}, {68, 65, 72}}};
    private final int[][][] tempCube = new int[3][3][3];

    public Rotations() {
        for (int f = 0; f < 3; f++) {
            for (int l = 0; l < 3; l++) {
                System.arraycopy(cube[f][l], 0, tempCube[f][l], 0, 3);
            }
        }
    }

    /* returns 3D array as a flatten list of indexes */
    public List<Integer> getCube() { 
        List<Integer> newArray = new ArrayList<>(27);
        for (int f = 0; f < 3; f++) {
            for (int l = 0; l < 3; l++) {
                for (int a = 0; a < 3; a++) {
                    newArray.add(cube[f][l][a]);
                }
            }
        }
        return newArray; 
    }
    
    public void setCube(List<Integer> order) {
        int index=0;
        for (int f = 0; f < 3; f++) {
            for (int l = 0; l < 3; l++) {
                for (int a = 0; a < 3; a++) {
                    cube[f][l][a]=order.get(index++);
                    tempCube[f][l][a]=cube[f][l][a];
                }
            }
        }
    }
    
    /* copy tempCube data in cube */
    public void save() {
        for (int f = 0; f < 3; f++) {
            for (int l = 0; l < 3; l++) {
                System.arraycopy(tempCube[f][l], 0, cube[f][l], 0, 3);
            }
        }
    }

    /* print 3D array as a flatten list of indexes in groups of 9 cubies (Front - Standing - Back) */
    public void printCube() {
        List<Integer> newArray = getCube();
        for (int i = 0; i < 27; i++) {
            if (i == 9 || i == 18) {
                System.out.print(" ||");
            }
            System.out.print(" " + newArray.get(i));            
        }
        System.out.println("");
    }

    /*
    This is the method to perform any rotation on the 3D array just by swapping indexes
    - first index refers to faces F-S-B
    - second index refers to faces U-E-D
    - third index refers to faces L-M-R
    
    For notation check http://en.wikipedia.org/wiki/Rubik%27s_Cube
    For clockwise rotations Capital letters are used, for counter-clockwise rotation an "i" is
    appended, instead of a ' or a lower letter.
    */
    public void turn(String rot) { 
        if (rot.contains("X") || rot.contains("Y") || rot.contains("Z")) {
            for (int z = 0; z < 3; z++) {
                int t = 0;
                for (int y = 2; y >= 0; --y) {
                    for (int x = 0; x < 3; x++) {
                        switch(rot) {
                            case "X":  tempCube[t][x][z] = cube[x][y][z]; break;
                            case "Xi": tempCube[x][t][z] = cube[y][x][z]; break;
                            case "Y":  tempCube[t][z][x] = cube[x][z][y]; break;
                            case "Yi": tempCube[x][z][t] = cube[y][z][x]; break;
                            case "Z":  tempCube[z][x][t] = cube[z][y][x]; break;
                            case "Zi": tempCube[z][t][x] = cube[z][x][y]; break;
                        }
                    }
                    t++;
                }
            }
        } else {
            int t = 0;
            for (int y = 2; y >= 0; --y) {
                for (int x = 0; x < 3; x++) {
                    switch (rot) {
                        case "L":  tempCube[x][t][0] = cube[y][x][0]; break;
                        case "Li": tempCube[t][x][0] = cube[x][y][0]; break;
                        case "M":  tempCube[x][t][1] = cube[y][x][1]; break;
                        case "Mi": tempCube[t][x][1] = cube[x][y][1]; break;
                        case "R":  tempCube[t][x][2] = cube[x][y][2]; break;
                        case "Ri": tempCube[x][t][2] = cube[y][x][2]; break;
                        case "U":  tempCube[t][0][x] = cube[x][0][y]; break;
                        case "Ui": tempCube[x][0][t] = cube[y][0][x]; break;
                        case "E":  tempCube[x][1][t] = cube[y][1][x]; break;
                        case "Ei": tempCube[t][1][x] = cube[x][1][y]; break;
                        case "D":  tempCube[x][2][t] = cube[y][2][x]; break;
                        case "Di": tempCube[t][2][x] = cube[x][2][y]; break;
                        case "F":  tempCube[0][x][t] = cube[0][y][x]; break;
                        case "Fi": tempCube[0][t][x] = cube[0][x][y]; break;
                        case "S":  tempCube[1][x][t] = cube[1][y][x]; break;
                        case "Si": tempCube[1][t][x] = cube[1][x][y]; break;
                        case "B":  tempCube[2][t][x] = cube[2][x][y]; break;
                        case "Bi": tempCube[2][x][t] = cube[2][y][x]; break;
                    }
                }
                t++;
            }
        }
        save();
    }
    
}
