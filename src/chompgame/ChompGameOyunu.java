package chompgame;

import static chompgame.ChompGameOyunu2.butonlar;
import java.awt.Button;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class ChompGameOyunu extends javax.swing.JFrame {

    static JButton[][] butonlar;
    Server server;
    int a, b;
    int satir = 0;
    int sutun = 0;
    boolean siraBelirleme = true;

    public ChompGameOyunu() {

        initComponents();
        jLabel1.setVisible(siraBelirleme);
        jLabel2.setVisible(false);
        this.setSize(1000, 500);
        server = new Server(1500);
        butonlariDiziyeAt();
        ImageIcon myico = new ImageIcon("/Users/Osx/NetBeansProjects/ChompGame/images.png");
        ImageIcon neg = new ImageIcon("/Users/Osx/NetBeansProjects/ChompGame/icon-web-gr-100x100.png");
        for (int i = 0; i < butonlar.length; i++) {
            for (int j = 0; j < butonlar[i].length; j++) {
                butonlar[i][j].setIcon(myico);
            }

        }
        butonlar[2][0].setIcon(neg);

    }

    public Socket socket;
    public ObjectInputStream sInput;
    public ObjectOutputStream sOutput;
    public int id;
    public String username;
    public Date ConDate;
    public ChompGameOyunu.ClientThread ListenThread;

    /**
     * Creates new form ServerForm
     */
    ChompGameOyunu(Socket socket) {
        this.id = ++Server.uniqueId;
        this.socket = socket;

        try {
            this.sOutput = new ObjectOutputStream(socket.getOutputStream());
            this.sInput = new ObjectInputStream(socket.getInputStream());

            this.username = (String) sInput.readObject();

            this.ConDate = new Date();
            this.ListenThread = new ChompGameOyunu.ClientThread(this);

        } catch (IOException e) {
            Server.display("Exception .... " + e);
            return;
        } catch (ClassNotFoundException e) {

        }

    }

    public void start() {
        this.ListenThread = new ClientThread(this);
        this.ListenThread.start();

    }

    public void close() {
        try {
            if (this.ListenThread != null) {
                this.ListenThread.interrupt();
            }
            if (this.sOutput != null) {
                this.sOutput.close();

            }

            if (this.sInput != null) {
                this.sInput.close();

            }

            if (this.socket != null) {
                this.socket.close();

            }

        } catch (Exception e) {
        }

    }

    public boolean writeMsg(Object msg) {

        if (!this.socket.isConnected()) {
            close();
            return false;
        }

        try {
            this.sOutput.writeObject(msg);
        } catch (Exception e) {
            Server.display("Error sending message to " + username);
            Server.display(e.toString());
        }
        return true;

    }

    public class ClientThread extends Thread {

        ChompGameOyunu TheClient;

        public ClientThread(ChompGameOyunu TheClient) {
            this.TheClient = TheClient;
        }

        @Override
        public void run() {

            while (TheClient.socket.isConnected()) {

                try {

                    String message = (String) this.TheClient.sInput.readObject();
                   

                    if (message.equals("Tamam")) {

                        server.broadcast("Tamam");

                    } else {

                        for (int i = 0; i < message.length(); i++) {
                            if (message.charAt(i) == ' ') {
                                satir = Integer.parseInt(message.substring(0, i));
                                sutun = Integer.parseInt(message.substring(i + 1, message.length()));

                            }
                        }
                        if (satir == 2 && sutun == 0) {
                            ButonlarıKarart();
                            jLabel2.setVisible(true);
                            jLabel2.setText("KAZANDINIZ");
                            jLabel1.setText("Oyun Bitti");

                        } else {

                            verilenButonuKarart(satir, sutun);
                            //     jLabel1.setText("Sıra Karşı tarafta");
                        }
                    }

                } catch (IOException e) {
                    Server.display(this.TheClient.username + "exception reading Streams :" + e);
                    break;

                } catch (ClassNotFoundException ex) {

                    Server.display(this.TheClient.username + "Exception reading Streams " + ex);
                }

            }
            Server.remove(this.TheClient.id);

        }

    }

    void butonlariDiziyeAt() {

        butonlar = new JButton[3][5];

        butonlar[0][0] = jButton1;
        butonlar[0][1] = jButton2;
        butonlar[0][2] = jButton3;
        butonlar[0][3] = jButton4;
        butonlar[0][4] = jButton5;
        butonlar[1][0] = jButton6;
        butonlar[1][1] = jButton7;
        butonlar[1][2] = jButton8;
        butonlar[1][3] = jButton9;
        butonlar[1][4] = jButton10;
        butonlar[2][0] = jButton11;
        butonlar[2][1] = jButton12;
        butonlar[2][2] = jButton13;
        butonlar[2][3] = jButton14;
        butonlar[2][4] = jButton15;

    }

    void ButonlarıKarart() {
        for (int i = 0; i < butonlar.length; i++) {
            for (int j = 0; j < butonlar[i].length; j++) {
                butonlar[i][j].setEnabled(false);
                jLabel2.setVisible(true);
                jLabel2.setText("Kaybettin ");

            }
        }

    }

    void verilenButonuKarart(int satir, int sutun) {

        for (int i = sutun; 0 <= i; i--) {

            for (int j = satir; j < butonlar[i].length; j++) {

                butonlar[i][j].setEnabled(false);
                //  server.broadcast("" + satir + " " +sutun);
                a = satir;
                b = sutun;

            }
        }

    }

    void OyunOyna(JButton bu) {
        int sutun = 0;
        int satir = 0;

        for (int i = 0; i < butonlar.length; i++) {
            for (int j = 0; j < butonlar[i].length; j++) {

                if (bu.equals(butonlar[i][j])) {

                    if (bu.equals(butonlar[2][0])) {
                        a = 2;
                        b = 0;
                        ButonlarıKarart();

                        jLabel1.setVisible(true);
                        jLabel1.setText("Yenildiniz");
                    } else {

                        sutun = i;
                        satir = j;
                        verilenButonuKarart(satir, sutun);
                    }
                }

            }

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox1 = new javax.swing.JCheckBox();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton16 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        jCheckBox1.setText("jCheckBox1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextField1.setText(" CHOMP GAME");

        jLabel1.setText(" ");

        jButton5.setText("5");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ButonDinleme(evt);
            }
        });

        jButton6.setText("6");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ButonDinleme(evt);
            }
        });

        jButton7.setText("7");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ButonDinleme(evt);
            }
        });

        jButton8.setText("8");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ButonDinleme(evt);
            }
        });

        jButton9.setText("9");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ButonDinleme(evt);
            }
        });

        jButton10.setText("10");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ButonDinleme(evt);
            }
        });

        jButton11.setText("11");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ButonDinleme(evt);
            }
        });

        jButton1.setText("1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ButonDinleme(evt);
            }
        });

        jButton12.setText("12");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ButonDinleme(evt);
            }
        });

        jButton2.setText("2");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ButonDinleme(evt);
            }
        });

        jButton13.setText("13");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ButonDinleme(evt);
            }
        });

        jButton3.setText("3");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ButonDinleme(evt);
            }
        });

        jButton14.setText("14");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ButonDinleme(evt);
            }
        });

        jButton4.setText("4");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ButonDinleme(evt);
            }
        });

        jButton15.setText("15");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ButonDinleme(evt);
            }
        });

        jButton16.setText("Rakip Bul");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton13, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton9)
                    .addComponent(jButton4)
                    .addComponent(jButton14))
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jButton16)
                            .addGap(63, 63, 63)))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton16))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton5)
                            .addComponent(jButton4)
                            .addComponent(jButton3)
                            .addComponent(jButton1)
                            .addComponent(jButton2))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton6)
                            .addComponent(jButton7)
                            .addComponent(jButton8)
                            .addComponent(jButton9)
                            .addComponent(jButton10))
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton15)
                            .addComponent(jButton14)
                            .addComponent(jButton13)
                            .addComponent(jButton12)
                            .addComponent(jButton11))))
                .addContainerGap(214, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);


    }//GEN-LAST:event_jButton5ButonDinleme

    private void jButton6ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);
    }//GEN-LAST:event_jButton6ButonDinleme

    private void jButton7ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);
    }//GEN-LAST:event_jButton7ButonDinleme

    private void jButton8ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);
    }//GEN-LAST:event_jButton8ButonDinleme

    private void jButton9ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);
    }//GEN-LAST:event_jButton9ButonDinleme

    private void jButton10ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);
    }//GEN-LAST:event_jButton10ButonDinleme

    private void jButton11ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);
    }//GEN-LAST:event_jButton11ButonDinleme

    private void jButton1ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);
    }//GEN-LAST:event_jButton1ButonDinleme

    private void jButton12ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);
    }//GEN-LAST:event_jButton12ButonDinleme

    private void jButton2ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);
    }//GEN-LAST:event_jButton2ButonDinleme

    private void jButton13ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);
    }//GEN-LAST:event_jButton13ButonDinleme

    private void jButton3ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);
    }//GEN-LAST:event_jButton3ButonDinleme

    private void jButton14ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);
    }//GEN-LAST:event_jButton14ButonDinleme

    private void jButton4ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);
    }//GEN-LAST:event_jButton4ButonDinleme

    private void jButton15ButonDinleme(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ButonDinleme
        JButton tıklananButon = (JButton) evt.getSource();
        OyunOyna(tıklananButon);

        server.broadcast("" + a + " " + b);
    }//GEN-LAST:event_jButton15ButonDinleme

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed

        server.start();

        // JOptionPane.showMessageDialog(this,"Oyuncu Aranıyor");

    }//GEN-LAST:event_jButton16ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChompGameOyunu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChompGameOyunu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChompGameOyunu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChompGameOyunu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChompGameOyunu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static final javax.swing.JButton jButton1 = new javax.swing.JButton();
    public static final javax.swing.JButton jButton10 = new javax.swing.JButton();
    public static final javax.swing.JButton jButton11 = new javax.swing.JButton();
    public static final javax.swing.JButton jButton12 = new javax.swing.JButton();
    public static final javax.swing.JButton jButton13 = new javax.swing.JButton();
    public static final javax.swing.JButton jButton14 = new javax.swing.JButton();
    public static final javax.swing.JButton jButton15 = new javax.swing.JButton();
    private javax.swing.JButton jButton16;
    public static final javax.swing.JButton jButton2 = new javax.swing.JButton();
    public static final javax.swing.JButton jButton3 = new javax.swing.JButton();
    public static final javax.swing.JButton jButton4 = new javax.swing.JButton();
    public static final javax.swing.JButton jButton5 = new javax.swing.JButton();
    public static final javax.swing.JButton jButton6 = new javax.swing.JButton();
    public static final javax.swing.JButton jButton7 = new javax.swing.JButton();
    public static final javax.swing.JButton jButton8 = new javax.swing.JButton();
    public static final javax.swing.JButton jButton9 = new javax.swing.JButton();
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
