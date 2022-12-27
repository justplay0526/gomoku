package com.example.gomoku;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    //declare a int for board size
    final static int maxN = 15;
    private Context context;
    //declare for imageView (Cells) array
    private final ImageView[][] ivCell = new ImageView[maxN][maxN];

    private final Drawable[] drawCell = new Drawable[4];//0 is empty, 1 is player, 2 is bot, 3 is background
    private Button btnPlay;
    private TextView tvTurn;

    private final int[][] valueCell = new int[maxN][maxN];///0 is empty,1 is player,2 is bot
    private int winner_play;//who is winner? 0 is none, 1 is player, 2 is bot
    private boolean firstMove;
    private int xMove, yMove;//x and y axis of cell => define position of cell
    private int turnPlay;// whose turn?
    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,MyService2.class);
            startService(intent);
            finish();
        });

        context = this;
        setListen();
        loadResources();
        designBoardGame();
    }

    private void setListen() {
        btnPlay = findViewById(R.id.btnPlay);
        tvTurn = findViewById(R.id.tvTurn);

        btnPlay.setText(getString(R.string.playgame));
        tvTurn.setText(getString(R.string.pressbuttonplaygame));

        btnPlay.setOnClickListener(v -> {
            init_game();
            play_game();
        });
    }

    private void play_game() {
        /*
        1. we need define who play first
         */
        Random r = new Random();
        turnPlay = r.nextInt(2) + 1;//r.nextint(2) return value in [0,1]

        if (turnPlay == 1) {//player play first
            //inform => make a toast
            Toast.makeText(context, getString(R.string.Playerplayfirst), Toast.LENGTH_SHORT).show();//dont forget show(); :D
            playerTurn();
        } else {//bot turn
            Toast.makeText(context, getString(R.string.Botplayfirst), Toast.LENGTH_SHORT).show();//dont forget show(); :D
            botTurn();
        }
    }

    private void botTurn() {
        Log.d("tuanh","bot turn");
        tvTurn.setText(getString(R.string.Bot));
        //if this is first move bot always choose center cell (7,7)
        if (firstMove) {
            firstMove = false;
            xMove = 7;
            yMove = 7;
            make_a_move();
        } else {
            //try to find best xMove,yMove
            findBotMove();
            make_a_move();
        }
    }


    private final int[] iRow={-1,-1,-1,0,1,1,1,0};
    private final int[] iCol={-1,0,1,1,1,0,-1,-1};
    private void findBotMove() {
        List<Integer> listX = new ArrayList<>();
        List<Integer> listY= new ArrayList<>();
        //find empty cell can move, and we we only move two cell in range 2
        final int range=2;
        for(int i=0;i<maxN;i++){
            for(int j=0;j<maxN;j++)
                if(valueCell[i][j]!=0){//not empty
                    for(int t=1;t<=range;t++){
                        for(int k=0;k<8;k++){
                            int x=i+iRow[k]*t;
                            int y=j+iCol[k]*t;
                            if(inBoard(x,y) && valueCell[x][y]==0){
                                listX.add(x);
                                listY.add(y);
                            }
                        }
                    }
                }
        }
        int lx=listX.get(0);
        int ly=listY.get(0);
        //bot always find min board_position_value
        int res= Integer.MAX_VALUE-10;
        for(int i=0;i<listX.size();i++){
            int x=listX.get(i);
            int y=listY.get(i);
            valueCell[x][y]=2;
            int rr=getValue_Position();
            if(rr<res){
                res=rr;lx=x;ly=y;
            }
            valueCell[x][y]=0;
        }
        xMove=lx;yMove=ly;
    }

    private int getValue_Position() {
        //this function will find the board_position_value
        int rr=0;
        int pl=turnPlay;
        //row
        for(int i=0;i<maxN;i++){
            rr+=CheckValue(maxN-1,i,-1,0,pl);
        }
        //column
        for(int i=0;i<maxN;i++){
            rr+=CheckValue(i, maxN - 1, 0, -1, pl);
        }
        //cross right to left
        for(int i=maxN-1;i>=0;i--){
            rr+=CheckValue(i,maxN-1,-1,-1,pl);
        }
        for(int i=maxN-2;i>=0;i--){
            rr+=CheckValue(maxN-1,i,-1,-1,pl);
        }
        //cross left to right
        for(int i=maxN-1;i>=0;i--){
            rr+=CheckValue(i,0,-1,1,pl);
        }
        for(int i=maxN-1;i>=1;i--){
            rr+=CheckValue(maxN-1,i,-1,1,pl);
        }
        return rr;
    }

    private int CheckValue(int xd, int yd, int vx, int vy, int pl) {
        //comback with check value
        int i,j;
        int rr=0;
        i=xd;j=yd;
        String st=String.valueOf(valueCell[i][j]);
        while(true){
            i+=vx;j+=vy;
            if(inBoard(i,j)){
                st=st+ valueCell[i][j];
                if(st.length()==6){
                    rr+=Eval(st,pl);
                    st=st.substring(1,6);
                }
            } else break;
        }
        return rr;

    }

    private void make_a_move() {
        Log.d("tuanh","make a move with "+xMove+";"+yMove+";"+turnPlay);
        ivCell[xMove][yMove].setImageDrawable(drawCell[turnPlay]);
        valueCell[xMove][yMove] = turnPlay;
        //check if anyone win
        //aw we forget 1 thing :D change the value of valuaCell
        //if no empty cell exist => draw
        if (noEmptycell()) {
            Toast.makeText(context, getString(R.string.Draw), Toast.LENGTH_SHORT).show();
            return;
        } else if (CheckWinner()) {
            if (winner_play == 1) {
                Toast.makeText(context, getString(R.string.WinnerisPlayer), Toast.LENGTH_SHORT).show();//
                tvTurn.setText(getString(R.string.WinnerisPlayer));
            } else {
                Toast.makeText(context, getString(R.string.WinnerisBot), Toast.LENGTH_SHORT).show();//
                tvTurn.setText(getString(R.string.WinnerisBot));
            }
            return;
        }

        if (turnPlay == 1) {//player
            turnPlay = (1 + 2) - turnPlay;
            botTurn();
        } else {//bot
            turnPlay = 3 - turnPlay;
            playerTurn();
        }
    }

    private boolean CheckWinner() {
        //we only need to check the recent move xMove,yMove can create 5 cells in a row or not
        if(winner_play!=0) return true;
        //check in row =( i forget that :D
        VectorEnd(xMove,0,0,1,xMove,yMove);
        //check column
        VectorEnd(0,yMove,1,0,xMove,yMove);
        //check left to right
        if(xMove+yMove>=maxN-1){
            VectorEnd(maxN-1,xMove+yMove-maxN+1,-1,1,xMove,yMove);
        } else{
            VectorEnd(xMove+yMove,0,-1,1,xMove,yMove);
        }
        //check right to left
        if(xMove<=yMove){
            VectorEnd(xMove-yMove+maxN-1,maxN-1,-1,-1,xMove,yMove);
        }else{
            VectorEnd(maxN-1,maxN-1-(xMove-yMove),-1,-1,xMove,yMove);
        }
        if(winner_play!=0) return true; else return false;
    }

    private void VectorEnd(int xx, int yy, int vx, int vy, int rx, int ry) {
        //this void will check the row base on vector(vx,vy) in range (rx,ry)-4*(vx,vy) -> (rx,ry)+4*(vx,vy)
        //ok i will explain this :) hope you understand :D if not yet feel free to comment below i will help you
        if(winner_play!=0) return;
        final int range=4;
        int i,j;
        int xbelow=rx-range*vx;
        int ybelow=ry-range*vy;
        int xabove=rx+range*vx;
        int yabove=ry+range*vy;
        String st="";
        i=xx;j=yy;
        while(!inside(i,xbelow,xabove)||!inside(j,ybelow,yabove)){
            i+=vx;j+=vy;
        }
        while(true){
            st=st+ valueCell[i][j];
            if(st.length()==5){
                EvalEnd(st);
                st=st.substring(1,5);//substring of st from index 1->5;=> delete first character
            }
            i+=vx;j+=vy;
            if(!inBoard(i,j) || !inside(i,xbelow,xabove)|| !inside(j,ybelow,yabove) || winner_play!=0){
                break;
            }
        }
    }

    private boolean inBoard(int i, int j) {
        //check i,j in board or not
        if(i<0||i>maxN-1||j<0||j>maxN-1) return false;
        return true;
    }

    private void EvalEnd(String st) {
        switch (st){
            case "11111": winner_play=1;break;
            case "22222": winner_play=2;break;
            default:break;
        }
    }

    private boolean inside(int i, int xbelow, int xabove) {//this check i in [xbelow,xabove] or not
        return (i-xbelow)*(i-xabove)<=0;
    }

    private boolean noEmptycell() {
        for(int i=0;i<maxN;i++){
            for(int j=0;j<maxN;j++)
                if(valueCell[i][j]==0) return false;
        }
        return true;
    }

    private void playerTurn() {
        Log.d("tuanh","player turn");
        tvTurn.setText(getString(R.string.Player));
        firstMove=false;
        isClicked = false;
        /// we get xMove,yMove of player by the way listen click on cell so turn listen on
    }

    private void init_game() {
        //this void will create UI before game start
        //for game control we need some variables
        firstMove = true;
        winner_play = 0;
        for (int i = 0; i < maxN; i++) {
            for (int j = 0; j < maxN; j++) {
                ivCell[i][j].setImageDrawable(drawCell[0]);//default or Empty cell
                valueCell[i][j] = 0;
            }
        }
        /////////////////////////////////above is init for game
    }

    private void loadResources() {
        drawCell[3] = context.getResources().getDrawable(R.drawable.cell_bg);//background
        //copy 2 image for 2 drawable player and bot
        //edit it :D
        drawCell[0] = null;//empty cell
        drawCell[1] = context.getResources().getDrawable(R.drawable.black_chess);//drawable for player
        drawCell[2] = context.getResources().getDrawable(R.drawable.white_chess);//for bot
    }


    private boolean isClicked;//track player click cell or not => make sure that player only click to 1 cell

    @SuppressLint("NewApi")
    private void designBoardGame() {
        //create layoutparams to optimize size of cell
        // we create a horizotal linearlayout for a row
        // which contains maxN imageView in
        //need to find out size of cell first

        int sizeofCell = Math.round(ScreenWidth() / maxN);
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(sizeofCell * maxN, sizeofCell);
        LinearLayout.LayoutParams lpCell = new LinearLayout.LayoutParams(sizeofCell, sizeofCell);

        LinearLayout linBoardGame = findViewById(R.id.linBoardGame);

        //create cells
        for (int i = 0; i < maxN; i++) {
            LinearLayout linRow = new LinearLayout(context);
            //make a row
            for (int j = 0; j < maxN; j++) {
                ivCell[i][j] = new ImageView(context);
                //make a cell
                //need to set background default for cell
                //cell has 3 status, empty(defautl),player,bot
                ivCell[i][j].setBackground(drawCell[3]);
                final int x = i;
                final int y = j;
                //make that for safe and clear
                ivCell[i][j].setOnClickListener(v -> {
                    if (valueCell[x][y] == 0) {//empty cell
                        if (turnPlay == 1 || !isClicked) {//turn of player
                            Log.d("tuanh","click to cell ");
                            isClicked = true;
                            xMove = x;
                            yMove = y;//i,j must be final variable
                            make_a_move();
                        }
                    }
                });
                linRow.addView(ivCell[i][j], lpCell);
            }
            linBoardGame.addView(linRow, lpRow);
        }
    }

    private float ScreenWidth() {
        Resources resources = context.getResources();//ok
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    //////////////funtion evaluate
    private int Eval(String st, int pl) {
        //this function is put score for 6 cells in a row
        //pl is player turn => you will get a bonus point if it's your turn
        //I will show you and explain how i can make it and what it mean in part improve bot move
        int b1, b2;
        if (pl == 1) {
            b1 = 2;
            b2 = 1;
        } else {
            b1 = 1;
            b2 = 2;
        }
        switch (st) {
            case "111110":
            case "011111":
            case "211111":
            case "111112":
                return b1* 100000000;
            case "011110":
                return b1* 10000000;
            case "101110":
            case "011101":
                return b1* 1002;
            case "011112":
                return b1* 1000;
            case "011100":
            case "001110":
                return b1* 102;
            case "210111":
            case "211110":
            case "211011":
            case "211101":
                return b1* 100;
            case "010100":
            case "011000":
            case "001100":
            case "000110":
                return b1* 10;
            case "211000":
            case "201100":
            case "200110":
            case "200011":
                return b1;
            case "222220":
            case "022222":
            case "122222":
            case "222221":
                return b2* -100000000;
            case "022220":
                return b2* -10000000;
            case "202220":
            case "022202":
                return b2* -1002;
            case "022221":
                return b2* -1000;
            case "022200":
            case "002220":
                return b2* -102;
            case "120222":
            case "122220":
            case "122022":
            case "122202":
                return b2* -100;
            case "020200":
            case "022000":
            case "002200":
            case "000220":
                return b2* -10;
            case "122000":
            case "102200":
            case "100220":
            case "100022":
                return b2* -1;
            default:
                break;
        }
        return 0;
    }

}