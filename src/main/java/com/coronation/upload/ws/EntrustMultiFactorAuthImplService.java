
package com.coronation.upload.ws;

import javax.xml.namespace.QName;
import javax.xml.ws.*;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "EntrustMultiFactorAuthImplService", targetNamespace = "http://ws.entrustplugin.expertedge.com/", wsdlLocation = "http://132.10.200.192:8282/cmb-entrust-webservice/ws?wsdl")
public class EntrustMultiFactorAuthImplService
    extends Service
{

    private final static URL ENTRUSTMULTIFACTORAUTHIMPLSERVICE_WSDL_LOCATION;
    private final static WebServiceException ENTRUSTMULTIFACTORAUTHIMPLSERVICE_EXCEPTION;
    private final static QName ENTRUSTMULTIFACTORAUTHIMPLSERVICE_QNAME = new QName("http://ws.entrustplugin.expertedge.com/", "EntrustMultiFactorAuthImplService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://132.10.200.192:8282/cmb-entrust-webservice/ws?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        ENTRUSTMULTIFACTORAUTHIMPLSERVICE_WSDL_LOCATION = url;
        ENTRUSTMULTIFACTORAUTHIMPLSERVICE_EXCEPTION = e;
    }

    public EntrustMultiFactorAuthImplService() {
        super(__getWsdlLocation(), ENTRUSTMULTIFACTORAUTHIMPLSERVICE_QNAME);
    }

    public EntrustMultiFactorAuthImplService(WebServiceFeature... features) {
        super(__getWsdlLocation(), ENTRUSTMULTIFACTORAUTHIMPLSERVICE_QNAME, features);
    }

    public EntrustMultiFactorAuthImplService(URL wsdlLocation) {
        super(wsdlLocation, ENTRUSTMULTIFACTORAUTHIMPLSERVICE_QNAME);
    }

    public EntrustMultiFactorAuthImplService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, ENTRUSTMULTIFACTORAUTHIMPLSERVICE_QNAME, features);
    }

    public EntrustMultiFactorAuthImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public EntrustMultiFactorAuthImplService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns EntrustMultiFactorAuthImpl
     */
    @WebEndpoint(name = "EntrustMultiFactorAuthImplPort")
    public EntrustMultiFactorAuthImpl getEntrustMultiFactorAuthImplPort() {
        return super.getPort(new QName("http://ws.entrustplugin.expertedge.com/", "EntrustMultiFactorAuthImplPort"), EntrustMultiFactorAuthImpl.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns EntrustMultiFactorAuthImpl
     */
    @WebEndpoint(name = "EntrustMultiFactorAuthImplPort")
    public EntrustMultiFactorAuthImpl getEntrustMultiFactorAuthImplPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://ws.entrustplugin.expertedge.com/", "EntrustMultiFactorAuthImplPort"), EntrustMultiFactorAuthImpl.class, features);
    }

    private static URL __getWsdlLocation() {
        if (ENTRUSTMULTIFACTORAUTHIMPLSERVICE_EXCEPTION!= null) {
            throw ENTRUSTMULTIFACTORAUTHIMPLSERVICE_EXCEPTION;
        }
        return ENTRUSTMULTIFACTORAUTHIMPLSERVICE_WSDL_LOCATION;
    }

}
