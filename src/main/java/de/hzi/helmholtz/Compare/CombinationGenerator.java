//CombinationGenerator.java
//--------------------------------------
// Systematically generate combinations.
//--------------------------------------
package de.hzi.helmholtz.Compare;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CombinationGenerator {

    private int[] a;
    private int n;
    private int r;
    private BigInteger numLeft;
    private BigInteger total;
	//------------
    // Constructor
    //------------
    public CombinationGenerator(int n, int r) {
        if (r > n) {
            throw new IllegalArgumentException();
        }
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        this.n = n;
        this.r = r;
        a = new int[r];
        BigInteger nFact = getFactorial(n);
        BigInteger rFact = getFactorial(r);
        BigInteger nminusrFact = getFactorial(n - r);
        total = nFact.divide(rFact.multiply(nminusrFact));
        reset();
    }

    public List<String> GenerateAllPossibleCombinations(List<String> elements) {
        int[] indices;
        int count = 0;
        List<String> combinations = new ArrayList<String>();
        for (int is = 1; is <= elements.size(); is++) {
            CombinationGenerator x = new CombinationGenerator(elements.size(), is);

            StringBuffer combination;
            int i1 = 0;
            while (x.hasMore()) {
                combination = new StringBuffer();
                indices = x.getNext();
                for (int i = 0; i < indices.length; i++) {
                    if (!combination.toString().trim().equals("")) {
                        combination.append("," + elements.get(indices[i]));
                    } else {
                        combination.append(elements.get(indices[i]));
                    }

                    i1++;
                }
                combinations.add(combination.toString());
                count++;

            }
        }
        return combinations;
    }
        //------
        // Reset
        //------
    public void reset() {
        for (int i = 0; i < a.length; i++) {
            a[i] = i;
        }
        numLeft = new BigInteger(total.toString());
    }
	//------------------------------------------------
    // Return number of combinations not yet generated
    //------------------------------------------------
    public BigInteger getNumLeft() {
        return numLeft;
    }
	//-----------------------------
    // Are there more combinations?
    //-----------------------------
    public boolean hasMore() {
        return numLeft.compareTo(BigInteger.ZERO) == 1;
    }
	//------------------------------------
    // Return total number of combinations
    //------------------------------------
    public BigInteger getTotal() {
        return total;
    }
	//------------------
    // Compute factorial
    //------------------
    private static BigInteger getFactorial(int n) {
        BigInteger fact = BigInteger.ONE;
        for (int i = n; i > 1; i--) {
            fact = fact.multiply(new BigInteger(Integer.toString(i)));
        }
        return fact;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        List<String> o = new ArrayList<String>();
        String jj = "ABCDEFGHIJKLMNOPQRS";
        char[] elements = new char[19];
        for (int i = 0; i < 19; i++) {
            elements[i] = jj.charAt(i);
        }
        System.out.println(elements);
        int[] indices;
        int count = 0;
        for (int is = 1; is <= 19; is++) {
            CombinationGenerator x = new CombinationGenerator(elements.length, is);

            StringBuffer combination;
            int i1 = 0;
            while (x.hasMore()) {
                combination = new StringBuffer();
                indices = x.getNext();
                for (int i = 0; i < indices.length; i++) {
                    combination.append(elements[indices[i]]);

                    i1++;
                }
                o.add(combination.toString());
                count++;
                System.out.println(combination.toString());
            }

            //System.out.println (i1);
        }
        System.out.println(count + "      ddsadas");
        long stopTime = System.currentTimeMillis();
        double elapsedTime = ((stopTime - startTime));
        System.out.println("elapsedTime: " + elapsedTime);
    }

	//--------------------------------------------------------
    // Generate next combination (algorithm from Rosen p. 286)
    //--------------------------------------------------------
    public int[] getNext() {
        if (numLeft.equals(total)) {
            numLeft = numLeft.subtract(BigInteger.ONE);
            return a;
        }
        int i = r - 1;
        while (a[i] == n - r + i) {
            i--;
        }
        a[i] = a[i] + 1;
        for (int j = i + 1; j < r; j++) {
            a[j] = a[i] + j - i;
        }
        numLeft = numLeft.subtract(BigInteger.ONE);
        return a;
    }
}
