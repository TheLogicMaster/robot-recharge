package com.thelogicmaster.robot_recharge.ui;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.thelogicmaster.robot_recharge.RobotRecharge;
import com.thelogicmaster.robot_recharge.code.Language;

public class LanguageSelect extends SelectBox<Language> {

    public LanguageSelect(Skin skin) {
        super(skin);

        setAlignment(Align.center);
        getList().setAlignment(Align.center);
        Array<Language> languages = new Array<>();
        for (Language language : Language.values())
            if (RobotRecharge.codeEngines.containsKey(language))
                languages.add(language);
        setItems(languages);
    }
}
