package edu.dartmouth.cs.stressmeter.psm;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import edu.dartmouth.cs.stressmeter.R;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class ResultsFragment extends Fragment {

	private TableLayout summaryTable;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.result_fragment, container, false);
		summaryTable = (TableLayout) view.findViewById(R.id.summary);
		File psmFile = new File(Environment.getExternalStorageDirectory(), "stress_meter_resp.csv");;

		List<Score> scores = readFile(psmFile);
		List<PointValue> values = new ArrayList<PointValue>();


		for(int i=0;i<scores.size();i++){
			values.add(new PointValue(i, scores.get(i).getScore()));
			TableRow row = createTableRow(scores.get(i).getTimestamp(), scores.get(i).getScore());
			summaryTable.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
					TableLayout.LayoutParams.WRAP_CONTENT));
		}
		if(scores.size()>0) {
			Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
			line.setValues(values);
			line.setFilled(true);
			List<Line> lines = new ArrayList<Line>();
			lines.add(line);

			LineChartData data = new LineChartData();
			data.setLines(lines);
			boolean hasAxes = true;
			boolean hasAxesNames = true;
			if (hasAxes) {
				Axis axisX = new Axis();
				Axis axisY = new Axis().setHasLines(true);
				if (hasAxesNames) {
					axisX.setName("Instances");
					axisY.setName("Stress Level");
				}
				data.setAxisXBottom(axisX);
				data.setAxisYLeft(axisY);
			} else {
				data.setAxisXBottom(null);
				data.setAxisYLeft(null);
			}

			LineChartView chart =  (LineChartView)view.findViewById(R.id.chart);
			chart.setLineChartData(data);
		}




		return view;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);




	}

	private TableRow createTableRow(String time, int stress) {

		TableRow row = new TableRow(getActivity());
		row.setLayoutParams(
				new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		TextView stressText = new TextView(getActivity());
		stressText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		stressText.setBackgroundResource(R.drawable.table_row);
		stressText.setPadding(20, 0, 0, 0);

		stressText.setText(String.valueOf(stress));
		TextView timeText = new TextView(getActivity());
		timeText.setLayoutParams(
				new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		timeText.setText(time);

		timeText.setBackgroundResource(R.drawable.table_row);
		timeText.setPadding(10, 0, 0, 0);
		row.addView(timeText);
		row.addView(stressText);
		row.setBackgroundResource(R.drawable.table_row);

		return row;

	}

	public List<Score> readFile(File filename) {

		List<Score> scores = new ArrayList<Score>();
		try {
			FileReader file = new FileReader(filename);
			BufferedReader buff = new BufferedReader(file);
			String line;

			line = buff.readLine();

			while (line != null) {

				StringTokenizer tokens = new StringTokenizer(line, ",");
				String timestamp = tokens.nextToken();
				int score = Integer.parseInt(tokens.nextToken());

				scores.add(new Score(timestamp,score));

				line = buff.readLine();
			}

			buff.close();

		} catch (IOException e) {

			System.out.println("Error " + e.toString());
		}

		return scores;
	}

	private Date convertToDate(String dateString) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(dateString);
		} catch (ParseException ex) {

		}

		return date;
	}
}
