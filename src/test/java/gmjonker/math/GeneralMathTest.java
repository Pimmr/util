package gmjonker.math;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;

import static com.google.common.primitives.Doubles.asList;
import static gmjonker.matchers.IsValueMatcher.isValueMatch;
import static gmjonker.math.GeneralMath.*;
import static gmjonker.math.NaType.NA;
import static gmjonker.math.NaType.isValue;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class GeneralMathTest
{
    @Test
    public void poww()
    {
        System.out.println("pow( 2,  2) = " + pow( 2,  2));
        System.out.println("pow( 2, .5) = " + pow( 2, .5));
        System.out.println("pow(-2, .5) = " + pow(-2, .5));
        System.out.println("powSS( 2,  2) = " + powSignSafe( 2,  2));
        System.out.println("powSS( 2, .5) = " + powSignSafe( 2, .5));
        System.out.println("powSS(-2, .5) = " + powSignSafe(-2, .5));
    }
    
    @Test
    public void roundd()
    {
        System.out.println("round(1.23, 1) = " + round(1.23, 1));
        System.out.println("round(1.25, 1) = " + round(1.25, 1));
        System.out.println("round(NaN, 1) = " + round(Double.NaN, 1));
    }

    @Test
    public void maxBy()
    {
        assertThat(maxByI(Arrays.asList("geert", "jonker"), String::length), is(6));
        assertThat(GeneralMath.maxBy(Arrays.asList("geert", "jonker"), s -> s.length() * 2.0), closeTo(12, .0001));
        assertThat(GeneralMath.maxBy(new ArrayList<String>(), s -> s.length() * 2.0), not(isValueMatch()));
    }



    @Test
    public void testMean()
    {
        Assert.assertThat(mean(asList(1, 2, 3.3)), closeTo(6.3/3, .000001));
    }

    @Test
    public void weightedMean()
    {
        double eps = 0.00001;
        assertEquals(4.2/6, GeneralMath.weightedMean(new double[]{0.2, 0.5, 1.0}, new double[]{1d, 2d, 3d}), eps);
        assertEquals(   .5, GeneralMath.weightedMean(new double[]{0.2, 0.5, 1.0}, new double[]{1d, Double.POSITIVE_INFINITY, 3d}), eps);
        assertEquals(  .35, GeneralMath.weightedMean(new double[]{0.2, 0.5, 1.0}, new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 3d}), eps);
    }

    @Test
    public void weightedMeanIgnoreNAss()
    {
        double eps = 0.00001;
        assertEquals(4.2/6, GeneralMath.weightedMeanIgnoreNAs(new double[]{0.2, 0.5, 1.0}, new double[]{1d, 2d, 3d}), eps);
        assertEquals(   .4, GeneralMath.weightedMeanIgnoreNAs(new double[]{0.2, 0.5,  NA}, new double[]{1d, 2d, 3d}), eps);
        assertEquals(   .4, GeneralMath.weightedMeanIgnoreNAs(new double[]{0.2, 0.5, 1.0}, new double[]{1d, 2d, NA}), eps);
    }

    @Test
    public void weightedMeanWithDefaults()
    {
        double eps = 0.00001;
        assertEquals(4.2/6, GeneralMath.weightedMeanWithDefaults(new double[]{0.2, 0.5, 1.0}, new double[]{1d, 2d, 3d}, new double[]{.5, .5, .5}), eps);
        assertEquals(4.2/6, GeneralMath.weightedMeanWithDefaults(new double[]{0.2, NA, 1.0}, new double[]{1d, 2d, 3d}, new double[]{.5, .5, .5}), eps);
        assertEquals(
                GeneralMath.weightedMeanWithDefaults(new double[]{0.2, NA, 1.0}, new double[]{1d, 2d, 3d}, new double[]{.5, .5, .5}),
                GeneralMath.weightedMeanWithDefaults(new double[]{0.2, NA, 1.0}, new double[]{2d, 4d, 6d}, new double[]{.5, .5, .5}),
                eps
        );
    }

    @Test
    public void weightedMeanWithDefaultWithExcludes()
    {
        double eps = 0.00001;
        assertEquals( .2, GeneralMath.weightedMeanWithDefaults(new double[]{0.2, 0.5, 1.0}, new double[]{1d, 2d, 3d},
                new boolean[]{true, false, false}, new double[]{.5, .5, .5}), eps);
        assertEquals( .4, GeneralMath.weightedMeanWithDefaults(new double[]{0.2, 0.5, 1.0}, new double[]{1d, 2d, 3d},
                new boolean[]{true, true, false}, new double[]{.5, .5, .5}), eps);
        assertEquals(1.0, GeneralMath.weightedMeanWithDefaults(new double[]{0.2, 0.5, 1.0}, new double[]{1d, 2d, 3d},
                new boolean[]{false, false, true}, new double[]{.5, .5, .5}), eps);
    }

    @Test
    public void meanAbsoluteError()
    {
        assertEquals(GeneralMath.meanAbsoluteError(new double[]{1.0, .9, .5}), .2, 0.00001);
    }

    @Test
    public void weightedMeanAbsoluteError()
    {
        assertEquals(GeneralMath.weightedMeanAbsoluteError(new double[]{1.0, .5, .2}, new double[]{1, 2, 5}), 5.0/8, 0.00001);
    }

    @Test
    public void powerMean()
    {
        int p = 25;
        System.out.println(GeneralMath.powerMean(new double[]{0, 0, 0}, p));
        System.out.println(GeneralMath.powerMean(new double[]{1, 0, 0}, p));
        System.out.println(GeneralMath.powerMean(new double[]{1, 1, 0}, p));
        System.out.println(GeneralMath.powerMean(new double[]{1, 1, 1}, p));
        System.out.println(GeneralMath.powerMean(new double[]{.1, .2, .3}, p));
        System.out.println();
        System.out.println(GeneralMath.powerMean(new double[]{.5, .5, .5}, p));
        System.out.println(GeneralMath.powerMean(new double[]{1, .5, .5}, p));
        System.out.println(GeneralMath.powerMean(new double[]{1, 1, .5}, p));
        System.out.println(GeneralMath.powerMean(new double[]{1, 1, 1}, p));
        System.out.println(GeneralMath.powerMean(new double[]{0, .5, .5}, p));
        System.out.println(GeneralMath.powerMean(new double[]{0, 1, .5}, p));
        System.out.println();
        System.out.println(GeneralMath.powerMean(new double[]{0, -.5, .5}, 2));
        System.out.println(GeneralMath.powerMean(new double[]{0, -1, .5}, p));
        System.out.println();
        System.out.println(GeneralMath.powerMean(new double[]{.8}, p));
        System.out.println(GeneralMath.powerMean(new double[]{.8, .5}, p));
        System.out.println(GeneralMath.powerMean(new double[]{.8, .5, .5}, p));
        System.out.println();
        System.out.println(GeneralMath.powerMean(new double[]{.8,  0,  0,  0,  0}, 1000));
        System.out.println(GeneralMath.powerMean(new double[]{.8, .8, .8, .8, .8}, 1000));
    }

    @Test
    public void rootMeanSquare()
    {
        assertEquals(Math.sqrt(14.0 / 3), GeneralMath.rootMeanSquare(new double[]{1, 2, 3}), .00001);
    }

    @Test
    public void rootMeanSquareError()
    {
        assertEquals(GeneralMath.rootMeanSquareError(new double[]{.3, .5, .7}), 0.5259911, 0.00001);
    }

    @Test
    public void rootWeightedMeanSquareError()
    {
        assertEquals(GeneralMath.rootWeightedMeanSquareError(new double[]{.3, .5, .7}, new double[]{.5, 1., 2}), 0.439155, 0.00001);
        assertEquals(GeneralMath.rootWeightedMeanSquareError(new double[]{.3, .5, .7}, new double[]{.5, 1., 2}),
                GeneralMath.rootWeightedMeanSquareError(asList(.3, .5, .7), asList(.5, 1., 2)), 0.00001);
    }
    
    @Test
    public void rootWeightedMeanSquareNegSafe()
    {
        assertThat(GeneralMath.rootWeightedMeanSquareNegSafe(Arrays.asList(Pair.of(-1.0, 1.0))), closeTo(-1.0, .000001));
        assertThat(GeneralMath.rootWeightedMeanSquareNegSafe(Arrays.asList(Pair.of(-1.0, 1.0), Pair.of(-3.0, 1.0))), closeTo(-sqrt(5), .000001));
    }

    @Test
    public void harmonicMean()
    {
        double eps = 0.00001;
        assertThat(GeneralMath.harmonicMean(new double[]{1, .5}), closeTo(2d/3, eps));
        assertThat(GeneralMath.harmonicMean(new double[]{2, 3}), closeTo(2.4, eps));
    }

    @Test
    public void f1Measure()
    {
        double eps = 0.00001;
        assertThat(GeneralMath.f1Measure(1, .5), closeTo(GeneralMath.harmonicMean(new double[]{1, .5}), eps));
    }

    @Test
    public void variance()
    {
        double eps = 0.00001;
        // Check our variance function against Colt
        assertThat(GeneralMath.variance(1), closeTo(getColtVariance(1), eps));
        assertThat(GeneralMath.variance(1, .5), closeTo(getColtVariance(1, .5), eps));
        assertThat(GeneralMath.variance(1, .5, .1), closeTo(getColtVariance(1, .5, .1), eps));

        // Check our variance function against commons math.
        Variance variance = new Variance(false);
        assertThat(GeneralMath.variance(1), closeTo(variance.evaluate(new double[]{1}), eps));
        assertThat(GeneralMath.variance(1, .5), closeTo(variance.evaluate(new double[]{1, .5}), eps));
        assertThat(GeneralMath.variance(1, .5, .1), closeTo(variance.evaluate(new double[]{1, .5, .1}), eps));
    }

    private double getColtVariance(double... values)
    {
        DoubleArrayList doubleArrayList = new DoubleArrayList(values);
        double sum = Descriptive.sum(doubleArrayList);
        double sumOfSquares = Descriptive.sumOfSquares(doubleArrayList);
        return Descriptive.variance(values.length, sum, sumOfSquares);
    }

    @Test
    public void exponentialMovingAverageV1()
    {
        double eps = 0.0001;
        assertTrue( ! isValue(GeneralMath.exponentialMovingAverageV1(new double[]{}, .7)));
        assertEquals(1  , GeneralMath.exponentialMovingAverageV1(new double[]{1}, .7), eps);
        assertEquals(.7 , GeneralMath.exponentialMovingAverageV1(new double[]{0, 1}, .7), eps);
        assertEquals(.7 , GeneralMath.exponentialMovingAverageV1(new double[]{0, 0, 1}, .7), eps);
        assertEquals(.21, GeneralMath.exponentialMovingAverageV1(new double[]{0, 1, 0}, .7), eps);
        assertEquals(.09, GeneralMath.exponentialMovingAverageV1(new double[]{1, 0, 0}, .7), eps);
    }

    @Test
    public void exponentialMovingAverageV2()
    {
        double eps = 0.0001;
        assertTrue( ! isValue(GeneralMath.exponentialMovingAverageV2(new double[]{}, .5)));
        assertEquals(.5   , GeneralMath.exponentialMovingAverageV2(new double[]{1}, .5), eps);
        assertEquals(.5   , GeneralMath.exponentialMovingAverageV2(new double[]{0, 1}, .5), eps);
        assertEquals(.5   , GeneralMath.exponentialMovingAverageV2(new double[]{0, 0, 1}, .5), eps);
        assertEquals(.25  , GeneralMath.exponentialMovingAverageV2(new double[]{0, 1, 0}, .5), eps);
        assertEquals(.125 , GeneralMath.exponentialMovingAverageV2(new double[]{1, 0, 0}, .5), eps);
        assertEquals(.5   , GeneralMath.exponentialMovingAverageV2(new double[]{0, 0, 1}, .5), eps);
        assertEquals(.75  , GeneralMath.exponentialMovingAverageV2(new double[]{0, 1, 1}, .5), eps);
        assertEquals(.875 , GeneralMath.exponentialMovingAverageV2(new double[]{1, 1, 1}, .5), eps);
    }

    @Test
    public void exponentialMovingAverageV3()
    {
        double eps = 0.0001;
        assertTrue( ! isValue(GeneralMath.exponentialMovingAverageV3(new double[]{}, .5)));
        assertEquals(1      , GeneralMath.exponentialMovingAverageV3(new double[]{1}, .5), eps);
        assertEquals(.625   , GeneralMath.exponentialMovingAverageV3(new double[]{0, 1}, .5), eps);
        assertEquals(13.0/24, GeneralMath.exponentialMovingAverageV3(new double[]{0, 0, 1}, .5), eps);
    }

    @Test
    public void exponentialMovingAverageV4()
    {
        double eps = 0.0001;
        assertEquals(.4       , GeneralMath.exponentialMovingAverageV4(new double[]{}, .4, .5), eps);
        assertEquals(.7       , GeneralMath.exponentialMovingAverageV4(new double[]{.8}, .4, .75), eps);
        assertEquals(.3+.25*.7, GeneralMath.exponentialMovingAverageV4(new double[]{.8, .4}, .4, .75), eps);
    }

    @Test
    public void tryExponentialMovingAverageV1()
    {
        // I'm trying to find the right alpha for the "local" average of recommendation subscores.
        // This would be good if it resembled the moving average of the last ten items
        GeneralMath.printExponentialCoefficients(.1, 20);
        double alpha = .1;
        System.out.println(GeneralMath.exponentialMovingAverageV1(new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, alpha));
        System.out.println(GeneralMath.exponentialMovingAverageV1(new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, alpha));
        System.out.println(GeneralMath.exponentialMovingAverageV1(new double[]{0, 0, 0, 0, 0, 0, 1, 0, 0, 0}, alpha));
        System.out.println(GeneralMath.exponentialMovingAverageV1(new double[]{0, 0, 0, 1, 0, 0, 0, 0, 0, 0}, alpha));
        System.out.println(GeneralMath.exponentialMovingAverageV1(new double[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, alpha));
    }

    @Test
    public void tryExponentialMovingAverageV2()
    {
        // Trying to find the right alpha to penalize recent similarity
        // similar item 1 down -> hefty penalty
        // similar item 5 down -> hardly any penalty anymore
        double alpha = .5;
        System.out.println(GeneralMath.exponentialMovingAverageV2(new double[]{0, 0, 0, 0, 1}, alpha));
        System.out.println(GeneralMath.exponentialMovingAverageV2(new double[]{0, 0, 0, 1, 0}, alpha));
        System.out.println(GeneralMath.exponentialMovingAverageV2(new double[]{0, 0, 1, 0, 0}, alpha));
        System.out.println(GeneralMath.exponentialMovingAverageV2(new double[]{0, 1, 0, 0, 0}, alpha));
        System.out.println(GeneralMath.exponentialMovingAverageV2(new double[]{1, 0, 0, 0, 0}, alpha));
    }

    @Test
    public void tryExponentialMovingAverageV3()
    {
        // Trying to find the right alpha to penalize recent similarity
        // similar item 1 down -> hefty penalty
        // similar item 5 down -> hardly any penalty anymore
        double alpha = .5;
        System.out.println(GeneralMath.exponentialMovingAverageV3(new double[]{0, 0, 0, 0, 1}, alpha));
        System.out.println(GeneralMath.exponentialMovingAverageV3(new double[]{0, 0, 0, 1, 0}, alpha));
        System.out.println(GeneralMath.exponentialMovingAverageV3(new double[]{0, 0, 1, 0, 0}, alpha));
        System.out.println(GeneralMath.exponentialMovingAverageV3(new double[]{0, 1, 0, 0, 0}, alpha));
        System.out.println(GeneralMath.exponentialMovingAverageV3(new double[]{1, 0, 0, 0, 0}, alpha));
    }

    @Test
    public void expand()
    {
        TextPlot.plotf(GeneralMath::expand, 0, 1, -.2, 1, 100, 40);
    }
    
    @Test
    public void softsign()
    {
        TextPlot.plotf(x -> GeneralMath.softSign(x, .1), -1, 1, -1, 1, 80, 30);
    }
    
    @Test
    public void translateTest()
    {
        assertThat(translate(3, 0, 10, 20, 40), closeTo(26, .000001));
        assertThat(translate(5, 10, 20, 4, 2), closeTo(5, .000001));
    }
    
    @Test
    public void center11()
    {
        double eps = .00001;
        assertThat(centerM11(-1, .2), closeTo(-1, eps));
        assertThat(centerM11( 0, .2), closeTo(-1 + 5d/6, eps));
        assertThat(centerM11(.2, .2), closeTo(0, eps));
        assertThat(centerM11(.4, .2), closeTo(.25, eps));
        assertThat(centerM11( 1, .2), closeTo(1, eps));
    }
}
