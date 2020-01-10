/*
 * Copyright 2018 Ricksoft Co., Ltd.
 * All rights reserved.
 */
package jp.ricksoft.auditlogbrowser.audit.dataextractor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.audit.extractor.AbstractDataExtractor;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;

public class UserNameDataExtractor extends AbstractDataExtractor {
    
    private NamespaceService namespaceService;
    
    public void setNamespaceService(NamespaceService namespaceService) {
        this.namespaceService = namespaceService;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        PropertyCheck.mandatory(this, "namespaceService", namespaceService);
    }

    /**
     * @return true if this extractor can do anything with the data
     */
    @Override
    public boolean isSupported(Serializable data) {
        return (data instanceof Map);
    }

    /**
     * Extract the site name / id
     *
     * @param in a string containing the site id
     * @return the site id
     */
    @Override
    public Serializable extractData(Serializable in) {

        // The data type of all arguments are passed in Serializable and italready checked using "isSupported" method.
        @SuppressWarnings("unchecked")
        Map<QName, Serializable> path = (Map<QName, Serializable>)in;
        
        HashMap<String, String> propMap = new HashMap<>();
        path.forEach((key, value) -> propMap.put(convertPrefixedQName(key), convertSerializableToString(value)));

        return propMap.entrySet().stream()
                                .filter(e -> e.getKey().equals("cm:userName"))
                                .findFirst()
                                .get().getValue();
        
    }
    
    private String convertPrefixedQName(QName key) {
        return key.getPrefixedQName(namespaceService).toPrefixString();
    }
    
    private String convertSerializableToString(Serializable value) {
        if (value instanceof Object) {
            return value.toString();
        } else {
            return String.valueOf(value);
        }
    }

}
