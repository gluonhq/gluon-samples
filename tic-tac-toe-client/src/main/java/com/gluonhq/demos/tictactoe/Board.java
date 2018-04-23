/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gluonhq.demos.tictactoe;

import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.RestClient;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

public class Board extends TilePane {

    public final static int SIZE = 3;
    public final static int GAP = 10;

    public static int NUM_CELLS = 9;
    public static char X_MARK = 'X';
    public static char O_MARK = 'O';
    //public static char EMPTY = 'I';

    public static String INITIAL_BOARD = "IIIIIIIII";

    public boolean gameFinished;

    private boolean isComputerMove = true;

    //private final List<Integer> priorMoves = new LinkedList<>();
    private StringBuffer gameBoard = new StringBuffer(INITIAL_BOARD);

    public Board() {

        super(GAP, GAP);
        getStyleClass().add("board");

        build();

        sceneProperty().addListener((o, os, ns) -> {
            DoubleBinding tileSize = getScene().widthProperty().subtract(GAP * 4).divide(SIZE);
            prefTileHeightProperty().bind(tileSize);
            prefTileWidthProperty().bind(tileSize);
        });

        setTileAlignment(Pos.CENTER);
        setPrefColumns(Board.SIZE);
        setAlignment(Pos.CENTER);
    }


    public void restart() {
        clear();
        isComputerMove = true;
        makeComputerMove();
    }

    private void build() {

        getChildren().clear();

        for (int j = 0; j < Board.SIZE; j++) {
            for (int i = 0; i < Board.SIZE; i++) {
                BoardCell cell = new BoardCell();
                getChildren().add(cell);
                final int fi = i;
                final int fj = j;
                cell.setOnMouseClicked(e -> {

                    if (!isComputerMove && cell.isEmpty() && !isFinished()) {

                        if (whoseTurn() == O_MARK) {
                            cell.setValue(BoardCell.Value.PLAYER);
                        }
                        else {
                            cell.setValue(BoardCell.Value.COMPUTER);
                        }

                        //priorMoves.add(Board.SIZE * fi + fj + 1);
                        gameBoard.setCharAt(Board.SIZE * fj + fi, whoseTurn());

                        /*
                        if (!isFinished()) {
                            isComputerMove = true;
                            makeComputerMove();
                        }
                        */

                        if (!gameFinished) {
                            gameFinished = isFinished();
                            isComputerMove = true;
                            makeComputerMove();
                        }
                    }

                });

            }
        }

    }

    private void makeComputerMove() {
        /*
        String pm = "";
        if (!priorMoves.isEmpty()) {
            pm = Integer.toString(priorMoves.get(0));
            for (int i = 1; i < priorMoves.size(); i++) {
                pm = pm + ","+priorMoves.get(i);
            }
        }
        */
//        String pm = priorMoves.stream().reduce("", (a, b) -> a+","+b, String::concat);
//System.out.println("pm = "+pm);
//        RestClient rc = RestClient.create().method("GET").host("http://t2.lodgon.com/tictactoe")
       RestClient rc = RestClient.create().method("GET").host(TicTacToe.getHost())
                .path("player").queryParam("gameBoard", gameBoard.toString())
                .queryParam("strategy", "neuralNetwork");
                //.queryParam("strategy", "default");
        GluonObservableObject<PlayerResponse> retrieved = DataProvider.retrieveObject(rc.createObjectDataReader(PlayerResponse.class));
        retrieved.stateProperty().addListener((Observable o) -> {
            if (retrieved.getState() == ConnectState.SUCCEEDED && !gameFinished) {
                //int pcMove = Integer.parseInt(retrieved.get().getNextMove());
                //computerMoved(pcMove);
                System.out.println("ConnectState.SUCCEEDED, retrieved: " + retrieved);

                StringBuffer updatedGameBoard = new StringBuffer(retrieved.get().getGameBoard().trim());

                // TODO: Decide whether to detect newest move by comparing gameBoard request from response, so
                //       only the affected cell needs to be updated

                gameBoard = updatedGameBoard;

                for (int j = 0; j < Board.SIZE; j++) {
                    for (int i = 0; i < Board.SIZE; i++) {
                        char mark = gameBoard.charAt(Board.SIZE * j + i);
                        //System.out.println("mark: " + mark);
                        if (mark == X_MARK) {
                            ((BoardCell) getChildren().get(childIndex(i,j))).setValue(BoardCell.Value.COMPUTER);
                        }
                        else if (mark == O_MARK) {
                            ((BoardCell) getChildren().get(childIndex(i,j))).setValue(BoardCell.Value.PLAYER);
                        }
                        else {
                            ((BoardCell) getChildren().get(childIndex(i,j))).setValue(BoardCell.Value.EMPTY);
                        }
                    }
                }

                isComputerMove = false;
                isFinished();
            }
        });

    }

    /*
    public void computerMoved(int idx) {
        System.out.println("computermove: "+idx);
        gameBoard.setCharAt(idx, 'X');
        //priorMoves.add(idx);
        idx --;
        int i = idx/3;
        int j = idx %3;
        ((BoardCell) getChildren().get(childIndex(i,j))).setValue(BoardCell.Value.COMPUTER);
        isComputerMove = false;
        isFinished();
    }
    */

    private static List<List<Pair<Integer,Integer>>> winLines = Arrays.asList(

        Arrays.asList( new Pair(0,0), new Pair(0,1), new Pair(0,2)),
        Arrays.asList( new Pair(1,0), new Pair(1,1), new Pair(1,2)),
        Arrays.asList( new Pair(2,0), new Pair(2,1), new Pair(2,2)),

        Arrays.asList( new Pair(0,0), new Pair(1,0), new Pair(2,0)),
        Arrays.asList( new Pair(0,1), new Pair(1,1), new Pair(2,1)),
        Arrays.asList( new Pair(0,2), new Pair(1,2), new Pair(2,2)),

        Arrays.asList( new Pair(0,0), new Pair(1,1), new Pair(2,2)),
        Arrays.asList( new Pair(2,0), new Pair(1,1), new Pair(0,2))


    );

    public boolean isFinished() {


        // check for winning combinations

        for(  List<Pair<Integer,Integer>> line: winLines) {

            int sum = 0;

            for ( Pair<Integer,Integer> cell: line ) {
                sum += ((BoardCell) getChildren().get(childIndex( cell.getKey(),cell.getValue()))).getValue().getId();
            }

            if ( Math.abs(sum) == SIZE ) {

                for ( Pair<Integer,Integer> cell: line ) {
                    ((BoardCell) getChildren().get(childIndex( cell.getKey(),cell.getValue()))).setSelected(true);
                }

                return true;

            }

        }

        // check if the board is filled up
        for ( Node node: getChildren()) {
            BoardCell cell = (BoardCell)node;
            if (cell.isEmpty()) return false;
        }

        return false;

    }


    private void clear() {
        gameFinished = false;
        for (Node cell : getChildren()) {
            if (cell instanceof BoardCell) {
                ((BoardCell) cell).setValue(BoardCell.Value.EMPTY);
            }
        }
        gameBoard = new StringBuffer(INITIAL_BOARD);
        //priorMoves.clear();
    }

    private Integer[][] getState() {
        Integer[][] state = new Integer[Board.SIZE][Board.SIZE];
        for (int j = 0; j < Board.SIZE; j++) {
            for (int i = 0; i < Board.SIZE; i++) {
                state[i][j] = ((BoardCell) getChildren().get(childIndex(i,j))).getValue().getId();
            }
        }
        return state;
    }

    private void setState(Integer[][] state) {
        for (int j = 0; j < Board.SIZE; j++) {
            for (int i = 0; i < Board.SIZE; i++) {
                ((BoardCell) getChildren().get(childIndex(i,j))).setValue(BoardCell.Value.fromId(state[i][j]));
        }
        }
    }

    private int childIndex( int row, int col ) {
        return (col + row) + ( col * 2);
    }

    /**
     * Calculate whose turn it is by comparing number of X and O marks, given
     * that X always goes first
     * TODO: Decide whether to return an exception if bad state is detected
     *
     * @return X or O
     */
    private char whoseTurn() {
        char retVal = X_MARK;
        int numXs = 0;
        int numOs = 0;

        for (int idx = 0; idx < NUM_CELLS; idx++) {
            char mark = gameBoard.charAt(idx);
            if (mark == X_MARK) {
                numXs++;
            } else if (mark == O_MARK) {
                numOs++;
            }
        }

        if (numXs == numOs) {
            retVal = X_MARK;
        }
        else if (numXs == numOs + 1) {
            retVal = O_MARK;
        }
        else {
            System.out.println("Invalid gameBoard state: " + gameBoard);
        }

        return retVal;
    }

}
