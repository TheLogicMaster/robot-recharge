package com.thelogicmaster.robot_recharge.gwt;

import com.badlogic.gdx.backends.gwt.preloader.DefaultAssetFilter;

public class BlocklyAssetFilter extends DefaultAssetFilter {

    @Override
    public boolean accept(String file, boolean isDirectory) {
        if (file.matches("assets/blockly"))
            return false;
        return super.accept(file, isDirectory);
    }
}
