package com.lti.lifht.constant;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelConstant {

    public final static String DELIMITER = "#%#%";

    public static final XSSFWorkbook WB_XSSF = new XSSFWorkbook();
    public static final HSSFWorkbook WB_HSSF = new HSSFWorkbook();

    public static final DataFormatter FORMATTER = new DataFormatter();
    public static final FormulaEvaluator EV_XSSF = new XSSFFormulaEvaluator(WB_XSSF);
    public static final FormulaEvaluator EV_HSSF = new HSSFFormulaEvaluator(WB_HSSF);

    public static final String CSV = ".csv";
    public static final String XLSX = ".xlsx";
    public static final String XLS = ".xls";
    public static final String INVALID = "invalid";
    public static final String NA = "N/A";

    // head-count map
    public static final Map<String, String> HC_MAP = new HashMap<>();
    static {
        HC_MAP.put("psNumber", "External Emp ID");
        HC_MAP.put("psName", "Name");
        HC_MAP.put("email", "Email");
        HC_MAP.put("offshore", "Offshore");
        HC_MAP.put("manager", "Manager");
        HC_MAP.put("dsId", "DSID");
        HC_MAP.put("billable", "Billable");
    }

    // project-allocation map
    public static final Map<String, String> ALC_MAP = new HashMap<>();
    static {
        ALC_MAP.put("psNumber", "PS Number");
        ALC_MAP.put("businessUnit", "Base BU");
        ALC_MAP.put("customer", "Customer Name");
    }

    // swipe-data map
    public static final Map<String, String> SWP_MAP = new HashMap<>();
    static {
        SWP_MAP.put("sequence", "Sequence");
        SWP_MAP.put("swipeDate", "Date");
        SWP_MAP.put("swipeTime", "Time");
        SWP_MAP.put("eventNumber", "Event Number");
        SWP_MAP.put("swipeDoor", "Description #1");
        SWP_MAP.put("psNumber", "Description #2");
    }

}
