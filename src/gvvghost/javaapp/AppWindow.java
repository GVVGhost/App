package gvvghost.javaapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeriesCollection;

public class AppWindow extends ApplicationFrame implements ActionListener
{
    AppWindow()
    {
        super("Server");
        this.seriesOne   = new TimeSeries("Wind speed");
        this.seriesTwo   = new TimeSeries("Current"   );
        this.seriesThree = new TimeSeries("Voltage"   );
        this.seriesFour  = new TimeSeries("Power"     );

        dataSetOne   = new TimeSeriesCollection(this.seriesOne);
        dataSetTwo   = new TimeSeriesCollection(this.seriesTwo);
        dataSetThree = new TimeSeriesCollection(this.seriesThree);
        dataSetFour  = new TimeSeriesCollection(this.seriesFour);

        chartOne   = createChart(dataSetOne   , "Speed"   ,"m/s");
        chartTwo   = createChart(dataSetTwo   , "Current" ,"A"  );
        chartThree = createChart(dataSetThree , "Voltage" ,"V"  );
        chartFour  = createChart(dataSetFour  , "Power"   ,"Wt" );

        chartPanelOne   = new ChartPanel( chartOne );
        chartPanelTwo   = new ChartPanel( chartTwo );
        chartPanelThree = new ChartPanel(chartThree);
        chartPanelFour  = new ChartPanel(chartFour );

        chartPanelOne.setPreferredSize  (new Dimension(400,300));
        chartPanelTwo.setPreferredSize  (new Dimension(400,300));
        chartPanelThree.setPreferredSize(new Dimension(400,300));
        chartPanelFour.setPreferredSize (new Dimension(400,300));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jPanel    = new JPanel();
        jTextArea = new JTextArea(8, 60);
        jTextArea.setEditable(     false);
        jTextArea.setLineWrap(      true);
        jTextArea.setWrapStyleWord(false);
        jPanel.setBackground(Color.WHITE);

        jScrollPaneTA = new JScrollPane(jTextArea);
        jScrollPaneTA.setVerticalScrollBarPolicy(ScrollPaneConstants
                .VERTICAL_SCROLLBAR_AS_NEEDED);

        buttonClearAll = new JButton("Clear text area"  );
        buttonShowLast = new JButton("last message"     );
        buttonAddData  = new JButton("Add new data item");

        buttonClearAll.addActionListener(this);
        buttonShowLast.addActionListener(this);
        buttonAddData.addActionListener (this);

        Box panelBox  = Box.createVerticalBox();
        Box buttonBox = Box.createHorizontalBox();
        Box one       = Box.createHorizontalBox();
        Box two       = Box.createHorizontalBox();

        buttonBox.add(buttonShowLast);
        buttonBox.add(buttonClearAll);
        buttonBox.add(buttonAddData);

        one.add(chartPanelOne);
        one.add(chartPanelTwo);
        two.add(chartPanelThree);
        two.add(chartPanelFour);

        panelBox.add(jScrollPaneTA);
        panelBox.add(buttonBox);
        panelBox.add(one);
        panelBox.add(two);

        jPanel.add(panelBox);

        jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(jScrollPane);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == buttonClearAll)
        {
            setToTextArea("");
        }
        else if(e.getSource() == buttonShowLast)
        {
            setToTextArea(log);
        }
        else if(e.getSource() == buttonAddData)
        {
            double factor = 0.9 + 0.25 * Math.random();
            this.lastValue = this.lastValue * factor;
            Millisecond now = new Millisecond();
            System.out.println("Now = " + now.toString());
            this.seriesOne.add  (new Millisecond(), this.lastValue);
            this.seriesTwo.add  (new Millisecond(), this.lastValue);
            this.seriesThree.add(new Millisecond(), this.lastValue);
            this.seriesFour.add (new Millisecond(), this.lastValue);
        }
    }

    private JFreeChart createChart(XYDataset dataset, String title, String value)
    {
        JFreeChart result = ChartFactory.createTimeSeriesChart
                (
                        title,
                        "Time",
                        value,
                        dataset,
                        true,
                        true,
                        false
                );

        final XYPlot plot = result.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(30000.0);
        axis = plot.getRangeAxis();
        axis.setRange(0.0, 200.0);
        return result;
    }

    public void newIncomingData(String data)
    {
        int count = 0;
        Scanner scanner = new Scanner(data);
        while (scanner.hasNext())
        {
            if(scanner.hasNextDouble())
            {
                if(count == 0) this.seriesOne.add  (new Millisecond(), scanner.nextDouble());
                if(count == 1) this.seriesTwo.add  (new Millisecond(), scanner.nextDouble());
                if(count == 2) this.seriesThree.add(new Millisecond(), scanner.nextDouble());
                if(count == 3) this.seriesFour.add (new Millisecond(), scanner.nextDouble());
                if(count  > 3) setToTextArea("Redundant data [" + count + "]: " + scanner.nextDouble());
            }
            count++;
        }
        if(count < 3) setToTextArea("Not enough data \nOnly " + count + " graphic(-s) were drawn");
        scanner.close();
    }

    public synchronized void setToTextArea(String logString)
    {
        jTextArea.setText(logString);
    }

    public String getFromTextArea()
    {
        return jTextArea.getText();
    }

    public synchronized void addStringToTextArea(String logString)
    {
        jTextArea.setText(log + "\n"+ logString + "\n");
        log = logString;
    }

    private JScrollPane jScrollPane;
    private JScrollPane jScrollPaneTA;
    private JTextArea   jTextArea;
    private JPanel      jPanel;
    private JButton     buttonClearAll;
    private JButton     buttonShowLast;
    private TimeSeries  seriesOne;
    private TimeSeries  seriesTwo;
    private TimeSeries  seriesThree;
    private TimeSeries  seriesFour;

    private String log       =    "";
    private double lastValue = 100.0;

    private TimeSeriesCollection dataSetOne;
    private TimeSeriesCollection dataSetTwo;
    private TimeSeriesCollection dataSetThree;
    private TimeSeriesCollection dataSetFour;
    private JFreeChart chartOne;
    private JFreeChart chartTwo;
    private JFreeChart chartThree;
    private JFreeChart chartFour;
    private ChartPanel chartPanelOne;
    private ChartPanel chartPanelTwo;
    private ChartPanel chartPanelThree;
    private ChartPanel chartPanelFour;
    private JButton    buttonAddData;
}