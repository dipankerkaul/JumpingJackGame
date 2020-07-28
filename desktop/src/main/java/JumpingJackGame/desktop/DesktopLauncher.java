package JumpingJackGame.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import JumpingJackGame.JumpingJackGame;

/** Launches the desktop (LWJGL) application. */
public class DesktopLauncher
{
	public static void main (String[] args)
	{
		JumpingJackGame myGame = new JumpingJackGame();
		LwjglApplication launcher = new LwjglApplication( myGame, "Game Title", 800, 600 );
	}
}