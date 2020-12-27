package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.RobotUtils;
import com.thelogicmaster.robot_recharge.code.Command;
import com.thelogicmaster.robot_recharge.code.Language;

public class CommandCatalog extends Window {

    public CommandCatalog(Skin skin, Language language) {
        super("Command Catalog", skin);
        padTop(100).padLeft(20);
        setSize(900, 300);
        ImageButton catalogCloseButton = new ImageButton(skin, "close");
        getTitleTable().add(catalogCloseButton).padRight(10).size(80, 80).right();
        final List<String> commandList = new List<>(skin);
        final IterativeStack commandInfoStack = new IterativeStack();
        final Array<Command> commands = RobotUtils.json.fromJson(Array.class, Command.class,
                Gdx.files.internal("language/commands-" + language.name().toLowerCase() + ".json"));
        Array<String> commandLabels = new Array<>();
        for (Command command : new Array.ArrayIterable<>(commands)) {
            commandLabels.add(command.getName());
            Table infoTable = new Table(skin);
            infoTable.setBackground("windowTen");
            infoTable.align(Align.top);
            infoTable.pad(10, 20, 10, 20);
            StringBuilder args = new StringBuilder(command.getName()).append("(");
            for (String arg : command.getArgs())
                args.append(arg).append(",");
            if (command.getArgs().length > 0)
                args.deleteCharAt(args.length() - 1);
            args.append(")");
            Label argsLabel = new Label(args.toString(), skin);
            argsLabel.setWrap(true);
            infoTable.add(argsLabel).growX().left().row();
            Label descriptionLabel = new Label(command.getDescription(), skin);
            descriptionLabel.setWrap(true);
            infoTable.add(descriptionLabel).growX().left().row();
            Label exampleLabel = new Label("Ex: " + command.getExample(), skin);
            exampleLabel.setWrap(true);
            infoTable.add(exampleLabel).growX().left();
            commandInfoStack.add(infoTable);
        }
        commandList.setItems(commandLabels);
        ScrollPane commandPane = new ScrollPane(commandList, skin);
        commandPane.setForceScroll(false, true);
        commandPane.setScrollingDisabled(true, false);
        commandPane.setFadeScrollBars(false);
        add(commandPane).width(300).growY();
        add(commandInfoStack).grow();
        setVisible(false);
        setMovable(true);
        commandList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                commandInfoStack.show(commandList.getSelectedIndex());
            }
        });
        catalogCloseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
            }
        });
    }
}
