package de.hzi.helmholtz.Compare;
import java.util.ArrayList;  
import java.util.Arrays;  
import java.util.Iterator;  
import java.util.LinkedHashMap;  
import java.util.List;  
import java.util.Map;  
import java.util.Set;  
import java.util.TreeMap;  
import java.util.TreeSet;  
public class Permutation {  
  
    /** 
     * @param args the command line arguments 
     */  
    public static void main(String[] args) {  
    	long startTime = System.currentTimeMillis();
    	
        long stopTime = System.currentTimeMillis();
		double elapsedTime = ((stopTime - startTime)) ;
		System.out.println("elapsedTime: " +  elapsedTime);
    }  
  
    public List<String> run(List<String> input) {  
    	List<String> output = new ArrayList<String>();
        Symbol[] domain = createInputData(input);  
        Map<String, Symbol[]> buckets = new LinkedHashMap<String, Symbol[]>();  
  
        for (int width = 1; width <= domain.length; width++) {  
            Symbol[] bucket = new Symbol[width];  
            combinations(bucket, domain, buckets);  
        }  
          
        for (Iterator<String> it = buckets.keySet().iterator(); it.hasNext();) {  
            String key = it.next();  
            //System.out.println(key);  
            output.add(key);
        }  
        return output;
    }  
  
    private void combinations(Symbol[] bucket, Symbol[] domain,  
            Map<String, Symbol[]> buckets) {  
  
        if (isNotFull(bucket)) {  
            int nextSlot = findNextEmptySlot(bucket);  
  
            for (int i = 0; i < domain.length; i++) {  
                Symbol[] bucketCopy = new Symbol[bucket.length];  
                System.arraycopy(bucket, 0, bucketCopy, 0, bucketCopy.length);  
                bucketCopy[nextSlot] = domain[i];  
  
                // create a new domain leaving  
                // out the object that has been used  
                Symbol[] domainCopy = new Symbol[domain.length - 1];  
                if (i == 0) {  
                    System.arraycopy(domain, 1, domainCopy, 0, domainCopy.length);  
                } else {  
                    System.arraycopy(domain, 0, domainCopy, 0, i);  
                    System.arraycopy(domain, i + 1, domainCopy, i, domainCopy.length - i);  
                }  
  
                combinations(bucketCopy, domainCopy, buckets);  
            }  
        } else {  
            // these two lines assumes that "AC" and CA are the same so the later is not printed  
            // if you want to see all the output;  
            // remove the Arrays.sort  
            // change the buckets variable from a Map to a normal List  
            Arrays.sort(bucket);  
            buckets.put(Arrays.asList(bucket).toString(), bucket);  
        }  
    }  
  
    boolean isNotFull(Symbol[] bucket) {  
        boolean value = false;  
  
        for (int i = 0; i < bucket.length; i++) {  
            Symbol symbol = bucket[i];  
  
            if (isNotValid(symbol)) {  
                value = true;  
                break;  
            }  
        }  
        return value;  
    }  
  
    boolean isNotValid(Symbol symbol) {  
        return symbol == null || symbol.getName() == null ||  
                symbol.getName().trim().equals("");  
    }  
  
    private Symbol[] createInputData(List<String> input) {  
        Symbol[] value = new Symbol[input.size()];
        for(int i = 0; i< input.size();i++)
        {
        	value[i] = new Symbol(""+input.get(i)+"");
        }
        return value;  
    }  
  
    int findNextEmptySlot(Symbol[] bucket) {  
        int value = -1;  
  
        for (int i = 0; i < bucket.length; i++) {  
            Symbol symbol = bucket[i];  
  
            if (isNotValid(symbol)) {  
                value = i;  
                break;  
            }  
        }  
        return value;  
    }  
}  
  
/** 
 * This class holds the items that are used in the combinations. 
 * The author of this class uses this permutaion and combinations 
 * for other purpose not just for letters (abc). 
 * In that case this class can be replaced by another class 
 * and the permutation will still work 
 *  
 * @author Brighton Kukasira 
 */  
class Symbol implements Comparable<Symbol> {  
  
    private String name;  
  
    Symbol(String aName) {  
        this.name = aName;  
    }  
  
    /** 
     * @return the name 
     */  
    public String getName() {  
        return name;  
    }  
  
    /** 
     * @param name the name to set 
     */  
    public void setName(String name) {  
        this.name = name;  
    }  
  
    @Override  
    public boolean equals(Object o) {  
        boolean value = false;  
  
        if (o instanceof Symbol) {  
            value = name.equals(((Symbol) o).getName());  
        }  
        return value;  
    }  
  
    @Override  
    public int hashCode() {  
        int hash = 7;  
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);  
        return hash;  
    }  
  
    public int compareTo(Symbol o) {  
        return name.compareTo(o.getName());  
    }  
  
    @Override  
    public String toString() {  
        return name;  
    }  
}  