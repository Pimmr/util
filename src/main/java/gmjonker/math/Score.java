package gmjonker.math;

import gmjonker.util.FormattingUtil;
import gmjonker.util.LambdaLogger;

import java.util.List;

import static gmjonker.math.GeneralMath.abs;
import static gmjonker.math.NaType.NA;
import static gmjonker.math.NaType.isValue;
import static gmjonker.util.ScoreValueUtil.scoreValueEquals;

/**
 * A tuple of a value and a confidence. The value is a point estimation of the true value of some variable, confidence
 * is a measure of the probability of the point estimation being true.
 * <ul>
 *     <li>confidence = 0: no indication</li>
 *     <li>0 &lt; confidence &lt;= .25: weak indication</li>
 *     <li>.25 &lt; confidence &lt;= .75: medium indication</li>
 *     <li>.75 &lt; confidence: strong indication</li>
 * </ul>
 */
public class Score
{
    // This is actually problematic, because the neutral score can differ per application
    public static final double NEUTRAL_SCORE = 0.5;
    public static final Score NA_SCORE = new Score(NA, NA);
    public static final Score UNKNOWN = new Score(NA, 0);
    public static final Score MAX = new Score(1, 1);

    public final double value;
    public final double confidence;
    
    protected static final LambdaLogger log = new LambdaLogger(Score.class);

    public Score(double value, double confidence)
    {
        this.value = value;
        this.confidence = confidence;
    }

    /** Converts from (-1,1) range to (0,1) range. **/
    public static Score fromMinusOneOneRange(double value, double confidence)
    {
        return new Score(ScoreMath.minusOneOneRangeToZeroOneRange(value), confidence);
    }

    public static boolean isValidScore(Score score)
    {
        return score != null && score.isValid();
    }

    /**
     * Has valid value and valid confidence.
     */
    public boolean isValid()
    {
        return isValue(value) && isValue(confidence);
    }

    /** Is valid and confidence > 0. **/
    public boolean isIndication()
    {
        return isValid() && confidence > 0;
    }

    public Score combineWith(Score score)
    {
        Score result = ScoreMath.combine01(this, score);
        log.trace("this: {}", this);
        log.trace("score: {}", score);
        log.trace("combined Score: {}", result);
        return result;
    }

    /**
     * Derives a double in range (0,1). Note that it is assumed that score values are in range (0, 1).
     */
    public double deriveDouble01()
    {
        if ( ! isValue(value) || ! isValue(confidence))
            return NA;
        return NEUTRAL_SCORE + (value - NEUTRAL_SCORE) * confidence;
    }

    /**
     * Derives a double in range (-1,1). Note that it is assumed that score values are in range (0, 1).
     */
    public double deriveDoubleM11()
    {
        if ( ! isValue(value) || ! isValue(confidence))
            return NA;
        return ScoreMath.zeroOneRangeToMinusOneOneRange(value) * confidence;
    }

    public boolean isWeakOrNeutral()
    {
        return abs(deriveDoubleM11()) < .25;
    }

    public Score withConfidence(double confidence)
    {
        return new Score(this.value, confidence);
    }

    public Score multiplyConfidence(double factor)
    {
        return new Score(value, confidence * factor);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score = (Score) o;
        return scoreValueEquals(score.value, value) && scoreValueEquals(score.confidence, confidence);
    }

    /** Hashcode function generated by IntelliJ. **/
    @Override
    public int hashCode()
    {
        int result;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(confidence);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String toShortString()
    {
        return FormattingUtil.asPercentage(value) + "/" + FormattingUtil.asPercentage(confidence);
    }

    public String toAlignedString()
    {
        return FormattingUtil.asPercentageTwoSpaces(value) + "/" + FormattingUtil.asPercentageTwoSpaces(confidence);
    }

    @Override
    public String toString()
    {
        return "Score{" + toShortString() + "}";
    }

    public static String printScoresAligned(List<Score> scores)
    {
        String result = "";
        for (Score score : scores)
            result += "  " + score.toAlignedString() + "\n";
        return result;
    }

    public boolean isNa()
    {
        return this.equals(NA_SCORE);
    }
}