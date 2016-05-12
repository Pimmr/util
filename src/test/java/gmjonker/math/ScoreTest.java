package gmjonker.math;

import gmjonker.util.ScoreValueUtil;
import org.junit.*;

import static gmjonker.matchers.ScoreDoubleEqualityMatcher.equalsScoreDouble;
import static gmjonker.math.NaType.NA;
import static gmjonker.math.Score.NEUTRAL_SCORE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class ScoreTest
{
    @Test
    public void isValidWorksCorrectly()
    {
        assertTrue(new Score(.1, .2).isValid());
        assertFalse(Score.NA_SCORE.isValid());
        assertFalse(new Score(1, NA).isValid());
        assertFalse(new Score(NA, 1).isValid());
    }

    @Test
    public void isIndication()
    {
        assertTrue(new Score(.1, .2).isIndication());
        assertFalse(new Score(.1, 0).isIndication());
        assertFalse(new Score(.1, -1).isIndication());
        assertFalse(new Score(.1, NA).isIndication());
        assertFalse(Score.NA_SCORE.isIndication());
    }

    @Test
    public void deriveDoubleWorksCorrectly()
    {
        assertThat(new Score(1 , 1 ).deriveDouble01(), equalsScoreDouble(1));
        assertThat(new Score(1 , 0 ).deriveDouble01(), equalsScoreDouble(NEUTRAL_SCORE));
        assertThat(new Score(0 , 1 ).deriveDouble01(), equalsScoreDouble(0));
        assertThat(new Score(.8, .3).deriveDouble01(), equalsScoreDouble(NEUTRAL_SCORE + (.8 - NEUTRAL_SCORE) * .3));
        assertThat(new Score(.2, .3).deriveDouble01(), equalsScoreDouble(NEUTRAL_SCORE + (.2 - NEUTRAL_SCORE) * .3));
    }

    @Test
    public void isWeakOrNeutralWorksCorrectly()
    {
        assertFalse(new Score(.1, .5).isWeakOrNeutral());
        assertTrue(new Score(NEUTRAL_SCORE, 1).isWeakOrNeutral());
        assertTrue(new Score(1, 0).isWeakOrNeutral());
        assertTrue(new Score(1, 0.2).isWeakOrNeutral());
        assertTrue(new Score(ScoreValueUtil.tenBasedScoreToScore(6), 1).isWeakOrNeutral());
        assertTrue(new Score(ScoreValueUtil.tenBasedScoreToScore(5), 1).isWeakOrNeutral());
    }

    @Test
    public void equalsWorksCorrectly()
    {
        double a = 1.000001;
        double b = 0.000001;
        assertFalse((a - b) == 1.0);
        assertTrue(new Score(0, 1).equals(new Score(0, 1)));
        assertTrue(new Score(1, 1).equals(new Score(a - b, a - b)));
        assertTrue(new Score(NA, NA).equals(new Score(NA, NA)));
    }
}