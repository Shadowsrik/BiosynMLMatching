package de.hzi.helmholtz.Compare;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

//Harry Hull
//Implements the following Smith-Waterman algorithm http://en.wikipedia.org/wiki/Smith_waterman
//        Affine Gap algorithm taken from:
//  http://en.wikipedia.org/wiki/Gap_penalty#Affine_gap_penalty
//        gap = o + (l-1)*e;
//        o:        gap opening penalty  (o < 0)
//        l:        size of the gap
//        e:        gap extension penalty (o < e < 0)
public class SmithWaterman {

    private List<String> one;
    private List<String> two;
    private int matrix[][];
    private int gap;
    private int match;
    private int o;
    private int l;
    private int e;
    long score = 0;

    public SmithWaterman(List<String> one, List<String> two) {
        this.one = one;
        this.two = two;
        this.match = 150;

        // Define affine gap starting values
        o = -100;
        l = 0;
        e = -50;

        // initialize matrix to 0
        matrix = new int[one.size() + 1][two.size() + 1];
        for (int i = 0; i < one.size(); i++) {
            for (int j = 0; j < two.size(); j++) {
                matrix[i][j] = 0;
            }
        }

    }

    // returns the alignment score
    public int computeSmithWaterman() {
        for (int i = 0; i < one.size(); i++) {
            for (int j = 0; j < two.size(); j++) {
                gap = o + (l - 1) * e;
                if (i != 0 && j != 0) {
                    if (one.get(i).equals(two.get(j))) {
                        // match
                        // reset l
                        l = 0;
                        matrix[i][j] = Math.max(0, Math.max(
                                matrix[i - 1][j - 1] + match, Math.max(
                                matrix[i - 1][j] + gap,
                                matrix[i][j - 1] + gap)));
                    } else {
                        // gap
                        l++;
                        matrix[i][j] = Math.max(0, Math.max(
                                matrix[i - 1][j - 1] + gap, Math.max(
                                matrix[i - 1][j] + gap,
                                matrix[i][j - 1] + gap)));
                    }
                }
            }
        }

        // find the highest value
        int longest = 0;
        int iL = 0, jL = 0;
        for (int i = 0; i < one.size(); i++) {
            for (int j = 0; j < two.size(); j++) {
                if (matrix[i][j] > longest) {
                    longest = matrix[i][j];
                    iL = i;
                    jL = j;
                }
            }
        }

        // Backtrack to reconstruct the path
        int i = iL;
        int j = jL;
        Stack<String> actions = new Stack<String>();
        //System.out.println(i + "\n" + j);
        while (i != 0 || j != 0) {
            // diag case
            //	System.out.println(i + "            " + j);
            if (i > 0 && j > 0) {

                if (Math.max(matrix[i - 1][j - 1],
                        Math.max(matrix[i - 1][j], matrix[i][j - 1])) == matrix[i - 1][j - 1]) {
                    actions.push("align");
                    i = i - 1;
                    j = j - 1;
                    // left case
                } else if (Math.max(matrix[i - 1][j - 1],
                        Math.max(matrix[i - 1][j], matrix[i][j - 1])) == matrix[i][j - 1]) {
                    actions.push("insert");
                    j = j - 1;
                    // up case
                } else {
                    actions.push("delete");
                    i = i - 1;
                }
            } else {
                if (i > j) {
                    actions.push("delete");
                    i = i - 1;
                } else {
                    actions.push("insert");
                    j = j - 1;
                }
            }
        }

        String alignOne = new String();
        String alignTwo = new String();

        Stack<String> backActions = (Stack<String>) actions.clone();
        for (int z = 0; z < one.size(); z++) {
            alignOne = alignOne + one.get(z) + " ";
            if (!actions.empty()) {
                String curAction = actions.pop();
                // System.out.println(curAction);
                if (curAction.equals("insert")) {
                    alignOne = alignOne + "-" + " ";
                    while (actions.peek().equals("insert")) {
                        alignOne = alignOne + "-" + " ";
                        actions.pop();
                    }
                }
            }

        }

        for (int z = 0; z < two.size(); z++) {


            alignTwo = alignTwo + two.get(z) + " ";
            if (!backActions.empty()) {
                String curAction = backActions.pop();
                if (curAction.equals("delete")) {
                    alignTwo = alignTwo + "-" + " ";
                    while (backActions.peek().equals("delete")) {
                        alignTwo = alignTwo + "-" + " ";
                        backActions.pop();
                    }
                }
            }

        }

        // print alignment
        System.out.println(alignOne + "\n" + alignTwo);

        return longest;
    }

    public void printMatrix() {
        for (int i = 0; i < one.size(); i++) {
            if (i == 0) {
                for (int z = 0; z < two.size(); z++) {
                    if (z == 0) {
                        System.out.print("   ");
                    }
                    System.out.print(two.get(z) + "  ");

                    if (z == two.size() - 1) {
                        System.out.println();
                    }
                }
            }

            for (int j = 0; j < two.size(); j++) {
                if (j == 0) {
                    System.out.print(one.get(i) + "  ");
                }
                System.out.print(matrix[i][j] + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // DNA sequence Test:


        //sw.printMatrix();

        /*List<String> Tgene4 = Arrays.asList("KS", "AT", "DH", "KR","ACP","KS", "AT", "DH", "KR","ACP");
         List<String> Tgene5 = Arrays.asList("DH", "KR","ACP", "AT", "DH", "KR","ACP");



         List<String> Qgene4 = Arrays.asList("ACP","KS", "AT","AT", "DH", "KR","ACP","KS", "AT", "DH", "KR","ACP","KS", "AT", "DH", "KR","ACP","KS", "AT", "DH", "KR","ACP","KS", "AT", "KR","ACP","KS", "AT", "DH", "KR","ACP","KS", "AT","DH", "ER", "KR", "ACP","C", "A", "PCP", "Red");
         List<String> Qgene5 = Arrays.asList("ACL","ACP","C","A","PCP","ACP","AT","AT", "DH", "KR","ACP","KS", "AT", "DH", "KR","ACP","KS", "AT", "DH", "KR","ACP","KS", "AT", "DH", "KR","ACP","KS", "AT", "KR","ACP","AT", "DH", "KR","ACP","KS", "AT","DH", "ER", "KR", "ACP","C", "A", "PCP", "?");




         System.out.println("------------------local alignment---------------------- ");
         SmithWaterman sw = new SmithWaterman(Tgene4, Tgene5);
         System.out.println("Alignment Score: " + sw.computeSmithWaterman());
         sw.printMatrix();








         System.out.println("\n\n ");
         System.out.println("------------------LCS---------------------- ");
         LCS_score sw1 = new LCS_score();  
         List<String> Qg5 = Arrays.asList("KS", "AT", "DH", "KR","ACP","KS", "AT", "DH", "KR","ACP");
         List<String> Qg6 = Arrays.asList("DH", "KR","ACP", "AT", "DH", "KR","ACP");

         List<String> Qg5 = Arrays.asList("C", "A", "PCP", "Red");
         List<String> Qg6 = Arrays.asList("C", "A", "PCP", "?");


         List<String> Qg5 = Arrays.asList("KS", "AT", "DH", "KR","ACP");
         List<String> Qg5 = Arrays.asList("KS", "AT",  "KR","ACP");
         List ll1 = new LinkedList();
         List ll2 = new LinkedList();
         sw1.setStrings(Qg5, Qg6);
         for (int i = 0 ; i < Qg5.size() ; i++)
         ll2.add(new String(Qg5.get(i)));
         for (int i = 0 ; i < Qg6.size() ; i++)
         ll1.add(new String(Qg6.get(i)));
         System.out.println(Qg5.toString().replace("[", "").replace("]", "").replace(",", ""));
         sw1.computeLengthMatrix();

         sw1.extractLongestcommonsubseq();
         sw1.printLongestcommonsubseq();
         sw1.fillScoreArray();
         //sw.printArray();
         //sw.printPath();

         System.out.println("value of best alignment is "+sw1.bestval);


         // sw.printMatrix();

         //  sw = new SmithWaterman("gcgcgtgc", "gcaagtgca");
         //  System.out.println("Alignment Score: " + sw.computeSmithWaterman());
         // sw.printMatrix();
         */    }
}