package smarttrafficlight.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class Fuzzy {
	
	private Pair<Integer, Integer> getInput() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter number of arrival vehicles : ");
		int arrivingVehicles = scan.nextInt();
		System.out.println("Enter number of queueu vehicles : ");
		int queueVehicles = scan.nextInt();
		scan.close();
		return new Pair<Integer, Integer>(arrivingVehicles, queueVehicles);
	}
	
	private double[][] zeros(int row, int col) {
		double[][] zeros = new double[row][col];
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < col; j++) {
				zeros[i][j] = 0;
			}
		}
		return zeros;
	}
	
	private double[] zeros(int row) {
		double[] zeros = new double[row];
		for(int i = 0; i < row; i++) {
			zeros[i] = 0;
		}
		return zeros;
	}
	
	private double fuzFewArrival(int arrivingVehicles) {
		if(arrivingVehicles <= 0) return 1;
		else if(arrivingVehicles >= 0 && arrivingVehicles <= 4) return (-0.25 * arrivingVehicles) + 1;
		else if(arrivingVehicles >= 4) return 0;
		return 0;
	}
	
	private double fuzSmallArrival(int arrivingVehicles) {
		if(arrivingVehicles <= 0 && arrivingVehicles >= 8) return 0;
		else if(arrivingVehicles >= 0 && arrivingVehicles <= 4) return (0.25 * arrivingVehicles);
		else if(arrivingVehicles >= 4 && arrivingVehicles <= 8) return (-0.25 * arrivingVehicles) + 2;
		return 0; 
	}
	
	private double fuzMediumArrival(int arrivingVehicles) {
		if(arrivingVehicles <= 4 && arrivingVehicles >= 12) return 0;
		else if(arrivingVehicles >= 4 && arrivingVehicles <= 8) return (0.25 * arrivingVehicles) - 1;
		else if(arrivingVehicles >= 8 && arrivingVehicles <= 12) return (-0.25 * arrivingVehicles) + 3;
		return 0;
	}
	
	private double fuzManyArrival(int arrivingVehicles) {
		if(arrivingVehicles < 8) return 0;
		else if(arrivingVehicles >= 8 && arrivingVehicles <= 12) return (0.25 * arrivingVehicles) - 2;
		return 0;
	}
	
	private double fuzFewQueue(int queueVehicles) {
		if(queueVehicles <= 0) return 1;
		else if(queueVehicles >= 0 && queueVehicles <= 4) return (-0.25 * queueVehicles) + 1;
		else if(queueVehicles >= 4) return 0;
		return 0;
	}
	
	private double fuzSmallQueue(int queueVehicles) {
		if(queueVehicles <= 0 && queueVehicles >= 8) return 0;
		else if(queueVehicles >= 0 && queueVehicles <= 4) return 0.25 * queueVehicles;
		else if(queueVehicles >= 4 && queueVehicles < 8) return (-0.25 * queueVehicles) + 2;
		return 0;
	}
	
	private double fuzMediumQueue(int queueVehicles) {
		if(queueVehicles <= 4 && queueVehicles >= 12) return 0;
		else if(queueVehicles >= 4 && queueVehicles <= 8) return (0.25 * queueVehicles) - 1;
		else if(queueVehicles >= 8 && queueVehicles < 12) return (-0.25 * queueVehicles) + 3;
		return 0;
	}
	
	private double fuzManyQueue(int queueVehicles) {
		if(queueVehicles < 8) return 0;
		else if(queueVehicles >= 8 && queueVehicles <= 12) return (0.25 * queueVehicles) - 2;
		return 1;
	}
	
	private double[][] evalRules(int arrivingVehicles, int queueVehicles) {
		double[][] rules = zeros(16, 4);
		
		double valueOne = 0.0, valueTwo = 0.0;
		
		// [0] = Zero
		// [1] = Short
		// [2] = Medium
		// [3] = Long
		
		// Rule 1
		valueOne = fuzFewArrival(arrivingVehicles);
		valueTwo = fuzFewQueue(queueVehicles);
		rules[0][0] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 2
		valueOne = fuzFewArrival(arrivingVehicles);
		valueTwo = fuzSmallQueue(queueVehicles);
		rules[1][0] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 3
		valueOne = fuzFewArrival(arrivingVehicles);
		valueTwo = fuzMediumQueue(queueVehicles);
		rules[2][0] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 4
		valueOne = fuzFewArrival(arrivingVehicles);
		valueTwo = fuzManyQueue(queueVehicles);
		rules[3][0] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 5
		valueOne = fuzSmallArrival(arrivingVehicles);
		valueTwo = fuzFewQueue(queueVehicles);
		rules[4][1] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 6
		valueOne = fuzSmallArrival(arrivingVehicles);
		valueTwo = fuzSmallQueue(queueVehicles);
		rules[5][1] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 7
		valueOne = fuzSmallArrival(arrivingVehicles);
		valueTwo = fuzMediumQueue(queueVehicles);
		rules[6][0] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 8
		valueOne = fuzSmallArrival(arrivingVehicles);
		valueTwo = fuzManyQueue(queueVehicles);
		rules[7][0] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 9
		valueOne = fuzMediumArrival(arrivingVehicles);
		valueTwo = fuzFewQueue(queueVehicles);
		rules[8][2] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 10
		valueOne = fuzMediumArrival(arrivingVehicles);
		valueTwo = fuzSmallQueue(queueVehicles);
		rules[9][2] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 11
		valueOne = fuzMediumArrival(arrivingVehicles);
		valueTwo = fuzMediumQueue(queueVehicles);
		rules[10][1] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 12
		valueOne = fuzMediumArrival(arrivingVehicles);
		valueTwo = fuzManyQueue(queueVehicles);
		rules[11][1] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 13
		valueOne = fuzManyArrival(arrivingVehicles);
		valueTwo = fuzFewQueue(queueVehicles);
		rules[12][3] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 14
		valueOne = fuzManyArrival(arrivingVehicles);
		valueTwo = fuzSmallQueue(queueVehicles);
		rules[13][2] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 15
		valueOne = fuzManyArrival(arrivingVehicles);
		valueTwo = fuzMediumQueue(queueVehicles);
		rules[14][2] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		// Rule 16
		valueOne = fuzManyArrival(arrivingVehicles);
		valueTwo = fuzManyQueue(queueVehicles);
		rules[15][1] = valueOne >= valueTwo ? valueTwo : valueOne;
		
		System.out.println("Rules");
		for(int i = 0; i < rules.length; i++) {
			for(int j = 0; j < rules[i].length; j++) {
				System.out.print(rules[i][j] + " | ");
			}
			System.out.println();
		}
		System.out.println();
		
		return rules;
	}
	
	private double[] fuzOutputZero() {
		double[] outputZero = zeros(25);
		for(int i = 0; i <= 5; i++) {
			outputZero[i] = (-0.166666666666667 * i) + 1;
		}
		System.out.println("Output Zero");
		for(int i = 0; i < outputZero.length; i++) {
			System.out.print(i + " : " + outputZero[i] + " | ");
		}
		System.out.println();
		return outputZero;
	}
	
	private double[] fuzOutputShort() {
		double[] outputShort = zeros(25);
		for(int i = 7; i <= 9; i++) {
			outputShort[i] = (0.33333333333333300000 * i) - 2;
		}
		for(int i = 10; i <= 11; i++) {
			outputShort[i] = (-0.33333333333333300000 * i) + 4;
		}
		System.out.println("Output Short : ");
		for(int i = 0; i < outputShort.length; i++) {
			System.out.print(i + " : " + outputShort[i] + " | ");
		}
		System.out.println();
		return outputShort;
	}
	
	private double[] fuzOutputMedium() {
		double[] outputMedium = zeros(25);
		for(int i = 13; i <= 15; i++) {
			outputMedium[i] = (0.33333333333333300000 * i) - 4;
		}
		for(int i = 16; i <= 17; i++) {
			outputMedium[i] = (-0.33333333333333300000 * i) + 6;
		}
		System.out.println("Output Medium : ");
		for(int i = 0; i < outputMedium.length; i++) {
			System.out.print(i + " : " + outputMedium[i] + " | ");
		}
		System.out.println();
		return outputMedium;
	}
	
	private double[] fuzOutputLong() {
		double[] outputLong = zeros(25);
		for(int i = 19; i <= 24; i++) {
			outputLong[i] = (0.16666666666666700000 * i) - 3;
		}
		System.out.println("Output Long : ");
		for(int i = 0; i < outputLong.length; i++) {
			System.out.print(i + " : " + outputLong[i] + " | ");
		}
		System.out.println();
		return outputLong;
	}
	
	private double[] fisAggregate(double[][] rules, double[] outputZero, double[] outputShort, double[] outputMedium, double[] outputLong) {
		double[][] fuzzy = zeros(16, 25);
		
		for(int i = 0; i < rules.length; i++) {
			for(int j = 0; j < fuzzy[0].length; j++) {
				if(rules[i][0] > 0) {
					if(j >= 0 && j <= 6) {
						fuzzy[i][j] = rules[i][0] < outputZero[j] ? rules[i][0] : outputZero[j];
					} else {
						fuzzy[i][j] = 0;
					}
				}
				
				if(rules[i][1] > 0) {
					if(j >= 6 && j <= 12) {
						fuzzy[i][j] = rules[i][1] < outputShort[j] ? rules[i][1] : outputShort[j];
					} else {
						fuzzy[i][j] = 0;
					}
				}
				
				if(rules[i][2] > 0) {
					if(j >= 12 && j <= 18) {
						fuzzy[i][j] = rules[i][2] < outputMedium[j] ? rules[i][2] : outputMedium[j];
					} else {
						fuzzy[i][j] = 0;
					}
				}
				
				if(rules[i][3] > 0) {
					if(j >= 18 && j <= 24) {
						fuzzy[i][j] = rules[i][3] < outputLong[j] ? rules[i][3] : outputLong[j];
					} else {
						fuzzy[i][j] = 0;
					}
				}
			}
		}
		
		double[] aggre = zeros(25);
		
		System.out.println("Fuzzy");
		for(int i = 0; i < fuzzy.length; i++) {
			for(int j = 0; j < fuzzy[i].length; j++) {
				System.out.print(fuzzy[i][j] + " | ");
			}
			System.out.println();
		}
		System.out.println();
		
		for(int i = 0; i < fuzzy[0].length; i++) {
			for(int j = 0; j < fuzzy.length; j++) {
				if(aggre[i] < fuzzy[j][i]) {
					aggre[i] = fuzzy[j][i];
				}
			}
		}
		
		System.out.println("Aggre");
		for(int i = 0; i < aggre.length; i++) {
			System.out.print(aggre[i] + " | ");
		}
		System.out.println();
		
		return aggre;
	}
	
	private double getCentroid(double[] aggre) {
		double centroidDenum = 0.0, centroidNum = 0.0;
		
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		
		for(int i = 0; i < aggre.length; i++) {
			centroidNum += i * aggre[i];
			centroidDenum += aggre[i];
			dataSet.addValue(aggre[i], "Green Light Extension", i + "");
		}
		
//		JFreeChart lineChart = ChartFactory.createLineChart("Green Light Extension", "Extension", "Aggregation", dataSet);
//		CategoryPlot plot = lineChart.getCategoryPlot();
//		LineAndShapeRenderer renderer = new LineAndShapeRenderer();
//		renderer.setSeriesPaint(0, Color.BLACK);
//		plot.setBackgroundPaint(Color.PINK);
//		plot.setRenderer(renderer);
//		
//		JFrame chartFrame = new JFrame("Bading si Cyril Jake");
//		JPanel panelChart = new JPanel();
//		panelChart.add(new ChartPanel(lineChart));
//		
//		chartFrame.add(panelChart);
//		chartFrame.pack();
//		chartFrame.setVisible(true);
//		chartFrame.setSize(700, 470);
//		chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		chartFrame.setLocationRelativeTo(null);
		
		return centroidNum / centroidDenum;
	}
	
	public static void main(String[] args) {
		Fuzzy fuz = new Fuzzy();
		Pair<Integer, Integer> pair = fuz.getInput();
		double[][] rules = fuz.evalRules(pair.getLeft(), pair.getRight());
//		for(int i = 1; i <= 12; i++) {
//			double[][] rules = fuz.evalRules(1, i);
//			System.out.println("[1][" + i + "]Centroid : " + fuz.getCentroid(fuz.fisAggregate(rules, fuz.fuzOutputZero(), fuz.fuzOutputShort(), fuz.fuzOutputMedium(), fuz.fuzOutputLong())));
//		}
//		double[][] rules = fuz.evalRules(6, 6);
		System.out.println("Centroid : " + fuz.getCentroid(fuz.fisAggregate(rules, fuz.fuzOutputZero(), fuz.fuzOutputShort(), fuz.fuzOutputMedium(), fuz.fuzOutputLong())));
	}
	
}

