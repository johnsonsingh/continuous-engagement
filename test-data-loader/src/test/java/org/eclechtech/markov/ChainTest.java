package org.eclechtech.markov;

import static org.junit.Assert.assertEquals;

import java.security.InvalidParameterException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { ChainTest.class })
public class ChainTest {

	private final int[] range = { -2, -1, 0, 1, 2 };

	private final int[][] probabilities = { { 20, 25, 35, 15, 05 }, { 05, 15, 35, 25, 20 }, { 25, 20, 15, 35, 05 },
			{ 35, 20, 05, 15, 25 }, { 05, 35, 25, 20, 15 } };

	@Test
	public void tryTransition() {
		Chain chain = new Chain(range, probabilities);
		assertEquals(chain.transition(-2, 0), range[0]);
		assertEquals(chain.transition(-2, 1), range[0]);

		int firstBand = probabilities[0][0];
		assertEquals(chain.transition(-2, firstBand), range[0]);

		int secondBand = probabilities[0][0] + probabilities[0][1];
		assertEquals(chain.transition(-2, secondBand), range[1]);
		assertEquals(chain.transition(-2, secondBand), range[1]);
		assertEquals(chain.transition(-2, secondBand - 1), range[1]);
		assertEquals(chain.transition(-2, secondBand - 1), range[1]);

		int thirdBand = secondBand + probabilities[0][2];
		assertEquals(chain.transition(-2, thirdBand), range[2]);
		assertEquals(chain.transition(-2, thirdBand - 1), range[2]);

		int fourthBand = thirdBand + probabilities[0][3];

		assertEquals(chain.transition(-2, fourthBand), range[3]);
		assertEquals(chain.transition(-2, fourthBand - 1), range[3]);

		int fifthBand = fourthBand + probabilities[0][4];
		assertEquals(chain.transition(-2, fifthBand), range[4]);
		assertEquals(chain.transition(-2, fifthBand - 1), range[4]);
		assertEquals(chain.transition(-2, 100), range[4]);
	}

	@Test(expected = InvalidParameterException.class)
	public void expectException() {
		// value not in range
		Chain chain = new Chain(range, probabilities);
		chain.transition(-3, 0);
	}

	@Test(expected = InvalidParameterException.class)
	public void expectException2() {
		// range length does not match matrix length
		@SuppressWarnings("unused")
		Chain chain = new Chain(new int[] { 1, 2, 3 }, new int[][] { { 1, 2, 3, 4 } });
	}
}
