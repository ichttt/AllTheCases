package ichttt.mods.allTheCases;

import ichttt.logicsimModLoader.util.LSMLUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Tobias Hotz
 * (c) Tobias Hotz, 2017
 * Licensed under GPL v3
 */
public class ExcelManager {
    private final Workbook wb;
    private final Sheet sheet;
    private int pos = 4;
    private final int inputCount;
    private final int outputCount;


    public ExcelManager(String sheetName, int inputCount, int outputCount) {
        wb = new HSSFWorkbook();
        this.inputCount = inputCount;
        this.outputCount = outputCount;
        sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(sheetName));
        Row row = sheet.createRow(3);
        int pos;
        for (pos = 0; pos < inputCount; pos++) {
            row.createCell(pos+1).setCellValue(ModInstance.translate("Input") + " " + pos);
        }
        pos++;
        for (int i = 0 ; i < outputCount; i++) {
            row.createCell(i+pos+1).setCellValue(ModInstance.translate("Output") +  " " + i);
        }
    }

    public void writeLine(String[] inputData, String[] outputData) {
        assert (inputData.length == inputCount && outputData.length == outputCount);
        Row row = sheet.createRow(pos);
        int currentCell;
        for (currentCell = 0; currentCell < inputData.length; currentCell++) {
            row.createCell(currentCell + 1).setCellValue(inputData[currentCell]);
        }
        currentCell++;
        for (int i = 0; i < outputData.length; i++) {
            row.createCell(i + currentCell + 1).setCellValue(outputData[i]);
        }
        pos++;
    }

    public boolean saveSheet(String filePath, String fileName) {
        ModInstance.getLogger().finer("Saving to file...");
        if (!fileName.endsWith(".xls")) {
            fileName += ".xls";
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(filePath  + "\\"+ fileName));
            wb.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, ModInstance.translate("ExportFailed") + "\n" + e, ModInstance.translate("Error"), JOptionPane.ERROR_MESSAGE);
            return false;
        }
        finally {
            LSMLUtil.closeSilent(outputStream);
        }
        return true;
    }
}
