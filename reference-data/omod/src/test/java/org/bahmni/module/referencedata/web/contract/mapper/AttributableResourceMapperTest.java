package org.bahmni.module.referencedata.web.contract.mapper;

import org.bahmni.module.referencedata.labconcepts.contract.Resource;
import org.bahmni.module.referencedata.labconcepts.mapper.AttributableResourceMapper;
import org.bahmni.test.builder.ConceptBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptAttribute;
import org.openmrs.ConceptAttributeType;

public class AttributableResourceMapperTest {

    private AttributableResourceMapper resourceMapper;



    @Before
    public void setUp() throws Exception {
        resourceMapper = new AttributableResourceMapper();
    }

    @Test
    public void shouldMapConceptAttributesAsResourceProperties() throws Exception {
        Concept procedureConcept = new ConceptBuilder()
                                           .withClass("Procedure")
                                           .withUUID("9d583329-5fb1-4e50-9420-dcbbf6991fbc")
                                           .withName("Sample Procedure")
                                           .withDescription("Sample Procedure")
                                           .withDataType("N/A").build();

        ConceptAttributeType sellableAttributeType = new ConceptAttributeType();
        sellableAttributeType.setDatatypeClassname("org.openmrs.customdatatype.datatype.BooleanDatatype");
        sellableAttributeType.setName("sellable");

        ConceptAttribute sellableAttribute = new ConceptAttribute();
        sellableAttribute.setAttributeType(sellableAttributeType);
        sellableAttribute.setVoided(false);
        sellableAttribute.setValueReferenceInternal("true");
        procedureConcept.addAttribute(sellableAttribute);


        ConceptAttributeType attributeUnderTest = new ConceptAttributeType();
        attributeUnderTest.setDatatypeClassname("java.lang.String");
        attributeUnderTest.setName("product_category");

        ConceptAttribute testAttribute = new ConceptAttribute();
        testAttribute.setAttributeType(attributeUnderTest);
        testAttribute.setVoided(false);
        testAttribute.setValueReferenceInternal("Dental");
        procedureConcept.addAttribute(testAttribute);


        Resource resource = resourceMapper.map(procedureConcept);
        Assert.assertEquals("true", resource.getProperties().get("sellable"));
        Assert.assertEquals("Dental", resource.getProperties().get("product_category"));

    }

}