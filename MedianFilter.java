import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import java.util.Random;
import java.util.Arrays;

public class MedianFilter implements ActionListener {
	private JFrame frame;
	private JTextField meanText, varText, sizeText;
	String meanVal = "0", varVal = "1", sizeVal = "3";
	//private Container pane;
	private BufferedImage oImg = null, nImg = null, fImg = null;

	public MedianFilter() {
		frame = new JFrame("Median Filter Demo");
		frame.setSize(1200, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		prepareGUI();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		MedianFilter medianFilter = new MedianFilter();
		/*javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                prepareGUI(frame.getContentPane());
            }
        });*/
	}

	private void prepareGUI() {
		Container pane = frame.getContentPane();
		JButton button;
		JLabel label;
		Image newimg;

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		button = new JButton("Load Image");
		button.setActionCommand("load");
		button.addActionListener(this);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0; c.gridy = 0;
		c.gridwidth = 2; c.gridheight = 1;
		c.weighty = 0.02; c.weightx = 0.5;
		pane.add(button, c);

		button = new JButton("Add Noise");
		button.setActionCommand("noise");
		button.addActionListener(this);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 2; c.gridy = 0;
		c.gridwidth = 2; c.gridheight = 1;
		c.weighty = 0.02; c.weightx = 0.5;
		pane.add(button, c);

		button = new JButton("Apply Filter");
		button.setActionCommand("filter");
		button.addActionListener(this);
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 4; c.gridy = 0;
		c.gridwidth = 2; c.gridheight = 1;
		c.weighty = 0.02; c.weightx = 0.5;
		pane.add(button, c);

		label = new JLabel("Mean of Noise");
		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = 0; c.gridy = 1;
		c.gridwidth = 1; c.gridheight = 1;
		c.weighty = 0.02; c.weightx = 0.5;
		c.ipadx = 20;
		pane.add(label, c);

		meanText = new JTextField(meanVal, 7);
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 1; c.gridy = 1;
		c.gridwidth = 1; c.gridheight = 1;
		c.weighty = 0.02; c.weightx = 0.5;
		c.ipadx = 20;
		pane.add(meanText, c);

		label = new JLabel("Var. of Noise");
		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = 2; c.gridy = 1;
		c.gridwidth = 1; c.gridheight = 1;
		c.weighty = 0.02; c.weightx = 0.5;
		c.ipadx = 20;
		pane.add(label, c);

		varText = new JTextField(varVal, 7);
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 3; c.gridy = 1;
		c.gridwidth = 1; c.gridheight = 1;
		c.weighty = 0.02; c.weightx = 0.5;
		c.ipadx = 20;
		pane.add(varText, c);

		label = new JLabel("Size of mask");
		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = 4; c.gridy = 1;
		c.gridwidth = 1; c.gridheight = 1;
		c.weighty = 0.02; c.weightx = 0.5;
		c.ipadx = 20;
		pane.add(label, c);

		sizeText = new JTextField(sizeVal, 7);
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 5; c.gridy = 1;
		c.gridwidth = 1; c.gridheight = 1;
		c.weighty = 0.02; c.weightx = 0.5;
		c.ipadx = 20;
		pane.add(sizeText, c);

		if(oImg != null) {
			newimg = oImg.getScaledInstance(300, 400,  java.awt.Image.SCALE_DEFAULT);
			label = new JLabel(new ImageIcon(newimg));
		}
		else label = new JLabel("");
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LAST_LINE_START;
		c.gridx = 0; c.gridy = 2;
		c.gridwidth = 2; c.gridheight = 1;
		c.weighty = 0.95; c.weightx = 0.5;
		pane.add(label, c);

		if(nImg != null) {
			newimg = nImg.getScaledInstance(300, 400,  java.awt.Image.SCALE_DEFAULT);
			label = new JLabel(new ImageIcon(newimg));
		}
		else label = new JLabel("");
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LAST_LINE_START;
		c.gridx = 2; c.gridy = 2;
		c.gridwidth = 2; c.gridheight = 1;
		c.weighty = 0.95; c.weightx = 0.5;
		pane.add(label, c);

		if(fImg != null) {
			newimg = fImg.getScaledInstance(300, 400,  java.awt.Image.SCALE_DEFAULT);
			label = new JLabel(new ImageIcon(newimg));
		}
		else label = new JLabel("");
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LAST_LINE_START;
		c.gridx = 4; c.gridy = 2;
		c.gridwidth = 2; c.gridheight = 1;
		c.weighty = 0.95; c.weightx = 0.5;
		pane.add(label, c);

		frame.setVisible(true);
	}


	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if(cmd.equals("load")) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int result = fileChooser.showOpenDialog(frame);

			if (result == JFileChooser.APPROVE_OPTION) {
    			File selectedFile = fileChooser.getSelectedFile();
  
    			try {
    				oImg = ImageIO.read(selectedFile);
    			} catch (IOException ex) {}

    			int height = oImg.getHeight();
    			int width = oImg.getWidth();
    			int red, blue, green;
    			Color col;

    			for (int i = 0; i < width; i++) {
      				for (int j = 0; j < height; j++) {      					
      					col = new Color(oImg.getRGB(i, j));
						red = (int)(col.getRed() * 0.21); 
						green = (int)(col.getGreen() * 0.72);
						blue = (int) (col.getBlue() *0.07);
						int sum = red + blue + green;
						Color greyColor = new Color(sum, sum, sum);
        				oImg.setRGB(i, j, greyColor.getRGB());
      				}
    			}

    			frame.getContentPane().removeAll();
    			frame.getContentPane().repaint();
    			prepareGUI();
    		}
		}
		else if(cmd.equals("noise")) {
			int height, width, seed;
			float[][] imbuf;
			float mean, var, noise;
			Color col, colFinal;
    		height = oImg.getHeight();
    		width = oImg.getWidth();
    		imbuf = new float[width][height];

    		nImg = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    		mean = Float.parseFloat(meanText.getText());
    		var = Float.parseFloat(varText.getText());
    		Random rnd = new Random();
    		float nval;
    		float min = 255.0f, max = 0.0f;
		    for (int i = 0; i < width; i++) {
      			for (int j = 0; j < height; j++) {
      				noise = (float) (rnd.nextGaussian() * Math.sqrt(var) + mean);
      				//System.out.println(noise);
      				nval = (new Color(oImg.getRGB(i, j))).getGreen() + noise;
      				imbuf[i][j] = nval;
      				if(nval < min) min = nval;
      				if(nval > max) max = nval;
      			}
    		}
    		float range = max - min;
    		if (range < 255.0) range = 255.0f;
    		int normVal;
    		for (int i = 0; i < width; i++) {
      			for (int j = 0; j < height; j++) {
      				normVal = (int)((imbuf[i][j]-min) * 255.0 / range);
      				Color greyColor = new Color(normVal, normVal, normVal);
        			nImg.setRGB(i, j, greyColor.getRGB());
      			}
      		}
      		meanVal = meanText.getText();
      		varVal = varText.getText();
			frame.getContentPane().removeAll();
    		frame.getContentPane().repaint();
    		prepareGUI();
		}
		else {
			int height, width, m, n;
			int ksize = Integer.parseInt(sizeText.getText());
			Color col, colFinal;

        	height = oImg.getHeight();
    		width = oImg.getWidth();
    		fImg = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        	int[] rMedian = new int [ksize*ksize];
        	int[] gMedian = new int [ksize*ksize];
        	int[] bMedian = new int [ksize*ksize];

        	int kerneliter = 0;

        	// Walk the entire image but stop before you go out of bounds at the kernel boundraries.
        	for (int i = 0; i < width - 1; i++){
            	for (int j = 0; j < height - 1; j++){
                	// Walk the kernel itself.
                	for (int ki = 0; ki < ksize; ki++){
                    	for(int kj = 0; kj < ksize; kj++){
                    		m = i + ki - ksize/2; n = j + kj - ksize/2;
                    		if (m < 0) m = 0;
                    		if (n < 0) n = 0;
                    		if (m > width-1) m = width - 1;
                    		if (n > height - 1) n = height - 1;
                        	col = new Color(oImg.getRGB(m, n));
                        	rMedian[kerneliter] = col.getRed();
                        	gMedian[kerneliter] = col.getGreen();
                        	bMedian[kerneliter] = col.getBlue();
                        	kerneliter++;
                    	}
                	}
                	kerneliter = 0;
                	Arrays.sort(rMedian);
                	Arrays.sort(gMedian);
                	Arrays.sort(bMedian);
                	colFinal = new Color(rMedian[ksize/2], gMedian[ksize/2], bMedian[ksize/2]);
                	fImg.setRGB(i, j, colFinal.getRGB());
                }
            }
            sizeVal = sizeText.getText();
			frame.getContentPane().removeAll();
    		frame.getContentPane().repaint();
    		prepareGUI();
		}
	}
}
