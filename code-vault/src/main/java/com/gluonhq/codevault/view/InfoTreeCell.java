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
package com.gluonhq.codevault.view;

import com.gluonhq.codevault.git.GitRef;
import com.gluonhq.codevault.git.Topic;
import com.gluonhq.codevault.util.UITools;
import javafx.scene.control.TreeCell;

public class InfoTreeCell extends TreeCell<GitRef> {

    private final String lastStyle = "topic";

    {
        getStyleClass().add("repoViewCell");
    }

    @Override
    protected void updateItem(GitRef ref, boolean empty) {
        super.updateItem(ref, empty);
        if (ref == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (ref instanceof Topic) {
                if (!getStyleClass().contains(lastStyle)) {
                    getStyleClass().add(lastStyle);
                }
            } else {
                if (getStyleClass().contains(lastStyle)) {
                    getStyleClass().remove(lastStyle);
                }
            }

            setText(ref.getShortName());
            setGraphic(UITools.getRefIcon(ref));
        }
    }
}
