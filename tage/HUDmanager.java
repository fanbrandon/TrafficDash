package tage;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.gl2.GLUT;
import org.joml.*;

/**
* Manages up to two HUD strings, implemented as GLUT strings.
* This class is instantiated automatically by the engine.
* Note that this class utilizes deprectated OpenGL functionality.
* <p>
* The available fonts are:
* <ul>
* <li> GLUT.BITMAP_8_BY_13
* <li> GLUT.BITMAP_9_BY_15
* <li> GLUT.BITMAP_TIMES_ROMAN_10
* <li> GLUT.BITMAP_TIMES_ROMAN_24
* <li> GLUT.BITMAP_HELVETICA_10
* <li> GLUT.BITMAP_HELVETICA_12
* <li> GLUT.BITMAP_HELVETICA_18
* </ul>
* @author Scott Gordon
*/

public class HUDmanager
{	
	private GLCanvas myCanvas;
	private GLUT glut = new GLUT();
	private Engine engine;

	private String[] HUDstrings = new String[20];
	private float[][] HUDcolors = new float[20][3];
	private int[] HUDfonts = new int[20];
	private int[] HUDx = new int[20];
	private int[] HUDy = new int[20];

	// Constructor initializes the twenty HUDs to empty strings.
	protected HUDmanager(Engine e)
	{	
		engine = e;
		for (int i = 0; i < 20; i++) {
			HUDstrings[i] = "";
			HUDcolors[i] = new float[3];
			HUDfonts[i] = GLUT.BITMAP_TIMES_ROMAN_24;
		}
	}
	
	protected void setGLcanvas(GLCanvas g) { myCanvas = g; }

	protected void drawHUDs()
	{	
		GL4 gl4 = (GL4) GLContext.getCurrentGL();
		GL4bc gl4bc = (GL4bc) gl4;

		gl4.glUseProgram(0);

		for (int i = 0; i < 20; i++) {
			gl4bc.glColor3f(HUDcolors[i][0], HUDcolors[i][1], HUDcolors[i][2]);
			gl4bc.glWindowPos2d(HUDx[i], HUDy[i]);
			glut.glutBitmapString(HUDfonts[i], HUDstrings[i]);
		}
	}

	// Methods to set each HUD from 1 to 20
	public void setHUD1(String string, Vector3f color, int x, int y) { setHUD(0, string, color, x, y); }
	public void setHUD2(String string, Vector3f color, int x, int y) { setHUD(1, string, color, x, y); }
	public void setHUD3(String string, Vector3f color, int x, int y) { setHUD(2, string, color, x, y); }
	public void setHUD4(String string, Vector3f color, int x, int y) { setHUD(3, string, color, x, y); }
	public void setHUD5(String string, Vector3f color, int x, int y) { setHUD(4, string, color, x, y); }
	public void setHUD6(String string, Vector3f color, int x, int y) { setHUD(5, string, color, x, y); }
	public void setHUD7(String string, Vector3f color, int x, int y) { setHUD(6, string, color, x, y); }
	public void setHUD8(String string, Vector3f color, int x, int y) { setHUD(7, string, color, x, y); }
	public void setHUD9(String string, Vector3f color, int x, int y) { setHUD(8, string, color, x, y); }
	public void setHUD10(String string, Vector3f color, int x, int y) { setHUD(9, string, color, x, y); }
	public void setHUD11(String string, Vector3f color, int x, int y) { setHUD(10, string, color, x, y); }
	public void setHUD12(String string, Vector3f color, int x, int y) { setHUD(11, string, color, x, y); }
	public void setHUD13(String string, Vector3f color, int x, int y) { setHUD(12, string, color, x, y); }
	public void setHUD14(String string, Vector3f color, int x, int y) { setHUD(13, string, color, x, y); }
	public void setHUD15(String string, Vector3f color, int x, int y) { setHUD(14, string, color, x, y); }
	public void setHUD16(String string, Vector3f color, int x, int y) { setHUD(15, string, color, x, y); }
	public void setHUD17(String string, Vector3f color, int x, int y) { setHUD(16, string, color, x, y); }
	public void setHUD18(String string, Vector3f color, int x, int y) { setHUD(17, string, color, x, y); }
	public void setHUD19(String string, Vector3f color, int x, int y) { setHUD(18, string, color, x, y); }
	public void setHUD20(String string, Vector3f color, int x, int y) { setHUD(19, string, color, x, y); }

	// Methods to set fonts for each HUD from 1 to 20
	public void setHUD1font(int font) { setHUDFont(0, font); }
	public void setHUD2font(int font) { setHUDFont(1, font); }
	public void setHUD3font(int font) { setHUDFont(2, font); }
	public void setHUD4font(int font) { setHUDFont(3, font); }
	public void setHUD5font(int font) { setHUDFont(4, font); }
	public void setHUD6font(int font) { setHUDFont(5, font); }
	public void setHUD7font(int font) { setHUDFont(6, font); }
	public void setHUD8font(int font) { setHUDFont(7, font); }
	public void setHUD9font(int font) { setHUDFont(8, font); }
	public void setHUD10font(int font) { setHUDFont(9, font); }
	public void setHUD11font(int font) { setHUDFont(10, font); }
	public void setHUD12font(int font) { setHUDFont(11, font); }
	public void setHUD13font(int font) { setHUDFont(12, font); }
	public void setHUD14font(int font) { setHUDFont(13, font); }
	public void setHUD15font(int font) { setHUDFont(14, font); }
	public void setHUD16font(int font) { setHUDFont(15, font); }
	public void setHUD17font(int font) { setHUDFont(16, font); }
	public void setHUD18font(int font) { setHUDFont(17, font); }
	public void setHUD19font(int font) { setHUDFont(18, font); }
	public void setHUD20font(int font) { setHUDFont(19, font); }

	// Helper methods for setting HUD text, color, and position
	private void setHUD(int index, String string, Vector3f color, int x, int y) {
		HUDstrings[index] = string;
		HUDcolors[index][0] = color.x();
		HUDcolors[index][1] = color.y();
		HUDcolors[index][2] = color.z();
		HUDx[index] = x;
		HUDy[index] = y;
	}

	// Helper method for setting HUD fonts
	private void setHUDFont(int index, int font) {
		HUDfonts[index] = font;
	}
}