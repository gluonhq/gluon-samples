/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gluonhq.demos.tictactoe;

/**
 *
 * @author johan
 */
public class MoveInfo {
    
    public MoveInfo() {}
    
    private String firstPlayer;
    private String priorMoves;
    private String nextMove;
    private String gameStatus; 

    /**
     * @return the firstPlayer
     */
    public String getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * @param firstPlayer the firstPlayer to set
     */
    public void setFirstPlayer(String firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    /**
     * @return the priorMoves
     */
    public String getPriorMoves() {
        return priorMoves;
    }

    /**
     * @param priorMoves the priorMoves to set
     */
    public void setPriorMoves(String priorMoves) {
        this.priorMoves = priorMoves;
    }

    /**
     * @return the nextMove
     */
    public String getNextMove() {
        return nextMove;
    }

    /**
     * @param nextMove the nextMove to set
     */
    public void setNextMove(String nextMove) {
        this.nextMove = nextMove;
    }

    /**
     * @return the gameStatus
     */
    public String getGameStatus() {
        return gameStatus;
    }

    /**
     * @param gameStatus the gameStatus to set
     */
    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }
}
