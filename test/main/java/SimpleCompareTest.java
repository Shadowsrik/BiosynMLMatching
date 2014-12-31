import com.google.common.annotations.VisibleForTesting;
import de.hzi.helmholtz.Compare.SimpleCompare;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by pchundi on 31/12/14.
 */
public class SimpleCompareTest{

    @Test
    public void compareTest(){
        SimpleCompare sc = new SimpleCompare();


        String result=sc.StartComparison("j0","3","3","BSYN1","1","","0","");
        Assert.assertEquals("Success",result);
    }

}
