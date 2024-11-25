package hk.polyu.comp.project2411.bms.dao;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class ChartGenerator {
    public Image createPieChart(Map<String, Integer> data, String title) throws IOException {
        // Create dataset
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        data.forEach(dataset::setValue);

        // Create chart
        JFreeChart chart = ChartFactory.createPieChart(
                title,   // Chart title
                dataset, // Data
                true,    // Include legend
                true,
                false
        );

        // Convert chart to image
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(chartOut, chart, 500, 400);
        ImageData imageData = ImageDataFactory.create(chartOut.toByteArray());
        Image image = new Image(imageData);

        return image;
    }
}