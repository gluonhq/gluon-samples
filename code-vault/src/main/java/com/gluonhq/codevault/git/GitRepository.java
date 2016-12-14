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
package com.gluonhq.codevault.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GitRepository {

    private static final String GIT_FOLDER_NAME = ".git";

    private Repository repo;
    private Git git;
    private final File location;

    public GitRepository(File location) throws GitRepoException {

        this.location = Objects.requireNonNull(location);

        if ( !isGitRepo(location)) {
            throw new GitRepoException("Git repository not found at " + location);
        }


        File gitDir =
                GIT_FOLDER_NAME.equals(location.getName()) ? location : new File(location, GIT_FOLDER_NAME);

        try {
            repo = new FileRepositoryBuilder()
                    .setGitDir(gitDir)
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir()      // scan up the file system tree
                    .build();

            git = new Git(repo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GitRepository that = (GitRepository) o;

        return location.equals(that.location);

    }

    public void close() {
        repo.close();
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }

    private boolean isGitRepo( File location ) {
        return (location.exists() && location.getName().endsWith(GIT_FOLDER_NAME)) ||
                (new File( location, GIT_FOLDER_NAME).exists());
    }

    public File getLocation() {
        return location;
    }

    public String getName() {
        return location.getName();
    }

    public Collection<GitCommit> getLog() {
        try {
            Collection<GitRef> refs = new HashSet<>(getBranches());
            refs.addAll(getTags());

            Map<String, List<GitRef>> refMap = refs
                    .stream()
                    .collect(Collectors.groupingBy(GitRef::getId));

            return StreamSupport
                    .stream(git.log().all().call().spliterator(), false)
                    .map( c -> new GitCommit(c, refMap.getOrDefault(c.getId().getName(), Collections.emptyList())))
                    .collect(Collectors.toList());
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Collection<GitBranch> getBranches() {
        try {
            return StreamSupport
                    .stream(git.branchList().call().spliterator(), false)
                    .map(ref -> new GitBranch(ref))
                    .collect(Collectors.toSet());
        } catch (GitAPIException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    public Collection<GitTag> getTags() {
        try {
            return StreamSupport
                    .stream(git.tagList().call().spliterator(), false)
                    .map(ref -> new GitTag(!ref.isPeeled() ? ref : repo.peel(ref)))
                    .collect(Collectors.toSet());
        } catch (GitAPIException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }
}
