= Release notes v.7.9

## Table of Contents

* Supported Funds Confirmation Consent in Decoupled approach with multilevel sca
* Fixed incorrect saving of owner_name_type and trusted_beneficiaries_type in consent table for piis consent
* Added a new optional header TPP-Rejection-NoFunds-Preferred
* Migrated to psd2-api 1.3.6 2020-08-14.yaml
* Changed types of remittance information structured properties in SpiTransaction and SpiSinglePayment
* Payment Controller refactoring
* Extend the event table with all types of Update PSU Data requests
* Added possibility to accept URL encoded certificates

## Supported Funds Confirmation Consent in Decoupled approach with multilevel sca

From now on, funds confirmation consent decoupled flow is supported by xs2a.

## Fixed incorrect saving of owner_name_type and trusted_beneficiaries_type in consent table for piis consent

From now on, piis consent is saving with correct 'NONE' value of owner_name_type and trusted_beneficiaries_type
columns in consent table. All old incorrect 'ALL_AVAILABLE_ACCOUNTS' value were replaced with 'NONE'.

## Added a new optional header TPP-Rejection-NoFunds-Preferred

From now on, `TPP-Rejection-NoFunds-Preferred` is passed to spi level inside `SpiContextData`

## Migrated to psd2-api 1.3.6 2020-08-14.yaml

From now on, XS2A uses version `1.3.6 2020-08-14.yaml` of OpenAPI file provided by the Berlin Group.

##  Changed types of remittance information structured properties in SpiTransaction and SpiSinglePayment

In accordance with the changes to OpenAPI file 1.3.6 2020-08-14.yaml, types of `remittanceInformationStructured`
and `remittanceInformationStructuredArray` properties in `de.adorsys.psd2.xs2a.spi.domain.account.SpiTransaction` and
'de.adorsys.psd2.xs2a.spi.domain.payment.SpiSinglePayment' were changed:

- from `Remittance` to `String` for `remittanceInformationStructured`
- from `List<Remittance>` to `List<String>` for `remittanceInformationStructuredArray`

## Payment Controller refactoring

Payment controller was refactored in terms of wrong payment type.

## Extend the event table with all types of Update PSU Data requests

From now on, events on update PSU data request are splitted by the following stages:

- Identification
- Authentication
- Select authentication method
- TAN
- Confirmation Code

In the following table you can find certain event type depending on authorisation process (AIS, PIS, PIS Cancellation or PIIS).

#### Event types on update PSU data request

| Stage                        | Authorisation process | Event type                                                                          |
|------------------------------|-----------------------|-------------------------------------------------------------------------------------|
| Identification               | AIS                   | UPDATE_AIS_CONSENT_PSU_DATA_IDENTIFICATION_REQUEST_RECEIVED                         |
|                              | PIIS                  | UPDATE_PIIS_CONSENT_PSU_DATA_IDENTIFICATION_REQUEST_RECEIVED                        |
|                              | PIS                   | UPDATE_PAYMENT_AUTHORISATION_PSU_DATA_IDENTIFICATION_REQUEST_RECEIVED               |
|                              | PIS Cancellation      | UPDATE_PAYMENT_CANCELLATION_PSU_DATA_IDENTIFICATION_REQUEST_RECEIVED                |
| Authentication               | AIS                   | UPDATE_AIS_CONSENT_PSU_DATA_AUTHENTICATION_REQUEST_RECEIVED                         |
|                              | PIIS                  | UPDATE_PIIS_CONSENT_PSU_DATA_AUTHENTICATION_REQUEST_RECEIVED                        |
|                              | PIS                   | UPDATE_PAYMENT_AUTHORISATION_PSU_DATA_AUTHENTICATION_REQUEST_RECEIVED               |
|                              | PIS Cancellation      | UPDATE_PAYMENT_CANCELLATION_PSU_DATA_AUTHENTICATION_REQUEST_RECEIVED                |
| Select authentication method | AIS                   | UPDATE_AIS_CONSENT_PSU_DATA_SELECT_AUTHENTICATION_METHOD_REQUEST_RECEIVED           |
|                              | PIIS                  | UPDATE_PIIS_CONSENT_PSU_DATA_SELECT_AUTHENTICATION_METHOD_REQUEST_RECEIVED          |
|                              | PIS                   | UPDATE_PAYMENT_AUTHORISATION_PSU_DATA_SELECT_AUTHENTICATION_METHOD_REQUEST_RECEIVED |
|                              | PIS Cancellation      | UPDATE_PAYMENT_CANCELLATION_PSU_DATA_SELECT_AUTHENTICATION_METHOD_REQUEST_RECEIVED  |
| TAN                          | AIS                   | UPDATE_AIS_CONSENT_PSU_DATA_TAN_REQUEST_RECEIVED                                    |
|                              | PIIS                  | UPDATE_PIIS_CONSENT_PSU_DATA_TAN_REQUEST_RECEIVED                                   |
|                              | PIS                   | UPDATE_PAYMENT_AUTHORISATION_PSU_DATA_TAN_REQUEST_RECEIVED                          |
|                              | PIS Cancellation      | UPDATE_PAYMENT_CANCELLATION_PSU_DATA_TAN_REQUEST_RECEIVED                           |
| Confirmation Code            | AIS                   | UPDATE_AIS_CONSENT_PSU_DATA_CONFIRMATION_CODE_REQUEST_RECEIVED                      |
|                              | PIIS                  | UPDATE_PIIS_CONSENT_PSU_DATA_CONFIRMATION_CODE_REQUEST_RECEIVED                     |
|                              | PIS                   | UPDATE_PAYMENT_AUTHORISATION_PSU_DATA_CONFIRMATION_CODE_REQUEST_RECEIVED            |
|                              | PIS Cancellation      | UPDATE_PAYMENT_CANCELLATION_PSU_DATA_CONFIRMATION_CODE_REQUEST_RECEIVED             |

## Added possibility to accept URL encoded certificates

From now on, the header `tpp-qwac-certificate` can be URL encoded and will still be
interpreted correctly.

This may be useful if the used gateway, which forwards the requests to the XS2A-Service, performs a URL
encoding on the sent certificate.
