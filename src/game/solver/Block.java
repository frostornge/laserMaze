package game.solver;

/**
 * Created by juungbae on 2017. 6. 10..
 */
public class Block {
    private int location;
    private int color;
    private int type;
    public Block[] conn; //Top, Down, Left, Right
    private int laserFrom = -1; //if laserFrom is -1, StartBlock(RED)
    private int[][] blockPosition;
    private Mov[] movement, shot;
    private int[] addAt = new int[]{2, 3, 0, 1};
    private boolean targeted = false;

    private class Mov {
        int x, y;

        public Mov(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean equals(int x, int y) {
            if(this.x == x && this.y == y) return true;
            return false;
        }

        public boolean equals(Mov mov) {
            if(this.x == mov.x && this.y == mov.y) return true;
            return false;
        }
    }

    //TODO : 탐색하는 함수 만들 것
    //TODO : conn에 연결하고 새로운 블록 find 시켜줄 것

    public Block(int laserFrom, int location, int[][] blockPosition) {
        this(location, blockPosition);
        this.laserFrom = laserFrom;
    }

    public Block(int location, int[][] blockPosition) {
        this.conn = new Block[4];
        this.blockPosition = blockPosition;
        this.movement = new Mov[]{
          new Mov(1, 0), new Mov(0, -1),
            new Mov(-1, 0),
            new Mov(0, 1)
        };
        this.shot = new Mov[]{
                new Mov(-1, 0), new Mov(0, 1),
                new Mov(1, 0),
                new Mov(0, -1)
        };
        this.location = location;
        this.convert(blockPosition[location/5][location%5]);
    }

    public void convert(int x) {
        this.color = x/10;
        this.type = x%10;
    }


    public void search(int x, int y, Mov mov) {
        while(true) {
            x += mov.x;
            y += mov.y;
            if(x < 0 || x >= 5 || y < 0 || y >= 5) return;

            if(blockPosition[x][y] != 0) {
                for(int i=0; i<movement.length; i++) {
                    if(mov.equals(movement[i])) {
                        conn[addAt[i]] = new Block(i, blockPosition);
                        conn[addAt[i]].find();
                        return;
                    }
                }
            }
        }
    }

    public void find() {
        Mov[] moveArray;
        int x = location/5, y = location%5;

        switch(color) {
            case 1 : //red
                search(x, y, shot[type]);
                break;
            case 2 : //blue
                if(type == 0) moveArray = new Mov[]{new Mov(0, -1), new Mov(1, 0), new Mov(0, 1), new Mov(-1, 0)};
                else moveArray = new Mov[]{new Mov(0, 1), new Mov(-1, 0), new Mov(0, -1), new Mov(1, 0)};
                search(x, y, moveArray[laserFrom]);
                break;
            case 3: //Green
                switch(type) {
                    case 0:
                        if(laserFrom == 0) { search(x, y, new Mov(1, 0)); search(x, y, new Mov(0, 1)); }
                        else if(laserFrom == 1) { search(x, y, new Mov(-1, 0)); search(x, y, new Mov(0, -1)); }
                        else if(laserFrom == 2) { search(x, y, new Mov(-1, 0)); search(x, y, new Mov(0, 1)); }
                        else { search(x, y, new Mov(-1, 0)); search(x, y, new Mov(0, 1)); }
                        break;
                    case 1:
                        if(laserFrom == 0) { search(x, y, new Mov(1, 0)); search(x, y, new Mov(0, -1)); }
                        else if(laserFrom == 1) { search(x, y, new Mov(1, 0)); search(x, y, new Mov(0, -1)); }
                        else if(laserFrom == 2) { search(x, y, new Mov(-1, 0)); search(x, y, new Mov(0, -1)); }
                        else { search(x, y, new Mov(1, 0)); search(x, y, new Mov(0, 1)); }
                        break;
                }
                break;
            case 4 : //Yellow
                if(type==0 && (laserFrom == 0 || laserFrom == 2))  //ㅡ
                    search(x, y, (laserFrom == 0 ? new Mov(0, 1) : new Mov(0, -1)));
                else if(type == 1 && (laserFrom == 1 || laserFrom == 3)) //|
                    search(x, y, (laserFrom == 1 ? new Mov(-1, 0) : new Mov(1, 0)));
                break;
            case 5:
            case 6:
                if(type == laserFrom) {
                    //Target on!
                    targeted = true;
                    return;
                }
                else if(type==0 && (laserFrom == 2 || laserFrom == 3))
                    search(x, y, (laserFrom==2 ? new Mov(0, -1): new Mov(1, 0)));
                else if(type==1 && (laserFrom == 0 || laserFrom == 3))
                    search(x, y, (laserFrom==0 ? new Mov(0, -1) : new Mov(-1, 0)));
                else if(type==2 && (laserFrom == 0 || laserFrom == 1))
                    search(x, y, (laserFrom == 0 ? new Mov(0, 1) : new Mov(-1 ,0)));
                else if(type==3 && (laserFrom == 1 || laserFrom == 2))
                    search(x, y, (laserFrom == 1 ? new Mov(1, 0) : new Mov(0, 1)));
                break;
            default :
                if(laserFrom == 0) search(x, y, new Mov(1, 0));
                else if(laserFrom == 1) search(x, y, new Mov(0, -1));
                else if(laserFrom == 2) search(x, y, new Mov(-1, 0));
                else search(x, y, new Mov(0, 1));
        }
    }

    public void validate(Validator validator) {
        if(color == 6 && targeted == false) {validator.targetOff(); return; }
        if(targeted) validator.addGoal();
        validator.addBlock();

        for(int i=0; i<conn.length; i++)
            if(conn[i] != null) conn[i].validate(validator);
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
