package de.flokyy.denaro;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class NormieWalletAdapter {
	
	/**
	 * Checks if a wallet exists for the given user UUID by making a GET request to the NormieWallet API.
	 * @param userUUID the UUID of the user to check for a wallet
	 * @return true if a wallet exists for the user, false otherwise
	 */
	public static boolean checkIfWalletExists(String userUUID) {
	    try {
	        // Create a TrustManager that trusts all certificates
	        TrustManager[] trustAllCerts = new TrustManager[] {
	            new X509TrustManager() {
	                @Override
	                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
	                    // TODO Auto-generated method stub
	                }

	                @Override
	                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
	                    // TODO Auto-generated method stub
	                }

	                @Override
	                public X509Certificate[] getAcceptedIssuers() {
	                    // TODO Auto-generated method stub
	                    return null;
	                }
	            }
	        };
	        
	        // Create an SSLContext that uses the TrustManager to trust all certificates
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        
	        // Create a URL for the API endpoint and open a connection
	        URL url = new URL("https://example.io:1111/getWalletOnly?uuid=" + userUUID);
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	        con.setRequestMethod("GET");

	        // Get the response code from the connection
	        int responseCode = con.getResponseCode();

	        // If the response code indicates success, read the response and check if a wallet exists for the user
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	            String inputLine;
	            StringBuffer response = new StringBuffer();

	            // Read the response into a StringBuffer
	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();

	            // If the response contains a wallet ID, return true (a wallet exists for the user)
	            if(!response.toString().contains("No linked wallet found")) {
	                return true;
	            }
	            // Otherwise, return false (no wallet exists for the user)
	            else {
	                return false;
	            }
	        } 
	        // If the response code indicates an error, log an error message and return false
	        else {
	            System.out.println("Error while checking wallet for user ID " + userUUID);
	            return false;
	        }
	    } 
	    // If an exception is thrown, log an error message and return false
	    catch (Exception e) {
	        System.out.println("Exception while checking/creating wallet: " + e.getMessage());
	        return false;
	    }
	} 
	
	public static String getWalletWithoutPIN(String userUUID) {
	    try {
	        // Create a TrustManager that trusts all certificates
	        TrustManager[] trustAllCerts = new TrustManager[] {
	            new X509TrustManager() {
	                @Override
	                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
	                    // TODO Auto-generated method stub
	                }

	                @Override
	                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
	                    // TODO Auto-generated method stub
	                }

	                @Override
	                public X509Certificate[] getAcceptedIssuers() {
	                    // TODO Auto-generated method stub
	                    return null;
	                }
	            }
	        };
	        
	        // Create an SSLContext that uses the TrustManager to trust all certificates
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        
	        // Create a URL for the API endpoint and open a connection
	        URL url = new URL("https://nexilabs.io:1111/getWalletOnly?uuid=" + userUUID);
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	        con.setRequestMethod("GET");

	        // Get the response code from the connection
	        int responseCode = con.getResponseCode();

	        // If the response code indicates success, read the response and check if a wallet exists for the user
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	            String inputLine;
	            StringBuffer response = new StringBuffer();

	            // Read the response into a StringBuffer
	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();

	            JSONParser parser = new JSONParser();
	            Object obj = null;
				try {
					obj = parser.parse(response.toString());
					
				} catch (ParseException e1) {
					return null;
				}

				JSONObject jsonObject = (JSONObject) obj;
	            
	            JSONArray results = (JSONArray) jsonObject.get("results");
	    		for (Object objj : results) {
	          
	    			JSONObject jsonTx = (JSONObject) objj;
	    			
	    			String wallet = (String) jsonTx.get("WALLET");
	    			return wallet;
	            }
	        } 
	        // If the response code indicates an error, log an error message and return false
	        else {
	            System.out.println("Error while checking wallet for user ID " + userUUID);
	            return "ERROR";
	        }
	    } 
	    // If an exception is thrown, log an error message and return false
	    catch (Exception e) {
	        System.out.println("Exception while checking/creating wallet: " + e.getMessage());
	        return "ERROR";
	    }
		return "ERROR";
	} 
	
	public static String sendTransaction(String secretPass, String userUUID, String receiver, String amount) {
	    try {
	        
	        // Create trust manager that trusts all certificates
	        TrustManager[] trustAllCerts = new TrustManager[] {
	            new X509TrustManager() {
	                @Override
	                public void checkClientTrusted(X509Certificate[] arg0, String arg1)
	                        throws CertificateException {
	                    // TODO Auto-generated method stub
	                }
	                @Override
	                public void checkServerTrusted(X509Certificate[] arg0, String arg1)
	                        throws CertificateException {
	                    // TODO Auto-generated method stub
	                }
	                @Override
	                public X509Certificate[] getAcceptedIssuers() {
	                    // TODO Auto-generated method stub
	                    return null;
	                }
	            }
	        };
	        
	        // Create SSL context and set it as the default SSL socket factory
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        
	        // Create URL for sending Solana transaction
	        URL url = new URL("https://example.io:1111/send-solana-transaction?receiver=" + receiver + "&uuid=" + userUUID + "&secret=" + secretPass + "&amount=" + amount);
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	        con.setRequestMethod("GET");

	        // Send the request and get the response code
	        int responseCode = con.getResponseCode();

	        // If the response code is HTTP_OK (200), read the response and extract the transaction signature
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	            String inputLine;
	            StringBuffer response = new StringBuffer();

	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();

	            // Parse the response as JSON and check if it contains a transaction signature
	            JSONParser parser = new JSONParser();
	            Object obj = null;
	            try {
	                obj = parser.parse(response.toString());
	            } catch (ParseException e1) {
	                return "ERROR";
	            }
	            JSONObject jsonObject = (JSONObject) obj;
	            if(!response.toString().contains("signature")) {
	                return "ERROR";
	            }
	            else {
	                String sign = (String) jsonObject.get("signature");
	                System.out.println("Transaction sent: " + sign);
	                return sign;
	            }
	        } 
	        // If the response code is not HTTP_OK, return an error message
	        else {
	            System.out.println("Error while sending transaction for user ID " + userUUID);
	            return "ERROR";
	        }
	    } catch (Exception e) {
	        System.out.println("Exception while sending transaction: " + e.getMessage());
	        return "ERROR";
	    }
	}

	
	public static String getUserWallet(String secretPass, String userUUID) {
	    try {
	    	
	    	TrustManager[] trustAllCerts = new TrustManager[] {
	    		    new X509TrustManager() {
	    		       
						@Override
						public void checkClientTrusted(X509Certificate[] arg0, String arg1)
								throws CertificateException {
							// TODO Auto-generated method stub
							
						}
						@Override
						public void checkServerTrusted(X509Certificate[] arg0, String arg1)
								throws CertificateException {
							// TODO Auto-generated method stub
							
						}
						@Override
						public X509Certificate[] getAcceptedIssuers() {
							// TODO Auto-generated method stub
							return null;
						}
	    		    }
	    		};
	    	
	    	SSLContext sc = SSLContext.getInstance("SSL");
	    	sc.init(null, trustAllCerts, new java.security.SecureRandom());
	    	HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	    	
	        URL url = new URL("https://example.io:1111/getWallet?secret=" + secretPass + "&uuid=" + userUUID);
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
	        con.setRequestMethod("GET");

	        int responseCode = con.getResponseCode();

	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	            String inputLine;
	            StringBuffer response = new StringBuffer();

	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            in.close();

	            JSONParser parser = new JSONParser();
	            Object obj = null;
				try {
					obj = parser.parse(response.toString());
					
				} catch (ParseException e1) {
					return null;
				}

				JSONObject jsonObject = (JSONObject) obj;
	            
	            JSONArray results = (JSONArray) jsonObject.get("results");
	    		for (Object objj : results) {
	    			JSONObject jsonTx = (JSONObject) objj;
	    			
	    			String wallet = (String) jsonTx.get("WALLET");
	    			return wallet;
	          }
	        } else {
	            System.out.println("Error while checking wallet for user ID " + userUUID);
	            return null;
	        }
	    } catch (Exception e) {
	        System.out.println("Exception while checking/creating wallet: " + e.getMessage());
	        return null;
	    }
		return null;
	}
}
