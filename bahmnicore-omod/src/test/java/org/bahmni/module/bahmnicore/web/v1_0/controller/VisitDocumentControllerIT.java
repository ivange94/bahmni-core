package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bahmni.module.bahmnicore.contract.visitDocument.VisitDocumentResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.web.controller.BaseEmrControllerTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@org.springframework.test.context.ContextConfiguration(locations = {"classpath:TestingApplicationContext.xml"}, inheritLocations = true)
public class VisitDocumentControllerIT extends BaseEmrControllerTest {

    public static final String TMP_DOCUMENT_IMAGES = "/tmp/document_images";
    private final String image = "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7";
    @Autowired
    private VisitService visitService;

    @Before
    public void setUp(){
        System.setProperty("bahmnicore.documents.baseDirectory", TMP_DOCUMENT_IMAGES);
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(new File(TMP_DOCUMENT_IMAGES));
    }

    @Test
    public void shouldCreateVisitEncounterAndObservation() throws Exception {
        executeDataSet("uploadDocuments.xml");
        String patientUUID = "75e04d42-3ca8-11e3-bf2b-0800271c1b75";
        String encounterTypeUUID ="759799ab-c9a5-435e-b671-77773ada74e4";
        String visitTypeUUID = "b45ca846-c79a-11e2-b0c0-8e397087571c";
        String testUUID = "e340cf44-3d3d-11e3-bf2b-0800271c1b75";
        String imageConceptUuid = "e060cf44-3d3d-11e3-bf2b-0800271c1b75";

        String json = "{" +
                    "\"patientUuid\":\"" + patientUUID + "\"," +
                    "\"visitTypeUuid\":\"" + visitTypeUUID + "\"," +
                    "\"visitStartDate\":\"2019-12-31T18:30:00.000Z\"," +
                    "\"visitEndDate\":\"2019-12-31T18:30:00.000Z\"," +
                    "\"encounterTypeUuid\":\"" + encounterTypeUUID + "\"," +
                    "\"encounterDateTime\":\"2019-12-31T18:30:00.000Z\"," +
                    "\"providerUuid\":\"331c6bf8-7846-11e3-a96a-0800271c1b75\"," +
                    "\"documents\": [{\"testUuid\": \"" + testUUID + "\", \"image\": \"" + image + "\", \"format\": \".jpeg\"}]" +
                "}";


        VisitDocumentResponse visitDocumentResponse = deserialize(handle(newPostRequest("/rest/v1/bahmnicore/visitDocument", json)), VisitDocumentResponse.class);
        Visit visit = visitService.getVisitByUuid(visitDocumentResponse.getVisitUuid());

        assertNotNull(visit);
        assertEquals(1, visit.getEncounters().size());
        Encounter encounter = new ArrayList<>(visit.getEncounters()).get(0);
        assertEquals(1, encounter.getAllObs().size());
        assertEquals(1, encounter.getEncounterProviders().size());
        EncounterProvider encounterProvider = encounter.getEncounterProviders().iterator().next();
        assertEquals("Jane Doe", encounterProvider.getProvider().getName());
        assertEquals("Unknown", encounterProvider.getEncounterRole().getName());
        Obs parentObs = new ArrayList<>(encounter.getAllObs()).get(0);
        assertEquals(1, parentObs.getGroupMembers().size());
        assertObservationWithImage(parentObs, testUUID, imageConceptUuid);
    }

    @Test
    public void shouldUpdateVisitEncounterAndObservation() throws Exception {
        executeDataSet("uploadDocuments.xml");
        String patientUUID = "75e04d42-3ca8-11e3-bf2b-0800271c1b75";
        String encounterTypeUUID ="759799ab-c9a5-435e-b671-77773ada74e4";
        String visitTypeUUID = "b45ca846-c79a-11e2-b0c0-8e397087571c";
        String testUUID = "e340cf44-3d3d-11e3-bf2b-0800271c1b75";
        String imageConceptUuid = "e060cf44-3d3d-11e3-bf2b-0800271c1b75";

        Patient patient = Context.getPatientService().getPatientByUuid(patientUUID);
        Visit visit = createVisitForDate(patient, null, new Date(), true);

        String json = "{" +
                    "\"patientUuid\":\"" + patientUUID + "\"," +
                    "\"visitTypeUuid\":\"" + visitTypeUUID + "\"," +
                    "\"visitStartDate\":\"2019-12-31T18:30:00.000Z\"," +
                    "\"visitEndDate\":\"2019-12-31T18:30:00.000Z\"," +
                    "\"encounterTypeUuid\":\"" + encounterTypeUUID + "\"," +
                    "\"visitUuid\":\"" + visit.getUuid() + "\"," +
                    "\"encounterDateTime\":\"2019-12-31T18:30:00.000Z\"," +
                    "\"providerUuid\":\"331c6bf8-7846-11e3-a96a-0800271c1b75\"," +
                    "\"documents\": [{\"testUuid\": \"" + testUUID + "\", \"image\": \"" + image + "\", \"format\": \".jpeg\"}]" +
                "}";


        VisitDocumentResponse visitDocumentResponse = deserialize(handle(newPostRequest("/rest/v1/bahmnicore/visitDocument", json)), VisitDocumentResponse.class);
        Visit existingVisit = visitService.getVisitByUuid(visitDocumentResponse.getVisitUuid());

        assertNotNull(existingVisit);
        assertEquals(visit.getUuid(), existingVisit.getUuid());
        assertEquals(1, existingVisit.getEncounters().size());
        Encounter encounter = new ArrayList<>(existingVisit.getEncounters()).get(0);
        assertEquals(1, encounter.getAllObs().size());
        assertEquals(1, encounter.getEncounterProviders().size());
        EncounterProvider encounterProvider = encounter.getEncounterProviders().iterator().next();
        assertEquals("Jane Doe", encounterProvider.getProvider().getName());
        assertEquals("Unknown", encounterProvider.getEncounterRole().getName());
        Obs parentObs = new ArrayList<>(encounter.getAllObs()).get(0);
        assertEquals(1, parentObs.getGroupMembers().size());
        assertObservationWithImage(parentObs, testUUID, imageConceptUuid);
    }

    private Visit createVisitForDate(Patient patient, Encounter encounter, Date orderDate, boolean isActive) {
        VisitType opdVisitType = visitService.getVisitType(1);
        Visit visit = new Visit(patient, opdVisitType, orderDate);
        if(encounter != null)
            visit.addEncounter(encounter);
        if (!isActive)
            visit.setStopDatetime(DateUtils.addDays(orderDate, 1));
        return visitService.saveVisit(visit);
    }


    private void assertObservationWithImage(Obs parentObs, String testUUID, String documentUUID) {
        Obs expectedObservation = null;
        assertEquals(parentObs.getConcept().getUuid(),testUUID);
        assertTrue(parentObs.getGroupMembers().size() > 0);
        for (Obs memberObs : parentObs.getGroupMembers()) {
            if(documentUUID.equals(memberObs.getConcept().getUuid())) {
                expectedObservation = memberObs;
                break;
            }
        }
        assertTrue(expectedObservation != null);
    }

}