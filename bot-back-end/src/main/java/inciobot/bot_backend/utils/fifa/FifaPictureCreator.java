package inciobot.bot_backend.utils.fifa;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

import inciobot.bot_backend.constants.IMessage;
import inciobot.bot_backend.model.fifa.FifaMatch;

@Component
public class FifaPictureCreator {
	private static final int CHAR_PER_ROW = 43;
	private static final int COMMENT_WIDTH = 165;
	private static final int INITIAL_COMMENT_HEIGHT = 1130;
	private static final int ROW_HEIGHT = 70;
	private static final int MAX_CHAR_RESULT_BIG = 27;
	private static final int FONT_SIZE_RESULT_BIG = 110;
	private static final int FONT_SIZE_RESULT_MEDIUM = 90;
	private static final int FONT_SIZE_RESULT_LITTLE = 75;
	private static final int MAX_CHAR_RESULT_MEDIUM = 35;
	private static int COMMENT_FONT_HEIGHT = 60;

	private static final int CHAR_PER_ROW_DARK = 70;
	private static final int COMMENT_WIDTH_DARK = 80;
	private static final int INITIAL_COMMENT_HEIGHT_DARK = 340;
	private static final int ROW_HEIGHT_DARK = 20;
	private static final int MAX_CHAR_RESULT_BIG_DARK = 26;
	private static final int FONT_SIZE_RESULT_BIG_DARK = 36;
	private static final int FONT_SIZE_RESULT_MEDIUM_DARK = 26;
	private static final int FONT_SIZE_RESULT_LITTLE_DARK = 22;
	private static final int MAX_CHAR_RESULT_MEDIUM_DARK = 36;
	private static int COMMENT_FONT_HEIGHT_DARK = 15;

	private static void writeResult(Graphics g, String result) {
		int fontSize = FONT_SIZE_RESULT_BIG;

		if (result.length() < MAX_CHAR_RESULT_BIG)
			fontSize = FONT_SIZE_RESULT_BIG;
		if (result.length() > MAX_CHAR_RESULT_BIG && result.length() < MAX_CHAR_RESULT_MEDIUM)
			fontSize = FONT_SIZE_RESULT_MEDIUM;
		if (result.length() > MAX_CHAR_RESULT_MEDIUM)
			fontSize = FONT_SIZE_RESULT_LITTLE;

		g.setFont(new Font("Calibrì", Font.PLAIN, fontSize));
		g.drawString(result, 180, 800);
	}

	private static void writeComment(Graphics g, String comment) {
		System.out.println(comment.length());
		g.setFont(new Font("Calibrì", Font.PLAIN, COMMENT_FONT_HEIGHT));
		String[] words = comment.split(" ");
		String row = "";
		String currentRow = "";
		int drawnRow = 0;

		for (int i = 0; i < words.length; i++) {
			currentRow = row;
			row += words[i] + " ";
			if (row.length() > CHAR_PER_ROW) {
				g.drawString(currentRow, COMMENT_WIDTH, INITIAL_COMMENT_HEIGHT + ROW_HEIGHT * drawnRow);
				drawnRow++;
				row = words[i] + " ";
			}
			g.drawString(row, COMMENT_WIDTH, INITIAL_COMMENT_HEIGHT + ROW_HEIGHT * drawnRow);
		}
	}

	public File createPictureFromMatch(FifaMatch match) throws Exception {
		BufferedImage image;
		File file = new File("tmp.png");
		try {
			image = ImageIO.read(getClass().getResource("/EueiPicTamplate.png"));

			Graphics g = image.getGraphics();
			g.setColor(new Color(0, 0, 0)); // Here
			writeResult(g, match.getRappresentationResultCenter(false, false, false));
			if (match.getComment().equals(" ")) {
				writeComment(g, IMessage.NO_COMMENT);
			} else {
				writeComment(g,
						"Commento di " + match.getCreator().getUser().getUsername() + ": " + match.getComment());
				;
			}
			g.setFont(new Font("Calibrì", Font.PLAIN, 45));
			g.drawString(new SimpleDateFormat("dd-MM-YY").format(match.getDateCreation()), 1380, 1570);

			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			throw e;
		}

		return file;
	}

	public File createPictureFromMatchDark(FifaMatch match) throws Exception {
		BufferedImage image;
		File file = new File("tmp.png");
		try {
			image = ImageIO.read(getClass().getResource("/EueiPicTamplateDark.png"));

			Graphics g = image.getGraphics();
			g.setColor(new Color(255, 255, 255)); // Here
			writeResultDark(g, match.getRappresentationResultCenter(false, false, false));
			if (match.getComment().equals(" ")) {
				// writeCommentDark(g, IMessage.NO_COMMENT);
			} else {
				writeCommentDark(g,
						"Commento di " + match.getCreator().getUser().getUsername() + ": " + match.getComment());
				;
			}
			g.setFont(new Font("Calibrì", Font.PLAIN, 15));
			g.drawString(new SimpleDateFormat("dd-MM-YY").format(match.getDateCreation()), 555, 465);

			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			throw e;
		}

		return file;
	}

	private static void writeResultDark(Graphics g, String result) {
		int fontSize = FONT_SIZE_RESULT_BIG_DARK;

		if (result.length() <= MAX_CHAR_RESULT_BIG_DARK)
			fontSize = FONT_SIZE_RESULT_BIG_DARK;
		if (result.length() > MAX_CHAR_RESULT_BIG_DARK && result.length() <= MAX_CHAR_RESULT_MEDIUM_DARK)
			fontSize = FONT_SIZE_RESULT_MEDIUM_DARK;
		if (result.length() > MAX_CHAR_RESULT_MEDIUM_DARK)
			fontSize = FONT_SIZE_RESULT_LITTLE_DARK;

		g.setFont(new Font("Calibrì", Font.PLAIN, fontSize));
		g.drawString(result, 100, 230);
	}

	private static void writeCommentDark(Graphics g, String comment) {
		g.setFont(new Font("Calibrì", Font.PLAIN, COMMENT_FONT_HEIGHT_DARK));
		String[] words = comment.split(" ");
		String row = "";
		String currentRow = "";
		int drawnRow = 0;

		for (int i = 0; i < words.length; i++) {
			currentRow = row;
			row += words[i] + " ";
			if (row.length() > CHAR_PER_ROW_DARK) {
				g.drawString(currentRow, COMMENT_WIDTH_DARK, INITIAL_COMMENT_HEIGHT_DARK + ROW_HEIGHT_DARK * drawnRow);
				drawnRow++;
				row = words[i] + " ";
			}
			g.drawString(row, COMMENT_WIDTH_DARK, INITIAL_COMMENT_HEIGHT_DARK + ROW_HEIGHT_DARK * drawnRow);
		}
	}

}
