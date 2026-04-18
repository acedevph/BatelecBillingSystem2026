import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// ══════════════════════════════════════════════════════════════════
//  BATELEC II — IMPLEMENTED BILLING RATES FOR MARCH 2026
//  Based on official rate table (Mainland & NPC-SPUG/Tingloy)
// ══════════════════════════════════════════════════════════════════

public class BatelecBillingSystem2026 {

    // ──────────────────────────────────────────────────────────────
    //  CUSTOMER TYPE ENUM
    // ──────────────────────────────────────────────────────────────
    enum CustomerType {
        RESIDENTIAL("RES'L — Residential"),
        BAPA_MM("BAPA (MM)"),
        COMMERCIAL("COMM'L — Commercial"),
        SMALL_INDUSTRIAL("Small Industrial"),
        CWS("CWS"),
        PUBLIC_BUILDING("Public Building"),
        STREET_LIGHTS("Street Lights"),
        COMM_HV("Commercial HV"),
        SMALL_IND_HV("Small Industrial HV"),
        PUBLIC_BUILDING_HV("Public Building HV"),
        LARGE_INDUSTRIAL("Large Industrial"),
        NPC_RESIDENTIAL("NPC-SPUG Residential (Tingloy)"),
        NPC_ST_LIGHTS("NPC-SPUG Street Lights (Tingloy)"),
        NPC_COMM_HV("NPC-SPUG Commercial HV (Tingloy)");

        final String label;
        CustomerType(String label) { this.label = label; }
    }

    // ──────────────────────────────────────────────────────────────
    //  EXACT RATES — MARCH 2026 (PHP/kWh unless noted)
    // ──────────────────────────────────────────────────────────────

    // Generation Charge — same for all mainland types
    static final double GEN_CHARGE             = 5.2173;

    // Transmission Delivery Charge
    static final double TRANS_RES_BAPA         = 1.0894;  // RES'L / BAPA
    static final double TRANS_LV               = 1.0419;  // COMM'L / LV types
    // HV types: 736.6300 PHP/kW — demand-based, not per kWh billing here

    // System Loss
    static final double SYSTEM_LOSS            = 0.6894;

    // Distribution Network Charge
    static final double DIST_NETWORK_RES       = 0.2748;  // RES'L / BAPA
    static final double DIST_NETWORK_LV        = 0.3748;  // COMM'L / LV / HV

    // Distribution Line Rental (HV): 118.5500 PHP/kW — demand-based

    // Retail Electric Service Charge
    static final double RETAIL_RES             = 0.4140;  // PHP/kWh — RES'L
    static final double RETAIL_BAPA            = 0.1140;  // PHP/kWh — BAPA
    static final double RETAIL_FIXED           = 59.7300; // PHP/meter/cust/mo — others

    // Metering Charge
    static final double METERING_RES           = 0.3460;  // PHP/kWh — RES'L
    static final double METERING_BAPA          = 0.2460;  // PHP/kWh — BAPA
    static final double METERING_FIXED         = 54.9200; // PHP/meter/cust/mo — others

    // Reinvestment Fund for Sustainable CAPEx (RFSC) — all types
    static final double RFSC                   = 0.3216;

    // Others: Lifeline Rate Subsidy & Senior Citizen Subsidy — all types
    static final double LIFELINE_SUBSIDY       = 0.0100;
    static final double SC_SUBSIDY_PER_KWH     = 0.0001;
    // Sub-total Others = 0.0101 for all

    // VAT on Government Revenues — 12%
    static final double VAT_GEN                = 0.4045;  // all
    static final double VAT_TRANS_RES_BAPA     = 0.1144;
    static final double VAT_TRANS_LV           = 0.1094;
    static final double VAT_TRANS_HV           = 0.2447;
    static final double VAT_SYSLOSS            = 0.0767;  // all
    static final double VAT_DIST_RES           = 0.1242;
    static final double VAT_DIST_BAPA          = 0.0762;
    static final double VAT_DIST_LV_HV        = 0.0450;
    static final double VAT_OTHERS             = 0.0012;  // all
    // VAT on fixed monthly charges (meter/customer/month)
    static final double VAT_FIXED_MONTHLY      = 13.7580;

    // Universal Charges
    static final double UC_STRANDED_CONTRACT   = 0.0000;
    static final double UC_NPC_STRANDED_DEBT   = 0.0428;
    static final double UC_MISSIONARY          = 0.2763;
    static final double UC_ENVIRONMENTAL       = 0.0025;

    // FIT ALL & GEA ALL (Renewable) — all types
    static final double FIT_ALL                = 0.2011;
    static final double GEA_ALL                = 0.0371;

    // Grand Total Rates per kWh (from official table)
    static final double GT_RES                 = 9.6434;
    static final double GT_BAPA                = 9.1954;
    static final double GT_COMM                = 8.8517;
    static final double GT_SMALL_IND           = 8.8517;
    static final double GT_CWS                 = 8.8517;
    static final double GT_PUB_BLDG            = 8.8517;
    static final double GT_ST_LIGHTS           = 8.8517;
    static final double GT_HV                  = 7.5253;
    static final double GT_NPC_RES             = 10.7410;
    static final double GT_NPC_ST              = 10.3018;
    static final double GT_NPC_HV              = 9.8820;

    // Senior Citizen Discount (RA 9994) — 5%
    static final double SC_DISCOUNT_RATE       = 0.05;

    // ──────────────────────────────────────────────────────────────
    //  HELPERS
    // ──────────────────────────────────────────────────────────────
    static double grandTotalRate(CustomerType t) {
        switch (t) {
            case RESIDENTIAL:        return GT_RES;
            case BAPA_MM:            return GT_BAPA;
            case COMMERCIAL:         return GT_COMM;
            case SMALL_INDUSTRIAL:   return GT_SMALL_IND;
            case CWS:                return GT_CWS;
            case PUBLIC_BUILDING:    return GT_PUB_BLDG;
            case STREET_LIGHTS:      return GT_ST_LIGHTS;
            case COMM_HV:
            case SMALL_IND_HV:
            case PUBLIC_BUILDING_HV:
            case LARGE_INDUSTRIAL:   return GT_HV;
            case NPC_RESIDENTIAL:    return GT_NPC_RES;
            case NPC_ST_LIGHTS:      return GT_NPC_ST;
            case NPC_COMM_HV:        return GT_NPC_HV;
            default:                 return GT_RES;
        }
    }

    static boolean isResType(CustomerType t) {
        return t == CustomerType.RESIDENTIAL || t == CustomerType.NPC_RESIDENTIAL;
    }

    static boolean isBapa(CustomerType t) { return t == CustomerType.BAPA_MM; }

    static boolean hasFixedCharges(CustomerType t) {
        return !isResType(t) && !isBapa(t);
    }

    // ──────────────────────────────────────────────────────────────
    //  BILL COMPUTATION & PRINT
    // ──────────────────────────────────────────────────────────────
    static void computeBill(String acctNo, String name, String addr,
                             CustomerType type, int prevRead, int presRead,
                             boolean isSenior, String billingMonth) {

        int kwh = presRead - prevRead;
        if (kwh < 0) {
            System.out.println("  ERROR: Present reading cannot be lower than previous reading.");
            return;
        }

        // ── Component amounts ──────────────────────────────────
        double gen    = GEN_CHARGE * kwh;

        double trans  = isResType(type) || isBapa(type)
                        ? TRANS_RES_BAPA * kwh : TRANS_LV * kwh;

        double sysl   = SYSTEM_LOSS * kwh;
        double subGTS = gen + trans + sysl;

        // Distribution
        double dNet, dRetail, dMeter, fixedRetail = 0, fixedMeter = 0;
        if (isResType(type)) {
            dNet    = DIST_NETWORK_RES * kwh;
            dRetail = RETAIL_RES * kwh;
            dMeter  = METERING_RES * kwh;
        } else if (isBapa(type)) {
            dNet    = DIST_NETWORK_RES * kwh;
            dRetail = RETAIL_BAPA * kwh;
            dMeter  = METERING_BAPA * kwh;
        } else {
            dNet    = DIST_NETWORK_LV * kwh;
            dRetail = 0;
            dMeter  = 0;
            fixedRetail = RETAIL_FIXED;
            fixedMeter  = METERING_FIXED;
        }
        double subDist = dNet + dRetail + dMeter + fixedRetail + fixedMeter;

        // RFSC
        double rfsc = RFSC * kwh;

        // Others
        double lifelineSub = LIFELINE_SUBSIDY * kwh;
        double scSub       = SC_SUBSIDY_PER_KWH * kwh;
        double subOthers   = lifelineSub + scSub;

        // VAT on govt revenues
        double vatGen  = VAT_GEN * kwh;
        double vatTr   = isResType(type) || isBapa(type)
                         ? VAT_TRANS_RES_BAPA * kwh : VAT_TRANS_LV * kwh;
        double vatSL   = VAT_SYSLOSS * kwh;
        double vatDist = isResType(type) ? VAT_DIST_RES * kwh
                         : isBapa(type)  ? VAT_DIST_BAPA * kwh
                         : VAT_DIST_LV_HV * kwh;
        double vatOth  = VAT_OTHERS * kwh;
        double vatFixed= hasFixedCharges(type) ? VAT_FIXED_MONTHLY : 0;
        double subVAT  = vatGen + vatTr + vatSL + vatDist + vatOth + vatFixed;

        // Universal Charges
        double ucDebt  = UC_NPC_STRANDED_DEBT * kwh;
        double ucMiss  = UC_MISSIONARY * kwh;
        double ucEnv   = UC_ENVIRONMENTAL * kwh;
        double subUC   = ucDebt + ucMiss + ucEnv;

        // FIT / GEA
        double fit = FIT_ALL * kwh;
        double gea = GEA_ALL * kwh;

        // Grand Total
        double rate       = grandTotalRate(type);
        double energyAmt  = rate * kwh;
        double grandTotal = energyAmt + fixedRetail + fixedMeter + vatFixed;

        // Senior Citizen Discount
        double scDiscount = (isSenior && isResType(type))
                            ? energyAmt * SC_DISCOUNT_RATE : 0;

        double amountDue  = grandTotal - scDiscount;
        double avgPerKwh  = kwh > 0 ? amountDue / kwh : 0;

        // ── Print ──────────────────────────────────────────────
        String L  = "─".repeat(62);
        String DL = "═".repeat(62);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        LocalDate today = LocalDate.now();

        System.out.println("\n" + DL);
        System.out.println("      BATANGAS ELECTRIC COOPERATIVE II (BATELEC II)");
        System.out.println("           Official Statement of Account");
        System.out.println("    Implemented Billing Rates — " + billingMonth);
        System.out.println(DL);
        System.out.printf("  Account No.   : %s%n", acctNo);
        System.out.printf("  Customer Name : %s%n", name);
        System.out.printf("  Address       : %s%n", addr);
        System.out.printf("  Customer Type : %s%n", type.label);
        System.out.printf("  Bill Date     : %s%n", today.format(df));
        System.out.printf("  Due Date      : %s%n", today.plusDays(15).format(df));
        System.out.printf("  Disconnection : %s%n", today.plusDays(30).format(df));
        if (isSenior && isResType(type))
            System.out.println("  Senior Citizen: YES — 5% discount applied (RA 9994)");
        System.out.println(L);

        // Meter reading
        System.out.println("  METER READING");
        System.out.printf("  %-30s: %,12d kWh%n", "Previous Reading", prevRead);
        System.out.printf("  %-30s: %,12d kWh%n", "Present Reading",  presRead);
        System.out.printf("  %-30s: %,12d kWh%n", "Total Consumption", kwh);
        System.out.println(L);

        // Rate breakdown header
        System.out.printf("  %-36s %9s  %13s%n","RATE COMPONENT","PHP/kWh","AMOUNT (PHP)");
        System.out.println(L);

        // GTS
        System.out.println("  Generation, Transmission & System Loss");
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  Generation Charge",GEN_CHARGE,gen);
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  Transmission Delivery Charge",trans/Math.max(kwh,1),trans);
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  System Loss",SYSTEM_LOSS,sysl);
        System.out.printf("  %-36s %9s  %,13.2f%n","  Sub-total","",subGTS);
        System.out.println();

        // Distribution
        System.out.println("  Distribution Revenue");
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  Distribution Network Charge",
                dNet/Math.max(kwh,1),dNet);
        if (isResType(type)) {
            System.out.printf("  %-36s %9.4f  %,13.2f%n","  Retail Electric Svc Charge",RETAIL_RES,dRetail);
            System.out.printf("  %-36s %9.4f  %,13.2f%n","  Metering Charge",METERING_RES,dMeter);
        } else if (isBapa(type)) {
            System.out.printf("  %-36s %9.4f  %,13.2f%n","  Retail Electric Svc Charge",RETAIL_BAPA,dRetail);
            System.out.printf("  %-36s %9.4f  %,13.2f%n","  Metering Charge",METERING_BAPA,dMeter);
        } else {
            System.out.printf("  %-36s %9s  %,13.2f%n","  Retail Elec. Svc (fixed/mo)","59.7300/mo",fixedRetail);
            System.out.printf("  %-36s %9s  %,13.2f%n","  Metering Charge (fixed/mo)","54.9200/mo",fixedMeter);
        }
        System.out.printf("  %-36s %9s  %,13.2f%n","  Sub-total","",subDist);
        System.out.println();

        // RFSC
        System.out.println("  Reinvestment Fund for Sustainable CAPEx");
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  RFSC",RFSC,rfsc);
        System.out.println();

        // Others
        System.out.println("  Others");
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  Lifeline Rate Subsidy (discount)",LIFELINE_SUBSIDY,lifelineSub);
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  Senior Citizen Subsidy",SC_SUBSIDY_PER_KWH,scSub);
        System.out.printf("  %-36s %9s  %,13.2f%n","  Sub-total","",subOthers);
        System.out.println();

        // VAT
        System.out.println("  VAT on Government Revenues (12%)");
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  Generation",VAT_GEN,vatGen);
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  Transmission",vatTr/Math.max(kwh,1),vatTr);
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  System Loss",VAT_SYSLOSS,vatSL);
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  Distribution",vatDist/Math.max(kwh,1),vatDist);
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  Others",VAT_OTHERS,vatOth);
        if (hasFixedCharges(type))
            System.out.printf("  %-36s %9s  %,13.2f%n","  VAT on Fixed Charges (13.758/mo)","",vatFixed);
        System.out.printf("  %-36s %9s  %,13.2f%n","  Sub-total","",subVAT);
        System.out.println();

        // Universal Charges
        System.out.println("  Universal Charges");
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  Stranded Contract Cost",UC_STRANDED_CONTRACT,0.0);
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  NPC Stranded Debt",UC_NPC_STRANDED_DEBT,ucDebt);
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  Missionary Electrification",UC_MISSIONARY,ucMiss);
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  Environmental Charges",UC_ENVIRONMENTAL,ucEnv);
        System.out.printf("  %-36s %9s  %,13.2f%n","  Sub-total","",subUC);
        System.out.println();

        // FIT / GEA
        System.out.println("  FIT ALL / GEA ALL (Renewable Energy)");
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  FIT ALL (Renewable)",FIT_ALL,fit);
        System.out.printf("  %-36s %9.4f  %,13.2f%n","  GEA ALL (Renewable)",GEA_ALL,gea);
        System.out.println(L);

        // Grand Total
        System.out.printf("  %-36s %9.4f  %,13.2f%n","GRAND TOTAL RATE (PHP/kWh)",rate,energyAmt);
        if (hasFixedCharges(type)) {
            System.out.printf("  %-36s %9s  %,13.2f%n","Add: Fixed Monthly Charges","",fixedRetail+fixedMeter);
            System.out.printf("  %-36s %9s  %,13.2f%n","Add: VAT on Fixed Charges","",vatFixed);
        }
        System.out.println(L);

        // Deductions
        if (scDiscount > 0)
            System.out.printf("  %-36s %9s  (%,12.2f)%n","Senior Citizen Discount (5%)","",scDiscount);

        System.out.println(L);
        System.out.printf("  %-36s %9s  %,13.2f%n","TOTAL AMOUNT DUE","",amountDue);
        System.out.printf("  %-36s %9s  %,13.4f%n","Average Rate (PHP/kWh)","",avgPerKwh);
        System.out.println(DL);
        System.out.println("  * 2% surcharge applies after due date.");
        System.out.println("  * BATELEC II Tel: (043) 756 – 6337 | www.batelec2.com.ph");
        System.out.println(DL);
    }

    // ──────────────────────────────────────────────────────────────
    //  RATE TABLE
    // ──────────────────────────────────────────────────────────────
    static void showRateTable() {
        String L  = "─".repeat(72);
        String DL = "═".repeat(72);
        System.out.println("\n" + DL);
        System.out.println("    IMPLEMENTED BILLING RATES — MARCH 2026  (PHP/kWh)");
        System.out.println(DL);
        System.out.printf("  %-34s %7s %7s %7s %7s%n","Component","RES'L","BAPA","COMM'L","HV");
        System.out.println(L);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","Generation Charge",5.2173,5.2173,5.2173,5.2173);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","Transmission Delivery",1.0894,1.0894,1.0419,1.0419);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","System Loss",0.6894,0.6894,0.6894,0.6894);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","  GTS Sub-total",6.9961,6.9961,6.9486,6.9486);
        System.out.println(L);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","Distribution Network Charge",0.2748,0.2748,0.3748,0.3748);
        System.out.printf("  %-34s %7.4f %7.4f %7s %7s%n","Retail Elec Svc (kWh)",0.4140,0.1140,"fixed","fixed");
        System.out.printf("  %-34s %7.4f %7.4f %7s %7s%n","Metering Charge (kWh)",0.3460,0.2460,"fixed","fixed");
        System.out.printf("  %-34s %7s %7s %7.2f %7.2f%n","Retail Elec (fixed/mo, PHP)","—","—",59.73,59.73);
        System.out.printf("  %-34s %7s %7s %7.2f %7.2f%n","Metering (fixed/mo, PHP)","—","—",54.92,54.92);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","  Dist Sub-total",1.0348,0.6348,0.3748,0.0000);
        System.out.println(L);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","RFSC",0.3216,0.3216,0.3216,0.3216);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","Others Sub-total",0.0101,0.0101,0.0101,0.0101);
        System.out.println(L);
        System.out.println("  VAT on Government Revenues (12%):");
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","  Generation",0.4045,0.4045,0.4045,0.4045);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","  Transmission",0.1144,0.1144,0.1094,0.2447);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","  System Loss",0.0767,0.0767,0.0767,0.0767);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","  Distribution",0.1242,0.0762,0.0450,0.0450);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","  Others",0.0012,0.0012,0.0012,0.0012);
        System.out.printf("  %-34s %7s %7s %7.4f %7.4f%n","  VAT Fixed Monthly (PHP/mo)","—","—",13.758,13.758);
        System.out.println(L);
        System.out.println("  Universal Charges:");
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","  NPC Stranded Debt",0.0428,0.0428,0.0428,0.0428);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","  Missionary Electrification",0.2763,0.2763,0.2763,0.2763);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","  Environmental Charges",0.0025,0.0025,0.0025,0.0025);
        System.out.println(L);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","FIT ALL (Renewable)",0.2011,0.2011,0.2011,0.2011);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","GEA ALL (Renewable)",0.0371,0.0371,0.0371,0.0371);
        System.out.println(DL);
        System.out.printf("  %-34s %7.4f %7.4f %7.4f %7.4f%n","GRAND TOTAL (PHP/kWh)",9.6434,9.1954,8.8517,7.5253);
        System.out.println(DL);
        System.out.println("  NPC-SPUG (TINGLOY):");
        System.out.printf("  %-34s %7.4f%n","  Residential",10.7410);
        System.out.printf("  %-34s %7.4f%n","  Street Lights",10.3018);
        System.out.printf("  %-34s %7.4f%n","  Commercial HV",9.8820);
        System.out.println(DL);
    }

    // ──────────────────────────────────────────────────────────────
    //  INPUT HELPERS
    // ──────────────────────────────────────────────────────────────
    static int getInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                int v = sc.nextInt(); sc.nextLine();
                if (v >= 0) return v;
                System.out.println("  Must be 0 or greater.");
            } else {
                System.out.println("  Invalid — enter a whole number.");
                sc.nextLine();
            }
        }
    }

    static CustomerType selectType(Scanner sc) {
        System.out.println("\n  MAINLAND:");
        System.out.println("   [1]  Residential (RES'L)");
        System.out.println("   [2]  BAPA (MM)");
        System.out.println("   [3]  Commercial (COMM'L)");
        System.out.println("   [4]  Small Industrial");
        System.out.println("   [5]  CWS");
        System.out.println("   [6]  Public Building");
        System.out.println("   [7]  Street Lights");
        System.out.println("   [8]  Commercial HV");
        System.out.println("   [9]  Small Industrial HV");
        System.out.println("  [10]  Public Building HV");
        System.out.println("  [11]  Large Industrial");
        System.out.println("  NPC-SPUG (TINGLOY):");
        System.out.println("  [12]  NPC Residential");
        System.out.println("  [13]  NPC Street Lights");
        System.out.println("  [14]  NPC Commercial HV");
        while (true) {
            System.out.print("  Select customer type (1-14): ");
            if (sc.hasNextInt()) {
                int c = sc.nextInt(); sc.nextLine();
                switch (c) {
                    case 1:  return CustomerType.RESIDENTIAL;
                    case 2:  return CustomerType.BAPA_MM;
                    case 3:  return CustomerType.COMMERCIAL;
                    case 4:  return CustomerType.SMALL_INDUSTRIAL;
                    case 5:  return CustomerType.CWS;
                    case 6:  return CustomerType.PUBLIC_BUILDING;
                    case 7:  return CustomerType.STREET_LIGHTS;
                    case 8:  return CustomerType.COMM_HV;
                    case 9:  return CustomerType.SMALL_IND_HV;
                    case 10: return CustomerType.PUBLIC_BUILDING_HV;
                    case 11: return CustomerType.LARGE_INDUSTRIAL;
                    case 12: return CustomerType.NPC_RESIDENTIAL;
                    case 13: return CustomerType.NPC_ST_LIGHTS;
                    case 14: return CustomerType.NPC_COMM_HV;
                    default: System.out.println("  Invalid. Enter 1–14.");
                }
            } else { sc.nextLine(); System.out.println("  Invalid input."); }
        }
    }

    static void generateOneBill(Scanner sc) {
        System.out.println("\n" + "─".repeat(62));
        System.out.println("  ENTER CUSTOMER INFORMATION");
        System.out.println("─".repeat(62));
        System.out.print("  Account Number  : "); String acct  = sc.nextLine().trim(); if(acct.isEmpty())acct="N/A";
        System.out.print("  Customer Name   : "); String name  = sc.nextLine().trim();
        System.out.print("  Address         : "); String addr  = sc.nextLine().trim();
        System.out.print("  Billing Month   : "); String month = sc.nextLine().trim();
        if (month.isEmpty()) month = "March 2026";

        CustomerType type = selectType(sc);
        int prev = getInt(sc, "  Previous Reading (kWh) : ");
        int pres = getInt(sc, "  Present Reading  (kWh) : ");

        boolean senior = false;
        if (isResType(type)) {
            System.out.print("  Senior Citizen? (yes/no) : ");
            senior = sc.nextLine().trim().toLowerCase().startsWith("y");
        }

        computeBill(acct, name, addr, type, prev, pres, senior, month);
    }

    // ──────────────────────────────────────────────────────────────
    //  MAIN
    // ──────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║      BATELEC II — ELECTRICITY BILLING SYSTEM              ║");
            System.out.println("║      Implemented Rates: MARCH 2026                        ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.println("║  [1]  Generate Bill                                       ║");
            System.out.println("║  [2]  View Complete Rate Table (March 2026)               ║");
            System.out.println("║  [3]  Batch Billing (multiple customers)                  ║");
            System.out.println("║  [0]  Exit                                                ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            System.out.print("  Choose option: ");
            while (!sc.hasNextInt()) { sc.nextLine(); System.out.print("  Invalid. Choose: "); }
            choice = sc.nextInt(); sc.nextLine();
            switch (choice) {
                case 1: generateOneBill(sc); break;
                case 2: showRateTable();     break;
                case 3:
                    System.out.print("  Number of customers to process: ");
                    int n = 1;
                    try { n = Integer.parseInt(sc.nextLine().trim()); } catch (Exception ignored) {}
                    for (int i = 1; i <= n; i++) {
                        System.out.println("\n  ══ Customer " + i + " of " + n + " ══");
                        generateOneBill(sc);
                    }
                    break;
                case 0:
                    System.out.println("\n  Salamat po! Maliwanag na bukas sa lahat. — BATELEC II");
                    break;
                default:
                    System.out.println("  Invalid option.");
            }
        } while (choice != 0);
        sc.close();
    }
}