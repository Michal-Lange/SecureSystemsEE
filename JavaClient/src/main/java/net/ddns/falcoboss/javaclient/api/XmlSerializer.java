package net.ddns.falcoboss.javaclient.api;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

public final class XmlSerializer {
	private static String filename = "contacts.xml";
	
	private XmlSerializer () {
    }
	
	public static List<User> unmarshall() throws JAXBException
	{
		List<User> userList;
		File file = new File(filename);
		if(file.exists()){
			JAXBContext jc = JAXBContext.newInstance(Wrapper.class, User.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			userList = unmarshal(unmarshaller, User.class, filename);
		}
		else{
			userList = new LinkedList<User>();
		}
        return userList;
	}
	
	public static void marshall(List<User> userList) throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(Wrapper.class, User.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshal(marshaller, userList, "users");
	}
	
	/**
	 * Unmarshal XML to Wrapper and return List value.
	 */
	private static <T> List<T> unmarshal(Unmarshaller unmarshaller, Class<T> clazz, String xmlLocation)
			throws JAXBException {
		StreamSource xml = new StreamSource(xmlLocation);
		Wrapper<T> wrapper = (Wrapper<T>) unmarshaller.unmarshal(xml, Wrapper.class).getValue();
		return wrapper.getItems();
	}

	/**
	 * Wrap List in Wrapper, then leverage JAXBElement to supply root element
	 * information.
	 */
	private static void marshal(Marshaller marshaller, List<?> list, String name) throws JAXBException {
		File file = new File(filename);
		QName qName = new QName(name);
		Wrapper wrapper = new Wrapper(list);
		JAXBElement<Wrapper> jaxbElement = new JAXBElement<Wrapper>(qName, Wrapper.class, wrapper);
		marshaller.marshal(jaxbElement, file);
	}
}
