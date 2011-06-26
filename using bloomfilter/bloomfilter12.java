/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */



import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Collection;

public class bloomfilter12<E> implements Serializable 
{
    private BitSet bitset;
    private int bitSetSize;
    private double bitsPerElement;
    private int expectedNumberOfFilterElements; // expected (maximum) number of elements to be added
    private int numberOfAddedElements; // number of elements actually added to the Bloom filter
    private int k; // number of hash functions

    static final Charset charset = Charset.forName("UTF-8"); // encoding used for storing hash values as strings

    static final String hashName = "MD5"; // MD5 gives good enough accuracy . 
    static final MessageDigest digestFunction;
    static 
    { // The digest method is reused between instances
        MessageDigest tmp;
        try {
            tmp = java.security.MessageDigest.getInstance(hashName);
        } catch (NoSuchAlgorithmException e) {
            tmp = null;
        }
        digestFunction = tmp;
    }

    /*
      Constructs an empty Bloom filter. The optimal number of hash functions (k) is estimated from the total size of the Bloom
      and the number of expected elements.
    */
    
    public bloomfilter12(double c, int n, int k) 
    {
      this.expectedNumberOfFilterElements = n;
      this.k = k;
      this.bitsPerElement = c;
      this.bitSetSize = (int)Math.ceil(c * n);
      numberOfAddedElements = 0;
      this.bitset = new BitSet(bitSetSize);
    }
     
    public bloomfilter12(int bitSetSize, int expectedNumberOElements) 
    {
        this(bitSetSize / (double)expectedNumberOElements,
             expectedNumberOElements,
             (int) Math.round((bitSetSize / (double)expectedNumberOElements) * Math.log(2.0)));
    }
    

    // Constructs an empty Bloom filter with a given false positive probability. 
    public bloomfilter12(double falsePositiveProbability, int expectedNumberOfElements) 
    {
        this(Math.ceil(-(Math.log(falsePositiveProbability) / Math.log(2))) / Math.log(2), // c = k / ln(2)
             expectedNumberOfElements,
             (int)Math.ceil(-(Math.log(falsePositiveProbability) / Math.log(2)))); // k = ceil(-log_2(false prob.))
    }



    // Generates a digest based on the contents of a String.
    public static long createHash(String val, Charset charset) {
        return createHash(val.getBytes(charset));
    }

     // Generates a digest based on the contents of a String.
    public static long createHash(String val) {
        return createHash(val, charset);
    }

    //Generates a digest based on the contents of an array of bytes.
    public static long createHash(byte[] data) 
    {
        long h = 0;
        byte[] res;

        synchronized (digestFunction) {
            res = digestFunction.digest(data);
        }

        for (int i = 0; i < 4; i++) {
            h <<= 8;
            h |= ((int) res[i]) & 0xFF;
        }
        return h;
    }

     //return expected probability of false positives.
    public double expectedFalsePositiveProbability() 
    {
        return getFalsePositiveProbability(expectedNumberOfFilterElements);
    }    
    
    public double getFalsePositiveProbability(double numberOfElements) 
    {
        // (1 - e^(-k * n / m)) ^ k
        return Math.pow((1 - Math.exp(-k * (double) numberOfElements / (double) bitSetSize)), k);

    }

     // Adds an object to the Bloom filter. The output from the object's
    public void add(E element) 
    {
       long hash;
       String valString = element.toString();
       for (int x = 0; x < k; x++) 
       {
           hash = createHash(valString + Integer.toString(x));
           hash = hash % (long)bitSetSize;
           bitset.set(Math.abs((int)hash), true);
       }
       numberOfAddedElements ++;
    }

    /*
      Returns true if the element could have been inserted into the Bloom filter.
      Use getFalsePositiveProbability() to calculate the probability of this
      being correct..
     */
    public boolean contains(E element) 
    {
       long hash;
       String valString = element.toString();
       for (int x = 0; x < k; x++) {
           hash = createHash(valString + Integer.toString(x));
           hash = hash % (long)bitSetSize;
           if (!bitset.get(Math.abs((int)hash)))
               return false;
       }
       return true;
    }

    // Read a single bit from the Bloom filter.
    public boolean getBit(int bit) 
    {
        return bitset.get(bit);
    }

    //  Set a single bit in the Bloom filter.
     
    public void setBit(int bit, boolean value) 
    {
        bitset.set(bit, value);
    }

    public static void main(String [] args)
    {
        bloomfilter12 <Integer> filter = new bloomfilter12 <Integer> (0.323,98000);
        filter.add(23);
        if(filter.contains(23))
        {
            System.out.println("FOund");
        }
    }
    
}
