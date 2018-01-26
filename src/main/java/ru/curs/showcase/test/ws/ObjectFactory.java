
package ru.curs.showcase.test.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.curs.showcase.test.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ProcName_QNAME = new QName("http://showcase.curs.ru", "procName");
    private final static QName _ShowcaseExportException_QNAME = new QName("http://showcase.curs.ru", "ShowcaseExportException");
    private final static QName _Handle_QNAME = new QName("http://showcase.curs.ru", "handle");
    private final static QName _HandleResponse_QNAME = new QName("http://showcase.curs.ru", "handleResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.curs.showcase.test.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ShowcaseExportException }
     * 
     */
    public ShowcaseExportException createShowcaseExportException() {
        return new ShowcaseExportException();
    }

    /**
     * Create an instance of {@link ResponseAnyXML }
     * 
     */
    public ResponseAnyXML createResponseAnyXML() {
        return new ResponseAnyXML();
    }

    /**
     * Create an instance of {@link Handle }
     * 
     */
    public Handle createHandle() {
        return new Handle();
    }

    /**
     * Create an instance of {@link RequestAnyXML }
     * 
     */
    public RequestAnyXML createRequestAnyXML() {
        return new RequestAnyXML();
    }

    /**
     * Create an instance of {@link HandleResponse }
     * 
     */
    public HandleResponse createHandleResponse() {
        return new HandleResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://showcase.curs.ru", name = "procName")
    public JAXBElement<String> createProcName(String value) {
        return new JAXBElement<String>(_ProcName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShowcaseExportException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://showcase.curs.ru", name = "ShowcaseExportException")
    public JAXBElement<ShowcaseExportException> createShowcaseExportException(ShowcaseExportException value) {
        return new JAXBElement<ShowcaseExportException>(_ShowcaseExportException_QNAME, ShowcaseExportException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Handle }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://showcase.curs.ru", name = "handle")
    public JAXBElement<Handle> createHandle(Handle value) {
        return new JAXBElement<Handle>(_Handle_QNAME, Handle.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HandleResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://showcase.curs.ru", name = "handleResponse")
    public JAXBElement<HandleResponse> createHandleResponse(HandleResponse value) {
        return new JAXBElement<HandleResponse>(_HandleResponse_QNAME, HandleResponse.class, null, value);
    }

}
