package com.example.siennaballot.candycrushgame;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.MotionEvent;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;

public class BoardView extends SurfaceView implements SurfaceHolder.Callback {
    Bitmap background;
    Bitmap blue;
    Bitmap red;
    Bitmap green;
    Bitmap purple;
    Bitmap yellow;
    Bitmap orange;
    Bitmap restart;
    Bitmap lose;
    Bitmap win;

    private int restart_check = 0;
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private int start_row;
    private int start_col;
    private int end_row;
    private int end_col;
    private int row_num = 12;
    private int col_num = 9;
    private boolean start = true;

    private int[][] board;
    private int[][] destroy;

    private int move_count = 0;
    private int score_count = 0;

    private boolean fillrandom = false;
    private boolean shift = false;
    private boolean check = false;

    //constructor for BoardView to initialize variables
    public BoardView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);

        x1 = 0;
        x2 = 0;
        y1 = 0;
        y2 = 0;
        start_row = 0;
        start_col = 0;
        end_row = 0;
        end_col = 0;

        board = new int[9][9];
        destroy = new int[9][9];

        background = BitmapFactory.decodeResource(getResources(), R.drawable.better);
        blue = BitmapFactory.decodeResource(getResources(), R.drawable.candy1);
        red = BitmapFactory.decodeResource(getResources(), R.drawable.candy2);
        green = BitmapFactory.decodeResource(getResources(), R.drawable.candy3);
        purple = BitmapFactory.decodeResource(getResources(), R.drawable.candy4);
        yellow = BitmapFactory.decodeResource(getResources(), R.drawable.candy5);
        orange = BitmapFactory.decodeResource(getResources(), R.drawable.candy6);
        restart = BitmapFactory.decodeResource(getResources(), R.drawable.restart);
        lose = BitmapFactory.decodeResource(getResources(), R.drawable.lose);
        win = BitmapFactory.decodeResource(getResources(), R.drawable.win);

        for (int x = 0; x < row_num-3; x++) {
            for (int y = 0; y < col_num; y++) {
                while(true) {
                    double random = Math.random()*6;
                    int rand = (int)random;

                    if (x == 0 || x == 1) {
                        if (y == 0 || y == 1) {
                            board[x][y] = rand;
                            break;
                        }
                        else if (board[x][y-1] != rand && board[x][y-2] != rand) {
                            board[x][y] = rand;
                            break;
                        }
                    }

                    else if (y == 0 || y == 1) {
                        if (x == 0 || x == 1) {
                            board[x][y] = rand;
                            break;
                        }
                        else if (board[x-1][y] != rand && board[x-2][y] != rand) {
                            board[x][y] = rand;
                            break;
                        }
                    }

                    else if (board[x-1][y] != rand && board[x-2][y] != rand) {
                        if (board[x][y-1] != rand && board[x][y-2] != rand) {
                            board[x][y] = rand; //fills board space with candy if it does not make a math with adjacent spaces
                            break;
                        }
                    }
                }
            }
        }
    }

    //method to draw canvas
    @Override
     protected void onDraw(Canvas c) {
        //super.onDraw(c);
        int width = getWidth();
        int height = getHeight();
        int row_height = height / row_num;
        int col_width = width / col_num;
        Rect dst = new Rect();
        dst.set(0, 0, width, height); //sets size of rectangle
        if (restart_check != 1) {
            c.drawColor(Color.BLACK); //fills canvas background with color

            c.drawBitmap(background, null, dst, null); //puts rectangle into bitmap
            System.out.println("drew background");


            //Text for Moves
            String movestring = Integer.toString(move_count);
            TextPaint moves = new TextPaint(Color.GREEN);
            moves.setTextSize(60);
            moves.setTextAlign(Paint.Align.RIGHT);
            moves.setTypeface(Typeface.create("requiem", Typeface.BOLD));
            c.drawText("Moves: ", width / 3 * 3, 0, moves);
            c.drawText(movestring, width, 0, moves);

            //Text for scores
            String scorestring = Integer.toString(score_count);
            TextPaint score = new TextPaint(Color.GREEN);
            score.setTextSize(60);
            score.setTextAlign(Paint.Align.LEFT);
            score.setTypeface(Typeface.create("Balloon", Typeface.BOLD));
            c.drawText("Score: ", 23, 0, score);
            c.drawText(scorestring, width / 3, 0, score);

            //Restart button
            dst.set((width / 2 - (width / 3) + 50), (height / 2 + (height / 3) + 100), (width - (width / 5)), height - 50);
            c.drawBitmap(restart, null, dst, null);
        }
        if (move_count > 20 && score_count < 2000) {
            restart_check = 1;
            c.drawColor(Color.BLACK);
            dst.set(0, 0, width, height);
            c.drawBitmap(lose, null, dst, null);
            dst.set((width / 2 - (width / 3) + 50), (height / 2 + (height / 3) + 100), (width - (width / 5)), height - 50);
            c.drawBitmap(restart, null, dst, null);
        }
        if (score_count >= 2000 && possible()) {
            restart_check = 1;
            c.drawColor(Color.BLACK);
            dst.set(0, 0, width, height);
            c.drawBitmap(win, null, dst, null);
            dst.set((width / 2 - (width / 3) + 50), (height / 2 + (height / 3) + 100), (width - (width / 5)), height - 50);
            c.drawBitmap(restart, null, dst, null);
        }
        if (!possible() && score_count < 2000) {
            restart_check = 1;
            c.drawColor(Color.BLACK);
            dst.set(0, 0, width, height);
            c.drawBitmap(lose, null, dst, null);
            dst.set((width / 2 - (width / 3) + 50), (height / 2 + (height / 3) + 100), (width - (width / 5)), height - 50);
            c.drawBitmap(restart, null, dst, null);
        }

        //set starting moves
        if (start) {
            start_moves();
        }

        if (fillrandom) {
            random_fill();
            fillrandom = false;
            //check = true;
            invalidate();
        }

        if (shift) {
            shift_down();
            shift = false;
            fillrandom = true;
            invalidate();
        }
        if(restart_check != 1) {
        //draws candies
            for (int x = 0; x < row_num - 3; x++) {
                for (int y = 0; y < col_num; y++) {
                    int rand = board[x][y];
                    dst.set(y * col_width, (x + 1) * row_height, (y + 1) * col_width, (x + 2) * row_height);
                    if (rand == 0)
                        c.drawBitmap(blue, null, dst, null);
                    else if (rand == 1)
                        c.drawBitmap(red, null, dst, null);
                    else if (rand == 2)
                        c.drawBitmap(green, null, dst, null);
                    else if (rand == 3)
                        c.drawBitmap(yellow, null, dst, null);
                    else if (rand == 4)
                        c.drawBitmap(orange, null, dst, null);
                    else if (rand == 5)
                        c.drawBitmap(purple, null, dst, null);
                }
            }
            System.out.println("drew candies");

        //fills space with background if space in destroy activated
            for (int x = 0; x < row_num - 3; x++) {
                for (int y = 0; y < col_num; y++) {
                    dst.set(y * col_width, (x + 1) * row_height, (y + 1) * col_width, (x + 2) * row_height);
                    if (destroy[x][y] == 10) {
                        c.drawBitmap(background, null, dst, null);
                    //destroy[x][y] = 0;
                    }
                }
            }
        }
        check_destroy();
    }


    //method to create surface
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setWillNotDraw(false);
        System.out.println("surfaceCreated");
    }

    //needs these 2 empty functions b/c abstract in parent
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int width = getWidth();
        int height = getHeight();
        int row_height = height / row_num;
        int col_width = width / col_num;
        boolean legal, legal2 = false;

            //if motion is press down, gets starting x,y coords of point
            if (e.getAction() == (MotionEvent.ACTION_DOWN)) {
                x1 = e.getX();
                y1 = e.getY();
                if(x1 >= (width / 2 - (width / 3) + 50) && y1 >= (height / 2 + (height / 3) + 100) && x1 <= (width - (width / 5))&& y1 <= height - 50){
                    for (int x = 0; x < row_num-3; x++) {
                        for (int y = 0; y < col_num; y++) {
                            while(true) {
                                double random = Math.random()*6;
                                int rand = (int)random;

                                if (x == 0 || x == 1) {
                                    if (y == 0 || y == 1) {
                                        board[x][y] = rand;
                                        break;
                                    }
                                    else if (board[x][y-1] != rand && board[x][y-2] != rand) {
                                        board[x][y] = rand;
                                        break;
                                    }
                                }

                                else if (y == 0 || y == 1) {
                                    if (x == 0 || x == 1) {
                                        board[x][y] = rand;
                                        break;
                                    }
                                    else if (board[x-1][y] != rand && board[x-2][y] != rand) {
                                        board[x][y] = rand;
                                        break;
                                    }
                                }

                                else if (board[x-1][y] != rand && board[x-2][y] != rand) {
                                    if (board[x][y-1] != rand && board[x][y-2] != rand) {
                                        board[x][y] = rand; //fills board space with candy if it does not make a math with adjacent spaces
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    move_count = 0;
                    score_count = 0;
                    start = true;
                    invalidate();

                }
                if(x1 >= (width / 2 - (width / 3) + 50) && y1 >= (height / 2 + (height / 3) + 100) && x1 <= (width - (width / 5))&& y1 <= height - 50 && restart_check == 1){
                    for (int x = 0; x < row_num-3; x++) {
                        for (int y = 0; y < col_num; y++) {
                            while(true) {
                                double random = Math.random()*6;
                                int rand = (int)random;

                                if (x == 0 || x == 1) {
                                    if (y == 0 || y == 1) {
                                        board[x][y] = rand;
                                        break;
                                    }
                                    else if (board[x][y-1] != rand && board[x][y-2] != rand) {
                                        board[x][y] = rand;
                                        break;
                                    }
                                }

                                else if (y == 0 || y == 1) {
                                    if (x == 0 || x == 1) {
                                        board[x][y] = rand;
                                        break;
                                    }
                                    else if (board[x-1][y] != rand && board[x-2][y] != rand) {
                                        board[x][y] = rand;
                                        break;
                                    }
                                }

                                else if (board[x-1][y] != rand && board[x-2][y] != rand) {
                                    if (board[x][y-1] != rand && board[x][y-2] != rand) {
                                        board[x][y] = rand; //fills board space with candy if it does not make a math with adjacent spaces
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    restart_check = 0;
                    move_count = 0;
                    score_count = 0;
                    start = true;
                    invalidate();
                }
                //System.out.println("("+x1+","+y1+")");
                start_row = (int) (y1 / row_height)-1;
                start_col = (int) x1 / col_width;
                //System.out.println("("+start_row+","+start_col+")");
            }

            //if motion is lift up, gets ending x,y coords and switches with start coords if legal
            else if (e.getAction() == (MotionEvent.ACTION_UP)) {
                this.x2 = e.getX();
                this.y2 = e.getY();
                //System.out.println("("+x2+","+y2+")");
                end_row = (int) (y2 / row_height)-1;
                end_col = (int) x2 / col_width;
                //System.out.println("("+end_row+","+end_col+")");

                if (start_row > 9 || start_col > 9 || end_row > 9 || end_col > 9 || start_row < 0 || start_col < 0 || end_row < 0 || end_col < 0)
                    return true;

                if (start_row == end_row) {
                    if ((start_col - end_col) == 1) {
                        legal = legal_move(start_row, start_col, end_row, end_col);
                        if (!legal)
                            legal2 = legal_move(end_row, end_col, start_row, start_col);
                        if (legal || legal2) {
                            this.move_count += 1;
                            check_destroy();
                            invalidate();
                        }
                    }
                    else if ((start_col - end_col) == -1) {
                        legal = legal_move(start_row, start_col, end_row, end_col);
                        if (!legal)
                            legal2 = legal_move(end_row, end_col, start_row, start_col);
                        if (legal || legal2) {
                            this.move_count += 1;
                            check_destroy();
                            invalidate();
                        }
                    }
                }

                else if (start_col == end_col) {
                    if ((start_row - end_row) == 1) {
                        legal = legal_move(start_row, start_col, end_row, end_col);
                        if (!legal)
                            legal2 = legal_move(end_row, end_col, start_row, start_col);
                        if (legal || legal2) {
                            this.move_count += 1;
                            check_destroy();
                            invalidate();
                        }
                    }
                    else if ((start_row - end_row) == -1) {
                        legal = legal_move(start_row, start_col, end_row, end_col);
                        if (!legal)
                            legal2 = legal_move(end_row, end_col, start_row, start_col);
                        if (legal || legal2) {
                            this.move_count += 1;
                            check_destroy();
                            invalidate();
                        }
                    }
                }

            }
            return true;
    }

    //checks if move makes a legal match
    public boolean legal_move(int startR, int startC, int endR, int endC) {
        int temp = board[startR][startC];
        System.out.println("checking move legality: "+temp);

        if (endR+2 <= 8) {
            if (board[endR + 1][endC] == temp && board[endR + 2][endC] == temp) {
                board[startR][startC] = board[endR][endC];
                board[endR][endC] = temp;
                return true;
            }
        }
        if (endR-2 >= 0) {
            if (board[endR - 1][endC] == temp && board[endR - 2][endC] == temp) {
                board[startR][startC] = board[endR][endC];
                board[endR][endC] = temp;
                return true;
            }
        }
        if (endR+1 <= 8 && endR-1 >= 0) {
            if (board[endR + 1][endC] == temp && board[endR - 1][endC] == temp) {
                board[startR][startC] = board[endR][endC];
                board[endR][endC] = temp;
                return true;
            }
        }
        if (endC+2 <= 8) {
            if (board[endR][endC + 1] == temp && board[endR][endC + 2] == temp) {
                board[startR][startC] = board[endR][endC];
                board[endR][endC] = temp;
                return true;
            }
        }
        if (endC-2 >= 0) {
            if (board[endR][endC - 1] == temp && board[endR][endC - 2] == temp) {
                board[startR][startC] = board[endR][endC];
                board[endR][endC] = temp;
                return true;
            }
        }
        if (endC+1 <= 8 && endC-1 >= 0) {
            if (board[endR][endC + 1] == temp && board[endR][endC - 1] == temp) {
                board[startR][startC] = board[endR][endC];
                board[endR][endC] = temp;
                return true;
            }
        }

        System.out.println("illegal move");
        return false;
    }

    //checks entire board for matches and sets space = 10 in destroy array if part of match
    public void check_destroy() {
        int temp;
        check = false;

        for (int x = 0; x < row_num-3; x++) {
            for (int y = 0; y < col_num; y++) {
                temp = board[x][y];

                if (x > 6 && y <= 6) {
                    if (board[x][y+1] == temp && board[x][y+2] == temp) {
                        destroy[x][y] = 10;
                        destroy[x][y+1] = 10;
                        destroy[x][y+2] = 10;
                        shift = true;
                    }
                }

                else if (y > 6 && x <= 6) {
                    if (board[x+1][y] == temp && board[x+2][y] == temp) {
                        destroy[x][y] = 10;
                        destroy[x+1][y] = 10;
                        destroy[x+2][y] = 10;
                        shift = true;
                    }
                }
                else if (x <= 6 && y <= 6) {
                    if (board[x][y+1] == temp && board[x][y+2] == temp) {
                        destroy[x][y] = 10;
                        destroy[x][y+1] = 10;
                        destroy[x][y+2] = 10;
                        shift = true;
                    }
                    if (board[x+1][y] == temp && board[x+2][y] == temp) {
                        destroy[x][y] = 10;
                        destroy[x+1][y] = 10;
                        destroy[x+2][y] = 10;
                        shift = true;
                    }
                }
            }
        }
    }

    //loop stuck in this function
    //keeps printing "3" so stuck when x=3 (loops for 2 and 1 sometimes)
    public void shift_down () {
        for (int x = 8; x >= 0; x--) {          //iterates through board
            for (int y = 8; y >= 0; y--) {
                while (destroy[x][y] == 10) {   //while destroy space =10
                    //System.out.println(x);
                    if (x == 2) {
                        System.out.println("two"); //if row2, checks above spaces and breaks if blank
                        if (destroy[x - 1][y] == 10 && destroy[x - 2][y] == 10) {
                            System.out.println("two-in");
                            break;
                        }
                    }
                    else if (x == 1) {        //if row1, checks space above and breaks if blank
                        System.out.println("one");
                        if (destroy[x - 1][y] == 10) {
                            System.out.println("one-in");
                            break;
                        }
                    }
                    else if (x == 0) {        //if row0 and blank, breaks
                        System.out.println("zero");
                        break;
                    }
                    else {                       //if row 3-8, iterates up column and shifts down one
                        for (int i = x; i >= 0; i--) {
                            System.out.println(i);
                            if (i - 1 < 0) {    //if row0, sets that space blank
                                System.out.println("3-8");
                                destroy[i][y] = 10;
                                //board[i][y] = 10;
                            }
                            else {
                                System.out.println("else 3-8");//else shifts space down 1
                                //board[i][y] = board[i - 1][y];
                                destroy[i][y] = destroy[i - 1][y];
                                //invalidate();
                            }

                        }
                    }
                    break;
                }
            }
        }
    }

    //fills empty spaces at top with random candies
    public void random_fill() {
        for (int x = 0; x < row_num-3; x++) {
            for (int y = 0; y < col_num; y++) {
                if (destroy[x][y] == 10) {
                    score_count += 10;
                    System.out.println("bitch is filling");
                    double random = Math.random()*5;
                    int rand = (int) random;
                    board[x][y] = rand;
                    destroy[x][y] = 0;          //resets destroy array when filling spaces
                }
            }
        }
    }
    //checks if any possible moves on the board left
    public boolean possible(){
        int startR, startC, endR, endC;
        boolean possible = false;

        for (startR = 0; startR < row_num-3; startR++) {
            for (startC = 0; startC < col_num; startC++) {
                endC = startC;
                endR = startR;
                //top left corner
                if(startR == 0 && startC == 0){
                    if (!possible) possible = legal_possible(startR,startC,endR,endC+1);
                    if (!possible) possible = legal_possible(startR,startC,endR+1,endC);
                    if (possible) return possible;
                }
                //upper right corner
                if(startR == 0 && startC == 8){
                    if (!possible) possible = legal_possible(startR,startC,endR,endC-1);
                    if (!possible) possible = legal_possible(startR,startC,endR+1,endC);
                    if (possible) return possible;
                }
                //lower left corner
                if(startR == 8 && startC == 0){
                    if (!possible) possible = legal_possible(startR,startC,endR,endC+1);
                    if (!possible) possible = legal_possible(startR,startC,endR-1,endC);
                    if (possible) return possible;
                }
                //lower right corner
                if(startR == 8 && startC == 8){
                    if (!possible) possible = legal_possible(startR,startC,endR,endC-1);
                    if (!possible) possible = legal_possible(startR,startC,endR-1,endC);
                    if (possible) return possible;
                }
                //top row in between left and right corner
                if(startR == 0 && startC < 8 && startC > 0){
                    if (!possible) possible = legal_possible(startR,startC,endR,endC+1);
                    if (!possible) possible = legal_possible(startR,startC,endR+1,endC);
                    if (!possible) possible = legal_possible(startR,startC,endR,endC-1);
                    if (possible) return possible;
                }
                //first column left side in between top and bottom
                if(startR > 0 && startR < 8 && startC == 0){
                    if (!possible) possible = legal_possible(startR,startC,endR-1,endC);
                    if (!possible) possible = legal_possible(startR,startC,endR,endC+1);
                    if (!possible) possible = legal_possible(startR,startC,endR+1,endC);
                    if (possible) return possible;
                }
                //last column right side in between top and bottom
                if(startR > 0 && startR < 8 && startC == 8){
                    if (!possible) possible = legal_possible(startR,startC,endR-1,endC);
                    if (!possible) possible = legal_possible(startR,startC,endR,endC-1);
                    if (!possible) possible = legal_possible(startR,startC,endR+1,endC);
                    if (possible) return possible;
                }
                //last row columns in between left and right sides
                if(startR == 8 && startC < 8 && startC > 0){
                    if (!possible) possible = legal_possible(startR,startC,endR,endC-1);
                    if (!possible) possible = legal_possible(startR,startC,endR-1,endC);
                    if (!possible) possible = legal_possible(startR,startC,endR,endC+1);
                    if (possible) return possible;
                }
                //everything move possible in the body |.....| this is to visualize
                //                                     |.....|
                //                                     |.....|
                if(startR > 0 && startC > 0 && startR < 8 && startC < 8){
                    if (!possible) possible = legal_possible(startR,startC,endR-1,endC);
                    if (!possible) possible = legal_possible(startR,startC,endR,endC-1);
                    if (!possible) possible = legal_possible(startR,startC,endR,endC+1);
                    if (!possible) possible = legal_possible(startR,startC,endR+1,endC);
                    if (possible) return possible;
                }
            }
        }
        return possible;
    }
    //guarantee possible moves on start
    public void start_moves() {
        double random = Math.random()*4;
        int rand = (int) random;
        if (rand == 0) {
            board[5][4] = 1;
            board[5][5] = 2;
            board[5][6] = 2;
            board[4][7] = 2;
            board[5][7] = 3;

            board[3][0] = 5;
            board[3][1] = 1;
            board[4][2] = 1;
            board[3][3] = 1;
            board[3][2] = 3;
            board[3][4] = 2;

            board[0][8] = 4;
            board[1][8] = 5;
            board[2][8] = 5;
            board[3][7] = 5;
            board[4][8] = 5;
            board[3][8] = 0;
            board[5][8] = 1;
        }
        else if (rand == 1) {
            board[1][0] = 3;
            board[1][1] = 2;
            board[2][2] = 2;
            board[1][3] = 2;
            board[1][2] = 5;
            board[1][4] = 4;

            board[4][8] = 1;
            board[5][8] = 0;
            board[6][8] = 0;
            board[7][7] = 0;
            board[7][8] = 2;

            board[3][2] = 4;
            board[3][3] = 3;
            board[3][4] = 3;
            board[4][5] = 3;
            board[2][5] = 3;
            board[3][5] = 1;
        }
        else if (rand == 2) {
            board[5][4] = 0;
            board[5][5] = 1;
            board[5][6] = 1;
            board[4][7] = 1;
            board[5][7] = 5;

            board[0][3] = 3;
            board[1][3] = 4;
            board[2][3] = 4;
            board[3][2] = 4;
            board[4][3] = 4;
            board[5][3] = 4;
            board[6][3] = 2;
            board[3][3] = 3;
        }
        else if (rand == 3) {
            board[7][0] = 1;
            board[7][1] = 5;
            board[6][2] = 5;
            board[7][3] = 5;
            board[7][2] = 2;
            board[7][4] = 1;

            board[0][0] = 3;
            board[1][0] = 0;
            board[2][0] = 0;
            board[4][0] = 0;
            board[3][1] = 0;
            board[3][2] = 0;
            board[3][0] = 4;
            board[3][3] = 2;
        }
        start = false;
    }
    public boolean legal_possible(int startR, int startC, int endR, int endC) {
        int temp = board[startR][startC];
        System.out.println("checking move legality: "+temp);

        if (endR+2 <= 8) {
            if (board[endR + 1][endC] == temp && board[endR + 2][endC] == temp) {
                return true;
            }
        }
        if (endR-2 >= 0) {
            if (board[endR - 1][endC] == temp && board[endR - 2][endC] == temp) {
                return true;
            }
        }
        if (endR+1 <= 8 && endR-1 >= 0) {
            if (board[endR + 1][endC] == temp && board[endR - 1][endC] == temp) {
                return true;
            }
        }
        if (endC+2 <= 8) {
            if (board[endR][endC + 1] == temp && board[endR][endC + 2] == temp) {
                return true;
            }
        }
        if (endC-2 >= 0) {
            if (board[endR][endC - 1] == temp && board[endR][endC - 2] == temp) {
                return true;
            }
        }
        if (endC+1 <= 8 && endC-1 >= 0) {
            if (board[endR][endC + 1] == temp && board[endR][endC - 1] == temp) {
                return true;
            }
        }

        System.out.println("illegal move");
        return false;
    }
}


