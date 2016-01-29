package net.ddns.falcoboss.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {
		private String serviceKey;
		private String serviceUrl;
		FileInputStream fileInputStream;
		InputStream inputStream;
		
		public PropertyReader()
		{

		}
		
		public void readPropertyValues() throws IOException {
	 
			try {
				Properties properties = new Properties();
				String propFileName = "config.properties";
				
				inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
				fileInputStream = new FileInputStream(propFileName);
				
				if (fileInputStream != null) {
					properties.load(fileInputStream);
				} else				
				if (inputStream != null) {
					properties.load(inputStream);
				} else {
					throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
				}

				setServiceKey(properties.getProperty("service_key"));
				setServiceUrl(properties.getProperty("service_url"));

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

