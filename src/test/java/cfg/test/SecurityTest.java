package cfg.test;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.FileReader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This class only tests the security methods, not the whole application
 * @author mtavares
 *
 */

public class SecurityTest {
	
	public static final String GOOD_GENE_LIST_FILE_NAME = "./test-data/good_gene_test_list.txt";
	public static final String BAD_GENE_LIST_FILE_NAME  = "./test-data/bad_gene_test_list.txt";
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void testGeneListSecurity() {
		String filename = GOOD_GENE_LIST_FILE_NAME;

		//-------------------------------------------------------
		// Test that good names pass
		//-------------------------------------------------------
		try {

			BufferedReader br = new BufferedReader(new FileReader(filename));
			String gene;
			while ((gene = br.readLine()) != null) {
				assertTrue("Good gene " + gene + " failed when it should have passed.", 
						cfg.secure.Security.passesGeneWhiteList(gene.trim()));
				System.out.println(gene + " passed.");
			}

			br.close();
		} 
		catch (Exception e) {
			fail("Test error: " + e);		
		}
		
		//-------------------------------------------------------
		// Test that bad names fail
		//-------------------------------------------------------
		filename = BAD_GENE_LIST_FILE_NAME;

		try {

			BufferedReader br = new BufferedReader(new FileReader(filename));
			String gene;
			while ((gene = br.readLine()) != null) {
				assertTrue("Bad " + gene + " passed when it should have failed.", 
						!cfg.secure.Security.passesGeneWhiteList(gene.trim()));
				System.out.println("Bad gene \"" + gene + "\" failed.");
			}

			br.close();
		} 
		catch (Exception e) {
			fail("Test error: " + e);		
		}
		
	}
	
	@Test
	public void testcoringWeightsSecurity() {
		String filename = GOOD_GENE_LIST_FILE_NAME;
		
		try {
    	
    	BufferedReader br = new BufferedReader(new FileReader(filename));
    	String gene;
    	while ((gene = br.readLine()) != null) {
    		assertTrue("Failed for gene: " + gene, cfg.secure.Security.passesGeneWhiteList(gene.trim()));
    		System.out.println(gene + " passed.");
    	}
    	
    	br.close();
	
		} catch (Exception e) {
			fail("Test error: " + e);		
		}		
		
	}
}
