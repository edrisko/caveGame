import java.awt.*;

public class Fireballs {
    //VARIABLE DECLARATION SECTION
    //Here's where you state which variables you are going to use.
    public String name;                //holds the name of the bat
    public int xpos;                   //the x position
    public int ypos;                   //the y position
    public double dYpos;
    public double dXpos;
    public double dx;                     //the speed of the bat in the x direction
    public double dy;                     //the speed of the bat in the y direction
    public int width;
    public int height;
    public double speed;
    public int xDifference;
    public int yDifference;
    public boolean isAlive;            //a boolean to denote if the bat is alive or dead.
    public Rectangle rec;              //sets up rectangle for interactions

    // METHOD DEFINITION SECTION

    // Constructor Definition
    // A constructor builds the object when called and sets variable values.


    //This is a SECOND constructor that takes 3 parameters.  This allows us to specify the hero's name and position when we build it.
    // if you put in a String, an int and an int the program will use this constructor instead of the one above.
    public Fireballs() {
        width = 66;
        height = 47;

        dYpos = 0;
        dXpos = 0;

        xpos = (int) dXpos;
        ypos = (int) dYpos;
        speed = 14;
        xDifference = 0;
        yDifference = 0;


        isAlive = false;
        rec = new Rectangle (xpos, ypos, width, height);
    } // constructor

    public void printInfo() {
        System.out.println("PLAYER INFORMATION");
        System.out.println(xpos + " ,  " + ypos);
    }


    //The move method.  Everytime this is run (or "called") the hero's x position and y position change by dx and dy
    public void move() {
        if (isAlive==true) {
            dx = xDifference*speed/(Math.sqrt(xDifference*xDifference+yDifference*yDifference));
            dy = yDifference*speed/(Math.sqrt(xDifference*xDifference+yDifference*yDifference));
            //changes position by dx and dy
            dXpos = dXpos + dx;
            dYpos = dYpos + dy;
            xpos = (int)dXpos;
            ypos = (int)dYpos;
            if(dXpos>2800||dXpos<0||dYpos<0||dYpos>1450){
                isAlive = false;
            }
            //updates rectangle position
            rec = new Rectangle(xpos, ypos, width, height);
        }
    }
}
