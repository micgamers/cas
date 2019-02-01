package org.apereo.cas;

import org.apereo.cas.authentication.support.DefaultCasProtocolAttributeEncoder;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.ServicesManager;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is test cases for {@link DefaultCasProtocolAttributeEncoder}.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public class DefaultCasAttributeEncoderTests extends BaseCasCoreTests {
    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    private static Collection<String> newSingleAttribute(final String attr) {
        return Collections.singleton(attr);
    }

    @Test
    public void checkNoPublicKeyDefined() {
        val attributes = getAttributes();
        val service = RegisteredServiceTestUtils.getService("testDefault");
        val encoder = new DefaultCasProtocolAttributeEncoder(servicesManager, CipherExecutor.noOpOfStringToString());
        val encoded = encoder.encodeAttributes(attributes, servicesManager.findServiceBy(service));
        assertEquals(attributes.size() - 2, encoded.size());
    }

    @Test
    public void checkAttributesEncodedCorrectly() {
        val attributes = getAttributes();
        val service = RegisteredServiceTestUtils.getService("testencryption");
        val encoder = new DefaultCasProtocolAttributeEncoder(servicesManager, CipherExecutor.noOpOfStringToString());
        val encoded = encoder.encodeAttributes(attributes, servicesManager.findServiceBy(service));
        assertEquals(encoded.size(), attributes.size());
        checkEncryptedValues(attributes, CasViewConstants.MODEL_ATTRIBUTE_NAME_PRINCIPAL_CREDENTIAL, encoded);
        checkEncryptedValues(attributes, CasViewConstants.MODEL_ATTRIBUTE_NAME_PROXY_GRANTING_TICKET, encoded);
    }

    private void checkEncryptedValues(final Map<String, Object> attributes, final String name, final Map<String, Object> encoded) {
        val v1 = ((Collection<?>) attributes.get(
            name)).iterator().next().toString();
        val v2 = (String) encoded.get(name);
        assertNotEquals(v1, v2);
    }

    public Map<String, Object> getAttributes() {
        val attributes = new HashMap<String, Object>();
        IntStream.range(0, 3).forEach(i -> attributes.put("attr" + i, newSingleAttribute("value" + i)));
        attributes.put(CasViewConstants.MODEL_ATTRIBUTE_NAME_PROXY_GRANTING_TICKET, newSingleAttribute("PGT-1234567"));
        attributes.put(CasViewConstants.MODEL_ATTRIBUTE_NAME_PRINCIPAL_CREDENTIAL, newSingleAttribute("PrincipalPassword"));
        return attributes;
    }
}
