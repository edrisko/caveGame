//Basic Game Application
//*******************************************************************************
//Import Section
//Add Java libraries needed for the game
//import java.awt.Canvas;

//Graphics Libraries
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.image.BufferStrategy;
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;
import java.awt.event.*;

/*Instructions: stay alive for as long as possible without getting caught be the bats that chase you
notes:
1. use arrow keys to move around platforms
2. your character can double jump
3. point and click to shoot fireballs towards your mouse; if a fireball hits a bat, it will die
4. your light source will diminish over time and when you shoot a fireball; collect stars to replenish it
5. point values are calculated by time survived + bats killed + stars consumed

Good luck!
*/

//******************************************************************************
// Class Definition Section

public class BasicGameApp implements Runnable, KeyListener, MouseListener, MouseMotionListener {

    //Variable Definition Section
    //Declare the variables used in the program
    //You can set their initial values too

    //Sets the width and height of the program window
    final int WIDTH = 1000;
    final int HEIGHT = 700;
    final int WORLDWIDTH = 2400;
    final int WORLDHEIGHT = 1450;

    public int i; //platform counter for intersections
    public int points;

    //Declare the variables needed for the graphics
    public JFrame frame;
    public Canvas canvas;
    public JPanel panel;

    public BufferStrategy bufferStrategy;

    public Image fireSpriteLeftPic;
    public Image fireSpriteRightPic;
    public Image platformPic;
    public Image batPic;
    public Image gameOverPic;
    public Image fireballPic;
    public Image lightPic;
    //public Image lightSourcePic;

    public Image background1;  //background

    public SoundFile backgroundMusic;
    public SoundFile jumpSound;
    public SoundFile jumpLandSound;
    public SoundFile defeatTheme;
    public SoundFile victoryTheme;
    public SoundFile fireballThrow;
    public SoundFile starCollect;

    //sidescrolling variables
    public BufferedImage worldImage;   //image for the entire world

    //window variables
    public int dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2;

    public int columns;
    public int rows;
    public int highScore;

    public long startTime;
    public long currentTime;
    public long elapsedTime;
    public long finalTime;

    public double batRespawnRate;
    public double batRespawn;

    public boolean gameOver;
    public boolean right;
    public boolean wasJumping;
    public boolean songPlayed;

    //Declaring the objects used in the program
    public Player player1;
    public Platform[] platformy;
    public Bat[] batty;
    public Fireballs[] firey;
    public Light[] lighty;

    //Spotlight or AlphaComposite
    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_IN);
    AlphaComposite AC = AlphaComposite.getInstance(AlphaComposite.CLEAR);

    //Mouse position variables
    public int mouseX, mouseY;

    // Main method definition
    // This is the code that runs first and automatically
    public static void main(String[] args) {
        BasicGameApp ex = new BasicGameApp();   //creates a new instance of the game
        new Thread(ex).start();                 //creates a threads & starts up the code in the run( ) method
    }


    // Constructor Method
    // This has the same name as the class
    // This section is the setup portion of the program
    // Initialize your variables and construct your program objects here.
    public BasicGameApp() {
        setUpGraphics();

        canvas.addKeyListener(this);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);

        columns = 0;
        rows = 0;
        points = 0;

        batRespawnRate = 0.01;
        batRespawn = 0;

        player1 = new Player();
        platformy = new Platform[30];
        for (int x = 0; x < platformy.length; x++) {
            rows = (int) Math.floor(x / 6);
            columns = x % 6;
            platformy[x] = new Platform((int) Math.floor(Math.random() * (206) + (60 + columns * 355)), (int) Math.floor(Math.random() * (217) + (50 + rows * 236)));
        }
        lighty = new Light[10];
        for (int i=0; i<lighty.length;i++){
            lighty[i] = new Light((int)Math.floor(Math.random()*2300+50), (int)Math.floor(Math.random()*1350+50));
        }
        batty = new Bat[50];
        for (int x = 0; x < batty.length; x++) {
            if(x<3){
                batty[x] = new Bat(true);
            }else{
                batty[x] = new Bat(false);
            }

        }

        firey = new Fireballs[20];
        for (int x = 0; x < firey.length; x++) {
            firey[x] = new Fireballs();
        }

        gameOverPic = Toolkit.getDefaultToolkit().getImage("gameOver.png");
        fireSpriteLeftPic = Toolkit.getDefaultToolkit().getImage("fireSpriteLeft.png");
        fireSpriteRightPic = Toolkit.getDefaultToolkit().getImage("fireSpriteRight.png");
        background1 = Toolkit.getDefaultToolkit().getImage("background1.jpg");
        platformPic = Toolkit.getDefaultToolkit().getImage("platform.png");
        batPic = Toolkit.getDefaultToolkit().getImage("bat.png");
        fireballPic = Toolkit.getDefaultToolkit().getImage("fireball.png");
        lightPic = Toolkit.getDefaultToolkit().getImage("light.png");

        backgroundMusic = new SoundFile("caveTheme.wav");
        jumpSound = new SoundFile("jump.wav");
        jumpLandSound = new SoundFile("jumpLand.wav");
        defeatTheme = new SoundFile("defeat.wav");
        victoryTheme = new SoundFile("Victory.wav");
        fireballThrow = new SoundFile("fireball.wav");
        starCollect = new SoundFile("starCollect.wav");

        //sidescrolling variables
        dx1 = 0;
        dx2 = WIDTH;
        dy1 = 0;
        dy2 = HEIGHT;
        sx1 = 0;
        sy1 = 0;
        sx2 = WIDTH;
        sy2 = HEIGHT;

        i = 0;

        highScore = 0;

        right = false;
        gameOver = false;
        wasJumping = false;
        songPlayed = false;

        render();
        startTime = System.currentTimeMillis();
        backgroundMusic.loop();
    }// BasicGameApp()


//*******************************************************************************
//User Method Section
//
// put your code to do things here.

    // main thread
    // this is the code that plays the game after you set things up
    public void run() {
        while (true) {
            if (gameOver) {
                render();
                pause(20);
            } else {
                currentTime = System.currentTimeMillis();
                elapsedTime = currentTime - startTime;
                if (elapsedTime >= 10000 && elapsedTime < 20000) {
                    for (int x = 0; x < batty.length; x++) {
                        batty[x].speed = 5;
                        batRespawnRate = 0.02;
                    }
                } else if (elapsedTime >= 20000 && elapsedTime < 30000) {
                    for (int x = 0; x < batty.length; x++) {
                        batty[x].speed = 6;
                        batRespawnRate = 0.03;
                    }
                } else if (elapsedTime >= 30000 && batty[0].speed <= 6.5) {
                    for (int x = 0; x < batty.length; x++) {
                        batty[x].speed += 0.1;
                        batRespawnRate += 0.01;
                    }
                }
                if(player1.inAir == false && wasJumping == true){
                    jumpLandSound.play();
                    wasJumping = false;
                }
                screenScrolling();
                move();
                checkIntersections();
                render();
                pause(20);
            }
        }
    }

    public void move() {
        batRespawn=batRespawn+batRespawnRate;
        if(batRespawn>=1){
            batRespawn-=1;
            for (int x = 0; x < batty.length; x++) {
                if(!batty[x].isAlive){
                    batty[x].isAlive = true;
                    batty[x].dYpos = Math.floor(Math.random() * (1400-batty[x].height-50) + 50);
                    batty[x].dXpos = Math.floor(Math.random() * (2350-batty[x].width-50) + 50);
                    batty[x].xpos = (int) batty[x].dXpos;
                    batty[x].ypos = (int) batty[x].dYpos;
                    System.out.println("bat respawned");
                    break;
                }
            }
        }

        for (int x = 0; x < batty.length; x++) {
            batty[x].xDifference = player1.xpos - batty[x].xpos;
            batty[x].yDifference = player1.ypos - batty[x].ypos;
        }
        player1.move();
        for(int x = 0;x<lighty.length; x++){
            lighty[x].respawn();
        }
        for (int x = 0; x < batty.length; x++) {
            if(batty[x].isAlive) {
                batty[x].move();
            }
        }
        for (int x = 0; x < firey.length; x++) {
            if(firey[x].isAlive) {
                firey[x].move();
                System.out.println("fireball is moving");
                System.out.println("fireball: " + firey[x].dXpos + ", " + firey[x].dYpos);
            }else{
                firey[x].dYpos = player1.ypos+player1.height/2;
                firey[x].dXpos = player1.xpos+player1.width/2;
                firey[x].ypos = player1.ypos+player1.height/2;
                firey[x].xpos = player1.xpos+player1.width/2;
            }
        }
    }


    //paints things on the screen using bufferStrategy
    public void render() {
        //world graphics
        Graphics2D world = (Graphics2D) worldImage.getGraphics();
        //screen graphics
        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
        Graphics2D g2 = (Graphics2D) g;
        int w = WIDTH;
        int h = HEIGHT;

        // Creates the buffered image.
        BufferedImage buffImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gbi = buffImg.createGraphics();
        g.clearRect(0, 0, WIDTH, HEIGHT);
        //game over screen
        if (gameOver) {
            if (((int) finalTime / 100 + points) >= highScore) {
                if(songPlayed == false) {
                    victoryTheme.play();
                    songPlayed = true;
                }
                highScore = ((int) finalTime / 100 + points);
            } else {
                if(songPlayed == false) {
                    defeatTheme.play();
                    songPlayed = true;
                }
            }
            g.drawImage(gameOverPic, 0, 0, WIDTH, HEIGHT, null);
            g.setColor(Color.WHITE);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 25));
            g.drawString("Your Score: " + ((int) finalTime / 100 + points) + " points", 250, 225);
            g.drawString("High Score: " + highScore + " points", 250, 275);
        } else {
            //draw to the worldImage
            world.clearRect(0, 0, WIDTH, HEIGHT);
            world.drawImage(background1, 0, 0, 2400, 1350, null);
            if (player1.dx > 0) {
                right = true;
                world.drawImage(fireSpriteRightPic, player1.xpos, player1.ypos, player1.width, player1.height, null);
            } else if (player1.dx < 0) {
                right = false;
                world.drawImage(fireSpriteLeftPic, player1.xpos, player1.ypos, player1.width, player1.height, null);
            } else if (player1.dx == 0) {
                if (right == false) {
                    world.drawImage(fireSpriteLeftPic, player1.xpos, player1.ypos, player1.width, player1.height, null);
                } else {
                    world.drawImage(fireSpriteRightPic, player1.xpos, player1.ypos, player1.width, player1.height, null);
                }
            }
            for (int x = 0; x < platformy.length; x++) {
                world.drawImage(platformPic, platformy[x].xpos, platformy[x].ypos, platformy[x].width, platformy[x].height, null);
            }
            for (int x = 0; x < lighty.length; x++) {
                world.drawImage(lightPic, lighty[x].xpos, lighty[x].ypos, lighty[x].width, lighty[x].height, null);
            }
            for (int x = 0; x < batty.length; x++) {
                if(batty[x].isAlive) {
                    world.drawImage(batPic, batty[x].xpos, batty[x].ypos, batty[x].width, batty[x].height, null);
                }
            }
            for (int x = 0; x < firey.length; x++) {
                if(firey[x].isAlive) {
                    AffineTransform at = AffineTransform.getTranslateInstance(firey[x].xpos,firey[x].ypos);

                    if(firey[x].dx>=0){
                        at.rotate(Math.atan(firey[x].dy/firey[x].dx), firey[x].width/2,firey[x].height/2);
                    }else{
                        at.rotate(-Math.PI+Math.atan(firey[x].dy/firey[x].dx), firey[x].width/2,firey[x].height/2);
                    }
                    at.scale(0.07,0.07);
                    world.drawImage(fireballPic, at, null);
                }
            }
            //draw part of the worldImage to the screen
            // d stand for destination, s stands for source
            g.drawImage(worldImage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
            //the spotlight code -- ac is the way they interact with each other when intersecting
            gbi.setColor(new Color(0.0f, 0.0f, 0.0f, 1.0f));
            gbi.fill(new Rectangle2D.Double(0, 0, WIDTH, HEIGHT));
            gbi.setColor(new Color(1.0f, 0.0f, 0.0f, 0.5f));
            gbi.setComposite(ac);
            gbi.fill(new Ellipse2D.Double(player1.xpos-sx1+player1.width/2-(player1.lightRadius/2),player1.ypos-sy1+player1.height/2-(player1.lightRadius/2),player1.lightRadius,player1.lightRadius));
            for (int x = 0; x < lighty.length; x++) {
                gbi.fill(new Ellipse2D.Double(lighty[x].xpos-sx1+lighty[x].width/2-75,lighty[x].ypos-sy1+lighty[x].height/2-75,150,150));
            }
            gbi.setColor(new Color(1.0f, 0.0f, 0.0f, 1.0f));
            gbi.setComposite(AC);
            gbi.fill(new Ellipse2D.Double(player1.xpos-sx1+player1.width/2-(player1.lightRadius/3),player1.ypos-sy1+player1.height/2-(player1.lightRadius/3),(int) Math.floor(player1.lightRadius*2/3),(int) Math.floor(player1.lightRadius*2/3)));
            for (int x = 0; x < lighty.length; x++) {
                gbi.fill(new Ellipse2D.Double(lighty[x].xpos-sx1+lighty[x].width/2-50,lighty[x].ypos-sy1+lighty[x].height/2-50,100,100));
            }
            for (int x = 0; x < firey.length; x++) {
                if(firey[x].isAlive) {
                    gbi.setColor(new Color(1.0f, 0.0f, 0.0f, 0.5f));
                    gbi.setComposite(ac);
                    gbi.fill(new Ellipse2D.Double(firey[x].xpos-sx1+firey[x].width/2-100,firey[x].ypos-sy1+firey[x].height/2-100,200,200));
                    gbi.setColor(new Color(1.0f, 0.0f, 0.0f, 1.0f));
                    gbi.setComposite(AC);
                    gbi.fill(new Ellipse2D.Double(firey[x].xpos-sx1+firey[x].width/2-50,firey[x].ypos-sy1+firey[x].height/2-50,100,100));
                }
            }

            // Draws the buffered image.
            g2.drawImage(buffImg, null, 0, 0);
        }
        bufferStrategy.show();

    }

    public void checkIntersections() {
      for (int i = 0; i < platformy.length; i++) {
            if (platformy[i].rec.intersects(player1.rec) && player1.ypos >= platformy[i].ypos - player1.height && player1.ypos <= platformy[i].ypos + platformy[i].height - player1.height && player1.dy >= 0 && player1.xpos >= platformy[i].xpos - 0.5 * player1.width && player1.xpos <= platformy[i].xpos + platformy[i].width - 0.5 * player1.width) {
                player1.onPlatform = true;
                break;
            } else if (i == platformy.length - 1) {
                player1.onPlatform = false;
            }
      }
      if (player1.onPlatform == true) {
            player1.onPlatform = true;
            player1.inAir = false;
      } else {
            player1.onPlatform = false;
            player1.inAir = true;
      }
      for (int i = 0; i < firey.length; i++) {
          for (int x = 0; x < batty.length; x++) {
              if (batty[x].rec.intersects(firey[i].rec)&&batty[x].isAlive&&firey[i].isAlive) {
                  batty[x].isAlive = false;
                  firey[i].isAlive = false;
                  points+=50;
              }
          }
      }
        for (int i = 0; i < firey.length; i++) {
            for (int x = 0; x < platformy.length; x++) {
                if (platformy[x].rec.intersects(firey[i].rec)&&firey[i].isAlive) {
                    firey[i].isAlive = false;
                }
            }
        }
      for (i = 0; i < batty.length; i++) {
            if (batty[i].rec.intersects(player1.rec)&&batty[i].isAlive) {
                finalTime = elapsedTime;
                gameOver = true;
                backgroundMusic.stop();
            }
      }
      for(i = 0; i<lighty.length;i++){
          if(lighty[i].rec.intersects(player1.rec)&&lighty[i].isAlive){
              player1.dLightRadius+=100;
              player1.lightRadius+=100;
              lighty[i].isAlive = false;
              points+=10;
              starCollect.play();
          }
      }
    }


    public void screenScrolling() {
        if (player1.dx != 0 && player1.xpos >= WIDTH / 2 && player1.xpos <= WORLDWIDTH - (WIDTH / 2)) {
            sx1 += player1.dx;
            if (sx1 > WORLDWIDTH - WIDTH) {
                sx1 = WORLDWIDTH - WIDTH;
            } else if (sx1 < 0) {
                sx1 = 0;
            }
            sx2 = sx1 + WIDTH;
        }
        if (player1.dy != 0 && player1.ypos >= HEIGHT / 2 && player1.ypos <= WORLDHEIGHT - (HEIGHT / 2) - 150) {
            sy1 += (player1.dy + 1);
            if (sy1 > WORLDHEIGHT - HEIGHT - 150) {
                sy1 = WORLDHEIGHT - HEIGHT - 150;
            } else if (sy1 < 0) {
                sy1 = 0;
            }
            sy2 = sy1 + HEIGHT;
        }
    }


    public void keyPressed(KeyEvent event) {
        char key = event.getKeyChar();
        int keyCode = event.getKeyCode();
        if (keyCode == 37) {
            player1.dx = -6;
        }
        if (keyCode == 38 && player1.jumpNum < 2) {
            player1.jumpNum += 1;
            player1.inAir = true;
            player1.dy = -12;
            jumpSound.play();
            wasJumping = true;
        }
        if (keyCode == 39) {
            player1.dx = 6;
        }
        if (keyCode == 65 && gameOver == true) { //restarts game
            for (int x = 0; x < platformy.length; x++) {
                rows = (int) Math.floor(x / 6);
                columns = x % 6;
                platformy[x].xpos = (int) Math.floor(Math.random() * (206) + (60 + columns * 355));
                platformy[x].ypos = (int) Math.floor(Math.random() * (217) + (50 + rows * 236));
            }
            for (int x = 0; x < platformy.length; x++) {
                platformy[x].rec = new Rectangle(platformy[x].xpos, platformy[x].ypos, platformy[x].width, platformy[x].height);
            }
            for (int x = 0; x < batty.length; x++) {
                batty[x].dYpos = Math.floor(Math.random() * (1400 - batty[x].height - 50) + 50);
                batty[x].ypos = (int) batty[x].dYpos;
                batty[x].dXpos = Math.floor(Math.random() * (2350 - batty[x].width - 50) + 50);
                batty[x].xpos = (int) batty[x].dXpos;
                batty[x].speed = 4;
            }
            player1.xpos = 500;
            player1.ypos = 350;
            player1.dy = 0;
            player1.dypos = player1.ypos;
            player1.jumpNum = 0;
            for(int i =0; i<firey.length; i++){
                firey[i].isAlive = false;
                firey[i].dYpos = player1.ypos+player1.height/2;
                firey[i].dXpos = player1.xpos+player1.width/2;
                firey[i].ypos = player1.ypos+player1.height/2;
                firey[i].xpos = player1.xpos+player1.width/2;
            }
            sx1 = 0;
            sy1 = 0;
            sx2 = WIDTH;
            sy2 = HEIGHT;
            songPlayed = false;
            player1.onGround = false;
            player1.onPlatform = false;
            for (int i=0; i<lighty.length;i++){
                lighty[i].isAlive = true;
                lighty[i].xpos = (int)Math.floor(Math.random()*2300+50);
                lighty[i].ypos = (int)Math.floor(Math.random()*1350+50);
                lighty[i].rec = new Rectangle(lighty[i].xpos, lighty[i].ypos,lighty[i].width,lighty[i].height);
            }
            player1.dLightRadius=600;
            player1.lightRadius=600;
            points=0;
            batRespawn = 0;
            batRespawnRate = 0.01;
            startTime = System.currentTimeMillis();
            gameOver = false;
            backgroundMusic.loop();
        }
    }

    public void keyReleased(KeyEvent event) {
        char key = event.getKeyChar();
        int keyCode = event.getKeyCode();
        if (keyCode == 37) {
            player1.dx = 0;
        }
        if (keyCode == 39) {
            player1.dx = 0;
        }
    }

    public void keyTyped(KeyEvent event) {
        //not used
    }

    public void mouseClicked(MouseEvent e) {

        int x, y;
        x = e.getX();
        y = e.getY();

        mouseX = sx1 + x;
        mouseY = sy1 + y;
        for (int i = 0; i < firey.length; i++) {
            if(firey[i].isAlive == false && player1.lightRadius>=200){
                firey[i].dYpos = player1.ypos+player1.height/2;
                firey[i].dXpos = player1.xpos+player1.width/2;
                firey[i].ypos = player1.ypos+player1.height/2;
                firey[i].xpos = player1.xpos+player1.width/2;
                firey[i].isAlive = true;
                firey[i].xDifference = mouseX - firey[i].xpos;
                firey[i].yDifference = mouseY - firey[i].ypos;
                player1.dLightRadius-=30;
                player1.lightRadius-=30;
                fireballThrow.play();
                break;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e){

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    // REQUIRED Mouse Listener methods
    @Override
    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        int x, y;
        x = e.getX();
        y = e.getY();
        mouseX = x;
        mouseY = y;
    }

    //Pauses or sleeps the computer for the amount specified in milliseconds
    public void pause(int time) {
        //sleep
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {

        }
    }


    //Graphics setup method
    private void setUpGraphics() {
        frame = new JFrame("My First Game");   //Create the program window or frame.  Names it.

        panel = (JPanel) frame.getContentPane();  //sets up a JPanel which is what goes in the frame
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));  //sizes the JPanel
        panel.setLayout(null);   //set the layout

        // creates a canvas which is a blank rectangular area of the screen onto which the application can draw
        // and trap input events (Mouse and Keyboard events)
        canvas = new Canvas();
        canvas.setBounds(0, 0, WIDTH, HEIGHT);
        canvas.setIgnoreRepaint(true);

        panel.add(canvas);  // adds the canvas to the panel.

        // frame operations
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //makes the frame close and exit nicely
        frame.pack();  //adjusts the frame and its contents so the sizes are at their default or larger
        frame.setResizable(false);   //makes it so the frame cannot be resized
        frame.setVisible(true);      //IMPORTANT!!!  if the frame is not set to visible it will not appear on the screen!

        worldImage = new BufferedImage(WORLDWIDTH, WORLDHEIGHT, BufferedImage.TYPE_INT_RGB);

        // sets up things so the screen displays images nicely.
        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();
        canvas.requestFocus();
        System.out.println("DONE graphic setup");

    }
}