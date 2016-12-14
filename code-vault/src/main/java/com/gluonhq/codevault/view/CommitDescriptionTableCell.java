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

import com.gluonhq.codevault.git.GitBranch;
import com.gluonhq.codevault.git.GitCommit;
import com.gluonhq.codevault.git.GitRef;
import com.gluonhq.codevault.git.GitTag;
import com.gluonhq.codevault.util.UITools;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import java.util.stream.Collectors;

public class CommitDescriptionTableCell extends TableCell<GitCommit, GitCommit> {

    private HBox labels = new HBox(1);

    @Override
    protected void updateItem(GitCommit commit, boolean empty) {
        super.updateItem(commit, empty);
        if (commit == null || empty ) {
            setText(null);
            setGraphic(null);
            setTooltip(null);
        } else {
            setText(commit.getShortMessage());
            setTooltip(new Tooltip(commit.getFullMessage()));
            labels.getChildren().setAll(
                    commit.getRefs().stream()
                            .map(this::makeRefLabel)
                            .collect(Collectors.toList())
            );
            setGraphic( labels.getChildren().isEmpty()? null: labels);
        }
    }

    private Label makeRefLabel(GitRef ref) {
        Label refLabel = new Label(ref.getShortName());
        refLabel.setGraphic(UITools.getRefIcon(ref));
        if (ref instanceof GitTag) {
            refLabel.getStyleClass().add("tag-ref");
        } else if (ref instanceof GitBranch) {
            refLabel.getStyleClass().add("branch-ref");
        } else {
            refLabel.getStyleClass().add("unknown-ref");
        }
        return refLabel;
    }
}
