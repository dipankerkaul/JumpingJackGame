package JumpingJackGame;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.Gdx;


public class LevelScreen extends BaseScreen
{   Koala jack;
    boolean gameOver;
    int coins;
    float time;
    Label coinLabel;
    Table keyTable;
    Label timeLabel;
    Label messageLabel;

    public void initialize() 
    {   TilemapActor tma = new TilemapActor("map.tmx", mainStage);
        for (MapObject obj : tma.getRectangleList("Solid") )
        {
            MapProperties props = obj.getProperties();
            new Solid( (float)props.get("x"), (float)props.get("y"),
                    (float)props.get("width"), (float)props.get("height"),
                    mainStage );

        }

        MapObject startPoint = tma.getRectangleList("start").get(0);
        MapProperties startProps = startPoint.getProperties();
        jack = new Koala( (float)startProps.get("x"), (float)startProps.get("y"), mainStage);

        gameOver = false;
        coins = 0;
        time = 60;
        coinLabel = new Label("Coins: " + coins, BaseGame.labelStyle);
        coinLabel.setColor(Color.GOLD);
        keyTable = new Table();
        timeLabel = new Label("Time: " + (int)time, BaseGame.labelStyle);
        timeLabel.setColor(Color.LIGHT_GRAY);
        messageLabel = new Label("Message", BaseGame.labelStyle);
        messageLabel.setVisible(false);

        uiTable.pad(20);
        uiTable.add(coinLabel);
        uiTable.add(keyTable).expandX();
        uiTable.add(timeLabel);
        uiTable.row();
        uiTable.add(messageLabel).colspan(3).expandY();

        for (MapObject obj : tma.getTileList("Flag") )
        {
            MapProperties props = obj.getProperties();
            new Flag( (float)props.get("x"), (float)props.get("y"), mainStage );
        }

        for (MapObject obj : tma.getTileList("Coin") )
        {
            MapProperties props = obj.getProperties();
            new Coin( (float)props.get("x"), (float)props.get("y"), mainStage );
        }

        for (MapObject obj : tma.getTileList("Timer") )
        {
            MapProperties props = obj.getProperties();
            new Timer( (float)props.get("x"), (float)props.get("y"), mainStage );
        }

        for (MapObject obj : tma.getTileList("Platform") )
        {
            MapProperties props = obj.getProperties();
            new Platform( (float)props.get("x"), (float)props.get("y"), mainStage );
        }


    }

    public void update(float dt)
    {
        for (BaseActor actor : BaseActor.getList(mainStage, Solid.class))
        {
            Solid solid = (Solid)actor;
            if ( solid instanceof Platform )
            {
                if ( jack.isJumping() && jack.overlaps(solid) )
                    solid.setEnabled(false);
                if ( jack.isJumping() && !jack.overlaps(solid) )
                    solid.setEnabled(true);

                if ( jack.isFalling() && !jack.overlaps(solid) && !jack.belowOverlaps(solid) )
                    solid.setEnabled(true);
            }
            if ( jack.overlaps(solid) && solid.isEnabled() )
            {
                Vector2 offset = jack.preventOverlap(solid);
                if (offset != null)
                {
                    // collided in X direction
                    if ( Math.abs(offset.x) > Math.abs(offset.y) )
                        jack.velocityVec.x = 0;
                    else // collided in Y direction
                        jack.velocityVec.y = 0;
                }
            }
            for (BaseActor coin : BaseActor.getList(mainStage, Coin.class))
            {
                if ( jack.overlaps(coin) )
                {
                    coins++;
                    coinLabel.setText("Coins: " + coins);
                    coin.remove();
                }
            }
        }

        if ( gameOver )
            return;
        for (BaseActor flag : BaseActor.getList(mainStage, Flag.class))
        {
            if ( jack.overlaps(flag) )
            {
                messageLabel.setText("You Win!");
                messageLabel.setColor(Color.LIME);
                messageLabel.setVisible(true);
                jack.remove();
                gameOver = true;
            }
        }

        time -= dt;
        timeLabel.setText("Time: " + (int)time);
        for (BaseActor timer : BaseActor.getList(mainStage, Timer.class))
        {
            if ( jack.overlaps(timer) )
            {
                time += 20;
                timer.remove();
            }
        }

        if (time <= 0)
        {
            messageLabel.setText("Time Up - Game Over");
            messageLabel.setColor(Color.RED);
            messageLabel.setVisible(true);
            jack.remove();
            gameOver = true;
        }
       
    }



        public boolean keyDown(int keyCode)
        {
            if (gameOver)
                return false;
            if (keyCode == Keys.SPACE)
            {
                if ( Gdx.input.isKeyPressed(Keys.DOWN) )
                {
                    for (BaseActor actor : BaseActor.getList(mainStage, Platform.class))
                    {

                        Platform platform = (Platform)actor;
                        if ( jack.belowOverlaps(platform) )
                        {
                            platform.setEnabled(false);
                        }
                    }
                }
                else if ( jack.isOnSolid() )
                {
                    jack.jump();
                }
            }
            return false;
        }

    }
