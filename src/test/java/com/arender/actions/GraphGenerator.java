package com.arender.actions;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import io.qameta.allure.Allure;

public class GraphGenerator
{

    public static void generateGraph(ArrayList<Long> dataList1, ArrayList<Long> dataList2, ArrayList<Long> dataList3,
            ArrayList<Long> dataList4, ArrayList<Long> dataList5, ArrayList<String> nameOfAxis, String title,
            String xAxisLabel, String yAxisLabel, String fileName, int numberUsers)
    {
        // Convert the ArrayList to a CategoryDataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < dataList1.size(); i++)
        {
            dataset.addValue(dataList1.get(i), "Doc 100KO", nameOfAxis.get(i));
            dataset.addValue(dataList2.get(i), "Pdf 100KO", nameOfAxis.get(i));
            dataset.addValue(dataList3.get(i), "Tiff low size", nameOfAxis.get(i));
            dataset.addValue(dataList4.get(i), "JPEG 100KO", nameOfAxis.get(i));
            dataset.addValue(dataList5.get(i), "Pdf 1MO", nameOfAxis.get(i));
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
        // GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, new Color(79, 129,
        // 189), 0.0f, 0.0f, new Color(79, 129, 189));
        // renderer.setSeriesPaint(0, gp0);
        TextTitle legendText = new TextTitle("\n number of users : " + numberUsers);
        legendText.setPosition(RectangleEdge.BOTTOM);
        chart.addSubtitle(legendText);
        byte[] graphImage = generateGraphImage(chart);

        // Attach the graph image to the Allure report
        Allure.addAttachment(fileName, "image/png", new ByteArrayInputStream(graphImage), ".png");
    }

    public static void globalGraph(int passed, int failed, int warning, String fileName, int numberUsers)
    {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<String>();
        if (passed != 0)
        {
            dataset.setValue("Passed : <800ms", passed);
        }
        if (warning != 0)
        {
            dataset.setValue("Warning : >800ms & <60000ms", warning);
        }
        if (failed != 0)
        {
            dataset.setValue("Failed >60000ms", failed);
        }
        JFreeChart chart = ChartFactory.createPieChart("Global graph", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Failed >60000ms", Color.RED);
        plot.setSectionPaint("Warning : >800ms & <60000ms", Color.YELLOW);
        plot.setSectionPaint("Passed : <800ms", Color.GREEN);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1}"));
        TextTitle legendText = new TextTitle("\n number of users : " + numberUsers);
        legendText.setPosition(RectangleEdge.BOTTOM);
        chart.addSubtitle(legendText);
        byte[] graphImage = generateGraphImage(chart);

        // Attach the graph image to the Allure report
        Allure.addAttachment(fileName, "image/png", new ByteArrayInputStream(graphImage), ".png");
    }

    public static void generateTable(List<Result> listOfResult)
    {

        StringBuilder tableHtml = new StringBuilder();
        tableHtml.append(
                "<style> table, th, td {border: 1px solid black;border-collapse: collapse;padding: 15px;}</style>");
        tableHtml.append("<table border=1>");
        tableHtml.append("<caption>Global result</caption>");
        tableHtml.append("<tr>");

        tableHtml.append("<th>").append("Request").append("</th>");
        tableHtml.append("<th>").append("Total").append("</th>");
        tableHtml.append("<th>").append("OK").append("</th>");
        tableHtml.append("<th>").append("KO").append("</th>");
        tableHtml.append("<th>").append("Min").append("</th>");
        tableHtml.append("<th>").append("Pct 50").append("</th>");
        tableHtml.append("<th>").append("Pct 75").append("</th>");
        tableHtml.append("<th>").append("Pct 95").append("</th>");
        tableHtml.append("<th>").append("Pct 99").append("</th>");
        tableHtml.append("<th>").append("Max").append("</th>");
        tableHtml.append("</tr>");

        for (int i = 0; i < listOfResult.size(); i++)
        {
            tableHtml.append("<tr>");
            tableHtml.append("<td>").append("Upload : " + listOfResult.get(i).getName()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getUploadList().size()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getTotalUploadOK()).append("</td>");
            tableHtml.append("<td>")
                    .append(listOfResult.get(i).getUploadList().size() - listOfResult.get(i).getTotalUploadOK())
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculMin(listOfResult.get(i).getUploadList())).append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getUploadList(), 50.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getUploadList(), 75.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getUploadList(), 95.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getUploadList(), 99.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculMax(listOfResult.get(i).getUploadList())).append("</td>");
            tableHtml.append("</tr>");
        }
        for (int i = 0; i < listOfResult.size(); i++)
        {
            tableHtml.append("<tr>");
            tableHtml.append("<td>").append("Get Layout : " + listOfResult.get(i).getName()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getGetLayoutList().size()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getTotalgetLayoutOK()).append("</td>");
            tableHtml.append("<td>")
                    .append(listOfResult.get(i).getGetLayoutList().size() - listOfResult.get(i).getTotalgetLayoutOK())
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculMin(listOfResult.get(i).getGetLayoutList())).append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetLayoutList(), 50.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetLayoutList(), 75.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetLayoutList(), 95.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetLayoutList(), 99.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculMax(listOfResult.get(i).getGetLayoutList())).append("</td>");
            tableHtml.append("</tr>");
        }
        for (int i = 0; i < listOfResult.size(); i++)
        {
            tableHtml.append("<tr>");
            tableHtml.append("<td>").append("Get Bookmarks : " + listOfResult.get(i).getName()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getGetBookmarksList().size()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getTotalGetBoomarksOK()).append("</td>");
            tableHtml.append("<td>").append(
                    listOfResult.get(i).getGetBookmarksList().size() - listOfResult.get(i).getTotalGetBoomarksOK())
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculMin(listOfResult.get(i).getGetBookmarksList())).append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetBookmarksList(), 50.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetBookmarksList(), 75.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetBookmarksList(), 95.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetBookmarksList(), 99.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculMax(listOfResult.get(i).getGetBookmarksList())).append("</td>");
            tableHtml.append("</tr>");
        }
        for (int i = 0; i < listOfResult.size(); i++)
        {
            tableHtml.append("<tr>");
            tableHtml.append("<td>").append("Get TextPosition : " + listOfResult.get(i).getName()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getGetTextPositionList().size()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getTotalGetTextPositionOK()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getGetTextPositionList().size()
                    - listOfResult.get(i).getTotalGetTextPositionOK()).append("</td>");
            tableHtml.append("<td>").append(Stat.calculMin(listOfResult.get(i).getGetTextPositionList()))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetTextPositionList(), 50.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetTextPositionList(), 75.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetTextPositionList(), 95.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetTextPositionList(), 99.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculMax(listOfResult.get(i).getGetTextPositionList()))
                    .append("</td>");
            tableHtml.append("</tr>");
        }
        for (int i = 0; i < listOfResult.size(); i++)
        {
            tableHtml.append("<tr>");
            tableHtml.append("<td>").append("Get Image 100px : " + listOfResult.get(i).getName()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getGetImage100pxList().size()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getTotalGetImage100pxOK()).append("</td>");
            tableHtml.append("<td>").append(
                    listOfResult.get(i).getGetImage100pxList().size() - listOfResult.get(i).getTotalGetImage100pxOK())
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculMin(listOfResult.get(i).getGetImage100pxList())).append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetImage100pxList(), 50.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetImage100pxList(), 75.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetImage100pxList(), 95.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetImage100pxList(), 99.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculMax(listOfResult.get(i).getGetImage100pxList())).append("</td>");
            tableHtml.append("</tr>");
        }
        for (int i = 0; i < listOfResult.size(); i++)
        {
            tableHtml.append("<tr>");
            tableHtml.append("<td>").append("Get Image 800px : " + listOfResult.get(i).getName()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getGetImage800pxList().size()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getTotalGetImage800pxOK()).append("</td>");
            tableHtml.append("<td>").append(
                    listOfResult.get(i).getGetImage800pxList().size() - listOfResult.get(i).getTotalGetImage800pxOK())
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculMin(listOfResult.get(i).getGetImage800pxList())).append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetImage800pxList(), 50.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetImage800pxList(), 75.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetImage800pxList(), 95.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getGetImage800pxList(), 99.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculMax(listOfResult.get(i).getGetImage800pxList())).append("</td>");
            tableHtml.append("</tr>");
        }
        for (int i = 0; i < listOfResult.size(); i++)
        {
            tableHtml.append("<tr>");
            tableHtml.append("<td>").append("Evict document : " + listOfResult.get(i).getName()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getEvictList().size()).append("</td>");
            tableHtml.append("<td>").append(listOfResult.get(i).getTotalEvictOK()).append("</td>");
            tableHtml.append("<td>")
                    .append(listOfResult.get(i).getEvictList().size() - listOfResult.get(i).getTotalEvictOK())
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculMin(listOfResult.get(i).getEvictList())).append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getEvictList(), 50.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getEvictList(), 75.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getEvictList(), 95.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculPercentile(listOfResult.get(i).getEvictList(), 99.0))
                    .append("</td>");
            tableHtml.append("<td>").append(Stat.calculMax(listOfResult.get(i).getEvictList())).append("</td>");
            tableHtml.append("</tr>");
        }
        tableHtml.append("</table>");
        Allure.addAttachment("JTable", "text/html", tableHtml.toString(), "html");

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
