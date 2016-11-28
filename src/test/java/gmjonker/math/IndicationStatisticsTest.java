package gmjonker.math;

import org.apache.commons.math3.util.MathArrays;
import org.junit.*;

import static gmjonker.TestUtil.ind;
import static gmjonker.math.GeneralMath.standardDeviation;
import static gmjonker.math.GeneralMath.weightedStandardDeviation;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static spock.util.matcher.HamcrestMatchers.closeTo;

public class IndicationStatisticsTest
{
    @Test
    public void euclideanDist()
    {
        double dist1 = MathArrays.distance(new double[]{.1, .2, .3}, new double[]{-.2, -.5, .12});
        System.out.println("dist1 = " + dist1);

        double dist2 = IndicationStatistics.euclideanDistance(
                asList(ind( .1, 1), ind( .2, 1), ind( .3 , 1)),
                asList(ind(-.2, 1), ind(-.5, 1), ind( .12, 1))
        );
        System.out.println("dist2 = " + dist2);
    }

    @Test
    public void standardDev()
    {
        double eps = .000001;
        assertThat(weightedStandardDeviation(new double[] {1, 2, 3}, new double[] {1, 1, 1}), closeTo(standardDeviation(new double[] {1, 2, 3}), eps));
        assertThat(weightedStandardDeviation(new double[] {1, 2, 3}, new double[] {1, 2, 1}), not(equalTo(standardDeviation(new double[] {1, 2, 3}))));
        System.out.println("weightedStandardDeviation(new double[] {1, 2, 3}, new double[] {1, 1, 1}) = " + weightedStandardDeviation(new double[]{1, 2, 3}, new double[]{1, 1, 1}));
        System.out.println("weightedStandardDeviation(new double[] {1, 2, 3}, new double[] {1, 2, 1}) = " + weightedStandardDeviation(new double[]{1, 2, 3}, new double[]{1, 2, 1}));
        System.out.println("weightedStandardDeviation(new double[] {1, 2, 3}, new double[] {1, 3, 1}) = " + weightedStandardDeviation(new double[]{1, 2, 3}, new double[]{1, 3, 1}));
        System.out.println("weightedStandardDeviation(new double[] {1, 2, 3}, new double[] {3, 1, 3}) = " + weightedStandardDeviation(new double[]{1, 2, 3}, new double[]{3, 1, 3}));
    }
}