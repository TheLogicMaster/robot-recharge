package com.thelogicmaster.robot_recharge.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.*;
import com.thelogicmaster.robot_recharge.Robot;
import com.thelogicmaster.robot_recharge.code.BasicEngine;
import com.thelogicmaster.robot_recharge.code.CodeEngine;
import com.thelogicmaster.robot_recharge.code.JavaCodeEditorUtils;
import com.thelogicmaster.robot_recharge.code.JavaRobotController;
import com.thelogicmaster.robot_recharge.code.Language;
import com.thelogicmaster.robot_recharge.code.LuaEngine;
import com.thelogicmaster.robot_recharge.code.PhpEngine;
import com.thelogicmaster.robot_recharge.code.RubyEngine;
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
    private final DesktopBlocklyEditor blocklyEditor;
    private final Array<WindowMode> windowModes;
    private final LwjglAWTCanvas lwjglAWTCanvas;

    private JCEFDesktopLauncher() {
        CefApp.addAppHandler(new CefAppHandlerAdapter(null) {
            @Override
            public void stateHasChanged(CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.TERMINATED)
                    Gdx.app.exit();
            }
        });
        windowModes = new Array<>(new WindowMode[]{
                WindowMode.Windowed,
                WindowMode.Fullscreen
        });
        blocklyEditor = new DesktopBlocklyEditor();
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.allowSoftwareMode = true;
        for (int size : new int[] { 128, 64, 32, 16 })
            config.addIcon("icons/icon" + size + ".png", Files.FileType.Internal);
        final DesktopGameServices desktopGameServices = new DesktopGameServices();
        HashMap<Language, CodeEngine> engines = new HashMap<>();
        engines.put(Language.JavaScript, new DesktopJavaScriptEngine());
        engines.put(Language.Python, new DesktopPythonEngine());
        engines.put(Language.Lua, new LuaEngine());
        engines.put(Language.PHP, new PhpEngine());
        engines.put(Language.Basic, new BasicEngine());
        engines.put(Language.Ruby, new RubyEngine());
        // Graceful exit
        lwjglAWTCanvas = new LwjglAWTCanvas(new RobotRecharge(engines, blocklyEditor, this, new DesktopTTSEngine(), desktopGameServices, new JavaCodeEditorUtils(), BuildConfig.DEBUG), config) {
            // Graceful exit
            @Override
            public void exit () {
                postRunnable(() -> {
                    CefApp.getInstance().dispose();
                    stop();
                    exitDelayed();
                });
            }

            @Override
            protected void exception (Throwable ex) {
                CefApp.getInstance().dispose();
                super.exception(ex);
                exitDelayed();
            }
        };
        blocklyEditor.setup();

        jFrame = createFrame();
        jFrame.add(blocklyEditor.getEditor());
        jFrame.add(lwjglAWTCanvas.getCanvas());
        jFrame.pack(); // Pack to calculate insets
        Insets insets = jFrame.getInsets();
        jFrame.setPreferredSize(new Dimension(1920 + insets.left + insets.right, 1080 + insets.top + insets.bottom));
        jFrame.pack();
        jFrame.setVisible(true);
    }

    private void exitDelayed() {
        new Thread(() -> System.exit(0)).start();
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
                Insets insets = frame.getInsets();
                lwjglAWTCanvas.getCanvas().setSize(size.width - insets.left - insets.right, size.height - insets.top - insets.bottom);
            }
        });
        return frame;
    }

    @Override
    public Array<WindowMode> getWindowModes() {
        return windowModes;
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

    @Override
    public RobotController createRobotController(Robot robot, RobotExecutionListener listener, CodeEngine engine) {
        return new JavaRobotController(robot, listener, engine);
    }

    public static void main(String[] args) {
        // Disable display scaling
        System.setProperty("sun.java2d.uiScale", "1.0");

        // Todo: Move to initial loading screen to prevent initial startup delay
        if (!CefApp.startup(args)) {
            System.out.println("Startup initialization failed!");
            return;
        }

        SwingUtilities.invokeLater(JCEFDesktopLauncher::new);
    }
}
