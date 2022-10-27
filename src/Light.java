import java.awt.*;

public class Light {
    //VARIABLE DECLARATION SECTION
    //Here's where you state which variables you are going to use.
    public String name;                //holds the name of the bat
    public int xpos;                   //the x position
    public int ypos;                   //the y position
    public int width;
    public int height;
    public boolean isAlive;            //a boolean to denote if is alive or not
    public Rectangle rec;              //sets up rectangle for interactions

    public Light(int pXpos, int pYpos) {
        width = 40;
        height = 40;
        xpos = pXpos;
        ypos = pYpos;
        isAlive = false;
        rec = new Rectangle(xpos, ypos, width, height);
    } // constructor
    public void respawn(){
        if(isAlive==false){
            xpos = (int)Math.floor(Math.random()*2300+50);
            ypos = (int)Math.floor(Math.random()*1350+50);
            isAlive=true;
            rec = new Rectangle(xpos, ypos, width, height);
        }
    }
}
