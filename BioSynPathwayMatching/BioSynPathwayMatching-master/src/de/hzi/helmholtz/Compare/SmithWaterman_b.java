package de.hzi.helmholtz.Compare;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SmithWaterman_b {
	List<String> mSeqA;
	List<String> mSeqB;
	int countdash=0,countlet=0;
        int[][] mD;
        int mScore;
        List<String>  mAlignmentSeqA = new ArrayList<String>();
        List<String>  mAlignmentSeqB = new ArrayList<String>();
        List<String> reverseViewA= new ArrayList<String>();
        List<String> reverseViewB= new ArrayList<String>();
        void init(List seqA, List seqB) {
                mSeqA = seqA;
                mSeqB = seqB;
                mD = new int[mSeqA.size() + 1][mSeqB.size() + 1];
                for (int i = 0; i <= mSeqA.size(); i++) {
                        mD[i][0] = 0;                   
                }
                for (int j = 0; j <= mSeqB.size(); j++) {
                        mD[0][j] = 0;
                }
        }
        
        void process() {
                for (int i = 1; i <= mSeqA.size(); i++) {
                        for (int j = 1; j <= mSeqB.size(); j++) {
                                int scoreDiag = mD[i-1][j-1] + weight(i, j);
                                int scoreLeft = mD[i][j-1] - 1;
                                int scoreUp = mD[i-1][j] - 1;
                                mD[i][j] = Math.max(Math.max(Math.max(scoreDiag, scoreLeft), scoreUp), 0);
                        }
                }
        }
        
        void backtrack() {
                int i = 1;
                int j = 1;
                int max = mD[i][j];

                for (int k = 1; k <= mSeqA.size(); k++) {
                        for (int l = 1; l <= mSeqB.size(); l++) {
                                if (mD[k][l] > max) {
                                        i = k;
                                        j = l;
                                        max = mD[k][l];
                                }
                        }
                }
                
                mScore = mD[i][j];
                
                int k = mSeqA.size();
                int l = mSeqB.size();
                
                while (k > i) {
                        mAlignmentSeqB.add( "-");
                        mAlignmentSeqA .add(mSeqA.get(k - 1));
                        countdash+=1;
                        k--;
                }
                while (l > j) {
                        mAlignmentSeqA.add( "-");
                        mAlignmentSeqB.add( mSeqB.get(l - 1));
                        countdash+=1;
                        l--;
                }
                
                while (mD[i][j] != 0) {                 
                        if (mD[i][j] == mD[i-1][j-1] + weight(i, j)) {                          
                                mAlignmentSeqA.add( mSeqA.get(i-1));
                                mAlignmentSeqB.add( mSeqB.get(j-1));
                                countlet+=1;
                                i--;
                                j--;                            
                                continue;
                        } else if (mD[i][j] == mD[i][j-1] - 1) {
                                mAlignmentSeqA.add( "-");
                                countdash+=1;
                                mAlignmentSeqB.add( mSeqB.get(j-1));
                                j--;
                                continue;
                        } else {
                                mAlignmentSeqA.add( mSeqA.get(i-1));
                                mAlignmentSeqB.add( "-");
                                countdash+=1;
                                i--;
                                continue;
                        }
                }
                
                while (i > 0) {
                        mAlignmentSeqB.add( "-");
                        mAlignmentSeqA.add( mSeqA.get(i - 1));
                        i--;
                }
                while (j > 0) {
                        mAlignmentSeqA.add( "-");
                        mAlignmentSeqB.add( mSeqB.get(j - 1));
                        j--;
                }
                
             /*   mAlignmentSeqA = new StringBuffer(mAlignmentSeqA).reverse().toString();
                mAlignmentSeqB = new StringBuffer(mAlignmentSeqB).reverse().toString();*/
                
               reverseViewA = Lists.reverse(mAlignmentSeqA);
                reverseViewB = Lists.reverse(mAlignmentSeqB);
                
        }
        
        private int weight(int i, int j) {
                if (mSeqA.get(i - 1).equals(mSeqB.get(j - 1))) {
                        return 2;
                } else {
                        return -1;
                }
        }
        
        void printMatrix() {
                System.out.print("D =       ");
                for (int i = 0; i < mSeqB.size(); i++) {
                        System.out.print(String.format("%s ", mSeqB.get(i)));
                }
                System.out.println();
                for (int i = 0; i < mSeqA.size() + 1; i++) {
                        if (i > 0) {
                                System.out.print(String.format("%s ", mSeqA.get(i-1)));
                        } else {
                                System.out.print("     ");
                        }
                        for (int j = 0; j < mSeqB.size() + 1; j++) {
                                System.out.print(String.format("%4d ", mD[i][j]));
                        }
                        System.out.println();
                }
                System.out.println();
        }
        public String printScoreAndAlignments1() {
        	
        	String k = reverseViewA.toString().replace("[", "").replace("]", "").replace(",", "") + "\n" + reverseViewB.toString().replace("[", "").replace("]", "").replace(",", "");
        	return reverseViewA.toString().replace("[", "").replace("]", "").replace(",", "");
        }
        public String printScoreAndAlignments2() {
        	
        	String k = reverseViewA.toString().replace("[", "").replace("]", "").replace(",", "") + "\n" + reverseViewB.toString().replace("[", "").replace("]", "").replace(",", "");
        	return reverseViewB.toString().replace("[", "").replace("]", "").replace(",", "");
        }
        
 public double scoring() {
        	
        	String k = reverseViewA.toString().replace("[", "").replace("]", "").replace(",", "") + "\n" + reverseViewB.toString().replace("[", "").replace("]", "").replace(",", "");
        	return mScore;
        }
        
        void printScoreAndAlignments() {
                System.out.println("Score: " + mScore);
                System.out.println("Sequence A: " + reverseViewA.toString().replace("[", "").replace("]", "").replace(",", ""));
                System.out.println("Sequence B: " + reverseViewB.toString().replace("[", "").replace("]", "").replace(",", ""));
                System.out.println();
                
        }
        
        public static void main(String [] args) {               
                char[] seqB = { 'A', 'C', 'G', 'A' };
                char[] seqA = { 'T', 'C', 'C', 'G' };
               /* List<String> Qg5 = Arrays.asList("KS", "AT", "DH", "KR","ACP","KS", "AT", "DH", "KR","ACP");
        		List<String> Qg6 = Arrays.asList("DH", "KR","ACP", "AT", "DH", "KR","ACP");
        		*/
        	/*	
                List<String> Qg5 = Arrays.asList("KS",  "KR","ACP","KS", "AT", "DH", "KR","ACP");
          		List<String> Qg6 = Arrays.asList("KS","AT", "KR","ACP", "AT", "DH", "KR","ACP");*/

                
                
                List<String> Qg5 = Arrays.asList("C", "A", "PCP", "Red");
        		List<String> Qg6 = Arrays.asList("C", "A", "PCP", "?");
                
                
                
                SmithWaterman_b sw = new SmithWaterman_b();
                sw.init(Qg5, Qg6);            
                sw.process();
                sw.backtrack();
                
               
                sw.printScoreAndAlignments();
               // sw.printMatrix();
                
                
              //  SmithWaterman1 sw1 = new SmithWaterman1(Qg5, Qg6);
        		System.out.println("\n\n ");
        		System.out.println("------------------LCS---------------------- ");
        		Longestcommonsubseq sw1 = new Longestcommonsubseq(Qg6,Qg5);  
        	/*	List<String> Qg5 = Arrays.asList("KS", "AT", "KR","ACP","KS", "AT", "DH", "KR","ACP");
        		List<String> Qg6 = Arrays.asList("KS","DH", "KR","ACP", "AT", "DH", "KR","ACP");
        			List<String> Qg5 = Arrays.asList("KS", "AT", "DH", "KR","ACP");
                		List<String> Qg5 = Arrays.asList("KS", "AT",  "KR","ACP");*/
        		List ll1 = new LinkedList();
        		List ll2 = new LinkedList();
        		//sw1.setStrings(Qg5, Qg6);
        		for (int i = 0 ; i < Qg5.size() ; i++)
        			ll1.add(new String(Qg5.get(i)));
        		for (int i = 0 ; i < Qg6.size() ; i++)
        			ll2.add(new String(Qg6.get(i)));
        		System.out.println(Qg5.toString().replace("[", "").replace("]", "").replace(",", ""));
        		sw1.computeLengthMatrix();

        		sw1.extractLongestcommonsubseq();
        		sw1.printLongestcommonsubseq();
        		System.out.println(sw.countdash+"           "+sw.countlet);
        		//sw1.fillScoreArray();
        		//sw.printArray();
        		//sw.printPath();

        		//System.out.println("value of best alignment is "+sw.bestval);
                
        }
}