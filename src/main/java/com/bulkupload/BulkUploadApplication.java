package com.bulkupload;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import com.dataanalytics.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Math.random;

public class BulkUploadApplication {
    Logger logger = LoggerFactory.getLogger(BulkUploadApplication.class);

	public String generateMD5Hash(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(value.getBytes(), 0, value.length());
		return new String(Base64.encodeBase64(messageDigest.digest(), false, true), "utf-8");
	}

	
	public BufferedInputStream saveJson(String json) throws Exception {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				json.getBytes());
		BufferedInputStream jsonStream = new BufferedInputStream(
				byteArrayInputStream);
		return jsonStream;
	}

	public void uploadBulkData(int threshold) throws IOException,
			NoSuchAlgorithmException {

        ExecutorService executor = Executors.newFixedThreadPool(32);
        List<Future<Result>> futureList = new ArrayList<>();
        Callable<Result> callable = new Callable<Result>() {
            @Override
            public Result call() throws Exception {
                PostMethod post = new PostMethod("http://localhost:8080/api/v1/event");
                HttpClient httpclient = new HttpClient();
                NameValuePair[] requestParameterArray = preparePostParameters();

                // Add request headers to authenticate request

                Header secretKeyHeader = new Header("secret-key", "l3yvgABkAFmJlpEViJFREy9nnP0=");
                Header dateTimeHeader = new Header("date-key", "1418723726110");
                Header appIdHeader = new Header("appId", "1341");

                post.addParameters(requestParameterArray);
                post.addRequestHeader(secretKeyHeader);
                post.addRequestHeader(dateTimeHeader);
                post.addRequestHeader(appIdHeader);
                int responseCode = 0;
                Result result = null;
                try {
                    long currentTime = System.currentTimeMillis();
                    responseCode = httpclient.executeMethod(post);
                    result = new Result(System.currentTimeMillis() - currentTime, responseCode);
                    if (logger.isTraceEnabled()) {
                        logger.trace("Response status code: " + responseCode);
                        logger.trace("Response body: " + post.getResponseBodyAsString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    post.releaseConnection();
                }
                return result;
            }
        };
        for(int i=0; i< threshold; i++){
            Future<Result> future = executor.submit(callable);
            futureList.add(future);
        }
        for(Future<Result> future : futureList){
            try {
                if (logger.isDebugEnabled()) {
                    Result result = future.get();
                    logger.debug("Got result: " + result.resultCode+ " took: "+result.timeTaken+"ms");
                    if (result.resultCode != 202) {
                        logger.error("Failed, shutting down");
                        //break;
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Failed to bulk upload", e);
            }
        }
        executor.shutdown();
	}

	private NameValuePair[] preparePostParameters()
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		NameValuePair cutsomerTokenParam = new NameValuePair("customerEmailHash", generateMD5Hash("abc@xyz.com"));
		NameValuePair employeeTokenParam = new NameValuePair("employeeId",""+(long)(1000000000000L*random()));
		NameValuePair requestTokenParam = new NameValuePair("transactionId",""+(long)(100000000000L*random())%500);
        Event.Type randomType = Event.Type.values()[(int) (random() * Event.Type.values().length)];
        NameValuePair type = new NameValuePair("type", randomType.toString());
        NameValuePair serialNumberParam = new NameValuePair("serialNumber", "");
        if (randomType == Event.Type.SALE_COMPLETE_EVENT
                || randomType == Event.Type.RUN_DELIVERED_EVENT
                || randomType == Event.Type.RUN_REQUESTED_EVENT) {
            serialNumberParam = new NameValuePair("serialNumber", "" + (long) (1000000000000L * random()));
        }
		NameValuePair storeNumberParam = new NameValuePair("storeNumber", "S0"+ (int) (100 * random()));

		NameValuePair[] requestParameterArray = new NameValuePair[6];
		requestParameterArray[0] = cutsomerTokenParam;
		requestParameterArray[1] = employeeTokenParam;
        requestParameterArray[2] = requestTokenParam;
		requestParameterArray[3] = serialNumberParam;
		requestParameterArray[4] = storeNumberParam;
		requestParameterArray[5] = type;
		return requestParameterArray;
	}

    private class Result {
        long timeTaken;
        int resultCode;
        public Result(long timeTaken, int resultCode) {
            this.timeTaken = timeTaken;
            this.resultCode = resultCode;
        }
    }

	public static void main(String[] args) throws Exception {
		BulkUploadApplication bulkUploadApplication = new BulkUploadApplication();
		bulkUploadApplication.uploadBulkData(10000);

	}

}
