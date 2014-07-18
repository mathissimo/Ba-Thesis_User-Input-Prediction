package de.htw_berlin.userinputprediction.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.htw_berlin.userinputprediction.copyjob.UFilePath;
import de.htw_berlin.userinputprediction.properties.ULocationEngine;

public class ULocationEngineTest {
  @BeforeClass
  public void beforeClass() {
  }
  
  @Test
  public void folderNamesAtHierarchieLevel() {
	  String [] expected1 = {
			  "alpha",
			  "alpha",
			  "alpha"
	  };
	  
	  UFilePath [] testPath1 = {
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/10.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/09.27.1977/test.txt"), 
	  };
	  Assert.assertEquals(ULocationEngine.folderNamesAtHierarchieLevel(testPath1,0),expected1);
	  
	  String [] expected2 = {
			  "12.27.1977",
			  "10.27.1977",
			  "09.27.1977"
	  };
	  
	  UFilePath [] testPath2 = {
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/10.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/09.27.1977/test.txt"), 
	  };
	  Assert.assertEquals(ULocationEngine.folderNamesAtHierarchieLevel(testPath2,3),expected2);
	  
	  
	  UFilePath [] testPath3 = {
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/10.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/"), 
	  };
	  Assert.assertNull(ULocationEngine.folderNamesAtHierarchieLevel(testPath3,3));
	  
  }

  @Test
  public void alikeDestinationDSF() {
	  UFilePath [] testPath1 = {
			  new UFilePath("/alpha/beta/gamma/test.txt"), 
			  new UFilePath("/alpha/beta/gammb/test.txt"), 
			  new UFilePath("/alpha/beta/gammc/test.txt") 
	  };
	  Assert.assertEquals(ULocationEngine.alikeDestinationDSF(testPath1), 1.0f);
  }

  @Test
  public void avrLevenshteinDistNxM() {
	  String [] teststring1 = {
			  "a",
			  "b",
			  "c"
	  };
	  Assert.assertEquals(ULocationEngine.avrLevenshteinDistNxM(teststring1), 1.0f);
	  
	  float expected2 = 10.0f/6.0f;
	  String [] teststring2 = {
			  "aaaa",
			  "aaab",
			  "aabb",
			  "abbb"
	  };
	  Assert.assertEquals(ULocationEngine.avrLevenshteinDistNxM(teststring2), expected2);
  }
  
  @Test
  public void indexOfDynamicSuperFolder() {
	  
	  UFilePath [] testPath1 = {
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/10.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/09.27.1977/test.txt"), 
	  };
	  Assert.assertEquals(ULocationEngine.indexOfDynamicSuperFolder(testPath1), 3);

	  UFilePath [] testPath2 = {
			  new UFilePath("/alpha/beta/gamma/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/10.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/09.27.1977/test.txt"), 
	  };
	  Assert.assertEquals(ULocationEngine.indexOfDynamicSuperFolder(testPath2), ULocationEngine.DSF_SEARCH_FAILED);
	  
	  UFilePath [] testPath3 = {
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta1/gamma/10.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/09.27.1977/test.txt"), 
	  };
	  Assert.assertEquals(ULocationEngine.indexOfDynamicSuperFolder(testPath3), ULocationEngine.DSF_SEARCH_FAILED);
	  
	  UFilePath [] testPath4 = {
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
	  };
	  Assert.assertEquals(ULocationEngine.indexOfDynamicSuperFolder(testPath4), ULocationEngine.DSF_SEARCH_ALL_EQUAL);
	  
	  UFilePath [] testPath5 = {
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta1/gamma/12.27.1977/test.txt"), 
	  };
	  Assert.assertEquals(ULocationEngine.indexOfDynamicSuperFolder(testPath5), 1);
  }

  @Test
  public void constantSuperFolderPattern() {
	  
	  UFilePath [] testPath1 = {
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/10.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/09.27.1977/test.txt"), 
	  };
	  Assert.assertTrue(ULocationEngine.constantSuperFolderPatternFound(testPath1));

	  UFilePath [] testPath2 = {
			  new UFilePath("/alpha/beta/gamma/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/10.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/09.27.1977/test.txt"), 
	  };
	  Assert.assertFalse(ULocationEngine.constantSuperFolderPatternFound(testPath2));
	  
	  UFilePath [] testPath3 = {
			  new UFilePath("/alpha/beta/gamma/30.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/10.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/09.27.1977/test.txt"), 
	  };
	  Assert.assertFalse(ULocationEngine.constantSuperFolderPatternFound(testPath3));
	  
	  UFilePath [] testPath4 = {
			  new UFilePath("/alpha/beta/gamma/12-27-1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
	  };
	  Assert.assertFalse(ULocationEngine.constantSuperFolderPatternFound(testPath4));
	  
	  UFilePath [] testPath5 = {
			  new UFilePath("/alpha/beta/gamma/1977-12-01/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/1977-12-27/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/1977-12-31/test.txt"), 
	  };
	  Assert.assertTrue(ULocationEngine.constantSuperFolderPatternFound(testPath5));
	  
	  UFilePath [] testPath6 = {
			  new UFilePath("/alpha/beta/gamma/02.30.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
			  new UFilePath("/alpha/beta/gamma/12.27.1977/test.txt"), 
	  };
	  Assert.assertFalse(ULocationEngine.constantSuperFolderPatternFound(testPath6));
  }

}
