
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.DefaultHighlighter;
import javax.swing .*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.Font;

class TextPad extends JFrame implements ActionListener
{
  private JEditorPane editor  = new JEditorPane();
  JRadioButtonMenuItem colorRadioButton[], fontRadioButton[];
  Font font = new Font( "Times New Roman", Font.PLAIN, 14  );
  File path = new File("");

  UIManager.LookAndFeelInfo look[];

  String fontName[] = { "Monospaced", "Times New Roman", "SansSerif", "Serif" };
  String fontStyle[] = { "Regular", "Bold", "Italic", "Bold Italic" };
  String fontSize[] = { " 10 ", " 12 ", " 14 ", " 16 ", " 18 ", " 20 ", " 22 ", " 24 ",
                        " 26 ", " 28 ", "30"};
  String selectText="";

  int fonts[] = { Font.BOLD, Font.ITALIC };


  public TextPad()
  {
    super( "TextPad  " );

    //for windows look and feel
    look = UIManager.getInstalledLookAndFeels();
    try{
      UIManager.setLookAndFeel( look[2].getClassName() );
      SwingUtilities.updateComponentTreeUI (this );
    }catch( Exception e ){ }

    Container container = getContentPane();

    editor.setFont(font);
    editor.setSelectedTextColor(Color.red);
    container.add( new JScrollPane( editor ), BorderLayout.CENTER );

    JMenuBar bar = new JMenuBar();
    setJMenuBar( bar );

    JMenu fileMenu = new JMenu( "File" );
    fileMenu.setMnemonic( 'F' );
    fileMenu.add( call( "New" ));
    fileMenu.add( call( "Open" ));
    fileMenu.add( call( "Save" ));
    fileMenu.add( call( "Save as..." ));
    fileMenu.add( call( "Exit" ));
    bar.add( fileMenu );

    JMenu editMenu = new JMenu( "Edit" );
    editMenu.add( call( "Cut"));
    editMenu.add( call( "Copy"));
    editMenu.add( call( "Paste"));
    editMenu.add( call( "Delete"));
    editMenu.addSeparator();
    editMenu.add( call( "Find"));
    editMenu.add( call( "Replace"));
    bar.add( editMenu );

    JMenu formatMenu = new JMenu( "Format");
    JMenu colorMenu = new JMenu( "Color" );
    JMenu fontMenu = new JMenu( "Font" );

    String colorName[] = { "Red", "Green", "Blue", "Yellow", "Pink" };
    colorRadioButton = new JRadioButtonMenuItem[ colorName.length ];
    for( int i=0; i< 5 ; i++)
    {
        colorRadioButton[ i ] = new JRadioButtonMenuItem( colorName[ i ]);
        colorMenu.add( colorRadioButton[ i ]);
    }

     formatMenu.add( colorMenu );
     formatMenu.add( call( "Font" ) );
     bar.add( formatMenu );

     JMenu helpMenu = new JMenu( "Help");
     bar.add( helpMenu );
     helpMenu.add( call( "About..."));



    setSize( 500,350 );
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

  }

  public void actionPerformed( ActionEvent e )
  {
    String command = e.getActionCommand();

    if( command.equals( "New" ) )newFile();
    else if( command.equals( "Open" ) )openFile();
    else if( command.equals( "Save" ) )saveFile();
    else if(command.equals("Save as..."))saveasFile();
    else if(command.equals("Exit"))
      System.exit(0);

    else if( command.equals( "Cut"))cutText();
    else if( command.equals( "Copy"))copyText();
    else if( command.equals( "Paste"))pasteText();
    else if( command.equals( "Find"))findText();
    else if( command.equals( "Replace"))replaceText();
    else if( command.equals( "About..."))showAbout();
    else if( command.equals( "Font"))
    {
        new FontPicker();
    }

  }

  public void newFile()
  {
      writeFile( path, editor.getText());
      path= new File("");
      editor.setText( "");
      System.out.print( ""+path);

  }

  public void openFile()
  {
    JFileChooser chooser =  new JFileChooser();
    int result = chooser.showOpenDialog( this );
    if ( result == JFileChooser.CANCEL_OPTION ) return;
    try
    {
      File file = chooser.getSelectedFile();
      path = file;
   //   java.net.URL url = file.toURL();
     // editor.setFont( new Font(  "Times New ROman", Font.BOLD, 20  ));
   //   editor.setPage( url );
      //editor.setT

      StringBuffer fileBuffer= new StringBuffer();
      String fileString = "", line ;

      BufferedReader br = new BufferedReader( new FileReader( file));

      while(( line = br.readLine()) !=null)
      {
          fileBuffer.append(  line+ "\n");
      }
      br.close();
      editor.setText(  fileBuffer.toString());
      System.out.print( "" + path);
    }
    catch(Exception e )
    {
      editor.setText( "" +e );
    }
  }


  public void saveFile()
  {
    if( path.equals(new File("")))
        selectLocation();
    else
        writeFile( path, editor.getText());
  }


  public void saveasFile()
  {
      selectLocation();

  }

  public void selectLocation()
  {
      JFileChooser chooser = new JFileChooser();
      int result = chooser.showSaveDialog(this);

      if (result != JFileChooser.APPROVE_OPTION) {
        return;
      }

      File file = chooser.getSelectedFile();
      path = file;
      if( file.exists())
      {
          int response = JOptionPane.showConfirmDialog(null, "Overwrite exiting data ",
                  "Confirm Overwrite", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

          if( response==JOptionPane.CANCEL_OPTION)
              return ;
      }

      writeFile( file, editor.getText());

  }


  public void writeFile( File f, String st)
  {

      try
      {
          PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter( f)));
          out.print(st);
          out.flush();
          out.close();
      }catch( IOException ex){}
  }

  public void copyText()
  {
      editor.copy();
    //  System.out.print( "Selected text  "+ editor.getSelectedText());

  }

  public void pasteText()
  {
      editor.paste();
  }

  public void cutText()
  {
      editor.cut();
  }

  public void findText()
  {
      String st = JOptionPane.showInputDialog(null);
      String editorText = editor.getText();

      Highlighter.HighlightPainter painter = new Highlight( Color.yellow);

      Highlighter hilite = editor.getHighlighter();
      hilite.removeAllHighlights();

      int pos=0;

      //System.out.print( editorText);

      if( st.length()>0)
      {
          StringTokenizer token = new StringTokenizer( editorText, " ");
          int count = token.countTokens();
          for( int j=0; j<editorText.length(); j++ )
          {
              try{
                  pos = editorText.indexOf(st, pos);
                  hilite.addHighlight(pos, pos+st.length(), painter);
                  pos += st.length();
              }catch( BadLocationException e){}
          }
      }


  }
  public void replaceText()
  {
      String st = "cancel";
      System.out.print( "Hello" + editor.getSelectedText()+ "Android ");
      st=JOptionPane.showInputDialog(null);
      if( !st.equals( "cancel" ) || !editor.getSelectedText().equals("null") )
          editor.replaceSelection(st);
      return;
  }

  public void showAbout()
  {
       JOptionPane.showMessageDialog( TextPad.this, "Md. Imdad Sarkar\nSecond Year\n2006-07 ",
                                            "Information", JOptionPane.PLAIN_MESSAGE);

  }

  private JMenuItem call( String name )
  {
    JMenuItem item = new JMenuItem( name );
    item.addActionListener( this );
    return item;
  }


  class FontPicker extends JFrame implements ActionListener
  {
      private JList fontList, fontStyleList, sizeList;
      private JButton btnOk, btnCancel;

      public FontPicker()
      {
          setLayout( new FlowLayout());

          fontList = new JList( fontName );
          fontStyleList = new JList( fontStyle );
          sizeList = new JList( fontSize );

          fontList.setVisibleRowCount( 5 );
          fontStyleList.setVisibleRowCount( 5 );
          sizeList.setVisibleRowCount( 5 );

          add( new JLabel( "Font        "));
          add( new JLabel( "            Style      "));
          add( new JLabel( "     Size  "));

          add( new JScrollPane( fontList ));
          add( new JScrollPane( fontStyleList ) );
          add( new JScrollPane( sizeList ) );


          btnOk = new JButton( "Ok" );
        //  btnCancel = new JButton( "Cancel");

          btnOk.addActionListener( new ActionListener()
          {
              public void actionPerformed( ActionEvent e)
              {
                  String name="";
                  int type=0, size=10;

                  if( !(fontList.isSelectionEmpty()))
                      name = fontList.getSelectedValue().toString();
                  if( !(fontStyleList.isSelectionEmpty()))
                      type=fontStyleList.getSelectedIndex();
                  if( !(sizeList.isSelectionEmpty()))
                      size=( 10+(sizeList.getSelectedIndex()*2 ) );

                  editor.setFont( new Font( name, type, size ) );


              }

          });

          add( btnOk );
         // add( btnCancel );



         setSize( 220, 210 );
         setVisible( true);
         setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
         setLocationRelativeTo( TextPad.this );
         setResizable( false );
      }

      public void actionPerformed( ActionEvent ev)
      {
          if( ev.getSource() == btnOk)
          {
              
          }
          
      }

    }

  class Highlight extends DefaultHighlighter.DefaultHighlightPainter
  {
      public Highlight( Color color)
      {
          super(color);
      }
  }
  public static void main( String args[] )
  {
    TextPad obj = new TextPad();
    obj.setVisible( true );
    obj.setSize( 600, 500);
    obj.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    obj.setLocation( 200, 100);

  }

}




