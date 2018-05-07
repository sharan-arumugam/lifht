package com.lti.lifht.service;

import static com.lti.lifht.constant.CommonConstant.ENTRY;
import static com.lti.lifht.constant.CommonConstant.EXIT;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException {

        InputStream stream = Main.class.getClassLoader().getResourceAsStream("april-2018.csv");

        Reader in = new InputStreamReader(stream);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);

        BufferedWriter writer = Files.newBufferedWriter(Paths.get("ODC_Access_LTI_04-01-2018.csv"));

        CSVPrinter csvPrinter = new CSVPrinter(writer,
                CSVFormat.DEFAULT.withHeader("Sequence", "Date", "Time", "Event message", "Event number", "Object #1",
                        "Description #1", "Object #2", "Description #2", "Object #3", "Description #3", "Object #4",
                        "Description #4", "Card number"));

        for (CSVRecord record : records) {

            if (StringUtils.isNotEmpty(record.get("Description #2"))) {

                String door = record.get("Description #1");
                String date = record.get("Date");

                LocalDate localDate = null;

                if (date.contains("/")) {
                    localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yy"));
                } else {
                    localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                }

                String[] arr = door.split(" ");

                String entryType = arr[arr.length - 1];

                if (door.contains("Apple Turnstile - 2")) {
                    entryType = door.endsWith(ENTRY) ? EXIT : ENTRY;
                }

                csvPrinter.printRecord(
                        record.get("Sequence"),
                        localDate.format(DateTimeFormatter.ofPattern("MM/dd/yy")),
                        record.get("Time"),
                        record.get("Event message"),
                        record.get("Event number"),
                        record.get("Object #1"),
                        record.get("Description #1"),
                        record.get("Object #2"),
                        record.get("Description #2"),
                        record.get("Object #3"),
                        entryType,
                        record.get("Object #4"),
                        record.get("Description #4"),
                        record.get("Card number"));
            }

        }

        csvPrinter.flush();
        csvPrinter.close();
    }

}
