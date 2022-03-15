/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.impl.javase.simulator;


import com.codename1.impl.javase.util.SwingUtils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javax.swing.*;

/**
 *
 * @author shannah
 */
public class AppPanel extends JPanel {

   
   
    private String label;
    private String id;
    private JComponent content;
    private AppFrame.FrameLocation preferredFrame;
    private JScrollPane scrollPane;
    private boolean scrollableX, scrollableY;
    private JButton popupMenu;
    private JToolBar leftToolBar, rightToolBar;

    private JMenu moveTo = new JMenu("Move To");
    private JMenuItem moveToLeft = new JMenuItem("Left");
    private JMenuItem moveToRight = new JMenuItem("Right");
    private JMenuItem moveToBottom = new JMenuItem("Bottom");
    private JMenuItem moveToCenter = new JMenuItem("Center");


    
    public AppPanel(String id, String label, JComponent content) {
        this.id = id;
        this.label = label;
        this.content = content;
        initAppPanelUI();
    }
    
    
    public void setScrollable(boolean scrollX, boolean scrollY) {
        if (scrollPane == null && (scrollX || scrollY)) {
            Container parent = content.getParent();
            if (parent != null) {
                content.getParent().remove(content);
            }
            scrollPane = new JScrollPane(content);
            if (parent != null) {
                parent.add(scrollPane, BorderLayout.CENTER);
            } 
        } else if (scrollPane != null && !scrollX && !scrollY) {
            Container parent = scrollPane.getParent();
            if (parent != null) {
                parent.remove(scrollPane);
            }
            if (content.getParent() != null) {
                content.getParent().remove(content);
            }
            if (parent != null) {
                parent.add(content, BorderLayout.CENTER);
            }
        }
    }
    
    
    protected void initAppPanelUI() {
        setLayout(new BorderLayout());
        if (scrollPane != null) {
            add(scrollPane, BorderLayout.CENTER);
        } else {
            add(this.content, BorderLayout.CENTER);
        }

        leftToolBar = new JToolBar();
        leftToolBar.setFloatable(false);

        rightToolBar = new JToolBar();
        rightToolBar.setFloatable(false);



        JPanel north = new JPanel();
        north.setLayout(new BorderLayout());
        ImageIcon moreIcon = SwingUtils.getImageIcon(AppPanel.class.getResource("more.png"), 16, 16);
        
        popupMenu = new JButton(moreIcon);
        popupMenu.addActionListener(new PanelListener());
        rightToolBar.add(popupMenu);

        north.add(rightToolBar, BorderLayout.EAST);
        north.add(leftToolBar, BorderLayout.WEST);
        add(north, BorderLayout.NORTH);


        for (JMenuItem menuItem : new JMenuItem[]{moveToLeft, moveToRight, moveToBottom, moveToCenter}) {
            menuItem.addActionListener(new PanelListener());
        }

        updateAppPanelUI();
                
    }
    
    protected void updateAppPanelUI() {
        
    }
    
    protected void respondAppPanelUI(AppEvent event) {
        Object source = event.getSource();
        if (source == popupMenu) {
            JPopupMenu menu = createPopupMenu();
            menu.show(popupMenu, popupMenu.getX(), popupMenu.getY() + popupMenu.getHeight());
        } else if (source == moveToLeft) {
            getAppFrame().moveTo(this, AppFrame.FrameLocation.LeftPanel);
        } else if (source == moveToRight) {
            getAppFrame().moveTo(this, AppFrame.FrameLocation.RightPanel);
        } else if (source == moveToBottom) {
            getAppFrame().moveTo(this, AppFrame.FrameLocation.BottomPanel);
        } else if (source == moveToCenter) {
            getAppFrame().moveTo(this, AppFrame.FrameLocation.CenterPanel);
        }
    }
    
     /**
     * @return the preferredFrame
     */
    public AppFrame.FrameLocation getPreferredFrame() {
        return preferredFrame;
    }

    /**
     * @param preferredFrame the preferredFrame to set
     */
    public void setPreferredFrame(AppFrame.FrameLocation preferredFrame) {
        this.preferredFrame = preferredFrame;
    }
    
     /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private class PanelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            respondAppPanelUI(new AppEvent(e));

        }
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();

        moveTo.removeAll();
        AppFrame.FrameLocation currentLocation = getAppFrame().getPanelLocation(AppPanel.this);

        if (currentLocation != AppFrame.FrameLocation.LeftPanel)
            moveTo.add(moveToLeft);
        if (currentLocation != AppFrame.FrameLocation.RightPanel) {
            moveTo.add(moveToRight);
        }
        if (currentLocation != AppFrame.FrameLocation.BottomPanel) {
            moveTo.add(moveToBottom);
        }
        if (currentLocation != AppFrame.FrameLocation.CenterPanel) {
            moveTo.add(moveToCenter);
        }


        menu.add(moveTo);
        return menu;
    }

    public AppFrame getAppFrame() {
        java.awt.Container parent = getParent();
        while (parent != null) {
            if (parent instanceof AppFrame) {
                return (AppFrame)parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    public void addAction(Action action) {
        leftToolBar.add(action);
    }

    String getPreferencesPrefix(AppFrame frame) {
        return frame.getPreferencesPrefix() + "panels."+getId()+".";
    }

    public void savePreferences(AppFrame frame, Preferences prefs) {
        String prefix = getPreferencesPrefix(frame);

        AppFrame.FrameLocation location = frame.getPanelLocation(this);
        if (location == null) {

            prefs.remove(prefix + "preferredFrame");
        } else {
            prefs.put(prefix + "preferredFrame", location.name());
        }
    }

    public void applyPreferences(AppFrame frame, Preferences prefs) {
        String preferredFrameName = prefs.get(getPreferencesPrefix(frame)+"preferredFrame", null);
        if (preferredFrameName != null) {
            try {
                preferredFrame = AppFrame.FrameLocation.valueOf(preferredFrameName);
            } catch (IllegalArgumentException ex){}
        }
    }
}
