package com.envoisolutions.sxc.jaxb.typesub;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;

import xfire.inheritance.Employee;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.envoisolutions.sxc.jaxb.JAXBContextImpl;
import com.envoisolutions.sxc.util.XoTestCase;

public class EmployeeTest extends XoTestCase {
    private WstxInputFactory xif = new WstxInputFactory();
    private XMLOutputFactory xof = new WstxOutputFactory();
    
    public void testEmployee() throws Exception {
        System.setProperty("com.envoisolutions.sxc.output.directory", "target/tmp-jaxb");
        
        JAXBContext ctx = new JAXBContextImpl(new Class[] { Employee.class } );
        
        JAXBElement<Employee> jaxbE = (JAXBElement<Employee>)
            ctx.createUnmarshaller().unmarshal(
                          xif.createXMLStreamReader(getResourceAsStream("employee.xml")), Employee.class);
        assertNotNull(jaxbE);
        assertNotNull(jaxbE.getValue());
        
        Employee e = jaxbE.getValue();
        assertEquals("foo", e.getDivision());
        assertEquals("bar", e.getName());

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLStreamWriter writer = xof.createXMLStreamWriter(bos);
        try {
            marshaller.marshal(e, writer);
            fail("MarshalException should have been thrown!");
        } catch (MarshalException ex) {
        }
        
        marshaller.marshal(jaxbE, writer);
        
        writer.close();
        Document d = readDocument(bos.toByteArray());
        addNamespace("i", "urn:xfire:inheritance");
        assertValid("/i:in0/i:name[text()='bar']", d);
        assertValid("/i:in0/i:division[text()='foo']", d);
    }
    
}
