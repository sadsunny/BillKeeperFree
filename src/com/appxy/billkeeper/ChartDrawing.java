package com.appxy.billkeeper;

import java.text.Format;
import java.util.Arrays;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.appxy.billkeeper.R.color;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;

@SuppressLint({ "NewApi", "ResourceAsColor" })
/**
 * ��android�л�����ͼ����״ͼ����ͼ��ͳ��ͼ��������achartengine������ߣ���ͨ������achartengine.jar
 * ��Ҫͨ�����ü�������
 * 1��XYSeries�������ڴ洢һ���ߵ������Ϣ��
 * 2��XYMultipleSeriesDataset���󣺼���ݼ�������Ӷ��XYSeries������Ϊһ������ͼ�п����ж����ߡ�
 * 3��XYSeriesRenderer������Ҫ����������һ�������ķ����ɫ������ϸ֮��ġ�
 * 4��XYMultipleSeriesRenderer������Ҫ��������һ��ͼ������������xTitle,yTitle,chartName�ȵ������Եķ��
 *    ����Ӷ��XYSeriesRenderer������Ϊһ��ͼ�п����ж������ߡ�
 * ��������Щ����֮�󣬿�ͨ�� org.achartengine.ChartFactory������ݼ�XYMultipleSeriesDataset����
 * ��XYMultipleSeriesRenderer��������ͼ����ͼ���ص�GraphicalView�У�
 * ChartFactory�ж���api��ͨ����Щapi�����������ǻ�����ͼ������״ͼ��
 * */
public class ChartDrawing {

	private String xTitle, yTitle, chartTitle;
	private String xLabel[];
	private XYMultipleSeriesDataset dataset;
	private XYMultipleSeriesRenderer multiRenderer;

	public XYMultipleSeriesRenderer getMultiRenderer() {
		return multiRenderer;
	}

	public XYMultipleSeriesDataset getDataset() {
		return dataset;
	}
	

	public ChartDrawing(String xTitle, String yTitle, String chartTitle,
			String xLabel[]) {

		this.xTitle = xTitle;
		this.yTitle = yTitle;
		this.xLabel = Arrays.copyOf(xLabel, xLabel.length);
		this.chartTitle = chartTitle;
		// Creating a XYMultipleSeriesRenderer to customize the whole chart
		this.multiRenderer = new XYMultipleSeriesRenderer();
		// Creating a dataset to hold each series
		this.dataset = new XYMultipleSeriesDataset();
	}
	
	/**
	 * ��XYSeries�����ơ�������ӵ���ݼ� XYMultipleSeriesDataset������ȥ
	 * */
	public void set_XYSeries(double value[], String lineName) {
		// ����һ��XYSeries�����ΪlineName�ϵ����
		XYSeries oneSeries = new XYSeries(lineName);
		// Adding data to Series
		for (int i = 0; i < value.length; i++) {
			oneSeries.add(i+1, value[i]);

		}
		// Adding Series to the dataset
		this.dataset.addSeries(oneSeries);
	}
   
	
	public XYSeriesRenderer set_XYSeriesRender_Style(Context context) {
		XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
		//������������ɫ
		seriesRenderer.setColor(Color.rgb(51, 181, 229));
		seriesRenderer.setFillPoints(true);
		//���������Ŀ��
		seriesRenderer.setLineWidth(Dp2Px(context, 1));
		seriesRenderer.setDisplayChartValues(false);
//		seriesRenderer.setChartValuesTextSize(20);
		seriesRenderer.setPointStyle(PointStyle.CIRCLE);
		seriesRenderer.setFillPoints(true); 
		return seriesRenderer;

	}
	
	
	public int Dp2Px(Context context, double dp) {  //��dpת��Ϊpix
		
	    final float scale = context.getResources().getDisplayMetrics().density; 
	    return (int) (dp * scale + 0.5f); 
	    
	}

	public void set_XYMultipleSeriesRenderer_Style(XYSeriesRenderer renderer ,double Max ,Context context) {
		// ���� X �᲻��ʾ����(���������ֶ���ӵ����ֱ�ǩ)
		
		this.multiRenderer.setMargins(new int[] {Dp2Px(context, 10), Dp2Px(context, 70), 0, 0}); //���ñ߾�
		this.multiRenderer.setXLabels(0);
		//����Y��Ľ����
		
		this.multiRenderer.setYLabels(5);
		this.multiRenderer.setShowLegend(false);
		this.multiRenderer.setShowGrid(true);
		this.multiRenderer.setGridColor(Color.rgb(213, 215, 217));
		this.multiRenderer.setLabelsTextSize(Dp2Px(context, 14)); 
		this.multiRenderer.setYLabelsAlign(Align.RIGHT);
//		this.multiRenderer.setZoomEnabled(true);
		
		this.multiRenderer.setApplyBackgroundColor(true);//���ñ������
		this.multiRenderer.setAxesColor(Color.rgb(213, 215, 217));//������������ɫ
		this.multiRenderer.setBackgroundColor(Color.rgb(248, 251, 253));//���ñ�������ɫ
		this.multiRenderer.setMarginsColor(Color.rgb(248, 251, 253));//���ñ�Ե��������ɫ
		
		this.multiRenderer.setXLabelsColor(Color.rgb(51, 181, 229));//���ú����̶ȵ���ɫ
		this.multiRenderer.setYLabelsColor(0, Color.rgb(51, 181, 229));//���������̶ȵ���ɫ
		this.multiRenderer.setPointSize(Dp2Px(context, 2));//���õ�Ĵ�С
		this.multiRenderer.setYLabelsPadding(11); //Y����ʾ������Y��ľ���
		
		this.multiRenderer.setYAxisMin(0);
		this.multiRenderer.setYAxisMax(Max+Max/5);
		this.multiRenderer.setXAxisMax(6.3);
		this.multiRenderer.setXAxisMin(0);
		this.multiRenderer.setPanEnabled(true, false);//����XY��Ļ���
		this.multiRenderer.setPanLimits(new double[] {0,12.4,0,12}); //������������Ľ���
//		this.multiRenderer.setXLabelsAlign(Align.LEFT);
		this.multiRenderer.setZoomEnabled(false, false); //�����Ƿ������XY
//		this.multiRenderer.setXRoundedLabels(false);
		
//		for (int i = 0; i < 5; i++) {
//			//���X���ǩ
//			this.multiRenderer.addYTextLabel(i+1, Max/(5-i)+"");
//		}
		
		for (int i = 0; i < xLabel.length; i++) {
			//���X���ǩ
			this.multiRenderer.addXTextLabel(i+1, this.xLabel[i]);
		}
//		this.multiRenderer.setFitLegend(false);// ������ʵ�λ��
//		this.multiRenderer.setClickEnabled(false);//�����Ƿ���Ի������Ŵ���С;
//		this.multiRenderer.setInScroll(true);
		
		this.multiRenderer.addSeriesRenderer(renderer);

	}

}
