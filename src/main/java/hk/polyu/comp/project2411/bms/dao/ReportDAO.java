package hk.polyu.comp.project2411.bms.dao;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import hk.polyu.comp.project2411.bms.connection.SQLConnection;

import javax.print.Doc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class ReportDAO {
    private SQLConnection sqlConnection;
    public ReportDAO(SQLConnection sqlConnection) { this.sqlConnection = sqlConnection; }

    public void generateRegistrationStatusReport(String outPath) throws IOException {
        try {
            PdfWriter writer = new PdfWriter(outPath);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            document.setMargins(10, 10, 10, 10);
            Table table = new Table(new float[]{25f, 25f, 25f, 25f});
            table.setTextAlignment(TextAlignment.CENTER);

            String sql = "SELECT * FROM Reserve;";
            List<Map<String, Object>> results = sqlConnection.executeQuery(sql);
            table.addHeaderCell(new Cell().add(new Paragraph("Email")).setWidth(25f));
            table.addHeaderCell(new Cell().add(new Paragraph("BanquetBIN")).setWidth(25f));
            table.addHeaderCell(new Cell().add(new Paragraph("Drink")).setWidth(25f));
            table.addHeaderCell(new Cell().add(new Paragraph("Meal")).setWidth(25f));

            for(Map<String, Object> result : results) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(result.get("ATTENDEEEMAIL"))).setWidth(25f)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(result.get("BANQUETBIN"))).setWidth(25f)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(result.get("DRINKCHOICE"))).setWidth(25f)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(result.get("MEALCHOICE"))).setWidth(25f)));
            }

            document.add(table);
            document.close();
            writer.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
