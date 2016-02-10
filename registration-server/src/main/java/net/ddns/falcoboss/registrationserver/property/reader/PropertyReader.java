package net.ddns.falcoboss.registrationserver.property.reader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PropertyReader {
		private String serviceKey;
		private String serviceUrl;
		FileInputStream fileInputStream;
		InputStream inputStream;
		
		public PropertyReader()
		{

		}
		
		public void readPropertyValues() throws IOException {
			//Dummy property
				setServiceKey("xxx");
				setServiceUrl("http://localhost:8080/mediator-server/rest/service/");
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

