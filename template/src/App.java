import GrammarLexer;
import com.glasiem.main.GrammarParser;
import com.glasiem.main.ThrowingErrorListener;
import com.glasiem.main.VisitorClass;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

public class App extends JFrame
{
    private DefaultTableModel tableModel;
    private JTable table1;
    private JButton calculate;
    private JButton close;
    private JButton window1;
    private JButton window2;
    private Object[][] otherSide = new String[][] {
            {"a", "b", "c", "d", "e"},
            {"a", "b", "c", "d", "e"},
            {"a", "b", "c", "d", "e"},
            {"a", "b", "c", "d", "e"},
            {"a", "b", "c", "d", "e"}
    };

    public String link(String expression, HashSet<String> set) throws Exception {
        char c;
        String temp = "";
        int i = 0;
        do{
            c = expression.charAt(i);
            if (c == '#') {
                String cell = "#" + expression.charAt(i + 1) + expression.charAt(i + 2);
                if (!set.contains(cell)) {
                    set.add(cell);
                    int row = Character.getNumericValue(expression.charAt(i + 2)) - 1;
                    int column = (expression.charAt(i + 1) - 'A');
                    String toEvaluate = link((String) tableModel.getValueAt(row, column), set);
                    temp += evaluate(toEvaluate);
                    i += 3;
                    set.remove(cell);
                }
                else
                {
                    temp = "ERROR";
                    return temp;
                }
            }
            else
            {
                temp += c;
                i++;
            }
        }
        while(i < expression.length());
        return temp;
    }

    public static double evaluate(String expression) {
        GrammarLexer lexer = new GrammarLexer(new ANTLRInputStream(expression));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ThrowingErrorListener());
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        GrammarParser parser = new GrammarParser(tokenStream);
        ParseTree tree = parser.expression();
        VisitorClass visitor = new VisitorClass();
        return visitor.visit(tree);
    }


    public App() {
        tableModel = new DefaultTableModel(5,5);
        for (int i = 0; i <  5; i++) {
            for (int j = 0; j < 5; j++) {
                tableModel.setValueAt(String.valueOf(i+j),i,j);
            }
        }
        close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        calculate = new JButton("Show results");
        calculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String temp = "";
                for (int i = 0; i <  5; i++) {
                    for (int j = 0; j < 5; j++) {
                        if (table1.isEnabled())
                        {
                            try {
                                String s;
                                HashSet<String> set = new HashSet<>();
                                s = String.valueOf('A' + j) + i;
                                set.add(s);
                                temp = String.valueOf(tableModel.getValueAt(i,j));
                                temp = link(temp, set);
                                set.remove(s);
                                otherSide[i][j] = String.valueOf(evaluate(temp));
                            }
                            catch (Exception ex){
                                otherSide[i][j] = "ERROR";
                            }
                        }
                        temp = String.valueOf(tableModel.getValueAt(i,j));
                        tableModel.setValueAt(otherSide[i][j],i,j);
                        otherSide[i][j] = temp;
                    }
                }
                if (table1.isEnabled()) {
                    calculate.setText("Show formulae");
                }
                else {
                    calculate.setText("Show results");
                }
                table1.setEnabled(!table1.isEnabled());
                temp = JOptionPane.showInputDialog("Hello");
                JOptionPane.showMessageDialog(null,temp);
            }
        });
        window1 = new JButton("Menu");
        window1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window2 = new JButton("Table");
                window2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Box contents = new Box(BoxLayout.Y_AXIS);
                        contents.add(new JScrollPane(table1));
                        contents.add(calculate);
                        contents.add(window1);
                        contents.add(close);
                        setContentPane(contents);
                        setSize(500, 400);
                        setVisible(true);
                    }
                });
                Box contents1 = new Box(BoxLayout.Y_AXIS);
                contents1.add(window2);
                contents1.add(close);
                setContentPane(contents1);
                setSize(500, 400);
                setVisible(true);
            }
        });
        table1 = new JTable(tableModel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Box contents = new Box(BoxLayout.Y_AXIS);
        contents.add(new JScrollPane(table1));
        contents.add(calculate);
        contents.add(window1);
        contents.add(close);
        setContentPane(contents);
        setSize(500, 400);
        setVisible(true);
    }
    public static void main(String[] args) {
        new App();
    }
}