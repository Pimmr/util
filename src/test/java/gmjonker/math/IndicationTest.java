package gmjonker.math;

import org.junit.*;

import static gmjonker.matchers.ScoreValueEqualityMatcher.equalsScoreValue;
import static gmjonker.math.NaType.NA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class IndicationTest
{
    @Test
    public void isValidWorksCorrectly()
    {
        assertTrue(new Indication(.1, .2).isValid());
        assertFalse(Indication.NA_INDICATION.isValid());
        assertFalse(new Indication(1, NA).isValid());
        assertFalse(new Indication(NA, 1).isValid());
    }

    @Test
    public void isIndication()
    {
        assertTrue(new Indication(.1, .2).isIndication());
        assertFalse(new Indication(.1, 0).isIndication());
        assertFalse(new Indication(.1, -1).isIndication());
        assertFalse(new Indication(.1, NA).isIndication());
        assertFalse(Indication.NA_INDICATION.isIndication());
    }

    @Test
    public void deriveDoubleWorksCorrectly()
    {
        double neutralValue = .3;
        assertThat(new Indication(1 , 1 ).deriveDouble01(neutralValue), equalsScoreValue(1));
        assertThat(new Indication(1 , 0 ).deriveDouble01(neutralValue), equalsScoreValue(neutralValue));
        assertThat(new Indication(0 , 1 ).deriveDouble01(neutralValue), equalsScoreValue(0));
        assertThat(new Indication(.8, .3).deriveDouble01(neutralValue), equalsScoreValue(neutralValue + (.8 - neutralValue) * .3));
        assertThat(new Indication(.2, .3).deriveDouble01(neutralValue), equalsScoreValue(neutralValue + (.2 - neutralValue) * .3));
    }

    @Test
    public void isWeakOrNeutralWorksCorrectly()
    {
        assertTrue(new Indication(.1, .5).isWeakOrNeutral());
        assertTrue(new Indication(1, 0).isWeakOrNeutral());
        assertTrue(new Indication(1, 0.2).isWeakOrNeutral());
    }

    @Test
    public void equalsWorksCorrectly()
    {
        double a = 1.000001;
        double b = 0.000001;
        assertFalse((a - b) == 1.0);
        assertTrue(new Indication(0, 1).equals(new Indication(0, 1)));
        assertTrue(new Indication(1, 1).equals(new Indication(a - b, a - b)));
        assertTrue(new Indication(NA, NA).equals(new Indication(NA, NA)));
    }
}