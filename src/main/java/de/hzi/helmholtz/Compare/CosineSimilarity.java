package de.hzi.helmholtz.Compare;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

public class CosineSimilarity {

    /**
     * Calculate the similarity of two strings using Cosine Similarity
     *
     * @param stringOne First input string
     * @param stringTwo Second input string
     * @return cosine of the two angles (percentage of similarity)
     */
    public static Collection<String> union(List<String> string1, List<String> string2) {
        Collection<String> mergedVector = new TreeSet<String>();
        mergedVector.addAll(stringToCharacterSet(string1));
        mergedVector.addAll(stringToCharacterSet(string2));
        return uniqueCharacters(mergedVector);
    }

    public static Collection<String> uniqueCharacters(Collection<String> vector) {
        Collection<String> uniqueSet = new HashSet<String>();
        for (String c : vector) {
            if (!uniqueSet.contains(c)) {
                uniqueSet.add(c);
            }
        }
        return uniqueSet;
    }

    /**
     * Convert a string to a set of characters
     *
     * @param string input string
     * @return set of characters
     */
    public static Collection<String> stringToCharacterSet(List<String> strings) {
        Collection<String> charSet = new HashSet<String>();
        for (String string : strings) {
            charSet.add(string);
        }
        return charSet;
    }

    public double calculate(List<String> stringOne, List<String> stringTwo) {
        Collection<String> unionOfStringsOneAndTwo = union(stringOne, stringTwo);
        Collection<Integer> stringOneOccurrenceVector = createFrequencyOfOccurrenceVector(stringOne, unionOfStringsOneAndTwo);
        Collection<Integer> stringTwoOccurrenceVector = createFrequencyOfOccurrenceVector(stringTwo, unionOfStringsOneAndTwo);

        int dotProduct = 0;
		//This should be an unnecessary exception since we're submitting the union
        //of both strings
        try {
            dotProduct = dotp(stringOneOccurrenceVector, stringTwoOccurrenceVector);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(stringOneOccurrenceVector);
            System.out.println(stringTwoOccurrenceVector);
            return -2;
        }

        double vectorOneMagnitude = magnitude(stringOneOccurrenceVector);
        double vectorTwoMagnitude = magnitude(stringTwoOccurrenceVector);
     //   double minmax = ((double) Math.min(stringOne.size(), stringTwo.size()) / Math.max(stringOne.size(), stringTwo.size())) * dotProduct;
        double sim_score = (double)dotProduct / (vectorOneMagnitude * vectorTwoMagnitude);
		
        double score = (double)Math.min(stringOne.size(), stringTwo.size())/Math.max(stringOne.size(), stringTwo.size());
        double s = sim_score*score;
        
        
        return sim_score;
    }

    public static int dotp(Collection<Integer> vectorOne, Collection<Integer> vectorTwo) {
        return dotp(vectorOne.toArray(new Integer[0]), vectorTwo.toArray(new Integer[0]));
    }

    /**
     * Calculate the Magnitude of a vector
     *
     * @param vector Vector
     * @return Magnitude of the Vector
     */
    public static double magnitude(Collection<Integer> vector) {
        return magnitude(vector.toArray(new Integer[0]));
    }

    /**
     * Calculate the Magnitude of a vector
     *
     * @param vector Vector
     * @return Magnitude of the Vector
     */
    public static double magnitude(Integer[] vector) {
        double magnitude = 0;
        for (int i = 0; i < vector.length; i++) {
            magnitude += Math.pow(vector[i], 2);
        }
        return Math.sqrt(magnitude);
    }

    /**
     * Calculate the Dot Product (inner product) of two vectors
     *
     * @param vectorOne Vector
     * @param vectorTwo Vector
     * @return Dot Product
     * @throws Exception
     * @throws VectorMathException Thrown if vectors are not of equal length
     */
    public static int dotp(Integer[] vectorOne, Integer[] vectorTwo) {
        if (vectorOne.length != vectorTwo.length) {

        }
        int dotProduct = 0;
        for (int i = 0; i < vectorOne.length; i++) {
            dotProduct += (vectorOne[i] * vectorTwo[i]);
        }
        return dotProduct;
    }

    /**
     * Get the Frequency of Occurrence Vector from the Union Set and target
     * string
     *
     * @param string Input String
     * @param unionSet Set of all Character-dimensions
     * @return Frequency of Occurrence Vector
     */
    private static Collection<Integer> createFrequencyOfOccurrenceVector(List<String> string, Collection<String> unionSet) {
        Collection<Integer> occurrenceVector = new Vector<Integer>();
        for (String c : unionSet) {
            occurrenceVector.add((Integer) countCharacter(string, c));
        }
        return occurrenceVector;
    }

    /**
     * Count the number of times a character occurs in a string
     *
     * @param string Input String
     * @param character Character to be counted
     * @return Frequency of Occurrence
     */
    private static int countCharacter(List<String> string, String character) {
        int count = 0;
        for (String c : string) {
            if (c.equals(character)) {
                count++;
            }
        }
        return count;
    }

}
