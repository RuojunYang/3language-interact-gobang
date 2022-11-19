import py4j.GatewayServer;

import java.util.*;

public class Main {
    static State[][] gameBoard = new State[15][15];

    public static void main(String[] args) {

        Main app = new Main();

        // app is now the gateway.entry_point
        GatewayServer server = new GatewayServer(app);
        server.start();


        //test
//        app.initial();
//
//        //System.out.println(app.ABAIMove(0));
//        while(!app.checkTie()){
//            String ai1 = app.ABAIMove(0);
//            app.makeMove(Integer.parseInt(ai1.split(",")[0]), Integer.parseInt(ai1.split(",")[1]), 1);
//            app.printBoard();
//            if(app.check(Integer.parseInt(ai1.split(",")[0]), Integer.parseInt(ai1.split(",")[1]))) {System.out.println(1);break;
//                }
//
//            String ai2 = app.ABAIMove(1);
//            app.makeMove(Integer.parseInt(ai2.split(",")[0]), Integer.parseInt(ai2.split(",")[1]), 0);
//            app.printBoard();
//            if(app.check(Integer.parseInt(ai2.split(",")[0]), Integer.parseInt(ai2.split(",")[1]))) {System.out.println(2);break;}
//        }
    }

    public String initial() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                gameBoard[i][j] = State.empty;
            }
        }
        return getBoard();
    }

    public String getBoard() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (gameBoard[i][j] == State.black) {
                    s.append("b");
                } else if (gameBoard[i][j] == State.white) {
                    s.append("w");
                } else {
                    s.append("e");
                }
            }
        }
        return s.toString();
    }

    public void printBoard() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            if (i == 0) {
                s.append("-".repeat(31));
                s.append("\n");
            }
            for (int j = 0; j < 15; j++) {
                if (j == 0) s.append("|");
                if (gameBoard[i][j] == State.black) {
                    s.append("X");
                } else if (gameBoard[i][j] == State.white) {
                    s.append("O");
                } else {
                    s.append(" ");
                }
                s.append("|");
            }
            s.append("\n");
            s.append("-".repeat(31));
            s.append("\n");
        }
        System.out.println(s);
    }

    public String makeMove(int row, int col, int player) {
        if (row >= 0 && row < 15 && col >= 0 && col < 15) {
            if (player == 0) {
                if (gameBoard[row][col] == State.empty) {
                    gameBoard[row][col] = State.black;
                }
            } else {
                if (gameBoard[row][col] == State.empty) {
                    gameBoard[row][col] = State.white;
                }
            }
        }
        return getBoard();
    }

    public boolean check(int row, int col) {
        boolean result = false;
        int count = 0;
        State cur = gameBoard[row][col];

        int step = 0;
        while (gameBoard[row + step][col] == cur) {
            step++;
            if (row + step >= 15) {
                break;
            }
        }
        count = step;
        step = 0;
        while (gameBoard[row - step][col] == cur) {
            step++;
            if (row - step < 0) {
                break;
            }
        }
        if (count + step - 1 >= 5) {
            result = true;
        }

        step = 0;
        while (gameBoard[row][col - step] == cur) {
            step++;
            if (col - step < 0) {
                break;
            }
        }
        count = step;
        step = 0;
        while (gameBoard[row][col + step] == cur) {
            step++;
            if (col + step >= 15) {
                break;
            }
        }
        if (count + step - 1 >= 5) {
            result = true;
        }

        step = 0;
        while (gameBoard[row + step][col + step] == cur) {
            step++;
            if (row + step >= 15 || col + step >= 15) {
                break;
            }
        }
        count = step;
        step = 0;
        while (gameBoard[row - step][col - step] == cur) {
            step++;
            if (row - step < 0 || col - step < 0) {
                break;
            }
        }
        if (count + step - 1 >= 5) {
            result = true;
        }

        step = 0;
        while (gameBoard[row + step][col - step] == cur) {
            step++;
            if (row + step >= 15 || col - step < 0) {
                break;
            }
        }
        count = step;
        step = 0;
        while (gameBoard[row - step][col + step] == cur) {
            step++;
            if (row - step < 0 || col + step >= 15) {
                break;
            }
        }
        if (count + step - 1 >= 5) {
            result = true;
        }
        return result;
    }

    public String MCSTAIMove(int AIColor) {
        int playerColor = 1 - AIColor;
        List<Pair<Integer, Integer>> positions = getAllPosition(gameBoard);

        int simulateTime = 500;
        List<Integer> score = new ArrayList<>();
        for (Pair<Integer, Integer> position : positions) {
            int curScore = 0;
            for (int j = 0; j < simulateTime; j++) {
                State[][] gameCopy = new State[15][15];
                for (int i = 0; i < 15; i++) {
                    for (int k = 0; k < 15; k++) {
                        gameCopy[i][k] = gameBoard[i][k];
                    }
                }
                makeMoveSimulate(position.getElement0(), position.getElement1(), AIColor, gameCopy);
                if (MCSTSimulate(AIColor, playerColor, gameCopy, position.getElement0(), position.getElement1()) > 0) {
                    curScore = curScore + 1;
                } else if (MCSTSimulate(AIColor, playerColor, gameCopy, position.getElement0(), position.getElement1()) < 0) {
                    curScore = curScore - 2;
                }
            }
            score.add(curScore);
        }
        int max = score.get(0);
        int index = 0;
        for (int i = 1; i < score.size(); i++) {
            if (max < score.get(i)) {
                max = score.get(i);
                index = i;
            }
        }
        return positions.get(index).getElement0() + "," + positions.get(index).getElement1();
    }

    private boolean checkAround(int row, int col, State[][] gameBoard) {
        int left = Math.max(0, col - 2);
        int right = Math.min(14, col + 2);
        int up = Math.max(0, row - 2);
        int down = Math.min(14, row + 2);
        for (int i = left; i < right; i++) {
            for (int j = up; j < down; j++) {
                if (gameBoard[i][j] != State.empty) {
                    return true;
                }
            }
        }

        return false;
    }

    private int MCSTSimulate(int AI, int player, State[][] board, int row, int col) {
        List<Pair<Integer, Integer>> positions;
        positions = getAllPosition(board);
        if (checkSimulate(row, col, board)) return 1;
        int randomChoice;
        Random random = new Random();
        while (positions.size() != 0) {
            positions = getAllPosition(board);
            if (positions.size() == 0) return 0;
            randomChoice = random.nextInt(positions.size());
            makeMoveSimulate(positions.get(randomChoice).getElement0(), positions.get(randomChoice).getElement1(), player, board);
            if (checkSimulate(positions.get(randomChoice).getElement0(), positions.get(randomChoice).getElement1(), board))
                return -1;

            positions = getAllPosition(board);
            if (positions.size() == 0) return 0;
            randomChoice = random.nextInt(positions.size());
            makeMoveSimulate(positions.get(randomChoice).getElement0(), positions.get(randomChoice).getElement1(), AI, board);
            if (checkSimulate(positions.get(randomChoice).getElement0(), positions.get(randomChoice).getElement1(), board))
                return 1;
        }
        return 1;
    }

    public boolean checkSimulate(int row, int col, State[][] gameBoard) {
        boolean result = false;
        int count;
        State cur = gameBoard[row][col];

        int step = 0;
        while (gameBoard[row + step][col] == cur) {
            step++;
            if (row + step >= 15) {
                break;
            }
        }
        count = step;
        step = 0;
        while (gameBoard[row - step][col] == cur) {
            step++;
            if (row - step < 0) {
                break;
            }
        }
        if (count + step - 1 >= 5) {
            result = true;
        }

        step = 0;
        while (gameBoard[row][col - step] == cur) {
            step++;
            if (col - step < 0) {
                break;
            }
        }
        count = step;
        step = 0;
        while (gameBoard[row][col + step] == cur) {
            step++;
            if (col + step >= 15) {
                break;
            }
        }
        if (count + step - 1 >= 5) {
            result = true;
        }

        step = 0;
        while (gameBoard[row + step][col + step] == cur) {
            step++;
            if (row + step >= 15 || col + step >= 15) {
                break;
            }
        }
        count = step;
        step = 0;
        while (gameBoard[row - step][col - step] == cur) {
            step++;
            if (row - step < 0 || col - step < 0) {
                break;
            }
        }
        if (count + step - 1 >= 5) {
            result = true;
        }

        step = 0;
        while (gameBoard[row + step][col - step] == cur) {
            step++;
            if (row + step >= 15 || col - step < 0) {
                break;
            }
        }
        count = step;
        step = 0;
        while (gameBoard[row - step][col + step] == cur) {
            step++;
            if (row - step < 0 || col + step >= 15) {
                break;
            }
        }
        if (count + step - 1 >= 5) {
            result = true;
        }
        return result;
    }

    private List<Pair<Integer, Integer>> getAllPosition(State[][] board) {

        List<Pair<Integer, Integer>> list = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board[i][j] != State.empty) count++;
            }
        }
        if (count == 0 || board[7][7] == State.empty) {
            list.add(new Pair<>(7, 7));
        } else {
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    if (board[i][j] == State.empty && checkAround(i, j, gameBoard)) list.add(new Pair<>(i, j));
                }
            }
        }
        return list;
    }

    public boolean checkTie() {
        int count = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (gameBoard[i][j] == State.empty) {
                    count++;
                }
            }
        }
        return count == 0;
    }

    private void makeMoveSimulate(int row, int col, int color, State[][] gameBoard) {
        if (color == 0) {
            gameBoard[row][col] = State.black;
        } else {
            gameBoard[row][col] = State.white;
        }
    }

    // this will see two steps in further
    public String ABAIMove(int AIColor) {
        int playerColor = 1 - AIColor;
        List<Pair<Integer, Integer>> firstAIPositions = getAllPosition(gameBoard);

        Pair<Integer, Integer> cur = firstAIPositions.get(0);

        int firstMaxAlpha = -80000;
        int firstMaxBeta = 80000;

        //first Max
        for (Pair<Integer, Integer> firstAIPosition : firstAIPositions) {
            //jump out
            if (firstMaxAlpha >= firstMaxBeta) {
                break;
            }
            List<Pair<Integer, Integer>> firstPlayerPositions;
            State[][] gameCopy1 = new State[15][15];
            for (int i = 0; i < 15; i++) {
                for (int k = 0; k < 15; k++) {
                    gameCopy1[i][k] = gameBoard[i][k];
                }
            }
            makeMoveSimulate(firstAIPosition.getElement0(), firstAIPosition.getElement1(), AIColor, gameCopy1);
            firstPlayerPositions = getAllPosition(gameCopy1);
            int firstMinAlpha = firstMaxAlpha;
            int firstMinBeta = firstMaxBeta;

            //first Min
            for (Pair<Integer, Integer> firstPlayerposition : firstPlayerPositions) {
                //jump out
                if (firstMinAlpha >= firstMinBeta) {
                    break;
                }
                List<Pair<Integer, Integer>> secondAIPositions;
                State[][] gameCopy2 = new State[15][15];
                for (int i = 0; i < 15; i++) {
                    for (int k = 0; k < 15; k++) {
                        gameCopy2[i][k] = gameCopy1[i][k];
                    }
                }
                makeMoveSimulate(firstPlayerposition.getElement0(), firstPlayerposition.getElement1(), playerColor, gameCopy2);
                secondAIPositions = getAllPosition(gameCopy2);

                int secondMaxAlpha = firstMinAlpha;
                int secondMaxBeta = firstMinBeta;


                //second Max
                for (Pair<Integer, Integer> secondAIPosition : secondAIPositions) {
                    //jump out
                    if (secondMaxAlpha >= secondMaxBeta) {
                        break;
                    }
                    List<Pair<Integer, Integer>> secondPlayerPositions;
                    State[][] gameCopy3 = new State[15][15];
                    for (int i = 0; i < 15; i++) {
                        for (int k = 0; k < 15; k++) {
                            gameCopy3[i][k] = gameCopy2[i][k];
                        }
                    }
                    makeMoveSimulate(secondAIPosition.getElement0(), secondAIPosition.getElement1(), AIColor, gameCopy3);
                    secondPlayerPositions = getAllPosition(gameCopy3);
                    int secondMinAlpha = secondMaxAlpha;
                    int secondMinBeta = secondMaxBeta;

                    //second Min
                    for (Pair<Integer, Integer> secondPlayerPosition : secondPlayerPositions) {
                        //jump out
                        if (secondMinAlpha >= secondMinBeta) {
                            break;
                        }

                        State[][] gameCopy4 = new State[15][15];
                        for (int i = 0; i < 15; i++) {
                            for (int k = 0; k < 15; k++) {
                                gameCopy4[i][k] = gameCopy3[i][k];
                            }
                        }
                        //update second Min Beta
                        makeMoveSimulate(secondPlayerPosition.getElement0(), secondPlayerPosition.getElement1(), playerColor, gameCopy4);

                        if (secondMinBeta > -score(secondPlayerPosition.getElement0(), secondPlayerPosition.getElement1(), gameCopy4, playerColor)) {
                            secondMinBeta = -score(secondPlayerPosition.getElement0(), secondPlayerPosition.getElement1(), gameCopy4, playerColor);
                        }

                        //update second Max Alpha
                        secondMaxAlpha = Math.max(secondMaxAlpha, Math.max(secondMinAlpha, secondMinBeta));
                    }//second Min end
                    //update first Min Beta
                    firstMinBeta = Math.min(firstMinBeta, Math.min(secondMaxAlpha, secondMaxBeta));
                }//second Max end
                //update first Max Alpha
                if (firstMaxAlpha < firstMinBeta || firstMaxAlpha < firstMinAlpha) {
                    cur = new Pair<>(firstAIPosition.getElement0(), firstAIPosition.getElement1());
                }
                firstMaxAlpha = Math.max(firstMaxAlpha, Math.max(firstMinBeta, firstMinAlpha));
            }//first Min end
        }


        return cur.getElement0() + "," + cur.getElement1();
    }


    private int score(int row, int col, State[][] gameBoard, int color) {
        //This is the possible cases for a straight line
        //Let's say you are x, you opponent is O, empty is E
        //Case first are OXO, OXE, EXE
        //Case second are OXXO, OXXE, EXXE
        //Case third are OXXXO, OXXXE, EXXXE
        //Case four are OXXXXO, OXXXXE, EXXXXE
        //Case five is XXXXX
        int[][] cases = {{-100, -50, -10}, {-50, 100, 50}, {-20, 500, 5000}, {-10, 2000, 5000}, {1000000}};

        //it will go though four lines though current point
        int totalScore = 0;
        int countSelf = 1;
        int countEmpty = 0;

        State cur;
        if (color == 0) {
            cur = State.black;
        } else {
            cur = State.white;
        }
        // row score
        int step = 1;
        while (row + step < 15) {
            if (gameBoard[row + step][col] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row + step][col] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        step = 0;
        while (row - step >= 0) {
            if (gameBoard[row - step][col] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row - step][col] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        if (countSelf >= 4) {
            totalScore += cases[4][0];
        } else {
            totalScore += cases[countSelf][countEmpty];
        }

        // col score
        countSelf = 0;
        countEmpty = 0;
        step = 1;
        while (col + step < 15) {
            if (gameBoard[row][col + step] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row][col + step] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        step = 0;
        while (col - step >= 0) {
            if (gameBoard[row][col - step] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row][col - step] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        if (countSelf >= 4) {
            totalScore += cases[4][0];
        } else {
            totalScore += cases[countSelf][countEmpty];
        }

        // upper left to right down score
        countSelf = 0;
        countEmpty = 0;
        step = 1;
        while (col + step < 15 && row + step < 15) {
            if (gameBoard[row + step][col + step] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row + step][col + step] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        step = 0;
        while (col - step >= 0 && row - step >= 0) {
            if (gameBoard[row - step][col - step] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row - step][col - step] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        if (countSelf >= 4) {
            totalScore += cases[4][0];
        } else {
            totalScore += cases[countSelf][countEmpty];
        }

        // upper right to left down score
        countSelf = 0;
        countEmpty = 0;
        step = 1;
        while (col + step < 15 && row - step >= 0) {
            if (gameBoard[row - step][col + step] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row - step][col + step] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        step = 0;
        while (col - step >= 0 && row + step < 15) {
            if (gameBoard[row + step][col - step] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row + step][col - step] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        if (countSelf >= 4) {
            totalScore += cases[4][0];
        } else {
            totalScore += cases[countSelf][countEmpty];
        }


        //check oppoent
        countSelf = 1;
        countEmpty = 0;


        if (color == 1) {
            cur = State.black;
        } else {
            cur = State.white;
        }
        // row score
        step = 1;
        while (row + step < 15) {
            if (gameBoard[row + step][col] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row + step][col] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        step = 0;
        while (row - step >= 0) {
            if (gameBoard[row - step][col] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row - step][col] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        if (countSelf >= 4) {
            totalScore -= cases[4][0];
        } else {
            totalScore -= cases[countSelf][countEmpty] * 0.8;
        }

        // col score
        countSelf = 0;
        countEmpty = 0;
        step = 1;
        while (col + step < 15) {
            if (gameBoard[row][col + step] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row][col + step] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        step = 0;
        while (col - step >= 0) {
            if (gameBoard[row][col - step] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row][col - step] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        if (countSelf >= 4) {
            totalScore -= cases[4][0];
        } else {
            totalScore -= cases[countSelf][countEmpty] * 0.8;
        }

        // upper left to right down score
        countSelf = 0;
        countEmpty = 0;
        step = 1;
        while (col + step < 15 && row + step < 15) {
            if (gameBoard[row + step][col + step] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row + step][col + step] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        step = 0;
        while (col - step >= 0 && row - step >= 0) {
            if (gameBoard[row - step][col - step] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row - step][col - step] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        if (countSelf >= 4) {
            totalScore -= cases[4][0];
        } else {
            totalScore -= cases[countSelf][countEmpty] * 0.8;
        }

        // upper right to left down score
        countSelf = 0;
        countEmpty = 0;
        step = 1;
        while (col + step < 15 && row - step >= 0) {
            if (gameBoard[row - step][col + step] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row - step][col + step] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        step = 0;
        while (col - step >= 0 && row + step < 15) {
            if (gameBoard[row + step][col - step] == State.empty) {
                countEmpty++;
                break;
            } else if (gameBoard[row + step][col - step] == cur) {
                countSelf++;
            } else {
                break;
            }
            step++;
        }

        if (countSelf >= 4) {
            totalScore -= cases[4][0];
        } else {
            totalScore -= cases[countSelf][countEmpty] * 0.8;
        }


        return totalScore;
    }
}
