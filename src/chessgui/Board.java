package chessgui;

import chessgui.pieces.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;


@SuppressWarnings("serial")
public class Board extends JComponent {
        
    public int turnCounter = 0;
    private static Image NULL_IMAGE = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);

    private final int Square_Width = 65;
    public static ArrayList<Piece> White_Pieces;
    public static ArrayList<Piece> Black_Pieces;
    

    public ArrayList<DrawingShape> Static_Shapes;
    public ArrayList<DrawingShape> Piece_Graphics;

    public Piece Active_Piece;

    private final int rows = 8;
    private final int cols = 8;
    private Integer[][] BoardGrid;
    private String board_file_path = "lib/board.png";
    private String active_square_file_path = "lib/active_square.png";

    public void initGrid()
    {
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                BoardGrid[i][j] = 0;
            }
        }

 

        White_Pieces.add(new King(3,7,true,"King.png",this, false));
        White_Pieces.add(new Queen(4,7,true,"Queen.png",this, false));
        White_Pieces.add(new Bishop(2,7,true,"Bishop.png",this, false));
        White_Pieces.add(new Bishop(5,7,true,"Bishop.png",this, false));
        White_Pieces.add(new Knight(1,7,true,"Knight.png",this, false));
        White_Pieces.add(new Knight(6,7,true,"Knight.png",this, false));
        White_Pieces.add(new Rook(0,7,true,"Rook.png",this, false));
        White_Pieces.add(new Rook(7,7,true,"Rook.png",this, false));
        White_Pieces.add(new Pawn(0,6,true,"Pawn.png",this, false));
        White_Pieces.add(new Pawn(1,6,true,"Pawn.png",this, false));
        White_Pieces.add(new Pawn(2,6,true,"Pawn.png",this, false));
        White_Pieces.add(new Pawn(3,6,true,"Pawn.png",this, false));
        White_Pieces.add(new Pawn(4,6,true,"Pawn.png",this, false));
        White_Pieces.add(new Pawn(5,6,true,"Pawn.png",this, false));
        White_Pieces.add(new Pawn(6,6,true,"Pawn.png",this, false));
        White_Pieces.add(new Pawn(7,6,true,"Pawn.png",this, false));

        Black_Pieces.add(new King(3,0,false,"King.png",this, false));
        Black_Pieces.add(new Queen(4,0,false,"Queen.png",this, false));
        Black_Pieces.add(new Bishop(2,0,false,"Bishop.png",this, false));
        Black_Pieces.add(new Bishop(5,0,false,"Bishop.png",this, false));
        Black_Pieces.add(new Knight(1,0,false,"Knight.png",this, false));
        Black_Pieces.add(new Knight(6,0,false,"Knight.png",this, false));
        Black_Pieces.add(new Rook(0,0,false,"Rook.png",this, false));
        Black_Pieces.add(new Rook(7,0,false,"Rook.png",this, false));
        Black_Pieces.add(new Pawn(0,1,false,"Pawn.png",this, false));
        Black_Pieces.add(new Pawn(1,1,false,"Pawn.png",this, false));
        Black_Pieces.add(new Pawn(2,1,false,"Pawn.png",this, false));
        Black_Pieces.add(new Pawn(3,1,false,"Pawn.png",this, false));
        Black_Pieces.add(new Pawn(4,1,false,"Pawn.png",this, false));
        Black_Pieces.add(new Pawn(5,1,false,"Pawn.png",this, false));
        Black_Pieces.add(new Pawn(6,1,false,"Pawn.png",this, false));
        Black_Pieces.add(new Pawn(7,1,false,"Pawn.png",this, false));

    }

    public Board() {

        BoardGrid = new Integer[rows][cols];
        Static_Shapes = new ArrayList();
        Piece_Graphics = new ArrayList();
        White_Pieces = new ArrayList();
        Black_Pieces = new ArrayList();

        initGrid();

        this.setBackground(new Color(0,0,0));
        this.setPreferredSize(new Dimension(600, 520));
        this.setMinimumSize(new Dimension(100, 100));
        this.setMaximumSize(new Dimension(1000, 1000));

        // add save game button
        JButton saveButton = new JButton("<html><center>"+"Save"+"<br>"+"Game"+"</center></html>");
        this.add(saveButton);
        saveButton.setBounds(520, 0, 80, 80);
        // add events for button 
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // save game
                try {
                    saveGame(turnCounter);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // add load game button
        JButton loadButton = new JButton();
        loadButton.setText("<html><center>"+"Load"+"<br>"+"Game"+"</center></html>");
        this.add(loadButton);
        loadButton.setBounds(520, 80, 80, 80);
        // add events for button
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // load game
                try {
                    loadGame();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });



        // add mouse listener for mouse clicks
        this.addMouseListener(mouseAdapter);
        this.addComponentListener(componentAdapter);
        this.addKeyListener(keyAdapter);


        
        this.setVisible(true);
        this.requestFocus();
        drawBoard();
    }

    private void saveGame(int turnCounter) throws IOException {
        // save game
        String fileName = "save.txt";
        FileWriter fileWriter = new FileWriter(fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        // save turn
        bufferedWriter.write("9 " + turnCounter + " " + "null" + " " + "null");
        bufferedWriter.newLine();

        // loop through all pieces and save their positions
        for (int i = 0; i < White_Pieces.size(); i++) {
            bufferedWriter.write(White_Pieces.get(i).getX() + " " + White_Pieces.get(i).getY() + " White " + White_Pieces.get(i).getFilePath());
            bufferedWriter.newLine();
        }
        for (int i = 0; i < Black_Pieces.size(); i++) {
            bufferedWriter.write(Black_Pieces.get(i).getX() + " " + Black_Pieces.get(i).getY() + " Black " + Black_Pieces.get(i).getFilePath());
            bufferedWriter.newLine();
        }
        bufferedWriter.close();        
        
        JOptionPane.showMessageDialog(null, "Game Saved!");
    }

    private void loadGame() throws IOException {
        // load game
        String fileName = "save.txt";
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;

        // clear all pieces
        White_Pieces.clear();
        Black_Pieces.clear();

        // loop through all lines and add pieces
        while ((line = bufferedReader.readLine()) != null) {
            String[] parts = line.split(" ");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            boolean isWhite = parts[2].equals("White");
            String filePath = parts[3];
            if (x == 9) {
                turnCounter = y;
                continue;
            }
            if (filePath.equals("King.png")) {
                if (isWhite) {
                    White_Pieces.add(new King(x, y, isWhite, filePath, this, false));
                } else {
                    Black_Pieces.add(new King(x, y, isWhite, filePath, this, false));
                }
            } else if (filePath.equals("Queen.png")) {
                if (isWhite) {
                    White_Pieces.add(new Queen(x, y, isWhite, filePath, this, false));
                } else {
                    Black_Pieces.add(new Queen(x, y, isWhite, filePath, this, false));
                }
            } else if (filePath.equals("Bishop.png")) {
                if (isWhite) {
                    White_Pieces.add(new Bishop(x, y, isWhite, filePath, this, false));
                } else {
                    Black_Pieces.add(new Bishop(x, y, isWhite, filePath, this, false));
                }
            } else if (filePath.equals("Knight.png")) {
                if (isWhite) {
                    White_Pieces.add(new Knight(x, y, isWhite, filePath, this, false));
                } else {
                    Black_Pieces.add(new Knight(x, y, isWhite, filePath, this, false));
                }
            } else if (filePath.equals("Rook.png")) {
                if (isWhite) {
                    White_Pieces.add(new Rook(x, y, isWhite, filePath, this, false));
                } else {
                    Black_Pieces.add(new Rook(x, y, isWhite, filePath, this, false));
                }
            } else if (filePath.equals("Pawn.png")) {
                if (isWhite) {
                    White_Pieces.add(new Pawn(x, y, isWhite, filePath, this, false));
                } else {
                    Black_Pieces.add(new Pawn(x, y, isWhite, filePath, this, false));
                }
            }
        }
        bufferedReader.close();

        // save changes by drawing the board
        drawBoard();
    }


    private void drawBoard()
    {
        Piece_Graphics.clear();
        Static_Shapes.clear();
        //initGrid();
        
        Image board = loadImage(board_file_path);
        Static_Shapes.add(new DrawingImage(board, new Rectangle2D.Double(0, 0, board.getWidth(null), board.getHeight(null))));
        if (Active_Piece != null)
        {
            Image active_square = loadImage(active_square_file_path);
            Static_Shapes.add(new DrawingImage(active_square, new Rectangle2D.Double(Square_Width*Active_Piece.getX(),Square_Width*Active_Piece.getY(), active_square.getWidth(null), active_square.getHeight(null))));
        }
        for (int i = 0; i < White_Pieces.size(); i++)
        {
            int COL = White_Pieces.get(i).getX();
            int ROW = White_Pieces.get(i).getY();
            Image piece = loadImage("lib/white_pieces/" + White_Pieces.get(i).getFilePath());  
            Piece_Graphics.add(new DrawingImage(piece, new Rectangle2D.Double(Square_Width*COL,Square_Width*ROW, piece.getWidth(null), piece.getHeight(null))));
        }
        for (int i = 0; i < Black_Pieces.size(); i++)
        {
            int COL = Black_Pieces.get(i).getX();
            int ROW = Black_Pieces.get(i).getY();
            Image piece = loadImage("lib/black_pieces/" + Black_Pieces.get(i).getFilePath());  
            Piece_Graphics.add(new DrawingImage(piece, new Rectangle2D.Double(Square_Width*COL,Square_Width*ROW, piece.getWidth(null), piece.getHeight(null))));
        }
        this.repaint();
    }

    
    public static Piece getPiece(int x, int y) {
        for (Piece p : White_Pieces)
        {
            if (p.getX() == x && p.getY() == y)
            {
                return p;
            }
        }
        for (Piece p : Black_Pieces)
        {
            if (p.getX() == x && p.getY() == y)
            {
                return p;
            }
        }
        return null;
    }

    private MouseAdapter mouseAdapter = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {

                
        }



        @Override
        public void mousePressed(MouseEvent e) {
            int d_X = e.getX();
            int d_Y = e.getY();  
            int Clicked_Row = d_Y / Square_Width;
            int Clicked_Column = d_X / Square_Width;
            boolean is_whites_turn = true;
            if (turnCounter%2 == 1)
            {
                is_whites_turn = false;
            }

            
            Piece clicked_piece = getPiece(Clicked_Column, Clicked_Row);
            
            
            // logic for moving pieces
            if (Active_Piece == null && clicked_piece != null && 
            ((is_whites_turn && clicked_piece.isWhite()) || (!is_whites_turn && clicked_piece.isBlack())))
            {
                Active_Piece = clicked_piece;
            }
            else if (Active_Piece != null && Active_Piece.getX() == Clicked_Column && Active_Piece.getY() == Clicked_Row)
            {
                Active_Piece = null;
            }
            else if (Active_Piece != null && Active_Piece.canMove(Clicked_Column, Clicked_Row) 
            && ((is_whites_turn && Active_Piece.isWhite()) || (!is_whites_turn && Active_Piece.isBlack())))
            {
                // if piece is there, remove it so we can be there
                if (clicked_piece != null)
                {
                    if (clicked_piece.isWhite())
                    {
                        White_Pieces.remove(clicked_piece);
                        if (clicked_piece.getClass().equals(King.class))
                        {
                            Active_Piece.setX(Clicked_Column); // let piece take the king's spot
                            Active_Piece.setY(Clicked_Row);
                            drawBoard();
                            JOptionPane.showMessageDialog(null, "Black wins!"); // tell user black wins
                            System.exit(0);
                        }
                        
                    }
                    else
                    {
                        Black_Pieces.remove(clicked_piece);
                        if (clicked_piece.getClass().equals(King.class))
                        {
                            Active_Piece.setX(Clicked_Column); // let piece take the king's spot
                            Active_Piece.setY(Clicked_Row);
                            drawBoard();
                            JOptionPane.showMessageDialog(null, "White wins!"); // tell user white wins
                            System.exit(0);
                        }
                    }
                }
                
                // save previous position
                int prevX = Active_Piece.getX();
                int prevY = Active_Piece.getY();
                if (clicked_piece != null)
                {
                    prevX = clicked_piece.getX();
                    prevY = clicked_piece.getY();
                }
                
                // do move logic
                Active_Piece.setX(Clicked_Column);
                Active_Piece.setY(Clicked_Row);
                
                // if king is still in check move back
                if (Active_Piece.getClass().equals(King.class))
                {
                    King piece = (King) Active_Piece;
                    if (King.isKingInCheck(piece.isWhite(), piece.getX(), piece.getY()))
                    {
                        Active_Piece.setX(prevX);
                        Active_Piece.setY(prevY);
                        Active_Piece = null;
                        return;
                    }
                }


                // check for pawn promotion
                if (Active_Piece.getClass().equals(Pawn.class))
                {
                    if (Active_Piece.isWhite() && Active_Piece.getY() == 0)
                    {
                        White_Pieces.remove(Active_Piece);
                        White_Pieces.add(new Queen(Active_Piece.getX(), Active_Piece.getY(), true, "Queen.png", Board.this, false));
                        Active_Piece = White_Pieces.get(White_Pieces.size()-1);
                    }
                    else if (!Active_Piece.isWhite() && Active_Piece.getY() == 7)
                    {
                        Black_Pieces.remove(Active_Piece);
                        Black_Pieces.add(new Queen(Active_Piece.getX(), Active_Piece.getY(), false, "Queen.png", Board.this, false));
                        Active_Piece = Black_Pieces.get(Black_Pieces.size()-1);
                    }
                }
                
                // check for castling
/*
check active piece is king or rook (good to castle)
check clicked piece is king or rook (good to castle)
check if same color (good to castle)

check if either has moved (bad to castle)
check if pieces are between (bad to castle)
check if king is in check (bad to castle)
check if king will move through check (bad to castle)
check if king will move into check (bad to castle)
*/
                
                Active_Piece = null;
                turnCounter++;
            }
            
            drawBoard();
        }

        @Override
        public void mouseDragged(MouseEvent e) {		
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) 
        {
        }	

        
    };

    private void adjustShapePositions(double dx, double dy) {

        Static_Shapes.get(0).adjustPosition(dx, dy);
        this.repaint();

    } 
        
        
      
    private Image loadImage(String imageFile) {
        try {
                return ImageIO.read(new File(imageFile));
        }
        catch (IOException e) {
                return NULL_IMAGE;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        drawBackground(g2);
        drawShapes(g2);
    }

    private void drawBackground(Graphics2D g2) {
        g2.setColor(getBackground());
        g2.fillRect(0,  0, getWidth(), getHeight());
    }
       

    private void drawShapes(Graphics2D g2) {
        for (DrawingShape shape : Static_Shapes) {
            shape.draw(g2);
        }	
        for (DrawingShape shape : Piece_Graphics) {
            shape.draw(g2);
        }
    }

    private ComponentAdapter componentAdapter = new ComponentAdapter() {

        @Override
        public void componentHidden(ComponentEvent e) {

        }

        @Override
        public void componentMoved(ComponentEvent e) {

        }

        @Override
        public void componentResized(ComponentEvent e) {

        }

        @Override
        public void componentShown(ComponentEvent e) {

        }	
    };

    private KeyAdapter keyAdapter = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

        @Override
        public void keyTyped(KeyEvent e) {

        }	
    };

    // return all pieces
    public static ArrayList<Piece> getPieces() {
        ArrayList<Piece> all_pieces = new ArrayList();
        all_pieces.addAll(White_Pieces);
        all_pieces.addAll(Black_Pieces);
        return all_pieces;
    }

    public static boolean isKingInCheck(int king_x, int king_y, boolean white) {
        // check if each piece can move to the king's position
        for (Piece p : White_Pieces)
        {
            if (p.isWhite() != white && p.canMove(king_x, king_y))
            {
                System.out.println("King in check");
                return true;
            }
        }
        for (Piece p : Black_Pieces)
        {
            if (p.isWhite() != white && p.canMove(king_x, king_y))
            {
                System.out.println("King in check");
                return true;
            }
        }

        //temp
        return false;
    }

    public static boolean isKingLegalMove(int destination_x, int destination_y, boolean white) {
        // check if each piece can move to the king's destination
        for (Piece p : White_Pieces)
        {
            if (p.isWhite() != white && p.canMove(destination_x, destination_y))
            {
                return false;
            }
        }
        for (Piece p : Black_Pieces)
        {
            if (p.isWhite() != white && p.canMove(destination_x, destination_y))
            {
                return false;
            }
        }
        //temp
        return true;
    }
 









interface DrawingShape {
    boolean contains(Graphics2D g2, double x, double y);
    void adjustPosition(double dx, double dy);
    void draw(Graphics2D g2);
    }


class DrawingImage implements DrawingShape {

    public Image image;
    public Rectangle2D rect;

    public DrawingImage(Image image, Rectangle2D rect) {
            this.image = image;
            this.rect = rect;
    }

    @Override
    public boolean contains(Graphics2D g2, double x, double y) {
            return rect.contains(x, y);
    }

    @Override
    public void adjustPosition(double dx, double dy) {
            rect.setRect(rect.getX() + dx, rect.getY() + dy, rect.getWidth(), rect.getHeight());	
    }

    @Override
    public void draw(Graphics2D g2) {
            Rectangle2D bounds = rect.getBounds2D();
            g2.drawImage(image, (int)bounds.getMinX(), (int)bounds.getMinY(), (int)bounds.getMaxX(), (int)bounds.getMaxY(),
                                            0, 0, image.getWidth(null), image.getHeight(null), null);
    }	
}
}
