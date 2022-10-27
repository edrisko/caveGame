import java.awt.*;

public class Platform {
    //VARIABLE DECLARATION SECTION
    //Here's where you state which variables you are going to use.
    public String name;             //holds the name of the hero
    public int xpos;                //the x position
    public int ypos;
    public int width;
    public int height;
    public Rectangle rec;           //sets up rectangle for interactions

    // METHOD DEFINITION SECTION

    // Constructor Definition
    // A constructor builds the object when called and sets variable values.


    //This is a SECOND constructor that takes 3 parameters.  This allows us to specify the hero's name and position when we build it.
    // if you put in a String, an int and an int the program will use this constructor instead of the one above.
    public Platform(int pXpos, int pYpos) {
        //branches off into negative + positive options (to avoid having 0)
        width = 150;
        height = 20;
        xpos = pXpos;
        ypos = pYpos;
        //sets random speed in x and y directions
        rec = new Rectangle(xpos, ypos, width, height);
    } // constructor
}