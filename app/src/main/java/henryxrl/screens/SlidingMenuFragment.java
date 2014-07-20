package henryxrl.screens;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.text.DecimalFormat;

import henryxrl.database.VocabDatabase;

public class SlidingMenuFragment extends Fragment
{
	private View view;
	private GraphicalView mChart;
	private String[] code;

	private VocabDatabase db;

	private String title;
	private long bookNumber;
	private long listNumber;
	private float rating;
	private double totalCount;
	private double star0Count;
	private double star1Count;
	private double star2Count;
	private double star3Count;
	private double star4Count;
	private double star5Count;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
	    db = new VocabDatabase(getActivity().getApplicationContext());

	    view = inflater.inflate(R.layout.sliding_menu, container, false);
	    view.setBackgroundColor(0xCC000000);

	    Bundle bundle = this.getArguments();
	    if (bundle != null) {
		    title = bundle.getString("title");
		    bookNumber = bundle.getLong("bookNumber");
		    listNumber = bundle.getLong("listNumber");
		    rating = bundle.getFloat("rating");
		    totalCount = bundle.getDouble("totalCount");
		    star0Count = bundle.getDouble("0star");
		    star1Count = bundle.getDouble("1star");
		    star2Count = bundle.getDouble("2star");
		    star3Count = bundle.getDouble("3star");
		    star4Count = bundle.getDouble("4star");
		    star5Count = bundle.getDouble("5star");
	    }

	    TextView textView = (TextView) view.findViewById(R.id.sliding_menu_title);
	    textView.setText(title);

	    TextView textView1 = (TextView) view.findViewById(R.id.sliding_menu_title2);
	    textView1.setText(String.format("%.1f", rating) + " 星");

	    createChart();

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

	private void createChart() {
		// Pie Chart Slice Names
		code = new String[] { "尚未学习（0星）", "只如初见（1星）", "似曾相识（2星）", "略知一二（3星）","耳熟能详（4星）", "了然于心（5星）" };

		// Pie Chart Slice Values
		double[] distribution = { star0Count/totalCount, star1Count/totalCount, star2Count/totalCount, star3Count/totalCount, star4Count/totalCount, star5Count/totalCount } ;

		// Color of each Pie Chart Slices
		int[] colors = { Color.GRAY, Color.argb(255, 215, 40, 65), Color.argb(255, 255, 120, 50), Color.argb(255, 250, 220, 80), Color.argb(255, 20, 215, 160), Color.argb(255, 45, 165, 255) };

		// Instantiating CategorySeries to plot Pie Chart
		//CategorySeries distributionSeries = new CategorySeries(" Android version distribution as on October 1, 2012");
		CategorySeries distributionSeries = new CategorySeries("");
		for (int i = 0; i < distribution.length; i++) {
			// Adding a slice with its values and name to the Pie Chart
			distributionSeries.add(code[i], distribution[i]);
		}

		// Instantiating a renderer for the Pie Chart
		DefaultRenderer defaultRenderer  = new DefaultRenderer();
		for (int i = 0; i < distribution.length; i++) {

			// Instantiating a render for the slice
			SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
			seriesRenderer.setColor(colors[i]);
			seriesRenderer.setDisplayChartValues(true);

			// Adding the renderer of a slice to the renderer of the pie chart
			defaultRenderer.addSeriesRenderer(seriesRenderer);
		}

		//defaultRenderer.setChartTitle("Android version distribution as on October 1, 2012 ");
		//defaultRenderer.setChartTitle("");
		//defaultRenderer.setChartTitleTextSize(20);
		defaultRenderer.setZoomButtonsVisible(false);

		// Getting a reference to view group linear layout chart_container
		LinearLayout chartContainer = (LinearLayout) view.findViewById(R.id.chart_container);

		// Getting PieChartView to add to the custom layout
		mChart = ChartFactory.getPieChartView(getActivity().getBaseContext(), distributionSeries, defaultRenderer);

		defaultRenderer.setClickEnabled(true);
		defaultRenderer.setSelectableBuffer(10);
		defaultRenderer.setLabelsTextSize(30);
		//defaultRenderer.setLegendTextSize(40);
		//defaultRenderer.setFitLegend(true);
		//defaultRenderer.setMargins(new int[] { 220, 220, 210, 220 });
		//defaultRenderer.setLegendHeight(45);
		defaultRenderer.setZoomEnabled(false);
		defaultRenderer.setPanEnabled(false);
		defaultRenderer.setShowLegend(false);

		TextView label_grey = (TextView) view.findViewById(R.id.label_grey);
		TextView label_red = (TextView) view.findViewById(R.id.label_red);
		TextView label_orange = (TextView) view.findViewById(R.id.label_orange);
		TextView label_yellow = (TextView) view.findViewById(R.id.label_yellow);
		TextView label_green = (TextView) view.findViewById(R.id.label_green);
		TextView label_blue = (TextView) view.findViewById(R.id.label_blue);

		label_grey.setText("■  " + code[0]);
		label_grey.setTextColor(colors[0]);
		label_red.setText("■  " + code[1]);
		label_red.setTextColor(colors[1]);
		label_orange.setText("■  " + code[2]);
		label_orange.setTextColor(colors[2]);
		label_yellow.setText("■  " + code[3]);
		label_yellow.setTextColor(colors[3]);
		label_green.setText("■  " + code[4]);
		label_green.setTextColor(colors[4]);
		label_blue.setText("■  " + code[5]);
		label_blue.setTextColor(colors[5]);

		mChart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				SeriesSelection seriesSelection = mChart.getCurrentSeriesAndPoint();
				if (seriesSelection != null) {

					// Getting the name of the clicked slice
					int seriesIndex = seriesSelection.getPointIndex();
					String selectedSeries="";
					selectedSeries = code[seriesIndex];

					// Getting the value of the clicked slice
					double value = seriesSelection.getXValue()*100;
					DecimalFormat dFormat = new DecimalFormat("#.##");

					// Displaying the message
					Toast.makeText(getActivity().getBaseContext(), selectedSeries + " : " + Double.valueOf(dFormat.format(value)) + " % ", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Adding the pie chart to the custom layout
		chartContainer.addView(mChart);
	}

}
