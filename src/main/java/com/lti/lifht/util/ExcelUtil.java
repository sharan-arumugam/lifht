package com.lti.lifht.util;

import static com.lti.lifht.constant.ExcelConstant.CSV;
import static com.lti.lifht.constant.ExcelConstant.DELIMITER;
import static com.lti.lifht.constant.ExcelConstant.EV_HSSF;
import static com.lti.lifht.constant.ExcelConstant.EV_XSSF;
import static com.lti.lifht.constant.ExcelConstant.FORMATTER;
import static com.lti.lifht.constant.ExcelConstant.NA;
import static com.lti.lifht.constant.ExcelConstant.XLS;
import static com.lti.lifht.constant.ExcelConstant.XLSX;
import static com.lti.lifht.util.CommonUtil.toStream;
import static com.monitorjbl.xlsx.StreamingReader.builder;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for handling .xlsx, .xlx, .csv formats
 * 
 * @author Sharan Arumugam
 * @version 2.0
 *
 */
public class ExcelUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * Row Stream with StreamingReader for .xlsx - compatible with large file
     * 
     * @param inputStream
     * @return list of row map - columns as keys
     */
    public static final Function<InputStream, List<Map<String, String>>> parseXlsx = inputStream -> {
        return asListMap(
                toStream(builder()
                        .open(inputStream)
                        .getSheetAt(0)
                        .iterator()),
                EV_XSSF);
    };

    /**
     * Parse .xls (1997-2004) with vanilla POI, not compatible with large file
     * 
     * @param inputStream
     * @return list of row map - columns as keys
     */
    public static final Function<InputStream, List<Map<String, String>>> parseXls = inputStream -> {
        try {
            return asListMap(
                    toStream(WorkbookFactory
                            .create(inputStream)
                            .getSheetAt(0)
                            .iterator()),
                    EV_HSSF);

        } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    };

    /**
     * IputStream to list map of column as keys
     * 
     * @param rowStream
     * @param evaluator
     * @return list of row map - columns as keys
     */
    private static final List<Map<String, String>> asListMap(Stream<Row> rowStream, FormulaEvaluator evaluator) {

        List<String> rowValueList = rowStream.map(row -> {
            return toStream(row.iterator()).map(cell -> {

                switch (cell.getCellTypeEnum()) {

                case STRING:
                    return cell.getStringCellValue();

                case NUMERIC:
                    return FORMATTER.formatCellValue(cell, evaluator);

                default:
                    return null;
                }
            }).collect(joining(DELIMITER));

        }).collect(toList());

        int headerRow = 0;

        // project allocation
        if (Arrays.asList(rowValueList.get(0).split(DELIMITER)).contains("Resource Project Details")) {
            headerRow = 2;
        }

        String[] headers = rowValueList.get(headerRow).split(DELIMITER);

        List<Map<String, String>> rows = rowValueList.stream()
                .skip(headerRow) // skip headers row
                .map(row -> row.split(DELIMITER))
                .map(row -> {
                    return range(0, row.length)
                            .boxed()
                            .collect(toMap(column -> headers[column], column -> row[column]));
                }).collect(toList());

        return rows;
    }

    /**
     * Parse CSV to list of row map - column as keys
     * 
     * @param inputStream
     * @return list of row map - columns as keys
     */
    public static final Function<InputStream, List<Map<String, String>>> parseCsv = inputStream -> {

        try {
            return StreamSupport.stream(CSVFormat.EXCEL
                    .withHeader()
                    .parse(new InputStreamReader(inputStream)).spliterator(), false)
                    .map(CSVRecord::toMap)
                    .collect(toList());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return null;
    };

    /**
     * Stream workbook to byte array
     * 
     * @param workbook
     * @return byte[] of workbook
     */
    public static final byte[] toByteArray(Workbook workbook) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return outputStream.toByteArray();
    }

    /**
     * get file extension [.csv | .xls | .xlsx]
     */
    public static final Function<String, String> getExtension = fileName -> {
        fileName = fileName.toLowerCase();
        String ext = NA;
        if (fileName.endsWith(CSV)) {
            ext = CSV;
        } else if (fileName.endsWith(XLSX)) {
            ext = XLSX;
        } else if (fileName.endsWith(XLS)) {
            ext = XLS;
        }
        return ext;
    };

    /**
     * Auto-detect extension and parse
     */
    public static final BiFunction<String, InputStream, List<Map<String, String>>> autoParse = (fileName,
            inputStream) -> {

        List<Map<String, String>> rows = null;

        switch (getExtension.apply(fileName)) {
        case CSV:
            rows = parseCsv.apply(inputStream);
            break;
        case XLSX:
            rows = parseXlsx.apply(inputStream);
            break;
        case XLS:
            rows = parseXls.apply(inputStream);
            break;
        }
        return rows;
    };
}
