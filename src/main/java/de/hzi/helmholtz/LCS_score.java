package de.hzi.helmholtz;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LCS_score {

    //affine gap penalty
    public static final int startgapscore = -12;
    public static final int continuegapscore = -1;
    public static final int matchscore = 7;
    public static final int mismatchscore = -3;
    int length[][];
    public static final int NOTDEF = 0;
    public static final int FROMNW = 1;//from northwest
    public static final int FROMN = 2;//from north
    public static final int FROMW = 3;//from west
    private int xlen, ylen; // their lengths
    private int[][] scoreArray;
    private int[][] fromWhere;
    List l1;
    List l2;
    private List<String> xalig = new ArrayList<String>();
    private List<String> yalig = new ArrayList<String>();// for the alignments
    LinkedList lcs;
    private int bestrow, bestcol;   // for location of best entry
    private int bestval;            // for best entry
    private int startrow, startcol; // for location of start of alignment

    public LCS_score() {
    }

    public void setStrings(List<String> qg5, List<String> qg6) {
        l1 = qg5;
        l2 = qg6;
        xlen = l1.size();
        ylen = l2.size();
        scoreArray = new int[ylen + 1][xlen + 1];
        fromWhere = new int[ylen + 1][xlen + 1];  // could be smaller, but ...
    }

    public String readFile(String s) throws IOException {
        BufferedReader br =
                new BufferedReader(new InputStreamReader(new FileInputStream(s)));
        StringBuffer myString = new StringBuffer();
        String line = br.readLine();
        while (line != null) {
            myString.append(line);
            line = br.readLine();
        }
        return (myString.toString());
    }

    public void fillScoreArray() {
        int col, row;    // for indexing through array
        int northwest, north, west;  // (row,col) entry will be max of these
        int best;   // will be the max
        int from;   // and will be from here
        // Fill the top row and left column:
        for (col = 0; col <= xlen; col++) {
            scoreArray[0][col] = 0;
            fromWhere[0][col] = NOTDEF;
        }
        for (row = 0; row <= ylen; row++) {
            scoreArray[row][0] = 0;
            fromWhere[row][0] = NOTDEF;
        }
        // Now fill in the rest of the array:
        bestcol = 0;
        bestrow = 0;
        bestval = 0;
        for (row = 1; row <= ylen; row++) {
            for (col = 1; col <= xlen; col++) {
                if (l1.get(col - 1) == l2.get(row - 1)) {
                    northwest = scoreArray[row - 1][col - 1] + matchscore;
                    //	System.out.println( "  matchscore          "+northwest+"   "+l1.get(col-1)+" -  "+l2.get(row-1));
                } else {
                    northwest = scoreArray[row - 1][col - 1] + mismatchscore;
                    //System.out.println( "  mismatchscore          "+northwest+"   "+l1.get(col-1)+" -  "+l2.get(row-1));
                }
                if (fromWhere[row][col - 1] == FROMW) {
                    west = scoreArray[row][col - 1] + continuegapscore;
                    System.out.println("  continuegapscore          " + west + "   " + fromWhere[row][col - 1] + " -  " + l2.get(row - 1));
                } else {
                    west = scoreArray[row][col - 1] + startgapscore;
                    System.out.println(row + "  " + col + "  startgapscore          " + west + "   " + fromWhere[row][col - 1] + " -  " + l2.get(row - 1));
                }
                if (fromWhere[row - 1][col] == FROMN) {
                    north = scoreArray[row - 1][col] + continuegapscore;
                    //System.out.println( "  continuegapscore          "+north+"   "+fromWhere[row-1][col]+" -  "+l2.get(row-1));
                } else {
                    north = scoreArray[row - 1][col] + startgapscore;
                    //System.out.println( "  startgapscore          "+north+"   "+fromWhere[row-1][col]+" -  "+l2.get(row-1));
                }

                best = northwest;
                from = FROMNW;
                if (north > best) {
                    best = north;
                    from = FROMN;
                }
                if (west > best) {
                    best = west;
                    from = FROMW;
                }
                if (best <= 0) {
                    best = best;
                    from = NOTDEF;
                }

                scoreArray[row][col] = best;
                fromWhere[row][col] = from;
                if (best > bestval) {
                    bestval = best;
                    bestrow = row;
                    bestcol = col;
                }
            }
        }
    }

    public void print3(int x) {
        // Print x in 3 spaces
        String s = "" + x;
        if (s.length() == 1) {
            System.out.print("  " + s);
        } else if (s.length() == 2) {
            System.out.print(" " + s);
        } else if (s.length() == 3) {
            System.out.print(s);
        } else {
            System.out.print("***");
        }
    }

    public void printArray() {

        // Otherwise print the array of scores
		/*for (int row=0; row<fromWhere.length; row++) {
         for (int col=0; col<fromWhere[row].length; col++) 
         print3(fromWhere[row][col]);
         System.out.println();
         }*/
        for (int row = 0; row < scoreArray.length; row++) {
            for (int col = 0; col < scoreArray[row].length; col++) {
                print3(scoreArray[row][col]);
            }
            System.out.println();
        }
    }

    public int getLength() {
        return length[length.length - 1][length[0].length - 1];
    }

    public void printPath() {
        StringBuffer s = new StringBuffer();
        int i = length.length - 1, j = length[0].length - 1;
        while (i != 0 || j != 0) {
            if (i - 1 >= 0 && j - 1 >= 0) {
                if (length[i - 1][j - 1] + 1 == length[i][j]
                        && length[i][j] > length[i - 1][j] && length[i][j] > length[i][j - 1]) {
                    s.insert(0, "KEEP\t" + l1.get(i - 1).toString() + "(" + i + "), "
                            + l2.get(j - 1).toString() + "(" + j + ")\n");
                    i = i - 1;
                    j = j - 1;
                } else if (length[i - 1][j] > length[i][j - 1]) {
                    s.insert(0, "DELETE\t" + l1.get(i - 1).toString()
                            + "(" + i + ") [1]\n");
                    i = i - 1;
                } else {
                    s.insert(0, "DELETE\t" + l2.get(j - 1).toString()
                            + "(" + j + ") [2]\n");
                    j = j - 1;
                }
            } else if (i == 0) {
                s.insert(0, "DELETE\t" + l2.get(j - 1).toString()
                        + "(" + j + ") [2]\n");
                j = j - 1;
            } else if (j == 0) {
                s.insert(0, "DELETE\t" + l1.get(i - 1).toString()
                        + "(" + i + ") [1]\n");
                i = i - 1;
            }
        }
        System.out.print(s);
    }

    public void computeLengthMatrix() {
        int i, j;
        length = new int[l1.size() + 1][l2.size() + 1];
        for (i = 0; i < length.length; i++) {
            length[i][0] = 0;
        }
        for (j = 1; j < length[0].length; j++) {
            length[0][j] = 0;
        }
        for (i = 1; i < length.length; i++) {
            for (j = 1; j < length[0].length; j++) {
                Object o1 = l1.get(i - 1);
                Object o2 = l2.get(j - 1);
                if (o1.equals(o2)) {
                    length[i][j] = length[i - 1][j - 1] + 1;
                } else {
                    length[i][j] = Math.max(length[i - 1][j], length[i][j - 1]);
                }
            }
        }
    }

    public void printLengthMatrix() {
        int i, j;

        for (j = length[0].length - 1; j >= 0; j--) {
            for (i = 0; i < length.length; i++) {
                System.out.print(length[i][j] + " ");
            }
            System.out.println();
        }
    }

    public int getDist() {
        return length.length + length[0].length - 2 - 2 * getLength();
    }

    public void extractLongestcommonsubseq() {
        lcs = new LinkedList();
        int i = length.length - 1, j = length[0].length - 1;
        while (i != 0 && j != 0) {
            if (length[i - 1][j - 1] + 1 == length[i][j]
                    && length[i][j] > length[i - 1][j] && length[i][j] > length[i][j - 1]) {

                lcs.add(0, l1.get(i - 1));
                i = i - 1;
                j = j - 1;
            } else if (length[i - 1][j] > length[i][j - 1]) {
                lcs.add(0, "-");
                i = i - 1;
            } else {
                lcs.add(0, "-");
                j = j - 1;
            }
        }
    }

    public void printLongestcommonsubseq() {
        int i;
        for (i = 0; i < lcs.size(); i++) {
            System.out.print(lcs.get(i) + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) throws IOException {
        LCS_score sw = new LCS_score();
        List ll1 = new LinkedList();
        List ll2 = new LinkedList();
        List<String> Qg5 = Arrays.asList("KQ", "AT", "DQ", "KC", "ACP");
        List<String> Qg6 = Arrays.asList("KQ", "AT", "KS");
        sw.setStrings(Qg5, Qg6);
        for (int i = 0; i < Qg5.size(); i++) {
            ll2.add(new String(Qg5.get(i)));
        }
        for (int i = 0; i < Qg6.size(); i++) {
            ll1.add(new String(Qg6.get(i)));
        }
        sw.computeLengthMatrix();

        sw.extractLongestcommonsubseq();
        sw.printLongestcommonsubseq();
        sw.fillScoreArray();
        sw.printArray();
        //sw.printPath();
        System.out.println("value of best alignment is " + sw.bestval);
    }
}
