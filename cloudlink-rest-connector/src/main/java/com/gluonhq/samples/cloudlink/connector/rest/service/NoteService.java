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
package com.gluonhq.samples.cloudlink.connector.rest.service;

import com.gluonhq.samples.cloudlink.connector.rest.model.Note;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * An EJB Session bean that works as a service class for managing instances of Note in the configured JPA database.
 */
@Stateless
public class NoteService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find a note from the database with the specified id.
     *
     * @param id the id of the Note to find in the database
     * @return the Note that matches the provided id or <code>null</code> if no such Note could be found
     */
    public Note findNote(String id) {
        return entityManager.find(Note.class, id);
    }

    /**
     * Finds all the Notes that exist in the database.
     *
     * @return a list of all Notes that are currently persisted in the database.
     */
    public List<Note> findAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Note> criteriaQuery = criteriaBuilder.createQuery(Note.class);
        TypedQuery<Note> allQuery = entityManager.createQuery(criteriaQuery.select(criteriaQuery.from(Note.class)));
        return allQuery.getResultList();
    }

    /**
     * Create a new instance of Note with the provided parameters and persist it in the database.
     *
     * @param id the id of the Note instance
     * @param title the title of the Note instance
     * @param text the text of the Note instance
     * @param creationDate the creation date of the Note instance
     * @return the newly persisted Note instance
     */
    public Note create(String id, String title, String text, long creationDate) {
        Note note = new Note();
        note.setId(id);
        note.setTitle(title);
        note.setText(text);
        note.setCreationDate(creationDate);
        entityManager.persist(note);
        return note;
    }

    /**
     * Updates an existing instance of Note in the database with the provided parameters.
     *
     * @param id the id of the Note to update
     * @param title the new title of the Note instance
     * @param text the new text of the Note instance
     * @return the merged Note instance
     */
    public Note update(String id, String title, String text) {
        Note note = findNote(id);
        if (note != null) {
            note.setTitle(title);
            note.setText(text);
            return entityManager.merge(note);
        }

        return null;
    }

    /**
     * Removes the Note from the database that matches the specified <code>id</code>.
     *
     * @param id the id of the Note to be removed from the database
     */
    public void remove(String id) {
        Note note = findNote(id);
        if (note != null) {
            entityManager.remove(note);
        }
    }
}
