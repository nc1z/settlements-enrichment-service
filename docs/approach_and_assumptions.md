# Approaches and Assumptions

This document journals my approach to the problem statement and requirements. Feel free to skip to the assumptions
section if the approach part is too verbose.

## Approach

I spent a night scoping out the requirements, and these are the key points I've gathered.

Settlements Enrichment Service

- The service main purpose is to enrich data.
- Receives Trade Request -> Enrich with SSI reference -> Return Market Settlement Message
- Tests would prioritize business logic (service), then others.
- RESTful APIs - CREATE and FINDONE (Find by TradeId)
    - CREATE:
        - Accepts Trade Request in request body
        - Returns Market Settlement Message
    - FIND:
        - Accepts TradeId as path variable
        - Returns existing Market Settlement Message

Next would be to scope out the provided data and references. This will help me to understand how the
schemas/models/entities/DTOs will look like. This will be covered in the below section 'Assumptions'.

## Assumptions

Refer to the comments beside the JSON key value pairs for my assumptions.

### Sample Trade Request Parameter Values

I was not given a JSON for this particular trade request reference. JSON keys are usually written in camelCase, however,
from experience, I know we cannot assume that from upstream services. Hence in this case, I decided to use the keys
as-is and deserialize it in the application instead.

For `Amount`, I've assumed to accept 0 - 2 decimal places. But in our Market Settlement Message we will always save and
return with 2 decimal places.

```
{
  "TradeId": "16846548", // numeric string, unique, required
  "SSI Code": "OCBC_DBS_1", // string, required
  "Amount": 12894.65, // BigDecimal, must be more than 0.00, required.
  "Currency": "USD", // ISO 4217
  "Value Date": "20022020" // string, ddMMyyyy
}
```

### SSI Reference Data

Again, the requirements did not provide a JSON format, so I had to make assumptions based on the reference data
provided.

Given that it was a group of reference data in the Appendix, I assumed it was seed data. With that assumption, I went
with the thought process that SSI data will only be set once in the application, when it starts.

The below fields are all required except supportingInformation.

```
{
    "code": "OCBC_DBS_1", // string, unique
    "payerAccountNumber": "438421", // numeric string
    "payerBank": "OCBCSGSGXXX", // string
    "receiverAccountNumber": "05461368", // Leading zeros suggest a numeric string
    "receiverBank": "DBSSGB2LXXX", // string
    "supportingInformation": "BNF:FFC-4697132" // Either it has BNF:* or nullable
  }
```

### Sample Market Settlement Message Format

This was an important entity as we need to:

1. Create
2. Store
3. Return

I noticed that since this is 'enrichment', many fields are the same from the initial trade request.
This means I just need to focus on validating them in the initial request, then pass along in the message body.

Since payerParty and receiverParty are of the same shape, they can share the same class of `Party`. To avoid having to
create a separate
table and relate them to this entity, I've went with the approach of `@Embeddable` and `@Embedded` classes.

Lastly, supportingInformation has a different format from what was given in the reference data, so mapper / formatter
has to be implemented and applied to the supportingInformation when building out the message.

```
{
  "tradeId": "16846548", // This is unchanged from the TradeRequest
  "messageId": "e8a57dc0-2119-49a4-85fe-5e94415b2cad", // Assumption: auto-generated UUID
  "amount": 12894.65, // This is unchanged from the TradeRequest
  "valueDate": "20022020", // This is unchanged from the TradeRequest
  "currency": "USD", // This is unchanged from the TradeRequest
  "payerParty": { 
    "accountNumber": "438421", // From SSI
    "bankCode": "OCBCSGSGXXX" // From SSI
  }, 
  "receiverParty": { 
    "accountNumber": "05461368", // From SSI
    "bankCode": "DBSSGB2LXXX" // From SSI
  },
  "supportingInformation": "/BNF/FFC-4697132" // From SSI but has been formatted
}
```
