package org.snpeff.snpEffect.testCases.integration;

import org.junit.Test;
import org.snpeff.util.Gpr;

/**
 *
 * Test case
 */
public class TestCasesIntegrationHgvsMnps extends TestCasesIntegrationBase {

	public TestCasesIntegrationHgvsMnps() {
		super();
		shiftHgvs = true;
	}

	@Test
	public void test_01() {
		Gpr.debug("Test");
		String genome = "testHg19Chr17";
		String vcf = "tests/hgvs_mnps.vcf";
		compareHgvs(genome, vcf, false);
	}

}
