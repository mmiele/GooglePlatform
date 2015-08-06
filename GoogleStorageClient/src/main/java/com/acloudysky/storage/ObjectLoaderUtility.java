/** 
 * LEGAL: Use and Disclaimer. 
 * This software belongs to the owner of the http://www.acloudysky.com site and supports the
 * examples described there. 
 * Unless required by applicable law or agreed to in writing, this software is distributed on 
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. 
 * Please, use the software accordingly and provide the proper acknowledgement to the author.
 * @author milexm@gmail.com  
 **/
package com.acloudysky.storage;

import java.io.InputStream;
import java.util.Random;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
// import com.google.common.base.Stopwatch;
import com.google.common.base.Stopwatch;

public class ObjectLoaderUtility {

	
	/**
	   * Generates a random data block and repeats it to provide the stream.
	   * Using a buffer instead of just filling from java.util.Random because the latter causes
	   * noticeable lag in stream reading, which detracts from upload speed. This class takes all that
	   * cost in the constructor.
	   */
	  public static class RandomDataBlockInputStream extends InputStream {

	    private long byteCountRemaining;
	    private final byte[] buffer;

	    public RandomDataBlockInputStream(long size, int blockSize) {
	      byteCountRemaining = size;
	      final Random random = new Random();
	      buffer = new byte[blockSize];
	      random.nextBytes(buffer);
	      
	    }

	    /*
	     * (non-Javadoc)
	     *
	     * @see java.io.InputStream#read()
	     */
	    @Override
	    public int read() {
	      throw new AssertionError("Not implemented; too slow.");
	    }

	    /*
	     * (non-Javadoc)
	     *
	     * @see java.io.InputStream#read(byte [], int, int)
	     */
	    @Override
	    public int read(byte b[], int off, int len) {
	      if (b == null) {
	        throw new NullPointerException();
	      } else if (off < 0 || len < 0 || len > b.length - off) {
	        throw new IndexOutOfBoundsException();
	      } else if (len == 0) {
	        return 0;
	      } else if (byteCountRemaining == 0) {
	        return -1;
	      }
	      int actualLen = len > byteCountRemaining ? (int) byteCountRemaining : len;
	      for (int i = off; i < actualLen; i++) {
	        b[i] = buffer[i % buffer.length];
	      }
	      byteCountRemaining -= actualLen;
	      return actualLen;
	    }
	  }

	  public static class CustomUploadProgressListener implements MediaHttpUploaderProgressListener {
	    
		private final Stopwatch stopwatch = Stopwatch.createStarted();
	    
//	    public  CustomUploadProgressListener() {
//	    }

	    public void progressChanged(MediaHttpUploader uploader) {
	      switch (uploader.getUploadState()) {
	        case INITIATION_STARTED:
	         // stopwatch.start();
	          System.out.println("Initiation has started!");
	          break;
	        case INITIATION_COMPLETE:
	          System.out.println("Initiation is complete!");
	          break;
	        case MEDIA_IN_PROGRESS:
	          // TODO(nherring): Progress works iff you have a content length specified.
	          // System.out.println(uploader.getProgress());
	          System.out.println(uploader.getNumBytesUploaded());
	          break;
	        case MEDIA_COMPLETE:
	          stopwatch.stop();
	          System.out.println(String.format("Upload is complete! (%s)", stopwatch));
	          break;
	        case NOT_STARTED:
	          break;
	      }
	    }
	  }
	
	
}
