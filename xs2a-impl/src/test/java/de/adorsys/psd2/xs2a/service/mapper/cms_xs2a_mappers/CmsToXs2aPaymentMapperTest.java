/*
 * Copyright 2018-2020 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.adorsys.psd2.xs2a.service.mapper.cms_xs2a_mappers;

import de.adorsys.psd2.consent.api.CmsAddress;
import de.adorsys.psd2.consent.api.pis.CmsRemittance;
import de.adorsys.psd2.consent.api.pis.PisPayment;
import de.adorsys.psd2.consent.api.pis.proto.PisCommonPaymentResponse;
import de.adorsys.psd2.xs2a.core.domain.address.Xs2aAddress;
import de.adorsys.psd2.xs2a.core.pis.*;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.profile.AccountReferenceType;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import de.adorsys.psd2.xs2a.domain.pis.BulkPayment;
import de.adorsys.psd2.xs2a.domain.pis.CommonPayment;
import de.adorsys.psd2.xs2a.domain.pis.PeriodicPayment;
import de.adorsys.psd2.xs2a.domain.pis.SinglePayment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CmsToXs2aPaymentMapperTest {
    private static final String PAYMENT_ID = "payment id";
    private static final String EXECUTION_ID = "execution id";
    private static final String END_TO_END_IDENTIFICATION = "RI-123456789";
    private static final String INSTRUCTION_IDENTIFICATION = "INSTRUCTION_IDENTIFICATION";
    private static final Currency CURRENCY = Currency.getInstance("EUR");

    private static final String DEBTOR_IBAN = "debtor iban";
    private static final AccountReference DEBTOR_ACCOUNT_REFERENCE = new AccountReference(AccountReferenceType.IBAN,
                                                                                          DEBTOR_IBAN,
                                                                                          CURRENCY);

    private static final String ULTIMATE_DEBTOR = "ultimate debtor";
    private static final String AMOUNT = "100";

    private static final String CREDITOR_IBAN = "creditor iban";
    private static final AccountReference CREDITOR_ACCOUNT_REFERENCE = new AccountReference(AccountReferenceType.IBAN,
                                                                                            CREDITOR_IBAN,
                                                                                            CURRENCY);

    private static final String CREDITOR_AGENT = "creditor agent";
    private static final String CREDITOR_NAME = "creditor name";

    private static final String CREDITOR_ADDRESS_STREET = "street";
    private static final String CREDITOR_ADDRESS_BUILDING_NUMBER = "building number";
    private static final String CREDITOR_ADDRESS_CITY = "city";
    private static final String CREDITOR_ADDRESS_POSTAL_CODE = "postal code";
    private static final String CREDITOR_ADDRESS_COUNTRY = "DE";

    private static final String REMITTANCE_INFORMATION_UNSTRUCTURED = "remittance information unstructured";
    private static final String REMITTANCE_INFORMATION_STRUCTURED_REFERENCE = "structured reference";
    private static final LocalDate REQUESTED_EXECUTION_DATE = LocalDate.of(2019, 2, 27);
    private static final OffsetDateTime REQUESTED_EXECUTION_TIME = OffsetDateTime.of(REQUESTED_EXECUTION_DATE,
                                                                                     LocalTime.NOON,
                                                                                     ZoneOffset.UTC);
    private static final OffsetDateTime STATUS_CHANGE_TIMESTAMP = OffsetDateTime.of(REQUESTED_EXECUTION_DATE,
                                                                                    LocalTime.NOON,
                                                                                    ZoneOffset.UTC);

    private static final String ULTIMATE_CREDITOR = "ultimate creditor";
    private static final String PURPOSE_CODE = "BKDF";
    private static final TransactionStatus TRANSACTION_STATUS = TransactionStatus.RCVD;
    private static final LocalDate START_DATE = LocalDate.of(2019, 2, 25);
    private static final LocalDate END_DATE = LocalDate.of(2019, 2, 28);
    private static final PisExecutionRule EXECUTION_RULE = PisExecutionRule.FOLLOWING;
    private static final String FREQUENCY = "ANNUAL";
    private static final PisDayOfExecution DAY_OF_EXECUTION = PisDayOfExecution._2;
    private static final List<PsuIdData> PSU_ID_DATA_LIST = Collections.singletonList(new PsuIdData("psu id", null, null, null, null));

    private static final String PAYMENT_PRODUCT = "payment product";
    private static final PaymentType PAYMENT_TYPE = PaymentType.SINGLE;
    private static final TppInfo TPP_INFO = buildTppInfo();
    private static final byte[] PAYMENT_DATA = "payment data".getBytes();


    @InjectMocks
    private CmsToXs2aPaymentMapper cmsToXs2aPaymentMapper;

    @Spy
    private Xs2aRemittanceMapper xs2aRemittanceMapper = Mappers.getMapper(Xs2aRemittanceMapper.class);

    @Test
    void mapToPeriodicPayment() {
        PisPayment pisPayment = buildPisPayment();

        PeriodicPayment periodicPayment = cmsToXs2aPaymentMapper.mapToPeriodicPayment(pisPayment);

        assertNotNull(periodicPayment);
        assertEquals(PAYMENT_ID, periodicPayment.getPaymentId());
        assertEquals(END_TO_END_IDENTIFICATION, periodicPayment.getEndToEndIdentification());
        assertEquals(INSTRUCTION_IDENTIFICATION, periodicPayment.getInstructionIdentification());
        assertEquals(DEBTOR_ACCOUNT_REFERENCE, periodicPayment.getDebtorAccount());

        Xs2aAmount instructedAmount = periodicPayment.getInstructedAmount();
        assertNotNull(instructedAmount);
        assertEquals(CURRENCY, instructedAmount.getCurrency());
        assertEquals(AMOUNT, instructedAmount.getAmount());

        assertEquals(CREDITOR_ACCOUNT_REFERENCE, periodicPayment.getCreditorAccount());
        assertEquals(CREDITOR_AGENT, periodicPayment.getCreditorAgent());
        assertEquals(CREDITOR_NAME, periodicPayment.getCreditorName());

        Xs2aAddress creditorAddress = periodicPayment.getCreditorAddress();
        assertNotNull(creditorAddress);
        assertEquals(CREDITOR_ADDRESS_STREET, creditorAddress.getStreetName());
        assertEquals(CREDITOR_ADDRESS_BUILDING_NUMBER, creditorAddress.getBuildingNumber());
        assertEquals(CREDITOR_ADDRESS_CITY, creditorAddress.getTownName());
        assertEquals(CREDITOR_ADDRESS_POSTAL_CODE, creditorAddress.getPostCode());
        assertEquals(CREDITOR_ADDRESS_COUNTRY, creditorAddress.getCountry().getCode());

        assertEquals(REMITTANCE_INFORMATION_UNSTRUCTURED, periodicPayment.getRemittanceInformationUnstructured());

        assertEquals(TRANSACTION_STATUS, periodicPayment.getTransactionStatus());
        assertEquals(PSU_ID_DATA_LIST, periodicPayment.getPsuDataList());

        assertEquals(START_DATE, periodicPayment.getStartDate());
        assertEquals(EXECUTION_RULE, periodicPayment.getExecutionRule());
        assertEquals(END_DATE, periodicPayment.getEndDate());

        FrequencyCode frequencyCode = FrequencyCode.valueOf(FREQUENCY);
        assertEquals(frequencyCode, periodicPayment.getFrequency());

        assertEquals(DAY_OF_EXECUTION, periodicPayment.getDayOfExecution());
        assertEquals(STATUS_CHANGE_TIMESTAMP, periodicPayment.getStatusChangeTimestamp());
        assertEquals(pisPayment.getUltimateDebtor(), periodicPayment.getUltimateDebtor());
        assertEquals(pisPayment.getUltimateCreditor(), periodicPayment.getUltimateCreditor());
        assertEquals(pisPayment.getPurposeCode(), periodicPayment.getPurposeCode().toString());
        assertEquals((pisPayment.getRemittanceInformationStructured()), periodicPayment.getRemittanceInformationStructured());
        assertEquals(pisPayment.getCreationTimestamp(), periodicPayment.getCreationTimestamp());
    }

    @Test
    void mapToPeriodicPayment_shouldNotMapRequestedExecutionDateAndTime() {
        PisPayment pisPayment = buildPisPayment();

        PeriodicPayment periodicPayment = cmsToXs2aPaymentMapper.mapToPeriodicPayment(pisPayment);

        assertNull(periodicPayment.getRequestedExecutionDate());
        assertNull(periodicPayment.getRequestedExecutionTime());
    }

    @Test
    void mapToPeriodicPayment_withNullPisPayment_shouldReturnNull() {
        PeriodicPayment periodicPayment = cmsToXs2aPaymentMapper.mapToPeriodicPayment(null);

        assertNull(periodicPayment);
    }

    @Test
    void mapToSinglePayment() {
        PisPayment pisPayment = buildPisPayment();

        SinglePayment singlePayment = cmsToXs2aPaymentMapper.mapToSinglePayment(pisPayment);

        assertNotNull(singlePayment);
        assertEquals(PAYMENT_ID, singlePayment.getPaymentId());
        assertEquals(END_TO_END_IDENTIFICATION, singlePayment.getEndToEndIdentification());
        assertEquals(INSTRUCTION_IDENTIFICATION, singlePayment.getInstructionIdentification());
        assertEquals(DEBTOR_ACCOUNT_REFERENCE, singlePayment.getDebtorAccount());

        Xs2aAmount instructedAmount = singlePayment.getInstructedAmount();
        assertNotNull(instructedAmount);
        assertEquals(CURRENCY, instructedAmount.getCurrency());
        assertEquals(AMOUNT, instructedAmount.getAmount());

        assertEquals(CREDITOR_ACCOUNT_REFERENCE, singlePayment.getCreditorAccount());
        assertEquals(CREDITOR_AGENT, singlePayment.getCreditorAgent());
        assertEquals(CREDITOR_NAME, singlePayment.getCreditorName());

        Xs2aAddress creditorAddress = singlePayment.getCreditorAddress();
        assertNotNull(creditorAddress);
        assertEquals(CREDITOR_ADDRESS_STREET, creditorAddress.getStreetName());
        assertEquals(CREDITOR_ADDRESS_BUILDING_NUMBER, creditorAddress.getBuildingNumber());
        assertEquals(CREDITOR_ADDRESS_CITY, creditorAddress.getTownName());
        assertEquals(CREDITOR_ADDRESS_POSTAL_CODE, creditorAddress.getPostCode());
        assertEquals(CREDITOR_ADDRESS_COUNTRY, creditorAddress.getCountry().getCode());

        assertEquals(REMITTANCE_INFORMATION_UNSTRUCTURED, singlePayment.getRemittanceInformationUnstructured());
        assertEquals(REQUESTED_EXECUTION_DATE, singlePayment.getRequestedExecutionDate());
        assertEquals(REQUESTED_EXECUTION_TIME, singlePayment.getRequestedExecutionTime());
        assertEquals(TRANSACTION_STATUS, singlePayment.getTransactionStatus());
        assertEquals(PSU_ID_DATA_LIST, singlePayment.getPsuDataList());
        assertEquals(STATUS_CHANGE_TIMESTAMP, singlePayment.getStatusChangeTimestamp());
        assertEquals(pisPayment.getUltimateDebtor(), singlePayment.getUltimateDebtor());
        assertEquals(pisPayment.getUltimateCreditor(), singlePayment.getUltimateCreditor());
        assertEquals(pisPayment.getPurposeCode(), singlePayment.getPurposeCode().toString());
        assertEquals(pisPayment.getRemittanceInformationStructured(), singlePayment.getRemittanceInformationStructured());
        assertEquals(pisPayment.getCreationTimestamp(), singlePayment.getCreationTimestamp());
    }

    @Test
    void mapToSinglePayment_withNullPisPayment_shouldReturnNull() {
        SinglePayment singlePayment = cmsToXs2aPaymentMapper.mapToSinglePayment(null);

        assertNull(singlePayment);
    }

    @Test
    void mapToBulkPayment() {
        PisPayment pisPayment = buildPisPayment();

        BulkPayment bulkPayment = cmsToXs2aPaymentMapper.mapToBulkPayment(Collections.singletonList(pisPayment));

        assertNotNull(bulkPayment);
        assertEquals(PAYMENT_ID, bulkPayment.getPaymentId());

        assertTrue(bulkPayment.getBatchBookingPreferred());

        assertEquals(DEBTOR_ACCOUNT_REFERENCE, bulkPayment.getDebtorAccount());
        assertEquals(REQUESTED_EXECUTION_DATE, bulkPayment.getRequestedExecutionDate());
        assertEquals(REQUESTED_EXECUTION_TIME, bulkPayment.getRequestedExecutionTime());

        assertEquals(TRANSACTION_STATUS, bulkPayment.getTransactionStatus());
        assertEquals(PSU_ID_DATA_LIST, bulkPayment.getPsuDataList());

        assertEquals(1, bulkPayment.getPayments().size());

        SinglePayment firstPayment = bulkPayment.getPayments().get(0);

        assertNotNull(firstPayment);
        assertEquals(PAYMENT_ID, firstPayment.getPaymentId());
        assertEquals(END_TO_END_IDENTIFICATION, firstPayment.getEndToEndIdentification());
        assertEquals(INSTRUCTION_IDENTIFICATION, firstPayment.getInstructionIdentification());
        assertEquals(DEBTOR_ACCOUNT_REFERENCE, firstPayment.getDebtorAccount());

        Xs2aAmount instructedAmount = firstPayment.getInstructedAmount();
        assertNotNull(instructedAmount);
        assertEquals(CURRENCY, instructedAmount.getCurrency());
        assertEquals(AMOUNT, instructedAmount.getAmount());

        assertEquals(CREDITOR_ACCOUNT_REFERENCE, firstPayment.getCreditorAccount());
        assertEquals(CREDITOR_AGENT, firstPayment.getCreditorAgent());
        assertEquals(CREDITOR_NAME, firstPayment.getCreditorName());

        Xs2aAddress creditorAddress = firstPayment.getCreditorAddress();
        assertNotNull(creditorAddress);
        assertEquals(CREDITOR_ADDRESS_STREET, creditorAddress.getStreetName());
        assertEquals(CREDITOR_ADDRESS_BUILDING_NUMBER, creditorAddress.getBuildingNumber());
        assertEquals(CREDITOR_ADDRESS_CITY, creditorAddress.getTownName());
        assertEquals(CREDITOR_ADDRESS_POSTAL_CODE, creditorAddress.getPostCode());
        assertEquals(CREDITOR_ADDRESS_COUNTRY, creditorAddress.getCountry().getCode());

        assertEquals(REMITTANCE_INFORMATION_UNSTRUCTURED, firstPayment.getRemittanceInformationUnstructured());
        assertEquals(REQUESTED_EXECUTION_DATE, firstPayment.getRequestedExecutionDate());
        assertEquals(REQUESTED_EXECUTION_TIME, firstPayment.getRequestedExecutionTime());
        assertEquals(TRANSACTION_STATUS, firstPayment.getTransactionStatus());
        assertEquals(PSU_ID_DATA_LIST, firstPayment.getPsuDataList());
        assertEquals(STATUS_CHANGE_TIMESTAMP, firstPayment.getStatusChangeTimestamp());
        assertEquals(pisPayment.getUltimateDebtor(), firstPayment.getUltimateDebtor());
        assertEquals(pisPayment.getUltimateCreditor(), firstPayment.getUltimateCreditor());
        assertEquals(pisPayment.getPurposeCode(), firstPayment.getPurposeCode().toString());
        assertEquals(pisPayment.getRemittanceInformationStructured(), firstPayment.getRemittanceInformationStructured());
        assertEquals(pisPayment.getCreationTimestamp(), firstPayment.getCreationTimestamp());
    }

    @Test
    void mapToXs2aCommonPayment() {
        PisCommonPaymentResponse pisCommonPaymentResponse = buildPisCommonPaymentResponse();

        CommonPayment commonPayment = cmsToXs2aPaymentMapper.mapToXs2aCommonPayment(pisCommonPaymentResponse);

        assertNotNull(commonPayment);
        assertEquals(PAYMENT_ID, commonPayment.getPaymentId());
        assertEquals(PAYMENT_PRODUCT, commonPayment.getPaymentProduct());
        assertEquals(TRANSACTION_STATUS, commonPayment.getTransactionStatus());
        assertEquals(PAYMENT_TYPE, commonPayment.getPaymentType());
        assertEquals(PAYMENT_DATA, commonPayment.getPaymentData());
        assertEquals(PSU_ID_DATA_LIST, commonPayment.getPsuDataList());
        assertEquals(STATUS_CHANGE_TIMESTAMP, commonPayment.getStatusChangeTimestamp());
        assertEquals(pisCommonPaymentResponse.getCreationTimestamp(), commonPayment.getCreationTimestamp());
    }

    @Test
    void mapToXs2aCommonPayment_withNullPisCommonPaymentResponse_shouldReturnNull() {
        CommonPayment commonPayment = cmsToXs2aPaymentMapper.mapToXs2aCommonPayment(null);

        assertNull(commonPayment);
    }

    private static TppInfo buildTppInfo() {
        TppInfo tppInfo = new TppInfo();
        tppInfo.setAuthorisationNumber("authorisation number");
        tppInfo.setAuthorityId("authority id");
        return tppInfo;
    }

    private PisPayment buildPisPayment() {
        PisPayment pisPayment = new PisPayment();
        pisPayment.setPaymentId(PAYMENT_ID);
        pisPayment.setExecutionId(EXECUTION_ID);
        pisPayment.setEndToEndIdentification(END_TO_END_IDENTIFICATION);
        pisPayment.setInstructionIdentification(INSTRUCTION_IDENTIFICATION);
        pisPayment.setDebtorAccount(DEBTOR_ACCOUNT_REFERENCE);
        pisPayment.setUltimateDebtor(ULTIMATE_DEBTOR);
        pisPayment.setCurrency(CURRENCY);
        pisPayment.setAmount(new BigDecimal(AMOUNT));
        pisPayment.setCreditorAccount(CREDITOR_ACCOUNT_REFERENCE);
        pisPayment.setCreditorAgent(CREDITOR_AGENT);
        pisPayment.setCreditorName(CREDITOR_NAME);
        pisPayment.setCreditorAddress(buildCmsAddress());
        pisPayment.setRemittanceInformationUnstructured(REMITTANCE_INFORMATION_UNSTRUCTURED);
        pisPayment.setRemittanceInformationStructured(REMITTANCE_INFORMATION_STRUCTURED_REFERENCE);
        pisPayment.setRequestedExecutionDate(REQUESTED_EXECUTION_DATE);
        pisPayment.setRequestedExecutionTime(REQUESTED_EXECUTION_TIME);
        pisPayment.setUltimateCreditor(ULTIMATE_CREDITOR);
        pisPayment.setPurposeCode(PURPOSE_CODE);
        pisPayment.setTransactionStatus(TRANSACTION_STATUS);
        pisPayment.setStartDate(START_DATE);
        pisPayment.setEndDate(END_DATE);
        pisPayment.setExecutionRule(EXECUTION_RULE);
        pisPayment.setFrequency(FREQUENCY);
        pisPayment.setDayOfExecution(DAY_OF_EXECUTION);
        pisPayment.setPsuDataList(PSU_ID_DATA_LIST);
        pisPayment.setStatusChangeTimestamp(STATUS_CHANGE_TIMESTAMP);
        pisPayment.setBatchBookingPreferred(Boolean.TRUE);
        pisPayment.setCreationTimestamp(OffsetDateTime.now());
        return pisPayment;
    }

    private CmsAddress buildCmsAddress() {
        CmsAddress cmsAddress = new CmsAddress();
        cmsAddress.setStreet(CREDITOR_ADDRESS_STREET);
        cmsAddress.setBuildingNumber(CREDITOR_ADDRESS_BUILDING_NUMBER);
        cmsAddress.setCity(CREDITOR_ADDRESS_CITY);
        cmsAddress.setPostalCode(CREDITOR_ADDRESS_POSTAL_CODE);
        cmsAddress.setCountry(CREDITOR_ADDRESS_COUNTRY);
        return cmsAddress;
    }

    private PisCommonPaymentResponse buildPisCommonPaymentResponse() {
        PisCommonPaymentResponse pisCommonPaymentResponse = new PisCommonPaymentResponse();

        pisCommonPaymentResponse.setPayments(Collections.singletonList(buildPisPayment()));
        pisCommonPaymentResponse.setPaymentProduct(PAYMENT_PRODUCT);
        pisCommonPaymentResponse.setPaymentType(PAYMENT_TYPE);
        pisCommonPaymentResponse.setTppInfo(TPP_INFO);
        pisCommonPaymentResponse.setExternalId(PAYMENT_ID);
        pisCommonPaymentResponse.setPsuData(PSU_ID_DATA_LIST);
        pisCommonPaymentResponse.setPaymentData(PAYMENT_DATA);
        pisCommonPaymentResponse.setTransactionStatus(TRANSACTION_STATUS);
        pisCommonPaymentResponse.setStatusChangeTimestamp(STATUS_CHANGE_TIMESTAMP);
        pisCommonPaymentResponse.setMultilevelScaRequired(false);
        pisCommonPaymentResponse.setCreationTimestamp(OffsetDateTime.now());

        return pisCommonPaymentResponse;
    }
}
