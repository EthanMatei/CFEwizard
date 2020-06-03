package cfe.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cfe.parser.MSAccessParser;
import cfe.services.DisorderService;


public class MSAccessParserTest {
	
	String path = "./test-data/ms-access/";
	
	File folder = new File(path);	
		
	File[] files = folder.listFiles(new FilenameFilter() {
	    public boolean accept(File dir, String name) {
	        return name.toLowerCase().endsWith(".accdb");
	    }
	});
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
//	@Test
	public void testParser() {
	
		// Just some basic timing
		// Could have used nanoTime(), but we don't need that much precision
		long startTime = System.currentTimeMillis();
		
	   	int NUM_THREADS = Math.min(files.length ,Runtime.getRuntime().availableProcessors());
    	
		List<Future<Long>> list = new ArrayList<Future<Long>>();
		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS); //ThreadUtils.getExecutor();
		
		try {
			for (File fileEntry : files) {
				
		        if (!fileEntry.isDirectory()) {
		        	
		        	 Worker worker = new Worker(path + fileEntry.getName());
				      
				      // I don't care about the return value
				      Future<Long> submit = executor.submit(worker);
				      
				      list.add(submit);
		        }
		    }
			
			// Force exception if necessary
			@SuppressWarnings("unused")
			long sum = 0;
			for (Future<Long> future : list) {
				sum += future.get();
			}
			
		    // This will make the executor accept no new threads
		    // and finish all existing threads in the queue
		    executor.shutdown();
		    
		    // Wait until all threads are finished
		    executor.awaitTermination(50L, TimeUnit.SECONDS);
		    
		    // Update the disorder table
		    DisorderService.update();
			
		    long endTime = System.currentTimeMillis();
			
			double totalTime = (endTime - startTime)*0.001; // in seconds
			
			if (totalTime > 60.0)
				System.out.println("Done. Total time: " + totalTime/60.0 + " minutes");
			else
				System.out.println("Done. Total time: " + totalTime + " seconds");
		    
		}	catch (Exception ioe)	{			
				executor.shutdownNow();
				Thread.currentThread().interrupt();
				fail("Test error" + ioe);			
		}
		/**
		//fail("Not yet implemented");
		MSAccessParser msparser = new MSAccessParser();
		
		try {
			for (File fileEntry : files) {
				
		        if (!fileEntry.isDirectory()) {
		        	
		        	System.out.println("********************************\nProcessing " + fileEntry.getName() + "\n********************************");
		        	
		        	msparser.parse(path + fileEntry.getName());	            
		        }
		    }
			
		} catch (Exception e) {
			fail("Test error: " + e);		
		}	
		*/	
	}
		
	class Worker implements Callable<Long> {
		
		private String filename;

		Worker(String filename){
			
			this.filename = filename;
		}

		public Long call() throws Exception{
		
			MSAccessParser msparser = new MSAccessParser();
					
			msparser.parse(filename);
								
			return 1L;
		}
	}
}
