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
package com.jpl.games.model;

import com.jpl.games.math.Rotations;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 *
 * @author jpereda, April 2014 - @JPeredaDnr
 */
public class Utils {
    
    // suitable MOVEMENTS to scramble the cube
    private static final List<String> MOVEMENTS = Arrays.asList("F", "Fi", "F2", "R", "Ri", "R2", 
                                                        "B", "Bi", "B2", "L", "Li", "L2",
                                                        "U", "Ui", "U2", "D", "Di", "D2");
    // 24 suitable ORIENTATIONS for solved cube
    private static final List<String> ORIENTATIONS = Arrays.asList("V-V", "V-Y", "V-Yi", "V-Y2",
                                                    "X-V", "X-Z", "X-Zi", "X-Z2",
                                                    "Xi-V", "Xi-Z", "Xi-Zi",
                                                    "X2-V", "X2-Z", "X2-Zi",
                                                    "X-Y", "X-Yi", "X-Y2",
                                                    "Xi-Y", "Xi-Yi", "X2-Y", "X2-Yi",
                                                    "Z-V", "Zi-V", "Z2-V");
    public static final List<String> getMovements() { return MOVEMENTS; }
    public static final List<String> getOrientations() { return ORIENTATIONS; }
    public static final double RAD_MINIMUM = 10d, RAD_CLICK = 30d;
    
    
    public static Affine getAffine(double dimCube, double d0, boolean bFaceArrow, String face){
        Affine aff;
        double d = 2d * dimCube / 3d;
        if (!bFaceArrow) {
            aff = new Affine(new Scale(80, 80, 50));
            aff.append(new Translate(-d0, -d0, d0));
        } else {
            aff=new Affine(new Scale(3, 3, 3));
            aff.append(new Translate(0, -d0, 0));
        }
        switch(face){
            case "F": 
            case "Fi":  aff.prepend(new Rotate(face.equals("F") ? 90 : -90, Rotate.X_AXIS));
                        aff.prepend(new Rotate(face.equals("F") ? 45 : -45, Rotate.Z_AXIS));
                        aff.prepend(new Translate(0, 0, dimCube / 2d));
                        break;
            case "B": 
            case "Bi":  aff.prepend(new Rotate(face.equals("Bi") ? 90 : -90, Rotate.X_AXIS));
                        aff.prepend(new Rotate(face.equals("Bi") ? 45 : -45, Rotate.Z_AXIS));
                        aff.prepend(new Translate(0, 0, -dimCube / 2d));
                        break;
            case "R":  
            case "Ri":  aff.prepend(new Rotate(face.equals("Ri") ? 90 : -90, Rotate.Z_AXIS));
                        aff.prepend(new Rotate(face.equals("Ri") ? 45 : -45, Rotate.X_AXIS));
                        aff.prepend(new Translate(dimCube/2d, 0, 0));
                        break;
            case "L":  
            case "Li":  aff.prepend(new Rotate(face.equals("L") ? 90 : -90, Rotate.Z_AXIS));
                        aff.prepend(new Rotate(face.equals("L") ? 45 : -45, Rotate.X_AXIS));
                        aff.prepend(new Translate(-dimCube/2d, 0, 0));
                        break;
            case "U":   
            case "Ui":  aff.prepend(new Rotate(face.equals("Ui") ? 180 : 0, Rotate.Z_AXIS));
                        aff.prepend(new Rotate(face.equals("Ui") ? 45 : -45, Rotate.Y_AXIS));
                        aff.prepend(new Translate(0, dimCube/2d, 0));
                        break;
            case "D": 
            case "Di":  aff.prepend(new Rotate(face.equals("D") ? 180 : 0, Rotate.Z_AXIS));
                        aff.prepend(new Rotate(face.equals("D") ? 45 : -45, Rotate.Y_AXIS));
                        aff.prepend(new Translate(0, -dimCube/2d, 0));
                        break;
            case "Z": 
            case "Zi":  aff.prepend(new Rotate(face.equals("Zi") ? 180 : 0, Rotate.Y_AXIS));
                        aff.prepend(new Rotate(face.equals("Zi") ? 45 : -45, Rotate.Z_AXIS));
                        aff.prepend(new Translate(0, 0, d));
                        break;
            case "X":  
            case "Xi":  aff.prepend(new Rotate(face.equals("X") ? 90 : -90, Rotate.Y_AXIS));
                        aff.prepend(new Rotate(face.equals("Xi") ? 45 : -45, Rotate.X_AXIS));
                        aff.prepend(new Translate(d, 0, 0));
                        break;
            case "Y":   
            case "Yi":  aff.prepend(new Rotate(face.equals("Yi") ? 90 : -90, Rotate.X_AXIS));
                        aff.prepend(new Rotate(face.equals("Yi") ? 45 : -45, Rotate.Y_AXIS));
                        aff.prepend(new Translate(0, d, 0));
                        break;
        }
        return aff;
    }
    
    public static PhongMaterial getMaterial(String face){
        PhongMaterial arrowMat = new PhongMaterial();
        arrowMat.setSpecularColor(Color.WHITESMOKE);
        Color color = Color.WHITE;
        switch(face){
            case "F": 
            case "Fi":  color = Color.BLUE.brighter();
                        break;
            case "B": 
            case "Bi":  color = Color.BLUE.brighter();
                        break;
            case "R":  
            case "Ri":  color = Color.RED.brighter();
                        break;
            case "L":  
            case "Li":  color = Color.RED.brighter();
                        break;
            case "U":   
            case "Ui":  color = Color.FORESTGREEN.brighter();
                        break;
            case "D": 
            case "Di":  color = Color.FORESTGREEN.brighter();
                        break;
            case "Z": 
            case "Zi":  color = Color.BLUE.brighter();
                        break;
            case "X":  
            case "Xi":  color = Color.RED.brighter();
                        break;
            case "Y":   
            case "Yi":  color = Color.FORESTGREEN.brighter();
                        break;
        }
        arrowMat.setDiffuseColor(color);
        return arrowMat;
    }
    
    public static Point3D getAxis(String face){
        Point3D p = new Point3D(0, 0, 0);
        switch(face.substring(0, 1)){
            case "L":  
            case "M":  p = new Point3D(-1, 0, 0); 
                       break;
            case "R":  p = new Point3D(1, 0, 0); 
                       break;
            case "U":  p = new Point3D(0, 1, 0); 
                       break;
            case "E":  
            case "D":  p = new Point3D(0, -1, 0); 
                       break;
            case "F":  
            case "S":  p = new Point3D(0, 0, 1); 
                       break;
            case "B":  p = new Point3D(0, 0, -1); 
                       break;
            case "X":  p = new Point3D(1, 0, 0); 
                       break;
            case "Y":  p = new Point3D(0, 1, 0); 
                       break;
            case "Z":  p = new Point3D(0, 0, 1); 
                       break;
        }
        return p;
    }
    
    public static int getCenter(String face){
        int c = 0;
        switch(face.substring(0, 1)){
            case "L":  c = 12; break;
            case "M":  c = 13; break;
            case "R":  c = 14; break;
            case "U":  c = 10; break;
            case "E":  c = 13; break;
            case "D":  c = 16; break;
            case "F":  c = 4;  break;
            case "S":  c = 13; break;
            case "B":  c = 22; break;
        }
        return c;
    }
    public static String getPickedRotation(int cubie, MeshView mesh){
        Point3D normal = getMeshNormal(mesh);
        String rots = ""; // Rx-Ry 
        switch(cubie){
            case 0: rots = (normal.getZ() > 0.99) ? "Ui-Li" : ((normal.getX() < -0.99) ? "Ui-F" : ((normal.getY() > 0.99) ? "Ui-Li" : ""));
                    break;
            case 1: rots = (normal.getZ() > 0.99) ? "F-Mi" : ((normal.getY() > 0.99) ? "Ui-Mi" : ""); // between L and R, as L
                    break;
            case 2:  rots = (normal.getZ() > 0.99) ? "Ui-R" : ((normal.getX() > 0.99) ? "Ui-Fi" : ((normal.getY() > 0.99) ? "Ui-R" : ""));
                    break;
            case 3: rots = (normal.getZ() > 0.99) ? "E-F" : ((normal.getX() < -0.99) ? "E-Li" : ""); // between U and D, as D
                    break;
            case 4:  rots = (normal.getZ() > 0.99) ? "Yi-X" : ""; 
                    break;
            case 5: rots = (normal.getZ() > 0.99) ? "E-Fi" : ((normal.getX() > 0.99) ? "E-R" : ""); // between U and D, as D
                    break;
            case 6: rots = (normal.getZ() > 0.99) ? "D-Li" : ((normal.getX() < -0.99) ? "D-F" : ((normal.getY() < -0.99) ? "D-Li" : ""));
                    break;
            case 7: rots = (normal.getZ() > 0.99) ? "Fi-Mi" : ((normal.getY() < -0.99) ? "Fi-Mi" : ""); // between L and R, as L
                    break;
            case 8: rots = (normal.getZ() > 0.99) ? "D-R" : ((normal.getX() > 0.99) ? "D-Fi" : ((normal.getY() < -0.99) ? "D-R" : ""));
                    break;
            
            case 9: rots = (normal.getY() > 0.99) ? "S-U" : ((normal.getX() < -0.99) ? "L-S" : ""); // between U and D, as D
                    break;
            case 10: rots = (normal.getY() > 0.99) ? "Z-X" : ""; 
                    break;
            case 11: rots = (normal.getY() > 0.99) ? "S-Ui" : ((normal.getX() > 0.99) ? "R-Si" : ""); // between U and D, as D
                    break;
            case 12: rots = (normal.getX() < -0.99) ? "Yi-Z" : ""; 
                    break;
            case 14: rots = (normal.getX() > 0.99) ? "Yi-Zi" : ""; 
                    break;
            case 15: rots = (normal.getY() < -0.99) ? "D-S" : ((normal.getX() < -0.99) ? "Li-S" : ""); // between U and D, as D
                    break;
            case 16: rots = (normal.getY() < -0.99) ? "Zi-X" : ""; 
                    break;
            case 17: rots = (normal.getY() < -0.99) ? "D-S" : ((normal.getX() > 0.99) ? "Ri-Si" : ""); // between U and D, as D
                    break;
            
            case 18: rots = (normal.getZ() < -0.99) ? "Ui-L" : ((normal.getX() < -0.99) ? "Ui-Bi" : ((normal.getY() > 0.99) ? "Ui-L" : ""));
                    break;
            case 19: rots = (normal.getZ() < -0.99) ? "B-M" : ((normal.getY() > 0.99) ? "U-M" : ""); // between L and R, as L
                    break;
            case 20: rots = (normal.getZ() < -0.99) ? "Ui-Ri" : ((normal.getX() > 0.99) ? "Ui-B" : ((normal.getY() > 0.99) ? "Ui-Ri" : ""));
                    break;
            case 21: rots = (normal.getZ() < -0.99) ? "E-Bi" : ((normal.getX() < -0.99) ? "E-L" : ""); // between U and D, as D
                    break;
            case 22: rots = (normal.getZ() < -0.99) ? "Yi-Xi" : ""; 
                    break;
            case 23: rots = (normal.getZ() < -0.99) ? "E-B" : ((normal.getX() > 0.99) ? "E-Ri" : ""); // between U and D, as D
                    break;
            case 24: rots = (normal.getZ() < -0.99) ? "D-L" : ((normal.getX() < -0.99) ? "D-Bi" : ((normal.getY() < -0.99) ? "D-L" : ""));
                    break;
            case 25: rots = (normal.getZ() < -0.99) ? "Bi-M" : ((normal.getY() < -0.99) ? "Bi-M" : ""); // between L and R, as L
                    break;
            case 26: rots = (normal.getZ() < -0.99) ? "D-Ri" : ((normal.getX() > 0.99) ? "D-B" : ((normal.getY() < -0.99) ? "D-B" : ""));
                    break;
            
        }
        return rots;
    }
    
    private static Point3D getMeshNormal(MeshView mesh){
        TriangleMesh tm = (TriangleMesh) mesh.getMesh();
        float[] fPoints = new float[tm.getPoints().size()];
        tm.getPoints().toArray(fPoints);
        Point3D BA = new Point3D(fPoints[3] - fPoints[0], fPoints[4] - fPoints[1], fPoints[5] - fPoints[2]);
        Point3D CA = new Point3D(fPoints[6] - fPoints[0], fPoints[7] - fPoints[1], fPoints[8] - fPoints[2]);
        Point3D normal = BA.crossProduct(CA);
        Affine a = new Affine(mesh.getTransforms().get(0));
        return a.transform(normal.normalize());
    }
    
    public static String getRightRotation(Point3D p, String selFaces){
        double radius = p.magnitude();
        double angle = Math.atan2(p.getY(), p.getX());
        String face="";
        if (radius >= RAD_MINIMUM && selFaces.contains("-") && selFaces.split("-").length == 2) {
            String[] faces = selFaces.split("-");
            // select rotation if p.getX>p.getY
            if (-Math.PI / 4d <= angle && angle < Math.PI / 4d ){ // X
                face = faces[0];
            } else if (Math.PI / 4d <= angle && angle < 3d * Math.PI / 4d) { // Y
                face = faces[1];
            } else if ((3d * Math.PI / 4d <= angle && angle <= Math.PI) || 
                      (-Math.PI <= angle && angle < -3d * Math.PI / 4d)) { // -X
                face = reverseRotation(faces[0]);
            } else { //-Y
                face = reverseRotation(faces[1]);
            }
//            System.out.println("face: "+face);
        } else if (!face.isEmpty() && radius < RAD_MINIMUM) { // reset previous face
            face = "";
        }
        return face;
    }
    
    public static String reverseRotation(String rot){
        if (rot.endsWith("i")) {
            return rot.substring(0, 1);
        }
        return rot.concat("i");
    }
    
    public static boolean checkOrientation(String r, List<Integer> order){
        Rotations rot=new Rotations();
        for (String s : r.split("-")) {
            if (s.contains("2")) {
                rot.turn(s.substring(0, 1));
                rot.turn(s.substring(0, 1));
            } else {
                rot.turn(s);
            }
        }
        return order.equals(rot.getCube());
    }
    
    public static boolean checkSolution(List<Integer> order) {
        for (String o : Utils.getOrientations()) {
            if (Utils.checkOrientation(o, order)) {
                return true;
            }
        }
        return false;
    }
    
    public static ArrayList<String> unifyNotation(String list) {
        List<String> asList = Arrays.asList(list.replaceAll("’", "i").replaceAll("'", "i").split(" "));

        ArrayList<String> sequence = new ArrayList<>();
        for (String s : asList) {
            if (s.contains("2")) {
                sequence.add(s.substring(0, 1));
                sequence.add(s.substring(0, 1));
            } else if (s.length() == 1 && s.matches("[a-z]")) {
                sequence.add(s.toUpperCase().concat("i"));
            } else {
                sequence.add(s);
            }
        }
        return sequence;
    }
}
