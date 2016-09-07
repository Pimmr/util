package gmjonker.math;

import com.google.common.base.Strings;
import com.google.common.primitives.Doubles;
import gmjonker.util.LambdaLogger;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static gmjonker.math.GeneralMath.abs;
import static gmjonker.math.GeneralMath.sign;
import static gmjonker.math.NaType.NA;
import static gmjonker.math.NaType.isValue;
import static gmjonker.util.FormattingUtil.*;
import static gmjonker.util.ScoreValueUtil.scoreValueEquals;

/**
 * A tuple of a value in range (-1,1) and a confidence in range (0,1).
 *
 * <p>The value is a point estimation of the true value of some variable. The range is (-1,1) is chosen so that Indication
 * can easily be used as a preference or a correlation, with value 0 meaning neutral preference or no correlation.
 *
 * <p>Confidence is a measure of the probability of the point estimation being true.
 * <ul>
 *     <li>confidence = 0: no indication</li>
 *     <li>0 &lt; confidence &lt;= .25: weak indication</li>
 *     <li>.25 &lt; confidence &lt;= .75: medium indication</li>
 *     <li>.75 &lt; confidence $lt; 1: strong indication</li>
 *     <li>confidence = 1: certainty</li>
 * </ul>
 *
 * Indication replaces Score, which didn't have a range defined which was inconvenient w.r.t. the neutral score.
 */
@SuppressWarnings("WeakerAccess")
@AllArgsConstructor
public class Indication implements Comparable<Indication>
{
    public static final Indication NA_INDICATION = new Indication(NA, NA);
    public static final Indication UNKNOWN = new Indication(NA, 0);
    public static final Indication NONE = new Indication(0, 0);
    public static final Indication CERTAINTY = new Indication(1, 1);

    public final double value;
    public final double confidence;

    @Getter public final String comment; // can be handy for explanations

    protected static final LambdaLogger log = new LambdaLogger(Indication.class);

    public Indication(double value, double confidence)
    {
        this.value = value;
        this.confidence = confidence;
        this.comment = "";
    }

    public static boolean isValidIndication(Indication indication)
    {
        return indication != null && indication.isValid();
    }

    /**
     * Has valid value and valid confidence.
     */
    public boolean isValid()
    {
        return isValue(value) && isValue(confidence);
    }

    /** Is valid and confidence > 0. **/
    public boolean indicatesSomething()
    {
        return isValid() && confidence > 0;
    }

    public boolean isNa()
    {
        return this.equals(NA_INDICATION);
    }

    public Indication combineWith(Indication indication)
    {
        Indication result = IndicationMath.combine(this, indication);
        log.trace("this: {}", this);
        log.trace("indication: {}", indication);
        log.trace("combined Indication: {}", result);
        return result;
    }

    public Indication combineWithNoDisagreementEffect(Indication indication)
    {
        Indication result = IndicationMath.combineNoDisagreementEffect(this, indication);
        log.trace("this: {}", this);
        log.trace("indication: {}", indication);
        log.trace("combined Indication: {}", result);
        return result;
    }

    public Indication multiply(double valueFactor, double confidenceFactor)
    {
        return new Indication(this.value * valueFactor, this.confidence * confidenceFactor);
    }

    /**
     * Simply multiplies respective values and indications.
     */
    public Indication multiplyWith(Indication indication)
    {
        return new Indication(this.value * indication.value, this.confidence * indication.confidence);
    }

    /**
     * Derives a double in range (0,1).
     * @param neutralIndication Value in (0,1) that corresponds with the neutral point (0 in range (-1,1)). For instance: .5
     */
    public double deriveDouble01(double neutralIndication)
    {
        if ( ! isValue(value) || ! isValue(confidence))
            return NA;
        if (value > 0) {
            return neutralIndication + value * confidence * (1 - neutralIndication);
        } else {
            return neutralIndication + value * confidence * neutralIndication;
        }
    }

    /**
     * Derives a double in range (-1,1).
     */
    public double deriveDouble()
    {
        if (confidence == 0)
            return 0;
        if ( ! isValue(value) || ! isValue(confidence))
            return NA;
        return value * confidence;
    }

    public boolean isWeakOrNeutral()
    {
        return abs(deriveDouble()) < .25;
    }

    public Indication withConfidence(double confidence)
    {
        return new Indication(this.value, confidence);
    }

    public Indication multiplyConfidence(double factor)
    {
        return new Indication(value, confidence * factor);
    }

    public Indication withComment(String comment)
    {
        return new Indication(value, confidence, comment);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Indication indication = (Indication) o;
        return scoreValueEquals(indication.value, value) && scoreValueEquals(indication.confidence, confidence);
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

    @Override
    public String toString()
    {
        return asPercentage(value).trim() + "/" + asPercentage(confidence).trim() + "(" + comment + ")";
    }

    public String toLongString()
    {
        return String.format("%.5f/%.5f->%.5f (%s)", value, confidence, deriveDouble(), comment);
    }

    public String toShortString()
    {
        return asPercentage(value) + "/" + asPercentage(confidence);
    }

    public String toShortStringWithComment()
    {
        return toShortString() + "(" + comment + ")";
    }

    /** 4/8, 0/T. Not used much... **/
    public String toMicroString()
    {
        return toMicroFormatM11(value) + "/" + toMicroFormatM01(confidence);
    }

    public String toMicroStringWithComment()
    {
        return toMicroString() + "(" + comment + ")";
    }

    /** 4A, 9F **/
    public String toPicoString()
    {
        return toMicroFormatM11(value) + toMicroFormatABC(confidence);
    }

    public String toPicoStringWithComment()
    {
        return toMicroFormatM11(value) + toMicroFormatABC(confidence) + "(" + comment + ")";
    }

    /** 1, 6 **/
    public String toNanoString()
    {
        return toMicroFormatM01(deriveDouble01(.5));
    }

    public String toAlignedString()
    {
        return asPercentageTwoSpaces(value) + "/" + asPercentageTwoSpaces(confidence);
    }

    public static String printIndicationsAligned(List<Indication> indications)
    {
        String result = "";
        for (Indication indication : indications)
            result += "  " + indication.toAlignedString() + "\n";
        return result;
    }

    public String serialize()
    {
        return String.format("%.5f/%.5f", value, confidence);
    }

    public static Indication deserialize(String s)
    {
        if (Strings.isNullOrEmpty(s))
            return NA_INDICATION;
        try {
            String[] split = s.split("/");
            Double value = Doubles.tryParse(split[0]);
            Double confidence = Doubles.tryParse(split[1]);
            return new Indication(value != null ? value : NA, confidence != null ? confidence : NA);
        } catch (Exception ex) {
            log.error("Could not parse '{}'", s);
            throw ex;
        }
    }

    public static Indication[] toPrimitiveIndicationArray(List<Indication> indicationList)
    {
        Indication[] indications = new Indication[indicationList.size()];
        for (int i = 0; i < indicationList.size(); i++) {
            Indication indication = indicationList.get(i);
            indications[i] = indication;
        }
        return indications;
    }

    @Override
    public int compareTo(Indication indication)
    {
        if ( ! isValid())
            return -1;
        if ( ! indication.isValid())
            return 1;
        return sign(this.deriveDouble() - indication.deriveDouble());
    }

    public Indication copy()
    {
        return new Indication(value, confidence, comment);
    }

    public Score toScore01()
    {
        return Score.fromMinusOneOneRange(value, confidence);
    }
}