package net.ddns.falcoboss.javaclient.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {
		private String serviceKey;
		private String serviceUrl;
		FileInputStream fileInputStream;
		
		public PropertyReader()
		{
			
		}
	 
		public void readPropertyValues() throws IOException {
	 
			try {
				Properties prop = new Properties();
				String propFileName = "config.properties";
				fileInputStream = new FileInputStream(propFileName);
				if (fileInputStream != null) {
					prop.load(fileInputStream);
				} else {
					throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
				}
				setServiceKey(prop.getProperty("service_key"));
				setServiceUrl(prop.getProperty("service_url"));

			} catch (Exception e) {
				System.out.println("Exception: " + e);
			} finally {
				fileInputStream.close();
			}
		}

		public String getServiceKey() {
			return serviceKey;
		}

		public void setServiceKey(String serviceKey) {
			this.serviceKey = serviceKey;
		}

		public String getServiceUrl() {
			return serviceUrl;
		}

		public void setServiceUrl(String serviceUrl) {
			this.serviceUrl = serviceUrl;
		}
	}

