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
package com.jpl.games.model3d;

import com.javafx.experiments.importers.obj.ObjImporter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

/**
 *
 * @author jpereda, April 2014 - @JPeredaDnr
 */
public class Model3D {
    
    /*
    Cube.obj, downloaded from  http://tf3dm.com/3d-model/rubik39s-cube-79189.html 
    from http://tf3dm.com/user/3dregenerator
    Contains 117 meshes, so each of the 27 cubies has 5 to 6 meshes.
    They are marked as "Block46", "Block46 (2)",...,"Block72 (6)" in this set:
    */
    private Set<String> meshes;
    /*
    HashMap to store a MeshView of each mesh with its key.
    */
    private final Map<String, MeshView> mapMeshes = new HashMap<>();
    /*
    custom designed arrows to visually mark the face or axis of rotation.
    */
    private MeshView faceArrow;
    private MeshView axisArrow;
    
    public Model3D(){
        
    }
    public void importObj(){
        try {// cube.obj
            ObjImporter reader = new ObjImporter(getClass().getResource("Cube.obj").toExternalForm());
            meshes = reader.getMeshes(); // set with the names of 117 meshes
            
            /*
            Since the model is oriented with White to the right and blue in the bottom,
            two rotations are required:
            - first rotate -90ºX B->F (Rx[-Pi/2])
            - then + 90º Z W->U (Rz[Pi/2])
            Mathematically, 2nd rotation matrix multiplies on the left the 1st matrix: Rz[Pi/2].Rx[-Pi/2]
            Check this link to notice the difference of add/append or prepend a transformation:
            http://hg.openjdk.java.net/openjfx/8/master/rt/file/f89b7dc932af/modules/graphics/src/main/java/javafx/scene/transform/Affine.java        
            
            Notice this will be wrong:
            * cubiePart.getTransforms().addAll(new Rotate(-90, Rotate.X_AXIS),new Rotate(90, Rotate.Z_AXIS)); 
            as it will perform this: Rx[-Pi/2].Rz[Pi/2] ->Red on top, yellow front!
            
            Also this is wrong:
            * cubiePart.getTransforms().addAll(new Rotate(90, Rotate.Z_AXIS),new Rotate(-90, Rotate.X_AXIS)); 
            It does the right rotations, but then it will require for further rotations of any cubie to be rotated from 
            its original position, which is quite more complicated than rotating always from the last state.
            
            PREPEND is the right way to proceed here, so we just need to prepend the last R matrix to the Affine matrix
            of the cubie with all the previous rotations stored there.
            
            */
            
            Affine affineIni = new Affine();            
            affineIni.prepend(new Rotate(-90, Rotate.X_AXIS));
            affineIni.prepend(new Rotate(90, Rotate.Z_AXIS));
            for (String s : meshes) { 
                MeshView cubiePart = reader.buildMeshView(s);
                // every part of the cubie is transformed with both rotations:
                cubiePart.getTransforms().add(affineIni); 
                // since the model has Ns=0 it doesn't reflect light, so we change it to 1
                PhongMaterial material = (PhongMaterial) cubiePart.getMaterial();
                material.setSpecularPower(1);
                cubiePart.setMaterial(material);
                // finally, add the name of the part and the cubie part to the hashMap:
                mapMeshes.put(s, cubiePart); 
            }
        } catch (IOException e) {
            System.out.println("Error loading model "+e.toString());
        }
        try {// arrow.obj
            ObjImporter reader = new ObjImporter(getClass().getResource("arrow.obj").toExternalForm());
            String mesh = reader.getMeshes().iterator().next();
            faceArrow = reader.buildMeshView(mesh);
        } catch (IOException e) {
            System.out.println("Error loading arrow "+e.toString());
        }
        try {// axis.obj
            ObjImporter reader = new ObjImporter(getClass().getResource("axis.obj").toExternalForm());
            String mesh = reader.getMeshes().iterator().next();
            
            axisArrow = reader.buildMeshView(mesh);
        } catch (IOException e) {
            System.out.println("Error loading arrow "+e.toString());
        }
    }

    public Map<String, MeshView> getMapMeshes() {
        return mapMeshes;
    }

    public MeshView getFaceArrow() {
        return faceArrow;
    }

    public MeshView getAxisArrow() {
        return axisArrow;
    }
}
