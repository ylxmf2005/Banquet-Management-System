package hk.polyu.comp.project2411.bms.dao;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;

public class ReportDAO {
    private SQLConnection sqlConnection;

    private BanquetDAO banquetDAO;
    private ChartGenerator chartGenerator;

    public ReportDAO(SQLConnection sqlConnection) throws SQLException {
        this.sqlConnection = sqlConnection;
        this.banquetDAO = new BanquetDAO(sqlConnection);
        this.chartGenerator = new ChartGenerator();
    }

    public File generateReport() throws Exception {
        String outputPath = "output/reports/";
        String fileName = "BMSReport_" + System.currentTimeMillis() + ".pdf";
        String dest = outputPath + fileName;
        
        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        File pdfFile = new File(dest);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        addCoverPage(document);
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        addBanquetSummary(document);
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        addDetailedBanquetReports(document);

        addOverallPopularMealsAndDrinks(document); // Add this line

        // Add attendance behavior analysis
        addAttendanceBehaviorSection(document);

        document.close();
        System.out.println("PDF Report generated successfully at: " + pdfFile.getAbsolutePath());
        
        return pdfFile;
    }

    private void addCoverPage(Document document) {
        // Add title
        Paragraph title = new Paragraph("Registration Status Analysis Report")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(24)
                .setBold();
        document.add(title);

        // Add subtitle
        Paragraph subtitle = new Paragraph("Banquet Management System")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(18);
        document.add(subtitle);

        // Add date
        Paragraph date = new Paragraph("Date: " + java.time.LocalDate.now())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12);
        document.add(date);
    }

    private void addBanquetSummary(Document document) throws SQLException {
        // Fetch banquet data from the database
        List<Map<String, Object>> banquets = banquetDAO.getAllBanquetsWithReservationStats();

        // Add Section Title
        document.add(new Paragraph("Banquet Summary").setFontSize(18).setBold());

        // Create a table with appropriate columns
        float[] columnWidths = {180F, 100F, 100F, 50F, 70F, 70F, 80F};
        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100));

        // Add table headers
        table.addHeaderCell(new Cell().add(new Paragraph("Banquet Name").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Date & Time").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Location").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Quota").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Seats Reserved").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Seats Available").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Reservation Rate (%)").setBold()));

        // Fill table rows with banquet data
        for (Map<String, Object> banquet : banquets) {
            int banquetBIN = ((Number) banquet.get("BIN")).intValue();
            String name = (String) banquet.get("NAME");
            String dateTime = banquet.get("DATETIME").toString();
            String location = (String) banquet.get("LOCATION");
            int quota = ((Number) banquet.get("QUOTA")).intValue();
            int seatsReserved = ((Number) banquet.get("SEATSRESERVED")).intValue();
            int seatsAvailable = quota - seatsReserved;
            double reservationRate = seatsReserved * 100.0 / quota;

            table.addCell(new Cell().add(new Paragraph(name)));
            table.addCell(new Cell().add(new Paragraph(dateTime)));
            table.addCell(new Cell().add(new Paragraph(location)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(quota))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(seatsReserved))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(seatsAvailable))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f%%", reservationRate))));
        }

        document.add(table);
    }

    private void addDetailedBanquetReports(Document document) throws SQLException, IOException {
        // Fetch banquet data from the database
        List<Map<String, Object>> banquets = banquetDAO.getAllBanquetsWithReservationStats();

        for (Map<String, Object> banquet : banquets) {
            int banquetBIN = ((Number) banquet.get("BIN")).intValue();
            String name = (String) banquet.get("NAME");
            String dateTime = banquet.get("DATETIME").toString();
            String location = (String) banquet.get("LOCATION");
            int quota = ((Number) banquet.get("QUOTA")).intValue();
            int seatsReserved = ((Number) banquet.get("SEATSRESERVED")).intValue();
            int seatsAvailable = quota - seatsReserved;
            double reservationRate = seatsReserved * 100.0 / quota;

            // Add banquet details
            document.add(new Paragraph("Banquet Details").setFontSize(16).setBold());
            document.add(new Paragraph("Name: " + name));
            document.add(new Paragraph("Date & Time: " + dateTime));
            document.add(new Paragraph("Location: " + location));
            document.add(new Paragraph("Quota: " + quota));
            document.add(new Paragraph("Seats Reserved: " + seatsReserved));
            document.add(new Paragraph(String.format("Reservation Rate: %.2f%%", reservationRate)));

            // Generate and add meal choice pie chart
            Map<String, Integer> mealChoiceData = banquetDAO.getMealChoiceCounts(banquetBIN);
            if (!mealChoiceData.isEmpty()) {
                document.add(new Paragraph("Meal Choice Distribution:").setBold());
                Image mealPieChart = chartGenerator.createPieChart(mealChoiceData, "Meal Choices for " + name);
                document.add(mealPieChart);

                // Start a new page after the pie chart
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }

            // Generate and add drink choice pie chart
            Map<String, Integer> drinkChoiceData = banquetDAO.getDrinkChoiceCounts(banquetBIN);
            if (!drinkChoiceData.isEmpty()) {
                document.add(new Paragraph("Drink Choice Distribution:").setBold());
                Image drinkPieChart = chartGenerator.createPieChart(drinkChoiceData, "Drink Choices for " + name);
                document.add(drinkPieChart);

                // Start a new page after the pie chart
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }

            // Add attendee details
            List<Map<String, Object>> attendees = banquetDAO.getAttendees(banquetBIN);
            if (!attendees.isEmpty()) {
                document.add(new Paragraph("Attendee Details:").setFontSize(16).setBold());

                float[] attendeeColumnWidths = {50F, 150F, 80F, 80F, 100F, 100F, 100F};
                Table attendeeTable = new Table(attendeeColumnWidths);
                attendeeTable.setWidth(UnitValue.createPercentValue(100));

                // Add attendee table headers
                attendeeTable.addHeaderCell(new Cell().add(new Paragraph("Seat No.").setBold()));
                attendeeTable.addHeaderCell(new Cell().add(new Paragraph("Attendee Email").setBold()));
                attendeeTable.addHeaderCell(new Cell().add(new Paragraph("First Name").setBold()));
                attendeeTable.addHeaderCell(new Cell().add(new Paragraph("Last Name").setBold()));
                attendeeTable.addHeaderCell(new Cell().add(new Paragraph("Meal Choice").setBold()));
                attendeeTable.addHeaderCell(new Cell().add(new Paragraph("Drink Choice").setBold()));
                attendeeTable.addHeaderCell(new Cell().add(new Paragraph("Remarks").setBold()));

                // Populate attendee table
                for (Map<String, Object> attendee : attendees) {
                    attendeeTable.addCell(new Cell().add(new Paragraph(attendee.get("SEATNO").toString())));
                    attendeeTable.addCell(new Cell().add(new Paragraph((String) attendee.get("ATTENDEEEMAIL"))));
                    attendeeTable.addCell(new Cell().add(new Paragraph((String) attendee.get("FIRSTNAME"))));
                    attendeeTable.addCell(new Cell().add(new Paragraph((String) attendee.get("LASTNAME"))));
                    attendeeTable.addCell(new Cell().add(new Paragraph((String) attendee.get("MEALCHOICE"))));
                    attendeeTable.addCell(new Cell().add(new Paragraph((String) attendee.get("DRINKCHOICE"))));
                    attendeeTable.addCell(new Cell().add(new Paragraph((String) attendee.get("REMARKS") != null ? (String) attendee.get("REMARKS") : "")));
                }

                document.add(attendeeTable);
            }

            // Start a new page at the end of each banquet's details
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        }
    }

    private void addOverallPopularMealsAndDrinks(Document document) throws SQLException, IOException {
        // Add Section Title
        document.add(new Paragraph("Overall Popular Meals and Drinks").setFontSize(18).setBold());

        // Fetch overall popular meals
        Map<String, Integer> popularMeals = banquetDAO.getOverallPopularMeals();

        if (!popularMeals.isEmpty()) {
            document.add(new Paragraph("Popular Meals Across All Banquets:").setBold());
            Image mealPieChart = chartGenerator.createPieChart(popularMeals, "Popular Meals");
            document.add(mealPieChart);

            // Start a new page after the meal pie chart
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        } else {
            document.add(new Paragraph("No meal data available."));
        }

        // Fetch overall popular drinks
        Map<String, Integer> popularDrinks = banquetDAO.getOverallPopularDrinks();

        if (!popularDrinks.isEmpty()) {
            document.add(new Paragraph("Popular Drinks Across All Banquets:").setBold());
            Image drinkPieChart = chartGenerator.createPieChart(popularDrinks, "Popular Drinks");
            document.add(drinkPieChart);

            // Start a new page after the drink pie chart
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        } else {
            document.add(new Paragraph("No drink data available."));
        }
    }

    private void addAttendanceBehaviorSection(Document document) throws SQLException, IOException {
        // Add Section Title
        document.add(new Paragraph("Attendance Behavior Analysis").setFontSize(18).setBold());

        // Registration Trends Over Time
        document.add(new Paragraph("Registration Trends Over Time:").setBold());
        Map<String, Integer> registrationTrends = banquetDAO.getRegistrationTrends();
        if (!registrationTrends.isEmpty()) {
            // Optionally, create a line chart of registration trends
            // For simplicity, we'll create a table
            Table trendTable = new Table(new float[]{200F, 100F});
            trendTable.setWidth(UnitValue.createPercentValue(50));

            trendTable.addHeaderCell(new Cell().add(new Paragraph("Date").setBold()));
            trendTable.addHeaderCell(new Cell().add(new Paragraph("Registrations").setBold()));

            for (Map.Entry<String, Integer> entry : registrationTrends.entrySet()) {
                trendTable.addCell(new Cell().add(new Paragraph(entry.getKey())));
                trendTable.addCell(new Cell().add(new Paragraph(entry.getValue().toString())));
            }
            document.add(trendTable);
        } else {
            document.add(new Paragraph("No registration data available."));
        }

        // Start a new page after the registration trends table
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        // Peak Registration Times
        document.add(new Paragraph("Peak Registration Times:").setBold());
        Map<Integer, Integer> peakTimes = banquetDAO.getPeakRegistrationTimes();
        if (!peakTimes.isEmpty()) {
            // Optionally, create a bar chart of peak registration times
            // For simplicity, we'll create a table
            Table peakTimeTable = new Table(new float[]{150F, 100F});
            peakTimeTable.setWidth(UnitValue.createPercentValue(50));

            peakTimeTable.addHeaderCell(new Cell().add(new Paragraph("Hour of Day").setBold()));
            peakTimeTable.addHeaderCell(new Cell().add(new Paragraph("Registrations").setBold()));

            for (Map.Entry<Integer, Integer> entry : peakTimes.entrySet()) {
                String hourRange = String.format("%02d:00 - %02d:59", entry.getKey(), entry.getKey());
                peakTimeTable.addCell(new Cell().add(new Paragraph(hourRange)));
                peakTimeTable.addCell(new Cell().add(new Paragraph(entry.getValue().toString())));
            }
            document.add(peakTimeTable);
        } else {
            document.add(new Paragraph("No registration time data available."));
        }

        // Start a new page after the peak registration times table
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
    }

    public static void main(String[] args) {
        try {
            ReportDAO reportGenerator = new ReportDAO(new SQLConnection());
            reportGenerator.generateReport();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
