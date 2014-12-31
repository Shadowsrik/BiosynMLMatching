package de.hzi.helmholtz.Compare;

import java.util.*;

class Longestcommonsubseq {

    List l1;
    List l2;
    List lcs;
    int length[][];

    public Longestcommonsubseq(List ll1, List ll2) {
        l1 = ll1;
        l2 = ll2;
    }

    // assumes that extractLongestcommonsubseq has been previously invoked
    public void printLongestcommonsubseq() {
        int i;
        for (i = 0; i < lcs.size(); i++) {
            System.out.print(lcs.get(i) + ", ");
        }
        System.out.println();
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

    public void printLengthMatrix() {
        int i, j;

        for (j = length[0].length - 1; j >= 0; j--) {
            for (i = 0; i < length.length; i++) {
                System.out.print(length[i][j] + " ");
            }
            System.out.println();
        }
    }

    public int getLength() {
        return length[length.length - 1][length[0].length - 1];
    }

    public int getDist() {
        return length.length + length[0].length - 2 - 2 * getLength();
    }

    // assumes that computeLengthMatrix has been previously invoked
    public void extractLongestcommonsubseq() {
        lcs = new LinkedList();
        int i = length.length - 1, j = length[0].length - 1;
        while (i != 0 && j != 0) {
            if (length[i - 1][j - 1] + 1 == length[i][j]
                    && length[i][j] > length[i - 1][j] && length[i][j] > length[i][j - 1]) {

                lcs.add(0, l1.get(i - 1) + " ");
                i = i - 1;
                j = j - 1;
            } else if (length[i - 1][j] > length[i][j - 1]) {
                lcs.add(0, "- ");
                i = i - 1;
            } else {
                //lcs.add(0,"-");
                j = j - 1;
            }
        }
    }

    public int LevenshteinDistance(List s, List t) {
        // degenerate cases
        if (s == t) {
            return 0;
        }
        if (s.size() == 0) {
            return t.size();
        }
        if (t.size() == 0) {
            return s.size();
        }

        // create two work vectors of integer distances


        int[] v0 = new int[t.size() + 1];
        int[] v1 = new int[t.size() + 1];

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        for (int i = 0; i < v0.length; i++) {
            v0[i] = i;
        }

        for (int i = 0; i < s.size(); i++) {
            // calculate v1 (current row distances) from the previous row v0

            // first element of v1 is A[i+1][0]
            //   edit distance is delete (i+1) chars from s to match empty t
            v1[0] = i + 1;

            // use formula to fill in the rest of the row
            for (int j = 0; j < t.size(); j++) {
                Object cost = (s.get(i) == t.get(j)) ? 0 : 1;
                v1[j + 1] = Math.min(Math.min(v1[j] + 1, v0[j + 1] + 1), v0[j] + Integer.parseInt(cost.toString()));
            }

            // copy v1 (current row) to v0 (previous row) for next iteration
            for (int j = 0; j < v0.length; j++) {
                v0[j] = v1[j];
            }
        }

        return v1[t.size()];
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
    /*public int getDHSimilarity(List compOne, List compTwo)
     {
		 
     int[][] matrix;
     int res = -1;
     int INF = compOne.size() + compTwo.size();
	 
     matrix = new int[compOne.size()+1][compTwo.size()+1];
	 
     for (int i = 0; i < compOne.size(); i++)
     {
     matrix[i+1][1] = i;
     matrix[i+1][0] = INF;
     }
	 
     for (int i = 0; i < compTwo.size(); i++)
     {
     matrix[1][i+1] = i;
     matrix[0][i+1] = INF;
     }
	 
     int[] DA = new int[24];
	 
     for (int i = 0; i < 24; i++)
     {
     DA[i] = 0;
     }
	 
     for (int i = 1; i < compOne.size(); i++)
     {
     int db = 0;
	 
     for (int j = 1; j < compTwo.size(); j++)
     {
	 
     int i1 = DA[compTwo.indexOf(compTwo.get(j-1))];
     int j1 = db;
     int d = ((compOne.get(i-1)==compTwo.get(j-1))?0:1);
     if (d == 0) db = j;
	 
     matrix[i+1][j+1] = Math.min(Math.min(matrix[i][j]+d, matrix[i+1][j]+1),Math.min(matrix[i][j+1]+1,matrix[i1][j1]+(i - i1-1)+1+(j-j1-1)));
     }
     DA[compOne.indexOf(compOne.get(i-1))] = i;
     }
	         
     return matrix[compOne.size()][compTwo.size()];
     }*/

    public static void main(String args[]) throws Exception {

        List ll1 = new LinkedList();
        List ll2 = new LinkedList();
        int i;

        String a = "sadsa";
        String b = "sd";

        /*	List<String> Qgene1 = Arrays.asList("C", "A", "PCP", "Red");
         List<String> Qgene2 = Arrays.asList("DH", "ER", "KR", "ACP");
         List<String> Qgene3 = Arrays.asList("KS", "AT");
         List<String> Qgene4 = Arrays.asList("KS", "AT", "DH", "KR","ACP","KS", "AT", "KR","ACP","KS", "AT", "DH", "KR","ACP");
         List<String> Qgene5 = Arrays.asList("KS", "AT", "DH", "KR","ACP");
         List<String> Qgene6 = Arrays.asList("KS", "AT", "DH", "KR","ACP");
         List<String> Qgene7 = Arrays.asList("ACP","KS", "AT","AT", "DH", "KR","ACP");

         List<String> Tgene9 = Arrays.asList("ACL","ACP","C","A","PCP");
         List<String> Tgene8 = Arrays.asList("ACP");
         List<String> Tgene7 = Arrays.asList("AT","AT", "DH", "KR","ACP");
         List<String> Tgene6 = Arrays.asList("KS", "AT", "DH", "KR","ACP");
         List<String> Tgene5 = Arrays.asList("KS", "AT", "DH", "KR","ACP");
         List<String> Tgene4 = Arrays.asList("KS", "AT", "DH", "KR","ACP","KS", "AT", "KR","ACP");
         List<String> Tgene3 = Arrays.asList("AT", "DH", "KR","ACP");
         List<String> Tgene2 = Arrays.asList("KS", "AT","DH", "ER", "KR", "ACP");
		
         List<String> Tgene1 = Arrays.asList("C", "A", "PCP", "?");*/


        List<String> Qgene4 = Arrays.asList("ACP", "KS", "AT", "AT", "DH", "KR", "ACP", "KS", "AT", "DH", "KR", "ACP", "KS", "AT", "DH", "KR", "ACP", "KS", "AT", "DH", "KR", "ACP", "KS", "AT", "KR", "ACP", "KS", "AT", "DH", "KR", "ACP", "KS", "AT", "DH", "ER", "KR", "ACP", "C", "A", "PCP", "Red");
        List<String> Qgene5 = Arrays.asList("ACL", "ACP", "C", "A", "PCP", "ACP", "AT", "AT", "DH", "KR", "ACP", "KS", "AT", "DH", "KR", "ACP", "KS", "AT", "DH", "KR", "ACP", "KS", "AT", "DH", "KR", "ACP", "KS", "AT", "KR", "ACP", "AT", "DH", "KR", "ACP", "KS", "AT", "DH", "ER", "KR", "ACP", "C", "A", "PCP", "?");

        /*List<String> Qgene4 = Arrays.asList("ACP","KS", "AT","AT", "DH", "KR","ACP");
         List<String> Qgene5 = Arrays.asList("KS","AT", "DH", "KR","ACP");
         */
        for (i = 0; i < Qgene4.size(); i++) {
            ll2.add(new String(Qgene4.get(i)));
        }
        for (i = 0; i < Qgene5.size(); i++) {
            ll1.add(new String(Qgene5.get(i)));
        }
        Longestcommonsubseq l = new Longestcommonsubseq(ll2, ll1);
        l.computeLengthMatrix();
        /*double score = 0.0;
         score = 1-((double)LevenshteinDistance.computeLevenshteinDistance(Qgene4, Qgene5)/(Math.max(Qgene4.size(),Qgene5.size())));
         //System.out.println("Score: " + score);*/
        //	System.out.println("Distance: " + l.getDist());
        System.out.println("LevenshteinDistance:   " + LevenshteinDistance.computeLevenshteinDistance(Qgene4, Qgene5));
        l.extractLongestcommonsubseq();
        System.out.println(Qgene4.toString().replace("[", "").replace("]", ""));
        l.printLongestcommonsubseq();
    }
}
