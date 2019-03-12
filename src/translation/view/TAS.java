package translation.view;

import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import test.DBManager;
import translation.*;

public class TAS extends javax.swing.JFrame {

    private String translate_site_baidu = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=zh&dt=t&q=%s";

    private TAS() {
        initComponents();
        jTable_kv.setModel(dataModel);
        for (int i = 0; i < ColumnNames.length; i++) {
            if (i == 0) {
                TableColumn tc_id = jTable_kv.getColumn(ColumnNames[i]);
                tc_id.setPreferredWidth(50);
            } else if (i == 1) {
                TableColumn tc_key = jTable_kv.getColumn(ColumnNames[i]);
                tc_key.setPreferredWidth(170);
            } else if (i == 2) {
                TableColumn tc_value_origin = jTable_kv.getColumn(ColumnNames[i]);
                tc_value_origin.setPreferredWidth(300);
            } else if (i == 3) {
                TableColumn tc_value_new = jTable_kv.getColumn(ColumnNames[i]);
                tc_value_new.setPreferredWidth(300);
            }
        }
        jTextField_file.setText("D:/softwave/Android/resources_en.jar");
        jTable_kv.setShowVerticalLines(false);
        jTable_kv.setShowHorizontalLines(false);
        jTable_kv.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableColumnModel = jTable_kv.getColumnModel();
        jTable_kv.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = jTable_kv.rowAtPoint(e.getPoint());
                int col = jTable_kv.columnAtPoint(e.getPoint());
                if (col != 0) {
                    String ttt = jTable_kv.getValueAt(row, col).toString();
                    if (ttt.length() != 0) {
                        jTable_kv.setToolTipText(ttt);
                    }
                }
            }
            @Override
            public void mouseDragged(MouseEvent e) {
            }
        });
    }
    private void translate(List<String> list, int index_list) {

        try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault()) {
            httpclient.start();
            final int count = list.size();
            for (int i = 0; i < count; i++) {
                if (isTranslateAll && !jCheckBox1.isSelected()) {
                    isTranslateAll = false;
                    break;
                }
                int index = i;
                if (count == 1) {
                    index = index_list;
                }
                final int index_final = index;
                final String origin = list.get(i).toLowerCase();
                Logger.getLogger(origin);
                final CountDownLatch latch = new CountDownLatch(1);
                String to = language[jComboBox_language.getSelectedIndex()];
                String translate_site_baidu = "http://viphp.sinaapp.com/baidu/translate/translate.php?to=%s&origin=%s";

                final HttpGet request = new HttpGet(String.format(translate_site_baidu, to, URLEncoder.encode(origin, "UTF-8")));
                httpclient.execute(request, new FutureCallback<HttpResponse>() {
                    @Override
                    public void completed(final HttpResponse response2) {
                        try {
                            latch.countDown();
                            HttpEntity entity = response2.getEntity();
                            String jsonContent = EntityUtils.toString(entity, "UTF-8");
                            String newValue = (origin.length() > 6 ? (origin.substring(0, 6).toLowerCase().equals("<html>") ? origin.substring(0, 6) : "") : "") + jsonContent.replaceAll("[{] ", "{").replaceAll(" }", "}").replaceAll("\\\\n", "\n");
                            valueNewList.set(index_final, newValue);
                            jTable_kv.updateUI();
                            jButton_saveProperties.setEnabled(true);
                            jLabel1.setText((index_final + 1) + "/" + valueNewList.size());
                            jProgressBar1.setValue((index_final + 1));
                            jTable_kv.setRowSelectionInterval(index_final, index_final);
                            Rectangle rect = jTable_kv.getCellRect(index_final, 0, true);
                            jTable_kv.scrollRectToVisible(rect);
                        } catch (IOException | ParseException ignored) {
                        }
                    }
                    @Override
                    public void failed(final Exception ex) {
                        latch.countDown();
                    }
                    @Override
                    public void cancelled() {
                        latch.countDown();
                    }
                });
                latch.await();
            }
        } catch (InterruptedException | UnsupportedEncodingException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        jDialog1 = new javax.swing.JDialog();
        javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new JTA();
        jButton2 = new javax.swing.JButton();
        jDialog2 = new javax.swing.JDialog();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JTextField jTextField2 = new javax.swing.JTextField();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        javax.swing.JTextField jTextField3 = new javax.swing.JTextField();
        javax.swing.JButton jButton4 = new javax.swing.JButton();
        javax.swing.JButton jButton5 = new javax.swing.JButton();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem jMenuItem_translate = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMenuItem_reset = new javax.swing.JMenuItem();
        jDialog_ra = new javax.swing.JDialog();
        jTextField_newfile = new javax.swing.JTextField();
        javax.swing.JButton jButton_new = new javax.swing.JButton();
        jTextField_oldfile = new javax.swing.JTextField();
        javax.swing.JButton jButton_old = new javax.swing.JButton();
        javax.swing.JButton jButton_compare = new javax.swing.JButton();
        javax.swing.JScrollPane jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea_list = new JTextArea();
        jTextField_file = new javax.swing.JTextField();
        javax.swing.JButton jButton_fileview1 = new javax.swing.JButton();
        jButton_getJarContext = new javax.swing.JButton();
        jTextField_bak = new javax.swing.JTextField();
        javax.swing.JButton jButton_fileview2 = new javax.swing.JButton();
        jButton_saveProperties = new javax.swing.JButton();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        javax.swing.JScrollPane jScrollPane4 = new javax.swing.JScrollPane();
        jTable_kv = new JTable();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        javax.swing.JButton jButton3 = new javax.swing.JButton();
        javax.swing.JButton jButton_replaceAll = new javax.swing.JButton();
        javax.swing.JButton jButton_mysql = new javax.swing.JButton();
        jComboBox_language = new javax.swing.JComboBox<>();
        jDialog1.setAlwaysOnTop(true);
        jDialog1.setLocationByPlatform(true);
        jDialog1.setMinimumSize(new java.awt.Dimension(500, 400));
        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);
        jButton2.setText("修改");
        jButton2.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jButton2MouseClicked();
            }
        });
        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
                jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
                        .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
                        .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
        );
        jDialog1Layout.setVerticalGroup(
                jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
                        .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jDialog1Layout.createSequentialGroup()
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                                        .addGap(39, 39, 39)))
                        .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog1Layout.createSequentialGroup()
                                        .addGap(0, 264, Short.MAX_VALUE)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jDialog2.setAlwaysOnTop(true);
        jDialog2.setMinimumSize(new java.awt.Dimension(300, 120));
        jLabel2.setText("替换");
        jLabel3.setText("为");
        jButton4.setText("全部替换");
        jButton4.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jButton4MouseClicked();
            }
        });
        jButton5.setText("取消");
        jButton5.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jButton5MouseClicked();
            }
        });
        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
                jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jDialog2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jDialog2Layout.createSequentialGroup()
                                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel3))
                                        .addComponent(jButton4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jDialog2Layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(jButton5))
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialog2Layout.setVerticalGroup(
                jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jDialog2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3)
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton4)
                                        .addComponent(jButton5))
                                .addContainerGap())
        );

        jMenuItem_translate.setText("翻译");
        jMenuItem_translate.addActionListener(evt -> jMenuItem_translateActionPerformed());
        jPopupMenu1.add(jMenuItem_translate);

        jMenuItem_reset.setText("重置");
        jMenuItem_reset.addActionListener(evt -> jMenuItem_resetActionPerformed());
        jPopupMenu1.add(jMenuItem_reset);

        jDialog_ra.setTitle("比较新旧版");
        jDialog_ra.setMinimumSize(new java.awt.Dimension(500, 300));

        jTextField_newfile.setColumns(30);
        jTextField_newfile.setText("G:\\as汉化\\resources_en20140221.jar");

        jButton_new.setText("新");
        jButton_new.addActionListener(evt -> jButton_newActionPerformed());

        jTextField_oldfile.setColumns(30);
        jTextField_oldfile.setText("G:\\as汉化\\old\\AndroidStudio源文件备份.jar");

        jButton_old.setText("旧");
        jButton_old.addActionListener(evt -> jButton_oldActionPerformed());

        jButton_compare.setText("比  较");
        jButton_compare.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jButton_compareMouseClicked();
            }
        });

        bar_compare = jScrollPane5.getVerticalScrollBar();

        jTextArea_list.setColumns(20);
        jTextArea_list.setEditable(false);
        jTextArea_list.setRows(5);
        jScrollPane5.setViewportView(jTextArea_list);

        javax.swing.GroupLayout jDialog_raLayout = new javax.swing.GroupLayout(jDialog_ra.getContentPane());
        jDialog_ra.getContentPane().setLayout(jDialog_raLayout);
        jDialog_raLayout.setHorizontalGroup(
                jDialog_raLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jDialog_raLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jDialog_raLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane5)
                                        .addGroup(jDialog_raLayout.createSequentialGroup()
                                                .addGroup(jDialog_raLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jDialog_raLayout.createSequentialGroup()
                                                                .addComponent(jTextField_newfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jButton_new))
                                                        .addGroup(jDialog_raLayout.createSequentialGroup()
                                                                .addComponent(jTextField_oldfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jButton_old)))
                                                .addGap(18, 18, 18)
                                                .addComponent(jButton_compare, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        jDialog_raLayout.setVerticalGroup(
                jDialog_raLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jDialog_raLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jDialog_raLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jButton_compare, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jDialog_raLayout.createSequentialGroup()
                                                .addGroup(jDialog_raLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jTextField_newfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jButton_new))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jDialog_raLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jTextField_oldfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jButton_old))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("翻译属性文件工具");

        jTextField_file.setToolTipText("原始属性文件");
        new DropTarget(jTextField_file, DnDConstants.ACTION_COPY_OR_MOVE, new MyDropTargetListener(this));

        jButton_fileview1.setText("浏览");
        jButton_fileview1.addActionListener(evt -> jButton_fileview1ActionPerformed());

        jButton_getJarContext.setText("获取jar包目录");
        jButton_getJarContext.addActionListener(evt -> jButton_getJarContextActionPerformed());

        jTextField_bak.setToolTipText("保存属性文件");
        jTextField_bak.setVisible(false);

        jButton_fileview2.setText("浏览");
        jButton_fileview2.setVisible(false);
        jButton_fileview2.addActionListener(evt -> jButton_fileview2ActionPerformed());

        jButton_saveProperties.setText("保存属性值文件");
        jButton_saveProperties.setEnabled(false);
        jButton_saveProperties.addActionListener(evt -> jButton_savePropertiesActionPerformed());

        listmousedoubleclicklistener();
        jScrollPane2.setViewportView(jList1);
        jList1.addListSelectionListener(e -> jTextField1.setText(jList1.getSelectedValue().toString()));

        jTable_kv.setCellSelectionEnabled(true);
        jTable_kv.setSelectionBackground(new java.awt.Color(0, 49, 255));
        jTable_kv.setShowHorizontalLines(false);
        jTable_kv.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent evt) {
                jTable_kvMouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(jTable_kv);

        jTextField1.setText("");

        jButton1.setText("查看");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        jButton1.setVisible(false);

        jCheckBox1.setText("翻译");
        jCheckBox1.addActionListener(evt -> jCheckBox1ActionPerformed());

        jButton3.setText("替换");
        jButton3.setVisible(false);
        jButton3.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jButton3MouseClicked();
            }
        });

        jButton_replaceAll.setText("比较新旧版本");
        jButton_replaceAll.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jButton_replaceAllMouseClicked();
            }
        });

        jButton_mysql.setText("写入Mysql");
        jButton_mysql.setVisible(false);
        jButton_mysql.addActionListener(evt -> jButton_mysqlActionPerformed());

        jComboBox_language.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"中文", "繁体中文", "英语", "粤语", "文言文", "日语", "韩语", "法语", "西班牙语", "泰语", "阿拉伯语", "俄语", "葡萄牙语", "德语", "意大利语", "希腊语", "荷兰语", "波兰语", "保加利亚语", "爱沙尼亚语", "丹麦语", "芬兰语", "捷克语", "罗马尼亚语", "斯洛文尼亚语", "瑞典语", "匈牙利语", "越南语"}));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane4)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(jTextField_file, javax.swing.GroupLayout.DEFAULT_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jTextField_bak, javax.swing.GroupLayout.DEFAULT_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(jButton_fileview2)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                .addComponent(jButton_mysql))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(jButton_fileview1)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jButton_getJarContext)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jLabel1)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jButton1)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jComboBox_language, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jButton_saveProperties, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(jButton_replaceAll)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jButton3))))
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jTextField_file, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jButton_fileview1)
                                                .addComponent(jButton_getJarContext)
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jButton1))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jCheckBox1)
                                                .addComponent(jComboBox_language, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jTextField_bak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jButton_fileview2))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jButton3)
                                                .addComponent(jButton_replaceAll)
                                                .addComponent(jButton_mysql)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                                .addGap(8, 8, 8)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel1))
                                        .addComponent(jButton_saveProperties))
                                .addContainerGap())
        );

        pack();
    }

    private void jButton_fileview1ActionPerformed() {
        JFileChooser jfc = new JFileChooser();
        jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setDialogTitle("请选择原始属性文件");
        if (jTextField_file.getText() != null) {
            jfc.setCurrentDirectory(new File(jTextField_file.getText()).getParentFile());
        }
        int result = jfc.showDialog(null, null);
        if (result == 0) {
            File f = jfc.getSelectedFile();
            String file = f.getAbsolutePath();
            jTextField_file.setText(file);
        }
    }

    private void jButton_fileview2ActionPerformed() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setDialogTitle("请选择原始属性文件");
        if (jTextField_file.getText() != null) {
            jfc.setCurrentDirectory(new File(jTextField_file.getText()).getParentFile());
        }
        int result = jfc.showDialog(null, null);
        if (result == 0) {
            File f1 = jfc.getSelectedFile();
            String file = f1.getAbsolutePath();
            jTextField_bak.setText(file);
        }
    }

    private void jButton_getJarContextActionPerformed() {
        saveTime = 0;
        jList1.setModel(new javax.swing.AbstractListModel() {

            String[] strings = PropertiesControl.getJarFileContent(new File(jTextField_file.getText()));

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        jList1.updateUI();
        jButton1.setVisible(true);
        jList1.setSelectedIndex(0);
    }

    private void jButton_savePropertiesActionPerformed() {

        InputStream is = null, is1 = null;
        OutputStream fos = null;
        File tmpFile;
        try {

            File jarFilePath = new File(jTextField_file.getText());
            tmpFile = File.createTempFile("tmp", ".properties");
            Properties prop = new Properties();
            is = new FileInputStream(tmpFile);
            prop.load(is);
            fos = new FileOutputStream(tmpFile);
            int count = keyList.size();
            for (int i = 0; i < count; i++) {
                prop.put(keyList.get(i), valueNewList.get(i));
            }
            prop.store(fos, "Update:" + chooseConfigPath);
            is1 = new FileInputStream(tmpFile);
            if (saveTime == 0) {
                pc2.write2JarFile(jarFilePath, "temp" + jarFilePath.getName(), chooseConfigPath, pc2.inputStream2byteArray(is1));
            } else {
                pc2.write2JarFile(new File(jarFilePath.getParent(), "temp" + jarFilePath.getName()), null, chooseConfigPath, pc2.inputStream2byteArray(is1), 2);
            }
            saveTime++;
            jButton_saveProperties.setEnabled(false);

        } catch (IOException ignored) {
        } finally {
            try {
                assert is != null;
                is.close();
                assert is1 != null;
                is1.close();
                assert fos != null;
                fos.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void jButton1MouseClicked(MouseEvent evt) {
        System.out.println(jTextField1.getText());
        if (jTextField1.getText() == null) {
            jTextField1.getText();
        }
        jDialog1.show();
        jButton2.setText("\u4fee\u6539");
        jTextArea1.setEditable(false);
        jDialog1.setTitle("预览属性文件（不可编辑）");
        final MouseEvent e = evt;
        new Thread(() -> {
            chooseConfigPath = jTextField1.getText();
            JarFile jarFile;
            try {
                jarFile = new JarFile(jTextField_file.getText());
                int index = jList1.locationToIndex(e.getPoint());

                ZipEntry entry = jarFile.getEntry(chooseConfigPath);
                if (entry == null) {
                    System.out.println(chooseConfigPath + "路径所代表的文件不存在!读取失败~");
                }

                assert entry != null;
                InputStream is = jarFile.getInputStream(entry);
                byte[] bytes = PropertiesControl.inputStream2byteArray(is);
                String[] values = PropertiesControl.getBytesValue(bytes);
                StringBuilder sb = new StringBuilder();
                for (String value : values) {
                    sb.append(WordsTransfer.UnicodeToGBK(value)).append(newline);
                }
                jDialog1.setTitle("预览属性文件（不可编辑）    " + values.length + "行");
                jTextArea1.setText(sb.toString());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }).start();
    }

    private void jButton2MouseClicked() {
        if (jTextArea1.isEditable()) {
            String[] kv = "\n".split(jTextArea1.getText());
            StringBuilder sb = new StringBuilder();
            File jarFilePath = new File(jTextField_file.getText());
            for (int i = 0; i < kv.length; i++) {
                if (i == 0) {
                    sb.append(WordsTransfer.utf8ToUnicode(kv[i]));
                } else {
                    sb.append(newline).append(WordsTransfer.utf8ToUnicode(kv[i]));
                }
            }
            PropertiesControl.write2JarFile(jarFilePath, chooseConfigPath, sb.toString().replace("{ ", "{").replace(" }", "}").getBytes());
            jDialog1.hide();
            jTextField_file.setText(jTextField_file.getText().substring(0, jTextField_file.getText().length() - 4) + "_temp.jar");
            jButton_getJarContext.doClick();
            jList1.setSelectedIndex(index);
            jButton2.setText("修                             改");
            jTextArea1.setEditable(false);
        } else {
            jButton2.setText("保                             存");
            jTextArea1.setEditable(true);
            jDialog1.setTitle(jDialog1.getTitle().replace("不可编辑", "可编辑"));
        }
    }

    private void jButton3MouseClicked() {
        jDialog2.show();
    }

    private void jButton4MouseClicked() {
        jDialog2.hide();
    }

    private void jButton5MouseClicked() {
        jDialog2.hide();
    }

    private void jCheckBox1ActionPerformed() {
    }

    private void jMenuItem_translateActionPerformed() {
        List<String> list = new ArrayList<>();
        list.clear();
        list.add(valueList.get(rightClickIndex));
        translate(list, rightClickIndex);
    }

    private void jTable_kvMouseReleased(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON3) {
            rightClickIndex = jTable_kv.rowAtPoint(evt.getPoint());
            jPopupMenu1.show(jTable_kv, evt.getX(), evt.getY());
        }
    }

    private void jButton_replaceAllMouseClicked() {
        jDialog_ra.show();
    }

    private void jButton_newActionPerformed() {
        if (!isReplaceStart) {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setDialogTitle("请选择新版属性文件");
            if (null != jTextField_newfile.getText()) {
                jfc.setCurrentDirectory(new File(jTextField_newfile.getText()).getParentFile());
            }
            int result = jfc.showDialog(null, null);
            if (result == 0) {
                File fnew = jfc.getSelectedFile();
                String file = fnew.getAbsolutePath();
                jTextField_newfile.setText(file);
                newFiles = PropertiesControl.getJarFileContent(new File(jTextField_newfile.getText()));
                System.out.println("newFiles count : " + newFiles.length);

            }
        } else {
            show("提示");
        }
    }

    private void jButton_oldActionPerformed() {
        if (!isReplaceStart) {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setDialogTitle("请选择旧版属性文件");
            if (jTextField_oldfile.getText() != null) {
                jfc.setCurrentDirectory(new File(jTextField_oldfile.getText()).getParentFile());
            }
            int result = jfc.showDialog(null, null);
            if (result == 0) {
                File fold = jfc.getSelectedFile();
                String file = fold.getAbsolutePath();
                jTextField_oldfile.setText(file);
                oldFiles = PropertiesControl.getJarFileContent(new File(jTextField_oldfile.getText()));
                System.out.println("oldFiles count : " + oldFiles.length);
            }
        } else {
            show("提示");
        }
    }

    private void jButton_compareMouseClicked() {
        if (!isReplaceStart) {
            isReplaceStart = true;
            new Thread(() -> {
                JarFile newjarFile, oldjarFile;
                try {
                    newjarFile = new JarFile(jTextField_newfile.getText());
                    oldjarFile = new JarFile(jTextField_oldfile.getText());
                    for (int i = 0; i < newFiles.length; i++) {
                        jTextArea_list.setText(jTextArea_list.getText().replaceAll("正在比对中->", ""));
                        System.out.println(i + "---->" + newFiles[i]);
                        jTextArea_list.append(newline + "正在比对中->" + newFiles[i]);
                        ZipEntry entrynew = newjarFile.getEntry(newFiles[i]);
                        ZipEntry entryold;

                        if (arryContains(oldFiles, newFiles[i])) {
                            entryold = oldjarFile.getEntry(newFiles[i]);
                        } else {
                            String listtemp;
                            if (i == 0) {
                                listtemp = newFiles[i];
                            } else {
                                listtemp = newline + "新版中增加的属性文件" + newFiles[i];
                            }
                            System.out.println(i + "--新版中增加的属性文件-->" + listtemp);
                            jTextArea_list.append(listtemp);
                            continue;
                        }
                        InputStream isnew = newjarFile.getInputStream(entrynew);
                        InputStream isold = oldjarFile.getInputStream(entryold);
                        PropertiesControl2 pc2new = new PropertiesControl2(isnew);
                        PropertiesControl2 pc2old = new PropertiesControl2(isold);
                        Map<String, String> kvMapNew = pc2new.getKeysAndValuesMap();
                        Map<String, String> kvMapOld = pc2old.getKeysAndValuesMap();
                        for (Map.Entry<String, String> entry : kvMapOld.entrySet()) {
                            if (kvMapNew.containsKey(entry.getKey())) {
                                System.out.println("old: " + kvMapOld.get(entry.getKey()) + " new: " + kvMapNew.get(entry.getKey()));
                                kvMapNew.put(entry.getKey(), entry.getValue());
                                System.out.println("new: " + kvMapNew.get(entry.getKey()));
                            }
                        }
                        Iterator<Map.Entry<String, String>> itnew = kvMapNew.entrySet().iterator();
                        InputStream is = null, is1 = null;
                        OutputStream fos = null;
                        File tmpFile;
                        try {
                            File jarFilePath = new File(jTextField_newfile.getText());
                            tmpFile = File.createTempFile("tmp", ".properties");
                            Properties prop = new Properties();
                            is = new FileInputStream(tmpFile);
                            prop.load(is);
                            fos = new FileOutputStream(tmpFile);
                            while (itnew.hasNext()) {
                                Map.Entry<String, String> entry = itnew.next();
                                String value = entry.getValue();
                                prop.put(entry.getKey(), value);
                            }
                            prop.store(fos, "Update:" + newFiles[i]);
                            is1 = new FileInputStream(tmpFile);
                            if (i == 0) {
                                pc2new.write2JarFile(jarFilePath, "new_" + jarFilePath.getName(), newFiles[i], pc2new.inputStream2byteArray(is1));
                            } else {
                                pc2new.write2JarFile(new File(jarFilePath.getParent(), "new_" + jarFilePath.getName()), null, newFiles[i], pc2new.inputStream2byteArray(is1), 2);
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            try {
                                assert is != null;
                                is.close();
                                assert is1 != null;
                                is1.close();
                                assert fos != null;
                                fos.close();
                            } catch (IOException ignored) {
                            }
                        }
                        bar_compare.setValue(bar_compare.getMaximum());
                        Thread.sleep(1000);
                    }

                } catch (Exception ignored) {
                }
                for (String sold : oldFiles) {
                    if (arryContains(newFiles, sold)) {

                    } else {
                        String listtemp = "";
                        listtemp = newline + "新版中删除的属性文件" + sold;
                        System.out.println("--新版中删除的属性文件-->" + listtemp);
                        jTextArea_list.append(listtemp);
                    }
                }
                jTextArea_list.setText(jTextArea_list.getText().replaceAll("正在比对中->", ""));
                jTextArea_list.append(newline + "比较完毕！！！" + newline + newline + newline + newline + newline);
                System.out.println("比较完毕！！！");
                bar_compare.setValue(bar_compare.getMaximum());
                isReplaceStart = false;
            }).start();
        } else {
            show("提示");
        }
    }

    private void jButton_mysqlActionPerformed() {
        writeIntoMysql();
    }

    private void jMenuItem_resetActionPerformed() {
        valueNewList.set(rightClickIndex, valueList.get(rightClickIndex));
        jTable_kv.updateUI();
    }

    private void writeIntoMysql() {
        if (jTextField_file.getText().length() != 0) {
            jTextField_file.getText();
            JarFile jarFile;
            DBManager dbm = new DBManager();
            Connection connection = null;
            Statement stmt;
            PreparedStatement pstmt;
            ResultSet rs;
            try {
                String sql_add1 = "insert into `AS`(`as_key`,`as_value_en`,`as_value_chinese`) values(%s,%s,%s)";
                String sql_addpstmt = "insert into `AS`(`as_key`,`as_value_en`,`as_value_chinese`) values(?,?,?)";
                String sql_select = "select * from `AS`";
                connection = dbm.getConnection();
                stmt = connection.createStatement();
                pstmt = connection.prepareStatement(sql_addpstmt);
                List<Integer> listTranslate = new ArrayList<>();
                List<String> listKey = new ArrayList<>();
                rs = stmt.executeQuery(sql_select);
                while (rs.next()) {
                    listKey.add(rs.getString("as_key"));
                    listTranslate.add(rs.getInt("as_translate_chinese"));
                }
                rs.close();
                boolean autoCommit = connection.getAutoCommit();
                connection.setAutoCommit(false);
                jarFile = new JarFile(jTextField_file.getText());
                String[] files = PropertiesControl.getJarFileContent(new File(jTextField_file.getText()));
                for (String file : files) {
                    ZipEntry entrynew = jarFile.getEntry(file);
                    InputStream is = jarFile.getInputStream(entrynew);
                    PropertiesControl2 pc2 = new PropertiesControl2(is);
                    Map<String, String> kvMap = pc2.getKeysAndValuesMap();
                    for (Map.Entry<String, String> entry : kvMap.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (listKey.contains(key)) {
                            System.out.printf("数据库中已包含文件[%s]中字段：%s", file, key);
                        } else {
                            System.out.println(String.format(sql_add1, setString(key), setString(value), setString("")));
                            stmt.executeUpdate(String.format(sql_add1, setString(key), setString(value), setString("")));
                        }
                    }
                }
                connection.commit();
                connection.setAutoCommit(autoCommit);
                stmt.close();
                pstmt.close();
            } catch (Exception ex) {
                Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    assert connection != null;
                    connection.rollback();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static boolean arryContains(String[] stringArray, String source) {
        if (stringArray != null) {
            for (String str : stringArray) {
                if (str.contains(source)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String setString(String x) {
        if (x == null) {
            return "null";
        } else {
            StringBuilder B = new StringBuilder(x.length() * 2);
            int i;
            B.append('\'');
            for (i = 0; i < x.length(); ++i) {
                char c = x.charAt(i);
                if (c == '\\' || c == '\'' || c == '"') {
                    B.append('\\');
                }
                B.append(c);
            }
            B.append('\'');
            return B.toString();
        }
    }

    private void listmousedoubleclicklistener() {
        jList1.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2 & e.getButton() == MouseEvent.BUTTON1) {
                    new Thread(() -> {
                        chooseConfigPath = jList1.getSelectedValue().toString();
                        index = jList1.locationToIndex(e.getPoint());
                        JarFile jarFile;
                        try {
                            jarFile = new JarFile(jTextField_file.getText());
                            int index = jList1.locationToIndex(e.getPoint());

                            ZipEntry entry = jarFile.getEntry(chooseConfigPath);
                            if (entry == null) {
                                System.out.printf("%s路径所代表的文件不存在!读取失败~%n", chooseConfigPath);
                            }

                            assert entry != null;
                            InputStream is = jarFile.getInputStream(entry);
                            if (pc2 != null) {
                                pc2 = null;
                            }
                            pc2 = new PropertiesControl2(is);
                            if (keyList.size() > 0) {
                                keyList.clear();
                            }
                            keyList = pc2.getKeyList();
                            if (valueList.size() > 0) {
                                valueList.clear();
                            }
                            valueList = pc2.getValueList();

                            if (valueNewList.size() > 0) {
                                valueNewList.clear();
                            }
                            valueNewList.addAll(valueList);
                            jProgressBar1.setMaximum(pc2.getCount());
                            jTable_kv.setRowSelectionInterval(0, 0);

                            jLabel1.setText(pc2.getCount() + "");

                            ((AbstractTableModel) jTable_kv.getModel()).fireTableDataChanged();
                            jTable_kv.setModel(dataModel);
                            if (jCheckBox1.isSelected()) {
                                isTranslateAll = true;
                                translate(valueList, 0);
                            } else {
                                isTranslateAll = false;
                            }

                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }).start();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    private void show(String title) {
        show(title, JOptionPane.WARNING_MESSAGE);
    }

    private void show(String title, int msgType) {
        int option = JOptionPane.showConfirmDialog(null, "替换正在进行中，请稍候。。。", title, JOptionPane.DEFAULT_OPTION, msgType, null);
    }

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> new TAS().setVisible(true));

    }

    class MyDropTargetListener extends DropTargetAdapter {

        private TAS tas;

        MyDropTargetListener(TAS tas) {
            this.tas = tas;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void drop(DropTargetDropEvent event) {
            if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                DataFlavor df = DataFlavor.javaFileListFlavor;
                List<File> list = null;
                try {
                    list = (List<File>) (event.getTransferable().getTransferData(df));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                assert list != null;
                for (File file : list) {
                    if (file.exists() && file.isFile()) {
                        String filePath = file.getAbsolutePath();
                        if (filePath.equals("")) {
                            System.out.println("文件名为 null 或为 \"\"~");
                            break;
                        }
                        if (!filePath.endsWith(".jar")) {
                            String str = "此工具专门为jar包设计，不通用!! 请注意!!";
                            JOptionPane.showMessageDialog(null, str);
                            break;
                        }
                        tas.jTextField_file.setText(filePath);
                    }
                    break;
                }
                event.dropComplete(true);
            } else {
                event.rejectDrop();
            }
        }
    }

    private TableModel dataModel = new AbstractTableModel() {
        private static final long serialVersionUID = 1L;

        public int getColumnCount() {
            return ColumnNames.length;
        }

        public int getRowCount() {
            return keyList.size();
        }

        @Override
        public String getColumnName(int col) {
            String s = null;
            if (col == 0) {
                s = ColumnNames[0];
            } else if (col == 1) {
                s = ColumnNames[1];
            } else if (col == 2) {
                s = ColumnNames[2];
            } else if (col == 3) {
                s = ColumnNames[3];
            }
            return s;
        }

        public Object getValueAt(int row, int col) {
            String s = null;
            if (col == 0) {
                s = row + 1 + "";
            } else if (col == 1) {
                s = keyList.get(row);
            } else if (col == 2) {
                s = valueList.get(row);
            } else if (col == 3) {
                s = valueNewList.get(row);
            }
            tableColumnModel.getColumn(col).setCellRenderer(multiLineHeaderRenderer);
            return s;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            super.setValueAt(aValue, rowIndex, columnIndex); //To change body of generated methods, choose Tools | Templates.
            if (!aValue.toString().equals(valueNewList.get(rowIndex))) {
                valueNewList.remove(rowIndex);
                valueNewList.add(rowIndex, aValue.toString());
                fireTableCellUpdated(rowIndex, columnIndex);
                jButton_saveProperties.setEnabled(true);
            } else {
                System.out.println("未编辑");
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 3;
        }

    };
    private int saveTime = 0;
    private PropertiesControl2 pc2;
    private static String newline = "\r\n";
    private static String chooseConfigPath = null;
    private String[] newFiles;
    private String[] oldFiles;
    private JScrollBar bar_compare;
    private int index = 0;
    private String[] ColumnNames = {"序号", "键名", "键值", "翻译"};
    private List<String> keyList = new ArrayList<>();
    private List<String> valueList = new ArrayList<>();
    private List<String> valueNewList = new ArrayList<>();
    private MultiLineRowRenderer multiLineHeaderRenderer = new MultiLineRowRenderer();
    private TableColumnModel tableColumnModel;
    private int rightClickIndex = 0;
    private boolean isReplaceStart = false;
    private boolean isTranslateAll = false;
    private final String[] language = {
            "zh",
            "cht",
            "en",
            "yue",
            "wyw",
            "jp",
            "kor",
            "fra",
            "spa",
            "th",
            "ara",
            "ru",
            "pt",
            "de",
            "it",
            "el",
            "nl",
            "pl",
            "bul",
            "est",
            "dan",
            "fin",
            "cs",
            "rom",
            "slo",
            "swe",
            "hu",
            "vie"
    };
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton_getJarContext;
    private javax.swing.JButton jButton_saveProperties;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBox_language;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JDialog jDialog_ra;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JProgressBar jProgressBar1;
    private JTable jTable_kv;
    private JTextArea jTextArea1;
    private JTextArea jTextArea_list;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField_bak;
    private javax.swing.JTextField jTextField_file;
    private javax.swing.JTextField jTextField_newfile;
    private javax.swing.JTextField jTextField_oldfile;
}
