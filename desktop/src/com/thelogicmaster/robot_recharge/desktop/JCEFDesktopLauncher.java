package com.thelogicmaster.robot_recharge.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import org.cef.CefApp;
import org.cef.handler.CefAppHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class JCEFDesktopLauncher extends JFrame {

    private JCEFDesktopLauncher() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CefApp.addAppHandler(new CefAppHandlerAdapter(null) {
            @Override
            public void stateHasChanged(CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.TERMINATED)
                    System.exit(0);
            }
        });

        final JCEFBlocklyEditor blocklyEditor = new JCEFBlocklyEditor();
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.allowSoftwareMode = true;
        final LwjglAWTCanvas canvas = new LwjglAWTCanvas(new RobotRecharge(new DesktopJavaScriptEngine(), blocklyEditor), config);
        blocklyEditor.setup();

        setSize(800, 480);
        setTitle("Robot Game");
        try {
            ArrayList<Image> icons = new ArrayList<>();
            icons.add(new ImageIcon(Gdx.files.internal("icon128.png").file().toURI().toURL()).getImage());
            icons.add(new ImageIcon(Gdx.files.internal("icon32.png").file().toURI().toURL()).getImage());
            icons.add(new ImageIcon(Gdx.files.internal("icon16.png").file().toURI().toURL()).getImage());
            setIconImages(icons);
        } catch (MalformedURLException e) {
            Gdx.app.error("JCEF Launcher", "Failed to set icon");
        }

        setMinimumSize(new Dimension(100, 100));
        setLayout(null);
        add(blocklyEditor.getEditor());
        add(canvas.getCanvas());

        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose();
                dispose();
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                Dimension size = getSize();
                canvas.getCanvas().setSize(size.width, size.height - 37); // Fix for bottom getting cut off
            }
        });
    }

    public static void main(String[] args) {
        if (!CefApp.startup(args)) {
            System.out.println("Startup initialization failed!");
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JCEFDesktopLauncher();
            }
        });
    }
}
