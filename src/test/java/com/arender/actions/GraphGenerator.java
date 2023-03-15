package com.arender.actions;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import io.qameta.allure.Allure;

public class GraphGenerator
{

    public static void generateGraph(ArrayList<Long> dataList, ArrayList<String> nameOfAxis, String title,
            String xAxisLabel, String yAxisLabel, String fileName)
    {
        // Convert the ArrayList to a CategoryDataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < dataList.size(); i++)
        {
            dataset.addValue(dataList.get(i), title, nameOfAxis.get(i));
        }

        // Create the JFreeChart object
        JFreeChart chart = ChartFactory.createBarChart(title, // chart title
                xAxisLabel, // x axis label
                yAxisLabel, // y axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
        );

        // Customize the chart
        chart.setBackgroundPaint(Color.white);
        chart.getTitle().setFont(new Font("Roboto", Font.BOLD, 18));
        chart.getTitle().setPaint(new Color(51, 51, 51));

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        // plot.setRangeGridlinePaint(Color.lightGray);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("Roboto", Font.PLAIN, 12));
        domainAxis.setLabelFont(new Font("Roboto", Font.BOLD, 14));
        domainAxis.setTickLabelPaint(new Color(51, 51, 51));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("Roboto", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("Roboto", Font.BOLD, 14));
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setTickLabelPaint(new Color(51, 51, 51));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(true);
        renderer.setShadowXOffset(1);
        renderer.setShadowYOffset(1);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setItemMargin(0.05);
        renderer.setMaximumBarWidth(0.2);
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, new Color(79, 129, 189), 0.0f, 0.0f, new Color(79, 129, 189));
        renderer.setSeriesPaint(0, gp0);
        // Generate the graph image
        byte[] graphImage = generateGraphImage(chart);

        // Attach the graph image to the Allure report
        Allure.addAttachment(fileName, "image/png", new ByteArrayInputStream(graphImage), ".png");
    }

    public static void globalGraph(int passed, int failed, int warning, String fileName)
    {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<String>();
        if (passed != 0)
        {
            dataset.setValue("Passed : <800ms", passed);
        }
        if (warning != 0)
        {
            dataset.setValue("Warning : >800ms & <120000ms", warning);
        }
        if (failed != 0)
        {
            dataset.setValue("Failed >120000ms", failed);
        }
        JFreeChart chart = ChartFactory.createPieChart("Global graph", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Failed >120000ms", Color.RED);
        plot.setSectionPaint("Warning : >800ms & <120000ms", Color.YELLOW);
        plot.setSectionPaint("Passed : <800ms", Color.GREEN);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1}"));
        byte[] graphImage = generateGraphImage(chart);

        // Attach the graph image to the Allure report
        Allure.addAttachment(fileName, "image/png", new ByteArrayInputStream(graphImage), ".png");
    }

    private static byte[] generateGraphImage(JFreeChart chart)
    {
        byte[] imageInBytes = null;
        try
        {
            BufferedImage chartImage = chart.createBufferedImage(800, 600);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(chartImage, "png", baos);
            baos.flush();
            imageInBytes = baos.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return imageInBytes;
    }

}
