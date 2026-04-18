# BatelecBillingSystem2026

# BATELEC II Electricity Billing System

A console-based Java application that computes and prints an itemized electricity bill based on the **official implemented billing rates of Batangas Electric Cooperative II (BATELEC II) for March 2026**, covering both Mainland and NPC-SPUG (Tingloy) customer classifications.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Requirements](#requirements)
- [How to Run](#how-to-run)
- [Menu Options](#menu-options)
- [Customer Types](#customer-types)
- [Rate Structure (March 2026)](#rate-structure-march-2026)
- [Bill Components](#bill-components)
- [Sample Bill Output](#sample-bill-output)
- [Project Structure](#project-structure)
- [Notes & Disclaimers](#notes--disclaimers)

---

## Overview

This program simulates the official billing computation of **BATELEC II** — the electric cooperative serving portions of Batangas, Philippines. All rate components are sourced directly from the **Implemented Billing Rates for the Month of March 2026** official rate table, including:

- Generation, Transmission & System Loss charges
- Distribution Revenue charges (per-kWh and fixed monthly)
- Reinvestment Fund for Sustainable CAPEx (RFSC)
- VAT on Government Revenues (12%)
- Universal Charges (NPC Stranded Debt, Missionary Electrification, Environmental)
- FIT ALL and GEA ALL (Renewable Energy)
- Senior Citizen discount under **Republic Act 9994**

---

## Features

- **14 customer type classifications** — Mainland (Residential to Large Industrial) and NPC-SPUG Tingloy
- **Fully itemized bill** — every rate component is listed separately with its PHP/kWh rate and computed amount
- **Fixed monthly charges** — Retail Electric Service (₱59.73) and Metering (₱54.92) applied correctly for non-residential types
- **Senior Citizen discount** — 5% deduction for eligible residential customers (RA 9994)
- **Auto-generated dates** — Bill date, due date (15 days), and disconnection date (30 days) computed from the system clock
- **Rate table viewer** — prints the full March 2026 rate schedule in a formatted table
- **Batch billing mode** — process multiple customers in one session
- **Input validation** — rejects negative readings, invalid meter values, and non-numeric input

---

## Requirements

| Requirement | Version |
|---|---|
| Java Development Kit (JDK) | 8 or higher |
| Operating System | Windows, macOS, or Linux |
| IDE (optional) | VS Code, IntelliJ IDEA, Eclipse |

> **VS Code users:** Save the file as exactly `BatelecBillingSystem2026.java` and run from the terminal using the commands below to avoid the `ClassNotFoundException` error.

---

## How to Run

### Step 1 — Compile

Open a terminal in the folder where `BatelecBillingSystem2026.java` is saved, then run:

```bash
javac BatelecBillingSystem2026.java
```

A file named `BatelecBillingSystem2026.class` will be created in the same folder.

### Step 2 — Run

```bash
java BatelecBillingSystem2026
```

### All-in-one (compile + run)

```bash
javac BatelecBillingSystem2026.java && java BatelecBillingSystem2026
```

---

## Menu Options

```
╔═══════════════════════════════════════════════════════════╗
║      BATELEC II — ELECTRICITY BILLING SYSTEM              ║
║      Implemented Rates: MARCH 2026                        ║
╠═══════════════════════════════════════════════════════════╣
║  [1]  Generate Bill                                       ║
║  [2]  View Complete Rate Table (March 2026)               ║
║  [3]  Batch Billing (multiple customers)                  ║
║  [0]  Exit                                                ║
╚═══════════════════════════════════════════════════════════╝
```

| Option | Description |
|---|---|
| `1` | Enter customer details and meter readings to generate a full itemized bill |
| `2` | Display the complete March 2026 rate schedule in a columnar table |
| `3` | Enter how many customers to process, then generate bills for each in sequence |
| `0` | Exit the program |

---

## Customer Types

### Mainland

| # | Code | Description | Grand Total Rate |
|---|---|---|---|
| 1 | `RESIDENTIAL` | RES'L — Residential | ₱9.6434 / kWh |
| 2 | `BAPA_MM` | BAPA (MM) | ₱9.1954 / kWh |
| 3 | `COMMERCIAL` | COMM'L — Commercial | ₱8.8517 / kWh |
| 4 | `SMALL_INDUSTRIAL` | Small Industrial | ₱8.8517 / kWh |
| 5 | `CWS` | CWS | ₱8.8517 / kWh |
| 6 | `PUBLIC_BUILDING` | Public Building | ₱8.8517 / kWh |
| 7 | `STREET_LIGHTS` | Street Lights | ₱8.8517 / kWh |
| 8 | `COMM_HV` | Commercial HV | ₱7.5253 / kWh |
| 9 | `SMALL_IND_HV` | Small Industrial HV | ₱7.5253 / kWh |
| 10 | `PUBLIC_BUILDING_HV` | Public Building HV | ₱7.5253 / kWh |
| 11 | `LARGE_INDUSTRIAL` | Large Industrial | ₱7.5253 / kWh |

### NPC-SPUG (Tingloy)

| # | Code | Description | Grand Total Rate |
|---|---|---|---|
| 12 | `NPC_RESIDENTIAL` | NPC Residential | ₱10.7410 / kWh |
| 13 | `NPC_ST_LIGHTS` | NPC Street Lights | ₱10.3018 / kWh |
| 14 | `NPC_COMM_HV` | NPC Commercial HV | ₱9.8820 / kWh |

---

## Rate Structure (March 2026)

All rates are taken directly from the official BATELEC II rate table for March 2026.

### Per-kWh Rates

| Component | RES'L | BAPA | COMM'L / LV | HV |
|---|---|---|---|---|
| Generation Charge | 5.2173 | 5.2173 | 5.2173 | 5.2173 |
| Transmission Delivery | 1.0894 | 1.0894 | 1.0419 | 1.0419 |
| System Loss | 0.6894 | 0.6894 | 0.6894 | 0.6894 |
| **GTS Sub-total** | **6.9961** | **6.9961** | **6.9486** | **5.9067** |
| Distribution Network | 0.2748 | 0.2748 | 0.3748 | 0.3748 |
| Retail Elec. Svc | 0.4140 | 0.1140 | *(fixed)* | *(fixed)* |
| Metering Charge | 0.3460 | 0.2460 | *(fixed)* | *(fixed)* |
| RFSC | 0.3216 | 0.3216 | 0.3216 | 0.3216 |
| Lifeline Rate Subsidy | 0.0100 | 0.0100 | 0.0100 | 0.0100 |
| Senior Citizen Subsidy | 0.0001 | 0.0001 | 0.0001 | 0.0001 |
| VAT — Generation | 0.4045 | 0.4045 | 0.4045 | 0.4045 |
| VAT — Transmission | 0.1144 | 0.1144 | 0.1094 | 0.2447 |
| VAT — System Loss | 0.0767 | 0.0767 | 0.0767 | 0.0767 |
| VAT — Distribution | 0.1242 | 0.0762 | 0.0450 | 0.0450 |
| VAT — Others | 0.0012 | 0.0012 | 0.0012 | 0.0012 |
| UC — NPC Stranded Debt | 0.0428 | 0.0428 | 0.0428 | 0.0428 |
| UC — Missionary Electrification | 0.2763 | 0.2763 | 0.2763 | 0.2763 |
| UC — Environmental Charges | 0.0025 | 0.0025 | 0.0025 | 0.0025 |
| FIT ALL (Renewable) | 0.2011 | 0.2011 | 0.2011 | 0.2011 |
| GEA ALL (Renewable) | 0.0371 | 0.0371 | 0.0371 | 0.0371 |
| **GRAND TOTAL** | **9.6434** | **9.1954** | **8.8517** | **7.5253** |

### Fixed Monthly Charges (COMM'L and above)

| Charge | Amount |
|---|---|
| Retail Electric Service Charge | ₱59.73 / meter / customer / month |
| Metering Charge | ₱54.92 / meter / customer / month |
| VAT on Fixed Charges | ₱13.758 / meter / customer / month |

### Discounts & Special Rates

| Discount | Amount | Legal Basis |
|---|---|---|
| Senior Citizen Discount | 5% of energy total | Republic Act 9994 |

---

## Bill Components

When a bill is generated, the following sections are printed in order:

1. **Customer Header** — Account number, name, address, customer type, bill date
2. **Meter Reading** — Previous reading, present reading, and total kWh consumed
3. **Generation, Transmission & System Loss** — itemized per component with rate and amount
4. **Distribution Revenue** — Network charge, Retail Electric Service, Metering Charge
5. **RFSC** — Reinvestment Fund for Sustainable CAPEx
6. **Others** — Lifeline Rate Subsidy, Senior Citizen Subsidy
7. **VAT on Government Revenues** — 12% breakdown per component
8. **Universal Charges** — NPC Stranded Debt, Missionary Electrification, Environmental
9. **FIT ALL / GEA ALL** — Renewable Energy charges
10. **Grand Total** — Rate × kWh, plus fixed charges where applicable
11. **Deductions** — Senior Citizen discount (if applicable)
12. **Total Amount Due** — Final payable amount and average rate per kWh
13. **Payment Dates** — Due date and disconnection date

---

## Sample Bill Output

```
══════════════════════════════════════════════════════════════
      BATANGAS ELECTRIC COOPERATIVE II (BATELEC II)
           Official Statement of Account
    Implemented Billing Rates — March 2026
══════════════════════════════════════════════════════════════
  Account No.   : 04-2026-08541
  Customer Name : Sean Sulayao
  Address       : Brgy. 9-A, Lipa City, Batangas
  Customer Type : RES'L — Residential
  Bill Date     : April 18, 2026
  Due Date      : May 03, 2026
  Disconnection : May 18, 2026
──────────────────────────────────────────────────────────────
  METER READING
  Previous Reading              :          1,320 kWh
  Present Reading               :          1,567 kWh
  Total Consumption             :            247 kWh
──────────────────────────────────────────────────────────────
  RATE COMPONENT                          PHP/kWh    AMOUNT (PHP)
──────────────────────────────────────────────────────────────
  Generation, Transmission & System Loss
    Generation Charge                      5.2173       1,288.67
    Transmission Delivery Charge           1.0894         269.08
    System Loss                            0.6894         170.28
    Sub-total                                          1,728.03
  ...
══════════════════════════════════════════════════════════════
  TOTAL AMOUNT DUE                                    2,381.92
  Average Rate (PHP/kWh)                              9.6434
══════════════════════════════════════════════════════════════
```

---

## Project Structure

```
📁 project-folder/
  ├── BatelecBillingSystem2026.java    ← Main source file (single-file program)
  └── README.md                    ← This file
```

After compilation:

```
📁 project-folder/
  ├── BatelecBillingSystem2026.java
  ├── BatelecBillingSystem2026.class   ← Compiled bytecode
  ├── BatelecBillingSystem2026$CustomerType.class
  └── README.md
```

---

## Notes & Disclaimers

- All billing rates are based on the **BATELEC II Implemented Billing Rates for March 2026** official document. Rates change monthly and should be updated accordingly for other billing periods.
- This program is intended for **educational purposes** and as a demonstration of Java programming concepts including enums, switch statements, input validation, formatted output, and date handling.
- This is **not an official BATELEC II system**. For official billing concerns, contact:
  - **Telephone:** (043) 756 – 6337
  - **Website:** [www.batelec2.com.ph](http://www.batelec2.com.ph)
- HV (High Voltage) customer types involve **demand-based charges** (PHP/kW) in actual billing. This program simplifies HV billing to use the published per-kWh grand total rate only.
- Senior Citizen discount is applied to **residential customers only**, consistent with RA 9994 guidelines.

---

*Maliwanag na bukas sa lahat. — BATELEC II*
