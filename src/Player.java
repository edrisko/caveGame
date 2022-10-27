import java.awt.*;

public class Player {
    //VARIABLE DECLARATION SECTION
    //Here's where you state which variables you are going to use.
    public String name;             //holds the name of the hero
    public int xpos;                //the x position
    public double dypos;                //the y position
    public int ypos;
    public int dx;                  //the speed of the hero in the x direction
    public double dy;                  //the speed of the hero in the y direction
    public int width;
    public int height;
    public int money;            //point system
    public boolean inAir;
    public boolean onGround;
    public int jumpNum;  // how many times has the character jumped
    public boolean onPlatform;
    public double acceleration;
    public int lightRadius;         //how radius of visibility around player
    public double dLightRadius;
    public Rectangle rec;           //sets up rectangle for interactions


    // METHOD DEFINITION SECTION

    // Constructor Definition
    // A constructor builds the object when called and sets variable values.


    //This is a SECOND constructor that takes 3 parameters.  This allows us to specify the hero's name and position when we build it.
    // if you put in a String, an int and an int the program will use this constructor instead of the one above.
    public Player() {
        xpos = 500;
        ypos = 350;
        dx = 0;
        dy = 0;
        width = 73;
        height = 117;
        money = 0;
        onGround = false;
        onPlatform = false;
        dLightRadius = 600;
        lightRadius = 600;
        jumpNum = 0;
        acceleration = 0.45;
        inAir = true;
        rec = new Rectangle (xpos, ypos, width, height);
    } // constructor

    public void printInfo() {
        System.out.println("PLAYER INFORMATION");
        System.out.println(xpos + " ,  " + ypos);
    }


    //The move method.  Everytime this is run (or "called") the hero's x position and y position change by dx and dy; also reduces light area
    public void move() {
        if(dLightRadius>150){
            dLightRadius-=0.3;
            lightRadius=(int)Math.floor(dLightRadius);
        }

        if(dypos>=1450-height-160 && dy>=0){
            onGround = true;
            inAir = false;
        }else{
            onGround = false;
        }
        if(inAir == true){
            dy = dy + acceleration;
            if((onGround == true || onPlatform == true)&&jumpNum==0){
                inAir=false;
                dy = 0;
            }
        }else{
            dy = 0;
            jumpNum = 0;
        }

        if(xpos>=0&&xpos<=2400-width){
            xpos = xpos + dx;
        }else if(xpos<=0&&dx>0){
            xpos = xpos + dx;
        }else if(xpos>=2400-width&&dx<0){
            xpos = xpos + dx;
        }

        dypos = dypos + dy;

        ypos = (int)dypos;
        //updates rectangle position
        rec = new Rectangle(xpos, ypos, width, height);
    }
}
