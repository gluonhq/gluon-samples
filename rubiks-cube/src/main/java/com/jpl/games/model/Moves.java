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

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author jpereda, April 2014 - @JPeredaDnr
 */
public class Moves {
 
    private final List<Move> moves = new ArrayList<>();
    private long timePlay;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
    
    public Moves(){
        moves.clear();
    }
    
    public void addMove(Move m){ moves.add(m); }
    public List<Move> getMoves() { return moves; }
    public Move getMove(int index){
        if(index > -1 && index < moves.size()){
            return moves.get(index);
        }
        return null;
    }
    public String getSequence(){
        StringBuilder sb = new StringBuilder("");
        moves.forEach(m -> sb.append(m.getFace()).append(" "));
        return sb.toString().trim();
    }
    public int getNumMoves() { return moves.size(); }
    public long getTimePlay() { return timePlay; }
    public void setTimePlay(long timePlay) { this.timePlay = timePlay; }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder( "Moves:\n");
        sb.append("Number of moves: ").append(moves.size()).append("\n");
        sb.append("Time of Play: ").append(LocalTime.ofNanoOfDay(timePlay).format(fmt)).append("\n\n");
        AtomicInteger ind=new AtomicInteger();
        for (Move m : moves) {
            sb.append("Move ")
                .append(ind.getAndIncrement()).append(": ")
                .append(m.getFace()).append(" at ")
                .append(LocalTime.ofNanoOfDay(m.getTimestamp()).format(fmt))
                .append("\n");
        }
        return sb.toString();
    }
}
