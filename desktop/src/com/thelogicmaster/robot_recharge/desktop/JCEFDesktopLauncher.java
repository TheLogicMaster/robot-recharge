package com.thelogicmaster.robot_recharge.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.thelogicmaster.robot_recharge.PlatformUtils;
import com.thelogicmaster.robot_recharge.WindowMode;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.Language;
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
import java.util.HashMap;

public class JCEFDesktopLauncher implements PlatformUtils {

    private final JFrame jFrame;
    private final LwjglAWTCanvas lwjglAWTCanvas;
    private final JCEFBlocklyEditor blocklyEditor;

    private JCEFDesktopLauncher() {
        CefApp.addAppHandler(new CefAppHandlerAdapter(null) {
            @Override
            public void stateHasChanged(CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.TERMINATED)
                    System.exit(0);
            }
        });

        blocklyEditor = new JCEFBlocklyEditor();
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.allowSoftwareMode = true;
        HashMap<Language, CodeEngine> engines = new HashMap<>();
        engines.put(Language.JavaScript, new DesktopJavaScriptEngine());
        engines.put(Language.Python, new DesktopPythonEngine());
        lwjglAWTCanvas = new LwjglAWTCanvas(new RobotRecharge(engines, blocklyEditor, this), config) {
            // Graceful exit
            @Override
            public void exit () {
                postRunnable(new Runnable() {
                    @Override
                    public void run () {
                        CefApp.getInstance().dispose();
                        stop();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                System.exit(0);
                            }
                        }).start();
                    }
                });
            }
        };
        blocklyEditor.setup();

        jFrame = createFrame();
        jFrame.getContentPane().setPreferredSize(new Dimension(960, 540));
        jFrame.add(blocklyEditor.getEditor());
        jFrame.add(lwjglAWTCanvas.getCanvas());
        jFrame.pack();
        jFrame.setVisible(true);
    }

    private JFrame createFrame() {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.setTitle("Robot Recharge");
        try {
            ArrayList<Image> icons = new ArrayList<>();
            icons.add(new ImageIcon(Gdx.files.internal("icon128.png").file().toURI().toURL()).getImage());
            icons.add(new ImageIcon(Gdx.files.internal("icon32.png").file().toURI().toURL()).getImage());
            icons.add(new ImageIcon(Gdx.files.internal("icon16.png").file().toURI().toURL()).getImage());
            frame.setIconImages(icons);
        } catch (MalformedURLException e) {
            Gdx.app.error("JCEF Launcher", "Failed to set icon");
        }

        frame.setMinimumSize(new Dimension(160, 90));
        frame.setLayout(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (Gdx.app != null)
                    Gdx.app.exit();
            }
        });

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                Dimension size = frame.getSize();
                lwjglAWTCanvas.getCanvas().setSize(size.width, size.height - 37); // Fix for bottom getting cut off
            }
        });
        return frame;
    }

    @Override
    public void setWindowMode(WindowMode windowMode) {
        GraphicsDevice device = jFrame.getGraphicsConfiguration().getDevice();
        switch (windowMode) {
            case Fullscreen:
                device.setFullScreenWindow(jFrame);
                break;
            case WindowedFullscreen:
            case Windowed:
                device.setFullScreenWindow(null);
                jFrame.setResizable(true);
                break;
        }
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
